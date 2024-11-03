# Status Callbacks

The callback functionality allows users to track the status of the notifications they send via 2 possible methods.

The following is the schema of the status notification payload:

```yaml
NotificationStatus:
  required:
    - event_id
    - recipient
    - success
    - error
    - provider
  type: object
  properties:
    event_id:
      type: string
      format: uuid
    recipient:
      type: string
      format: uuid
      description: City OS UID of the original recipient
    success:
      type: boolean
      description: false if failed to deliver, true otherwise
    error:
      type: string
      description: error message if failed to deliver, empty otherwise
    provider:
      type: string
      description: a name of the provider used for notifying      
```

!!! Note
    In case of a multicast notification, there will be a `NotificationStatus` record per recipient having a common `event_id`.

## Callbacks example

=== "Webhook"

    To receive a notification receipt via webhook, the following conditions need to be satisfied:

    1. An exposed http server to the internal cluster (need to ask for assistance if outside the cluster)
    2. A separate endpoint for receiving the status receipt that returns `200` status code

    An example of using `Webhook` as callback method in a `Notification`

    ```go
    n := &notification.Notification{
      ID: uuid.NewRandom().String()
      Recipients: []string{"2089ad58-857e-4b31-a647-b78432c129d2"},
      Provider: notification.Slack,
      Data: data,
      Template: &notification.TemplateRef{
        ID:         "quickstart.txt"
      },
      Callback: &notification.StatusCallback{
        Type: notification.Webhook,
        Target: "<target_url>",
      },
    }
    ```

=== "Kafka"

    ### Adding callback topic to ACL

    To receive a notification receipt via kafka, the receiving topic needs to be added to the ACL beforehand.
    Please create a new k8s manifest for your kafka client (if you don't have one) with the following content:

    ```yaml
    apiVersion: kafkagroup.woven-city.global/v1alpha3
    kind: CityOsKafka
    metadata:
      name: <name>
      namespace: <namespace>
    spec:
      topics:
        - name: <service>-callback  # This will result in <namespace>.<service>-callback
          partitions: 1
          replicationFactor: 1
      acls:
        # ACLs for the topic should be declared in the same place as the topic.
        - resource:
            resourceName: <service>-callback  # This will result in <namespace>.<service>-callback
            resourcePattern: literal
            resourceType: topic
          policies:
          - principal:
              - User:CN=notification,OU=CityOS
            operation:
              - write
            permission: allow

          - principal:
              - User:CN=<namespace>,OU=CityOS
            operation:
              - read
            permission: allow
        # ACLs for consumer groups should be declared for every namespace that wants to read from Kafka,
        # regardless of whether it is reading from "its own" topics or other topics.
        - resource:
            resourceName: consumers  # This will result in <namespace>.consumers
            resourcePattern: literal
            resourceType: group
          policies:
            - principal:
                - User:CN=<namespace>,OU=CityOS
              operation:
                - read
              permission: allow
    ```

    ### Example notification with callback

    ```go
    n := &notification.Notification{
      ID: uuid.NewRandom().String()
      Recipients: []string{"2089ad58-857e-4b31-a647-b78432c129d2"},
      Provider: notification.Slack,
      Data: data,
      Template: &notification.TemplateRef{
        ID:         "quickstart.txt"
      },
      Callback: &notification.StatusCallback{
        Type: notification.Kafka,
        Target: "<namespace>.<service>-callback",
      },
    }
    ```
