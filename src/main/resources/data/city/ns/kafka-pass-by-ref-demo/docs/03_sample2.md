## Sample 2: Self-host Personal Data

In the earlier example, we used an existing service PDS, as the data source, because PDS is just a good fit for the type of data that we were interested in (i.e. _heath_).

But what if there is literally no service suitable for the personal data you want to send to another service? …The last resort is hosting yourself.

While it sounds like a lot to do, it’s not dreadfully complex, so let us walk through the reference implementation.

Let’s set a concrete scenario to demonstrate this.
In this scenario, Check-in service that owns user’s location data wants to asynchronously request Spot Recommendation service that gives the user some recommendations on which spot the user should visit by sharing its data. Spot Recommendation service consumes the request at its own pace.

Here’s the precise overview of this sample program.

![self-host-personal-data](./assets/self-host-personal-data.png)

(Source: https://drive.google.com/file/d/1L1kn3V2kWolLD20SAozfuhzbR2XkXl48/view)

Let’s assume there’s no _canonical_ service for handling _UserLocation_ data. Notice Check-in service, along with writing the URL to Kafka, it hosts _UserLocation_ data as a SYNChronous API. We integrate this synchronous API to Agora’s [consent check mechanism](https://developer.woven-city.toyota/docs/default/Component/consent/consent_101/00_README/) and we care very least about consent-checks (at step5).

Please take a look at [this minimal Golang implementation](https://github.com/wp-wcm/city/tree/main/ns/kafka-pass-by-ref-demo/sample2-self-host-pii).

!!! tip

    **Everything important is kept in main.go** of each service. Anything in misc.go is technical details that you can dig through only if you’re so interested.

### Check-in service

Check-in service is the producer service that conforms to SpotRecommendation service’s API (that uses Kafka as the async request channel), following the message schema that SpotRecommendation service defines.

Let’s take a look at [main.go](https://github.com/wp-wcm/city/blob/main/ns/kafka-pass-by-ref-demo/sample2-self-host-pii/check-in-svc/main.go).

You see the numbered comments like `1. Save UserLocation`. These correspond to the numbered arrows in the above diagram.

Let’s skip some client setup boilerplate and jump to the important part.

#### 1. Save UserLocation

Here, you would just write `userLoc` data to Kafka if `userLoc` is NOT personal data. But the truth is that it is personal data, so let’s apply pass-by-reference pattern to respect user privacy.

We save the data in its own storage.

```go
repo.saveUserLocation(&userLoc)
```

!!! note "Use secure storage"

    In this sample program, if you are curious enough to dig into misc.go you’ll find it uses an in-memory store for the data storage. However, it’s highly RECOMMENDED to use a more reliable and secure solution such as [Secure KVS](https://developer.woven-city.toyota/docs/default/Component/Steelcouch).

#### 2. Write full URL for UserLocation

Write the full URL to the endpoint (that Check-in service itself hosts) to Kafka so that the consumer can retrieve the actual data only if it has the user’s consent.

```go
client.writeMessage(
    userLoc.WovenId,
    &Message{
        UserLocationURL: fmt.Sprintf("%s/user-location/%s/%s", ServerConfig.baseUrl, userLoc.WovenId, userLoc.Id),
    },
)
```

It is further discussed in a later section but please keep in mind that writing the full URL might be something you want to avoid (more precisely, the consumer might want to avoid receiving) if possible. But for now, let’s just go on.

#### 5. Check if SpotRecomSvc can read UserLocation, 6. Return UserLocation

This is about the sync endpoint Check-in service hosts to serve _UserLocation_ data, and this is where the consent check magic happens.

Here’s the full definition of this endpoint.

```go
// 5. Check if SpotRecomSvc can read UserLocation (done in `consentMiddleware`)
mux.HandleFunc("/user-location/", func(w http.ResponseWriter, req *http.Request) {
    wovenId, userLocId := getIdsFromReq(req)

    // 6. Return UserLocation
    userLoc := repo.findUserLocation(wovenId, userLocId)
    if userLoc == nil {
        w.WriteHeader(http.StatusNotFound)
        return
    }
    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(userLoc)
})
```

Interestingly it doesn’t have any explicit logic to check if SpotRecommendation service can access _UserLocatoin_ data for the user.

It does not because the work is done by `consentMiddleware`. How to configure the consent middleware is not the main topic of this document so we won’t look into it, but if you are seriously interested you can take a look at `func configureConsentMiddleware` in [misc.go](https://github.com/wp-wcm/city/blob/main/ns/kafka-pass-by-ref-demo/sample2-self-host-pii/check-in-svc/misc.go), or better yet, try [the consent codelab](https://developer.woven-city.toyota/docs/default/Component/consent)!


The important thing is that there’s minimal code required to be written to integrate a service into Agora’s consent mechanism, and this is the main reason that makes _Self-host Personal Data_ method a viable option.

OK, that’s it for the producer-side. Let’s turn to the consumer-side.


### Spot Recommendation service

Let’s now take a look at the consumer-side.
SpotRecommendation service takes the full URL to `UserLocation` data through Kafka as the async request channel, query it, and give the end user some recommendation on where to visit based on it.

In the async request-response context, the consumer is responsible for defining the message (=request) schema (in the same manner as SYNC request-response), and it SHOULD NOT define an async message schema that includes personal data directly in it.

Let’s take a look at [main.go](https://github.com/wp-wcm/city/blob/main/ns/kafka-pass-by-ref-demo/sample2-self-host-pii/spot-recommendation-svc/main.go).

#### 3. Read full URL for UserLocation

Reading the message from Kafka is the first thing it does. The message, as the consumer defines its schema, contains the full URL for `userLocation` data.


#### 3.5. Verify the received URL

OK, it got the URL, let’s go fetch it! …No, wait.

**It’s important to verify the URL is safe before actually hitting it** (e.g. by validating the hostname, etc) in order to avoid fetching data from potentially malicious endpoints.

!!! danger

    There is an [access control in Kafka](https://developer.woven-city.toyota/docs/default/Component/kafka-service/05_config/#acls), and you can configure the topic such that only trusted principals can write to it. However, you should also take into account cases where those services get compromised, and a bad actor may successfully inject a message with malicious URLs.

!!! success

    **A better approach is to choose NOT to receive any URLs** but some sort of reference (e.g. IDs, paths) like we did in the first example. Then the consumer will automatically hit endpoints of only known (statically configured) hosts/services.

#### 4. Query for UserLocation


Now that it verified the given URL, let’s fetch the actual `userLocation` data.

```go
userLocation, isConsentMissing, err := userLocationClient.fetchUserLocation(message.UserLocationURL)

/* Ommited */

if isConsentMissing {
	fmt.Printf("[Info] Missing user-consent for this service to access %s\n", message.UserLocationURL)
	// Implement better missing-user-consent handling here
	break
}
```

Notice that SpotRecommendation service only succeeds in fetching `userLocation` if the user has consented to it, that’s why explicitly handling missing-consent cases is highly RECOMMENDED.


#### 7. Process UserLocation

Now that it gets the desired data, after steps that guarantee the user consent, let it do its job!

```go
recommendedSpot := smartRecommendationLogic(userLocation)
notifyRecommendation(userLocation.WovenId, recommendedSpot)
```

### Wrap up

So that’s about it for this example.

Re-stating several caveats compared to the first method:

* As you are storing personal data, you should be using secure storage such as [Secure KVS](https://developer.woven-city.toyota/docs/default/Component/Steelcouch).
* There’s minimal code required to be written to integrate the sync endpoint into Agora’s consent mechanism.
* As a consumer, don’t blindly hit the URL given through the async channel to protect yourself. Wherever possible, avoid including URLs as the data source reference in your request message schema.

The second method (self-host personal data) has more things to think about than the first method (use an existing service), it’s generally recommended to adopt the first one. However,
In case there is no service suitable for the personal data you want to send to/receive from another service, self-host personal data is a viable option as we have looked through together.
