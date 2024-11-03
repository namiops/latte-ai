# Notification service


## Running in VS code in debugging mode

For debugging Notifier code in VS code, you have to make sure all the necessary dependencies are taken care of first.
> **Note:**  The following configuration uses lab cluster.

### Prepare dependencies

1. Keycloak

    In order to retrieve the Woven user data, Notifier needs use a configured confidential Keycloak client. It authorizes the Notifier to get a jwt access token for making http requests.

    For lab cluster, the Keycloak client is already created with a `notification` client id. You may find the correspondent client secret in the (VPN) [Keycloak admin panel](https://id.agora-lab.woven-planet.tech/auth/admin/woven/console/#/realms/woven/clients/notification/credentials).
    Reach out to @agora-services for getting an admin panel access to your Keycloak user.

2. Kafka message bus

    Since Kafka cluster is deployed externally on AWS, we have no option to port-forward it like other services. For this reason, we will need to deploy a local Kafka cluster.

    **Local deployment:**

    > **Note:** Make sure you are using the local context for `kubectl` for this section. You can also append `--context <local-context>` to the command. We do not need the complete Agora local cluster, a basic minikube cluster is enough.

    Head over to `infrastructure/k8s/local/notification/kafka` and run `kubectl apply -k .` Most likely, you will get an error because some of the dependency resources are not ready, so run the same command again until you get no errors.

    You can monitor the process of deployment with the [k9s](https://k9scli.io/topics/install/) cli tool. The whole process may take around 10-15 minutes.

    **Getting the kafka url:**

    If you're running Minikube, then simply run the command `minikube ip` to get the ip address of the node.

    Otherwise, for something like Rancher Desktop, run `kubectl get nodes -o wide` and take note of the `EXTERNAL-IP` value.

    Next, you will need to retrieve the port number of the kafka service with `kubectl get service cityos-kafka-kafka-external-bootstrap -o=jsonpath='{.spec.ports[0].nodePort}{"\n"}' -n kafka`

    Lastly, put the two together as `<node-external-ip:kafka-port>` and set the value in the notifier config (more details below).

3. Setup port forwarding by running `sh ./setup-dep-local-env.sh`

    - SMTP will be forwarded to: localhost:2525
    - SKVS will be forwarded to: localhost:15984

    Notifier needs the Secure KVS (SKVS) for storing custom attributes, like slack channel webhook and woven app token, that are not appropriate for storing in Keycloak.

4. Template registry
    Notifier uses [Github](https://github.tri-ad.tech/cityos-platform/notification-template-registry) (subject to change) to store the notification templates in a separate repository, so we need to provide an access token for the Notifier to retrieve them.

    Head over to https://github.tri-ad.tech/settings/tokens and create a new Personal Access Token with only `repo` scope enabled.
    > **Note:** If you joined the team after the github migration and have an account only for https://github.com/wp-wcm/city, then you may not have the access to the former github. In that case, reach out to @agora-services for assistance.

5. Last Mile Providers:
    - SMTP: already handled in Step 3. 
    - Slack: nothing to do, except get an invite to the monitoring slack channel from @agora-services
    - Woven App (Optional): reach out to @agora-services for the relevant secrets

### Local configuration file

Once all dependencies are taken care of, you are ready to create the local Notifier config. It's recommended to create it under `/ns/.vscode/notifier_config.json` to avoid getting tracked by git.

```json
{ 
    "KEYCLOAK_CONFIG":{ 
        "KEYCLOAK_URL": "https://id.agora-lab.woven-planet.tech",
        "KEYCLOAK_REALM": "woven",
        "KEYCLOAK_CLIENT_ID":"notification", 
        "KEYCLOAK_CLIENT_SECRET":"<keycloak-client secret>"
    },
    "KAFKA_CONFIG":{
        "KAFKA_SERVER": ["<node-external-ip:kafka-port>"],
        "KAFKA_TOPIC": "notification.notifications",
        "KAFKA_CONSUMER_GROUP": "notification.consumers"
    },
    "LMP_CONFIG":{ 
        "SMTP_CONFIG":{ 
            "SMTP_HOST": "localhost", // port-forwarded
            "SMTP_PORT": "2525",
            "SMTP_FROM": "city-lab-noreply@woven-planet.global"
        }, 
        "APP_CONFIG":{ 
            "APP_DISABLED":true 
        },
        "APP_CONFIG_DEV":{ 
            "APP_DISABLED_DEV":true 
        } 
    },
    "SKVS_CONFIG":{
        "SECURE_KVS_URL": "localhost:15984", // port-forwarded with tinyproxy
        "SECURE_KVS_DBNAME": "notification_settings"
    },
    "NOTIFIER_RETRY_COUNT":1,
    "REGISTRY_TOKEN": "<personal-github-token>"
}
```

### Prepare configuration for `launch.json`

```json
{
    "name": "Launch Notifier",
    "type": "go",
    "request": "launch",
    "mode": "auto",
    "program": "notification/cmd/main.go",
    "env": {
        "CONFIG_PATH": "/<path-to-repo>/city/ns/.vscode/notifier_config.json"
    }
}
```

### Re-generating client.go
run
```sh
oapi-codegen -config=generator-configs/models-client.yaml api/api.yaml
```

### Test notification 

Now, you are ready to launch the Notifier and send a test notification via [REST API](api/api.yaml).

#### Getting the user's woven ID
User woven ID here is the same as Keyclock ID. 

1. If you want to know your own woven ID, the easiest way is to get it from [Agora UI > Notifications](https://agora-ui.agora-lab.woven-planet.tech/admin/notifications) -> create new notification and click "Notify me"
2. Getting it from Keyclock, login to Keyclock admin UI using the credential below, [lab](https://id.agora-lab.woven-planet.tech/auth) |  [lab2](https://id.agora-lab.woven-planet.tech/auth/) -> Users -> and you can get the ID from the ID column.

```sh
KEYCLOAK_LOGIN=$(kubectl --context=lab -n id get secret credential-keycloak -o jsonpath='{.data.ADMIN_USERNAME}' | base64 -d)
KEYCLOAK_PASSWORD=$(kubectl --context=lab -n id get secret credential-keycloak -o jsonpath='{.data.ADMIN_PASSWORD}' | base64 -d)
```

> Note: use credential-keycloak-22 for lab2


#### Sending email

This curl request will send an email notification with a sample bill graph
```shell
curl --location 'localhost:8081/notify' \
--header 'Content-Type: application/json' \
--data '{
    "id": "67a39e14-ebaf-43b8-be72-62474d412112",
    "recipients": ["<your-woven-id>"],
    "provider": 2,
    "template": {
        "id": "bill.txt"
    },
    "data": {
        "total": "200"
    }
}'
```

#### Sending to slack channel
1. Slack notification can be sent to a channel through a Slack webhook, in order to do this, you have to register the webhook from [Incoming WebHooks](https://woven-by-toyota.slack.com/apps/A0F7XDUAZ-incoming-webhooks) integration page.
2. Then save the Webhook URL and copy the ending part after `https://hooks.slack.com/services/<slackChannelWebhook>` e.g. `TZSD58W5A/B06VDXN1UJ5/jY2eDP3FSMr1dRLqso12WShq`
3. Call the user settings endpoint to register the Slack channel e.g.
```sh
curl --location --request PATCH 'http://localhost:8081/<user-id>/settings/slackChannelWebhook' \
--header 'Content-Type: text/plain' \
--data '{
  "slackChannelWebhook": "TCSD68W5A/B06VDEN1UJ2/jY2eDP3PSMr1dRLqson2WThq"
}'
```
4. Call endpoint Notify
```sh
curl --location 'http://localhost:8081/notify' \
--header 'Content-Type: text/plain' \
--data '{
  "id": "a658ab8b-9f8c-4ce9-9b46-c358bd2a2988",
  "name": "test",
  "provider": 1,
  "template": {
    "id": "sus-release-created.txt"
  },
  "data": {
    "tenantName": "test-tenant",
    "releaseId": "test-tenant/bumblebee/main-aaaa-bbbbb",
    "groupName": "bumblebee",
    "releaseLink": "https://agora-ui.agora-lab.woven-planet.tech/admin/xenia-management/distributions"
  },
  "recipients": [
    "<your-woven-id>"
  ]
}'
```

5. Check your Slack channel, you should get the message shortly!
