# Providers

The system is currently able to use a few last mile providers to deliver notifications. In order to do so, each provider needs a specific configuration. The common ground for all the providers is the usage of a `Notification` structure. The notifier is able to parse it and use the fields to deliver the message using a text template and some data map to render values.

Once the notification is delivered the rendering engine will combine the values in `Data`, user values retrieved from the Identity Provider and the template in `Template.ID`. Example:

If our source link points at a template with the following text


`Hey {{.firstName}}, your coins count is {{.coins}}`


and the Data field is a simple map such as `{"coins":42}` and the Recipient.ID points to a user whose first name is "Mario", then the rendering engine will return:

`"Hey Mario, your coins count is 42"`

Check the [Upload a custom template](templates.md) section below for more details.

### Providers example

=== "Slack"
	
	To deliver notification through Slack, you will need to prepare an [incoming webhook](https://api.slack.com/messaging/webhooks) for that channel.

	Additionally, you need to associate the webhook url with the target user by saving the last 3 tokens of the url to the Notifier, so if the original url is `https://hooks.slack.com/services/uasd/uasfj/asdUsda123`, then we need the `uasd/uasfj/asdUsda123` part.
	
	It can be done by calling the Notifier API's [PATCH /{user_id}/settings/slackChannelWebhook](https://developer.woven-city.toyota/catalog/default/api/notification-api/definition#/Notification/createOrPatchUserSettings) endpoint with the following payload:
	```json
	{
		"slackChannelWebhook": "uasd/uasfj/asdUsda123"
	}
	```

	After you've saved the webhook data successfully, you may send a Slack notification to that user like this:

	```go
	n := &notification.Notification{
		ID: uuid.NewRandom().String()
		Recipients: []string{"2089ad58-857e-4b31-a647-b78432c129d2"},
		Provider: notification.Slack,
		Data: data,
		Template: &notification.TemplateRef{
			ID:         "quickstart.txt"
		},
	}

	// Send your notification with Publish
	if err := notifier.Publish(n); err != nil {
		log.Println("Notifer got issues sending message", err)
	} else {
		log.Println("Great success!")
	}
	
	```

=== "Email"

	To deliver notifications through email, it's necessary to add a valid address in the Keycloak email attribute for the user to be notified. Optionally, it's possible to attach base64-encoded small image (png/jpeg/gif) files that are not bigger than `765Kb`.  
	
	An example of an email notification payload is as follows:

	```go
	n := &notification.Notification{
		ID: uuid.NewRandom().String()
		Recipients: []string{"2089ad58-857e-4b31-a647-b78432c129d2"},
		Provider: notification.Email,
		Data: data,
		Template: &notification.TemplateRef{
			ID:         "bill.txt"
		},
		Files: []Attachment{
			{
				Filename: "some-picture",
				MimeType: "image/jpeg", // only image/jpeg, image/png, image/gif are supported
				Data: "<base64_encoded_file_content>", // max file size before encoding is 765kb
			},
		},
	}

	// Send your notification with Publish
	if err := notifier.Publish(n); err != nil {
		log.Println("Notifer got issues sending message", err)
	} else {
		log.Println("Great success!")
	}
	
	```
	
	It's also necessary to use a template with a proper email setup (ie. quickstart.txt won't work with email dispatching because there's no metadata the SMTP server is able to understand, check [bill.txt](https://github.tri-ad.tech/cityos-platform/notification-template-registry/blob/main/templates/bill.txt) for a working email template example)

=== "Woven App"
	
	In order to deliver a notification via Woven App, the user needs to have their firebase token saved in the Notifier.
	The token is automatically saved upon Woven resident's login to the Woven App, so no action should be required from the service calling the notifier.

	If, for some reason, you'd like to set the firebase token explicitly, it can be done by calling the Notifier API's [PATCH /{user_id}/settings/wovenAppToken](https://developer.woven-city.toyota/catalog/default/api/notification-api/definition#/Notification/createOrPatchUserSettings) endpoint with the following payload:

	```json
	{
		"wovenAppToken": "someToken123"
	}
	```

	Finally, you may send a WovenApp notification to a desired user like this:

	```go

	data := make(map[string]interface{})
	data["title"] = "Woven App notification"
	data["imageUrl"] = ""
	data["body"] = "The notification body to render"

	n := &notification.Notification{
		ID: uuid.NewRandom().String()
		Recipients: []string{"2089ad58-857e-4b31-a647-b78432c129d2"}, // replace with the actual woven-id
		Provider: notification.App,
		Data: data,
		Template: &notification.TemplateRef{
			ID:         "example.fcm.txt"
		},
	}

	// Send your notification with Publish
	if err := notifier.Publish(n); err != nil {
		log.Println("Notifer got issues sending message", err)
	} else {
		log.Println("Great success!")
	}
	
	```

	!!! Note
		This example uses an [example template](https://github.tri-ad.tech/cityos-platform/notification-template-registry/blob/main/templates/example.fcm.txt) and renders in the body of the notification the value of `Data.body` after processing its value, but we can use a different template with text such as `Anything you like {{.variable1}}` and add `variable1` as key in the Data object. The only 2 keys that have a static rendering are `title` and `imageUrl` because they correspond to the [notification fields](https://pkg.go.dev/firebase.google.com/go/messaging#Notification) as defined in FCM, therefore the `data` map could look like:
		``` go
		data["title"] = "Woven App notification"
		data["imageUrl"] = ""
		data["variable1"] = "potato!"
		```
	
	To test with firebase there's a ready-to-use [JS quickstart](https://github.com/firebase/quickstart-js/blob/master/messaging/README.md), clone the project and follow the steps to set an app up and retrieve your device id.
	After this step, run [the demo client](https://github.com/wp-wcm/city/blob/main/ns/notification/examples/mobile/main.go) with the correct environment variable values or copy the launcher below in your `.vscode/launch.json` file.

	```JSON
	{
    	"name": "Launch Firebase Test Client",
        "type": "go",
        "request": "launch",
        "mode": "auto",
        "program": "notification/examples/mobile/main.go",
        "env": {
            "FCM_PROJECT_ID":"<firebase-project-id>",
            "FCM_CREDENTIALS_FILE":"<credentials.json path>",
            "DEVICE_TOKEN":"<token retrieved running the quickstart>"
        }
    }
	```
