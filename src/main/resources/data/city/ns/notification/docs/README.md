# Notifier

[:fontawesome-brands-confluence: TN-0056](http://go/tn-0056)

This system includes several services and tools aiming to reduce the effort to integrate a reliable notification mechanism in any service deployed to Agora and deliver messages to one or more end users.

## Send notifications from your service

[:octicons-file-code-24: Source](https://github.com/wp-wcm/city/blob/main/ns/demo/notifier/main.go)

There are 2 ways to publish notifications to the Notifier:

- [Kafka](kafka.md)
- [REST API](https://developer.woven-city.toyota/catalog/default/api/notification-api) (recommended)

For a full example of a service reading from an IoT device and pushing notification to a user refer to [temperature service](https://github.com/wp-wcm/city/tree/main/ns/demo/temperature-svc)

## Notification schema

Considering the example above:
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
We can identify 5 required and 2 optional properties with their corresponding subfields needed to deliver a correctly rendered notification:

1. `ID`: a uuid string to identify a single event
2. `Recipients`: an array of users receiving the notification represented as CityOS IDs stored in Keycloak (the identity management platform).
3. `Provider`: a last mile provider to deliver the notification with. For more details about using each provider see the [providers](providers.md) page.
4. `Template`: a template reference for the notification message. The `ID` field is used as index to retrieve a template from the [registry](https://github.tri-ad.tech/cityos-platform/notification-template-registry). For details about creating a new template see the [templates](templates.md) page.
5. `Data`: this is the payload we want to populate the template with and render the message.
6. `Callback`: is an optional notification status callback. It's used to receive receipts about successful or failed notifications. Both webhook and kafka methods are available. See [callbacks](callbacks.md) for more details.
7. `Files`: is an optional array of file attachments, currently supported only by email provider. See [providers](providers.md) for an email with attachments example.
