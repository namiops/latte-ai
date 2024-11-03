# Kafka 

In order to send a notification to Notifier via Kafka, your application needs to publish to the `notification.notifications` topic.

!!! Note
	In Speedway-prod environment, the topic name will be `agora-notification-prod.notifications`. 
	And in Speedway-dev it will be `agora-notification-dev.notifications` accordingly.

Prior to that, Notifier will need to allow messages coming from your app's principal, e.g. `User:CN=kafka-quickstart.${cluster_domain},OU=CityOS`, so please let **@agora-services** know beforehand.

Please refer to [Kafka Quickstart](https://developer.woven-city.toyota/docs/default/Component/kafka-service/01_quickstart/) docs on how to set up Kafka for your application. 

## Example notification

=== "Go"
	```go
	
	package main

	import (
		"log"

		"github.com/alexflint/go-arg"
		"github.com/wp-wcm/city/ns/kafka"
		notification "github.com/wp-wcm/city/ns/notification/pkg"
		"github.com/google/uuid"
	)

	func main() {

		// parsing the command line flags or environment variables
		config := &KafkaWriterFlags{}
		arg.MustParse(config)

		kwConfig := kafka.Config(*config)
		kWriter := kafka.NewWriter(&kwConfig)

		// Injecting the kafka writer into the notifier
		notifier := notification.NewProducer(kWriter)

		data := make(map[string]interface{})
		data["coins"] = "42"

		// Prepare the Notification payload. This will be serialized by the notifier logic.
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
			}, // optional if not set up yet
		}

		// Send your notification with Publish
		if err := notifier.Publish(n); err != nil {
			log.Println("Notifer got issues sending message", err)
		} else {
			log.Println("Great success!")
		}

	}

	type KafkaWriterFlags struct {
		User            string   `arg:"--kafka-user,env:KAFKA_USER,required"`
		Password        string   `arg:"--kafka-password,env:KAFKA_PASSWORD,required"`
		ServerAddresses []string `arg:"--kafka-urls,env:KAFKA_SERVER,required"`
		Topic           string   `arg:"--kafka-topic,env:KAFKA_TOPIC,required"`
	}
	```

=== "Javascript"

	This example uses [KafkaJS](https://kafka.js.org/docs/getting-started) but any other library will work. 

	```shell
	npm install kafkajs
	```
	Copy the code below in `main.js`

	```js
	const { Kafka } = require('kafkajs')

	const kafka = new Kafka({
  		clientId: 'my-app', // set your client-id
  		brokers: ['kafka:9092'], // set the correct kafka broker address
	})

	const providers = ["slack", "email", "app"]
	const callbacks = ["webhook", "kafka"]
	const producer = kafka.producer()

	const start = async function() {

		await producer.connect()

		notification = {
				id: "mobile-notification-test1",
				recipients: ["2089ad58-857e-4b31-a647-b78432c129d2"],
				provider: providers.indexOf("app"),
				data: {
					"title":    "A sample app notfication",
					"body":     "This is the body of the notification",
					"imageUrl": "",
				},
				template: {
					id: "example.fcm.txt",
				},
				callback: {
					type: callbacks.indexOf("webhook"),
					target: "<callback-url>",
				}, // optional if not set up yet
			}

		await producer.send({
		topic: 'notification.notifications',
		messages: [JSON.stringify(notification)],
		})

		await producer.disconnect()
	}

	start()
	```
	Run the code with

	```shell
	node <your-path>/main.js
	```
	This will produce a message in the notification.notifications topic and see it delivered to the device associated to the `wovenAppToken` for the user set in `Recipient.ID`


## How to run locally

=== "Bazel"

	```shell
	bazel run //ns/demo/notifier -- --kafka-user KAFKA-USER --kafka-password KAFKA-PASSWORD --kafka-urls KAFKA-URLS --kafka-topic KAFKA-TOPIC
	```

	!!! Tip
		Check your `BUILD.bazel` is updated to avoid errors. To fix the file in case something goes wrong run
		```
		bazel run //:gazelle
		```

=== "Go binary"

	```
	go main --kafka-user KAFKA-USER --kafka-password KAFKA-PASSWORD --kafka-urls KAFKA-URLS --kafka-topic KAFKA-TOPIC
	```


Assuming all the Kafka credentials and details are already known it's possible to pass them as arguments in the command line or set the environment variables as shown below.


```shell
go run main.go --help
Usage: main --kafka-user KAFKA-USER --kafka-password KAFKA-PASSWORD --kafka-urls KAFKA-URLS --kafka-topic KAFKA-TOPIC

Options:
  --kafka-user KAFKA-USER [env: KAFKA_USER]
  --kafka-password KAFKA-PASSWORD [env: KAFKA_PASSWORD]
  --kafka-urls KAFKA-URLS [env: KAFKA_SERVER]
  --kafka-topic KAFKA-TOPIC [env: KAFKA_TOPIC]
  --help, -h             display this help and exit
```
