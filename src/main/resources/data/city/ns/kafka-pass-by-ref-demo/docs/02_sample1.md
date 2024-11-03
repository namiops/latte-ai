## Sample 1: Use an existing service as the data source

One way of implementing this pattern is to leverage an existing service that owns the type of personal data Producer wants to convey to Consumer.

Let’s set a concrete scenario to demonstrate this.
In this scenario, Clinic service that owns the user’s health data wants to asynchronously request Medical Advisory service that gives the user some medical advice by sharing its data. Medical Advisory service consumes the request at its own pace.

Here’s the overview of this sample program.

![use-existing-service](./assets/use-existing-service.png)

(Source: https://drive.google.com/file/d/1L1kn3V2kWolLD20SAozfuhzbR2XkXl48/view)

It uses [Personal Data Store (a.k.a PDS)](https://developer.woven-city.toyota/docs/default/Component/pds-service) as the data source of the personal data it handles (i.e. _health_).

Please take a look at [this minimal Golang implementation](https://github.com/wp-wcm/city/tree/main/ns/kafka-pass-by-ref-demo/sample1-use-existing-service).

!!! tip

    **Everything important is kept in main.go** of each service. Anything in misc.go is technical details that you can dig through only if you’re so interested.

### Clinic service

Clinic service is the producer service that conforms to MedicalAdvisory service’s API (that uses Kafka as the async request channel), following the message schema that MedicalAdvisory service defines.

Let’s take a look at [main.go](https://github.com/wp-wcm/city/blob/main/ns/kafka-pass-by-ref-demo/sample1-use-existing-service/clinic-svc/main.go).

You see the numbered comments like `1. Save Health`. These correspond to the numbered arrows in the above diagram.

Let’s skip some client setup boilerplate and jump to the important part.

#### 1. Save Health

Here, you would just write `health` data to Kafka if `health` were NOT personal data. But it is personal data, so let’s apply pass-by-reference pattern to respect user privacy.

We save the data in PDS and get the reference.

```go
path, err := pdsClient.saveHealth(wovenId, &health)
```

#### 2. Write PDS URL path for Health

And write the reference to Kafka so that the consumer can retrieve the actual data only if it has the user’s consent.

```go
if err := kafkaClient.writeMessage(
	wovenId,
	&Message{
		HealthPath: path,
	},
)
```

So that’s it for the producer-side.

### Medical Advisory service

Let’s now take a look at the consumer-side.
MedicalAdvisory service takes a reference to `health` data through Kafka as the async request channel, queries it, and gives the end user some advice based on it.

In the async request-response context, the consumer is responsible for defining the message (=request) schema, and it SHOULD NOT define an async message schema that includes personal data directly in it.

!!! question "Who should define the message schema?"

    If you think about SYNChronous request-response, such as a REST API server, it’s always the responder (=server) side who defines the structure of the request body, and the requestor (=client) conforms to it.

    In the same manner in ASYNChronous request-response, it’s the responder (=consumer) side who should define the message schema it expects.

    Note, this is only about async request-response. In other forms of async communication (e.g. pub/sub), this rule doesn’t apply.

Let’s take a look at [main.go](https://github.com/wp-wcm/city/blob/main/ns/kafka-pass-by-ref-demo/sample1-use-existing-service/medical-advisory-svc/main.go).

Again the numbered comments correspond to the numbered arrows in the above diagram.
Let’s skip some client setup boilerplate and jump to the important part.

#### 3. Read PDS URL path for Health

Reading the message from Kafka is the first thing it does. The message, as the consumer defines its schema, contains the reference for `health` data.

```go
message, err := kafkaClient.readMessage()
```

#### 4. Query for Health

Let’s fetch the actual `health` data for the given reference.

```go
health, isConsentMissing, err := pdsClient.fetchHealth(message.HealthPath)

/* Omitted */

if isConsentMissing {
	fmt.Printf("[Info] Missing user-consent for this service to access %s\n", message.HealthPath)
	// Implement better missing-user-consent handling here
	break
}
```

Notice that MedicalAdvisory service only succeeds in fetching `health` if the user has consented to it, that’s why explicitly handling missing-consent cases is highly RECOMMENDED.

#### 5. Check if MedicalAdvisorySvc can read Health, 6. Return Health

These are done by PDS.

#### 7. Process Health

Now that it gets the desired data, after steps that guarantee the user consent, let it do its job!

```go
advice := computeAdvice(health)
offerAdvice(advice)
```

### About Data Source

This sample demonstrates how to respect user privacy by applying pass-by-reference pattern using an existing service for the data source, PDS in this case.

It is generally recommended to use the _canonical_ service that controls the type of data of your interest. For example, if you are handling calendar events, _calendar service_ should be the first option you should consider.

[PDS](https://developer.woven-city.toyota/docs/default/Component/pds-service) is a flexible data store suitable for storing various kinds of personal data, so it might be a good choice for many use cases.

[Basic Users and Residents Register (a.k.a BURR)](https://developer.woven-city.toyota/docs/default/Component/brr-service), with a lot more strict data model than PDS addressing people’s basic profiles, might be another good data source if the data of your interest is in the scope of BURR.
