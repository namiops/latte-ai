# Production environment: Onboarding

# Summary

For better overall service stability and user experience, Agora IoTA platform will be using cloud-managed RabbitMQ instances in production clusters.

# How does this affect me, the tenant?

## Connectivity

### Devices
Your devices will continue to connect to IoTA services as usual (consistent with the pre-production cluster).

- `IoTA` access:
  - External URL: `iot.woven-city-api.toyota`
    - 443: HTTPS, IoTA API.
    - 4318: HTTPS, IoTA telemetry-collector.
    - 5671: AMQPS.
    - 8883: MQTTS.
  - Internal URL: `iota.agora-iot-prod.svc`
    - 8081: HTTP, IoTA API.
    - 8080: HTTP, IoTA telemetry-collector.
    - 5672: AMQP.
    - 1883: MQTT.
- `RabbitMQ UI` access: https://iot.woven-city-api.toyota/rabbitmq/index.html

Connection will be secured by mTLS with certificates being created by `iotad` or `iotactl`.

### In-cluster services
In order to ensure the cloud RabbitMQ instance connectivity, you'll need to execute a couple of Bazel commands:

1. Add the following to your in-cluster manifests' BUILD file:
    ```bash
    load("//ns/iot/iota/ytt:iota_speedway_ytt.bzl", "iota_rabbitmq_speedway")
    
    iota_rabbitmq_speedway(name = "iota-rabbitmq-access")
    ```
2. Run the following command from the BUILD file directory:
    ```bash
    bazel run :iota-rabbitmq-access.copy
    ```
3. Add a newly-generated `z-iota-rabbitmq-speedway.yaml` file to your `kustomization.yaml`
4. To connect to rabbitmq, add the following annotations to your deployment:

    ```yaml
    apiVersion: apps/v1
    kind: Deployment
    metadata:
      name: <my-deployment>
      namespace: <my-namespace>
    spec:
      template:
        metadata:
          annotations:
            # Speedway: Istio DNS resolution to be able to access ServiceEntries.
            proxy.istio.io/config: |
              proxyMetadata:
                ISTIO_META_DNS_CAPTURE: "true"
                ISTIO_META_DNS_AUTO_ALLOCATE: "true"
    ```

