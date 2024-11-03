# Broker resources 

This section will explain how to add static resources to RabbitMQ using the topology operator.
These resources can be Vhosts, Users, Permissions and Policies. 

Find out more visiting the offical operator [documentation](https://www.rabbitmq.com/kubernetes/operator/using-topology-operator.html).


## Add Vhost :material-security:{ title="This is an administrative task and requires Agora admins approval" }

This is an administrative operation and will require Agora admin approval because it defines a self contained environment (or tenant) in which teams are free to create users, queues and in general all the other resources needed with data separation and security in place.

### Configuration

Add an entry to the [Vhosts](../../../../../infrastructure/k8s/dev/iot/rabbitmq-vhosts.yaml) file by replacing the `<tenant>` keyword template below with the name of the tenant you want to add. 

!!! Note

    The `<tenant>` MUST be a string prefixed with your namespace due to some permission settings. If your namespace is `robots` your tenant could look like `robots-services` or `robots-system`

```yaml
apiVersion: rabbitmq.com/v1beta1
kind: Vhost
metadata:
  name: <tenant>-vhost
  namespace: iot
spec:
  name: <tenant>
  rabbitmqClusterReference:
    name: rabbitmq

```

Example, given `robots` as tenant we'll have:

```yaml
apiVersion: rabbitmq.com/v1beta1
kind: Vhost
metadata:
  name: robots-systems-vhost
  namespace: iot
spec:
  name: robots-systems
  rabbitmqClusterReference:
    name: rabbitmq

```
This will create a vhost called robots-systems.

## Add a RabbitMQ User

In order to talk to IoTA a dev might need to create a RabbitMQ user. To avoid manual intervention and maintenance we can use the Topology operator as follows:

### Define user and permissions

In [resources](../../../../../infrastructure/k8s/dev/iot/resources/) create a folder named as the tenant above and add a users.yaml file that will look like the code below

```YAML
apiVersion: rabbitmq.com/v1beta1
kind: User
metadata:
  name: <tenant>-svc-1 # this is the name of the secret in the namespace
  namespace: <tenant namespace> #this might be different than the vhost if a team has multiple iot tenants but only 1 namespace
spec:
  rabbitmqClusterReference:
    name: rabbitmq
    namespace: iot
---
apiVersion: rabbitmq.com/v1beta1
kind: Permission
metadata:
  name: <tenant>-svc-1-permission
  namespace: <tenant namespace>
spec:
  vhost: "<tenant>"
  userReference: 
    name: <tenant>-svc-1
  permissions:
    write: ".*"
    configure: ".*"
    read: ".*"
  rabbitmqClusterReference:
    name: rabbitmq
    namespace: iot
```

**Note:** the name in the User resource does not refer to the rabbitmq user but to the secret created in the k8s namespace, let's say **robots**. This because the user and password data will be published by the operator in the secret transparently to avoid manipulation and plain text transfer. 

The secret will look like `<tenant>-svc-1-user-credentials` and to inspect its content you can run 

```shell
 kubectl get secret robots-svc-1-user-credentials -n robots -o json | jq '.data | map_values(@base64d)'
```
which will returns something like

```json
{
  "password": "asd123",
  "username": "randomusername"
}
```

We suggest to avoid using the credentials as is and reference the secret keys username and password in the service deployment.

!!! Note
  Make sure that your namespace is added to the `rabbitmq.com/topology-allowed-namespaces` annotation in the [RabbitMQ cluster specification](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/dev/iot/patches/rabbit-annotations.patch.yaml) for the RabbitMQ operator to reconcile resources.


### Use the secret in a deployment
At this point a dev can prepare its service deployment as follows (note the YAML below might change depending on your needs):

```YAML
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: anyservice
spec:
  replicas: 1
  selector:
    matchLabels:
      app: anyserviceapp
  template:
    metadata:
      labels:
        app: anyservice
    spec:
      containers:
      - name: anycontainer
        image: docker.artifactory-ha.tri-ad.tech/myimage
        imagePullPolicy: Always
        env:
        - name : IOTA_SVC_USER
          valueFrom:
            secretKeyRef:
              name: <tenant>-svc-1-user-credentials
              key: username # this MUST match the key in the secret
        - name : IOTA_SVC_PASSWORD
          valueFrom:
            secretKeyRef:
              name: <tenant>-svc-1-user-credentials
              key: password # this MUST match the key in the secret
```

At this point your service should be able to consume from IoTA.


### Exchanges, Queues and Bindings

Once you define exchange, queue and binding as shown below you can open a PR. Upon merge the CI/CD pipeline will do the rest and deploy these resources to your tenant / vhost.

!!! Note
    The namespace, vhost and the tenant are not the same thing. When you define a resource you choose the namespace in which CI/CD will deploy it (iot) and the vhost in which it will be created and on which configure RBAC (myVhost). In this case the vhost is only one of the possible data spaces a tenant can manage.

```YAML
---
apiVersion: rabbitmq.com/v1beta1
kind: Exchange
metadata:
  name: myExchange
  namespace: iot
spec:
  vhost: "myVhost"
  name: dev.myexchange.direct
  type: direct
  autoDelete: false
  durable: true
  rabbitmqClusterReference:
    name: rabbitmq
---
apiVersion: rabbitmq.com/v1beta1
kind: Queue
metadata:
  name: dev-robots-response
  namespace: iot
spec:
  vhost: "myVhost"
  name: dev.robots.response
  autoDelete: false
  durable: true
  rabbitmqClusterReference:
    name: rabbitmq
---
apiVersion: rabbitmq.com/v1beta1
kind: Binding
metadata:
  name: dev-robots-binding
  namespace: iot
spec:
  vhost: "myVhost"
  source: dev.myexchange.direct
  destination: dev.robots.response
  destinationType: queue
  routingKey: robots.key1
  rabbitmqClusterReference:
    name: rabbitmq
---
```