5. Add a hosts entry to a Sidecar manifest in `your` namespace (SMC [example](https://portal.tmc-stargate.com/docs/default/component/stargate-welcome-guides/stargate-multicloud/documentation/features/service-mesh/intra-mesh-traffic/#one-way-a-b)); then add your namespace to [this file](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-iot/speedway/prod/tenant-api-access.yaml), so that `autocommit` could automatically create an allow-listing authorization policy.

    Example tenant-api-access.yaml (the [telemetry access](https://developer.woven-city.toyota/docs/default/component/telemetry-collector) is optional in this case):
   
    ```yaml
    #@data/values
    ---
    - tenant: <my-tenant>
      namespace: <my-namespace>
      telemetry:
        include: true
    ```

    Example Sidecar custom resource hosts entry (`your` namespace):

    ```yaml
    - "agora-iot-prod/*"
    ```

6. Run:
    ```bash
    bazel run //:gazelle
    ```
7. Commit your changes to the monorepo.

Example: [access setup for IoTA services themselves](https://github.com/wp-wcm/city/tree/main/infra/k8s/agora-iot/speedway/common/rabbitmq-cloud-0.0.3).

## Resource creation
Since Agora IoTA will no longer manage in-cluster RabbitMQ instance, operator manifests from pre-prod or legacy clusters will no longer work, 
but worry not, instead you can create RabbitMQ objects via [pulumi](https://www.pulumi.com/) and the `onboarding service`. 
Pulumi will work as an IaC tool for your needs and the onboarding service will execute your code, effectively working as your code runner for RabbitMQ management.

# Projects

There are 2 types of projects the onboarding service can deploy: `system` and `tenant`. Both are written using `pulumi`.

## System

Location:
`ns/iot/onboarding/pulumi/system`

System projects are the ones owned by the Agora IoTA team, these serve as out-of-the-box solutions that you can use.
Typically, all you need to do is to modify a single yaml file (pulumi stack configuration, ex: `Pulumi.prod.yaml`)  and raise a PR for IoTA team to approve.

Available projects are:

- [tenant-creation](https://github.com/wp-wcm/city/blob/main/ns/iot/onboarding/pulumi/system/tenant-creation/README.md) - 
a project that will allocate an IoTA "tenant" for you, specify the name, users you want to add to it and you're done!
- [tenant-rabbitmq-routing-creation](https://github.com/wp-wcm/city/blob/main/ns/iot/onboarding/pulumi/system/tenant-rabbitmq-routing-creation/README.md) -
a project that will create RabbitMQ exchanges, queues and bindings for you.
- [tenant-rabbitmq-user-creation](https://github.com/wp-wcm/city/blob/main/ns/iot/onboarding/pulumi/system/tenant-rabbitmq-user-creation/README.md) -
a project that will create RabbitMQ users and their permissions.
- [vhost-creation](https://github.com/wp-wcm/city/blob/main/ns/iot/onboarding/pulumi/system/vhost-creation/README.md) -
a project that will create a RabbitMQ VHost for you, note that `tenant-creation` stack allocate 1 VHost with the same name automatically.
Use this project if you have a specific need for more VHosts.
- more to come.

The main benefit of system stack is the ease of use, these are pre-created in order to cover the most common use cases, 
but you can add more using `tenant` projects.

## Optional: Tenant

Location:
`ns/iot/onboarding/pulumi/tenant`

Tenant projects are custom projects for you to own. 
If all of your use cases can be covered by `system` projects, and you don't need the flexibility of `pulumi`, you can stop reading here. 

Otherwise, you are free to create RabbitMQ resources using `pulumi` golang dialect. 
The main benefit of using self-managed `pulumi` projects is the versatility: because your project is backed by a full-fledged programming language,
you can add your elaborate logic during the resource creation process or simply bulk create with loops.
On top of that, each tenant project will get a set of tenant-scoped RabbitMQ credentials injected for your convenience!

In order to create your own project, after you've merged `tenant-creation` PR, add a subfolder with the same name under `tenant`, 
write your `pulumi` code and name your stack configuration yaml file after the env you're aiming for, for example: `Pulumi.prod.yaml`.
Additionally, please add your program [here](https://github.com/wp-wcm/city/blob/main/ns/iot/onboarding/BUILD#L52-L55).
The onboarding service will run your stack in a given environment and create the resources once the PR is merged.

Examples of tenant projects:

- [test stack (used by IoTA team for testing)](https://github.com/wp-wcm/city/tree/main/ns/iot/onboarding/pulumi/tenant/test)
- [sample stack (used as a barebones example)](https://github.com/wp-wcm/city/tree/main/ns/iot/onboarding/pulumi/tenant/sample)

## Validation

When adding users to the tenant you need to make sure that the user's username is correct, some people have their email address as their username, others have specified another username. please check each user's username via the following [link](https://id.woven-city.toyota/auth/realms/woven/account/#/personal-info) and add them

Once created, you can verify that the resources have been created by running the following commands:

```sh
curl https://iot.woven-city-api.toyota/onboarding/api/v1/{kind}/{project}/{stack}/outputs
```

- `kind`: Choose between `system` or `tenant`.
- `project`: Select the directory name from either https://github.com/wp-wcm/city/tree/main/ns/iot/onboarding/pulumi/system or https://github.com/wp-wcm/city/tree/main/ns/iot/onboarding/pulumi/tenant.
- `stack`: Enter the name of the file you created without the `.yaml` extension.

For example:

To verify that the resources have been successfully created, you can run the following curl commands:

```sh
curl https://iot.woven-city-api.toyota/onboarding/api/v1/system/tenant-creation/prod_ac-access-control-host/outputs
```

or

```sh
curl https://iot.woven-city-api.toyota/onboarding/api/v1/system/tenant-creation/prod_ac-access-control-host/summary
```

These commands will retrieve the outputs or a summary of the created resources based on the specified `system`, `project`, and `stack` values.

For more detailed information, please refer to [this link](https://github.com/wp-wcm/city/blob/main/ns/iot/onboarding/service/api/onboarding.yaml)

### Previewing

Because you will be using `pulumi` as your go-to IaC tool, you will be able to plan/preview your `tenant` stacks.

For each `tenant` project the onboarding service will allocate a unique set of RabbitMQ credentials scoped to your `vhost`
as well as a unique pulumi passphrase that will be distributed to you via [1password](https://wovenbytoyota.1password.com/).
This will allow you to import your stack `state` locally and plan against it.

### Example

In this example we'll use [test tenant project](https://github.com/wp-wcm/city/tree/main/ns/iot/onboarding/pulumi/tenant/test) located in the `prod` environment.

1. [Install pulumi](https://www.pulumi.com/docs/install/), then use the local backend `pulumi login --local`
2. Retrieve your pulumi passphrase from `1password` provided by IoTA team and export it:
    ```bash
    export PULUMI_CONFIG_PASSPHRASE="..."
    ```
3. Navigate to the folder with your project, for example `cd your/path/ns/iot/onboarding/pulumi/tenant/test`
4. Download an encrypted state for your stack, in `prod` and `test` project case:
    ```bash
    curl https://iot.woven-city-api.toyota/onboarding/api/v1/tenant/test/prod/export > export.json
    ```
5. Run the following script to import the stack (RabbitMQ variables can be random during planning, they just have to be set):
    ```bash
    pulumi config set rabbitmq:endpoint http://localhost
    pulumi config set rabbitmq:username foo
    pulumi config set rabbitmq:password bar --secret
    pulumi stack import --file export.json
    ```
6. Run the preview:
    ```bash
    pulumi preview --refresh=false
    ```

    Since there are no changes, the above will give the following output:
    ```bash
    Previewing update (prod):
         Type                 Name       Plan
         pulumi:pulumi:Stack  test-prod
    
    Resources:
        4 unchanged
    ```

    But if we modify something, the output will be as following:
    ```bash
    Previewing update (prod):
         Type                       Name                                 Plan        Info
         pulumi:pulumi:Stack        test-prod
     +-  ├─ rabbitmq:index:Binding  test-metrics_rabbitmq_queue_binding  replace     [diff: ~destination]
     +-  └─ rabbitmq:index:Queue    test-metrics_rabbitmq_queue          replace     [diff: ~name]
    
    Resources:
        +-2 to replace
        2 unchanged
    ```
7. Once ready - push your changes to the monorepo, but omit the mock variables and `encryptionsalt` you've set during the step #5.
