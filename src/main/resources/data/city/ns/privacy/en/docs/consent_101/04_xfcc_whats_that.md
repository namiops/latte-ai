# XFCC? What's that?!

[ama-channel]: http://go/agora-ama
[httpbin-headers]: https://httpbin.cityos-dev.woven-planet.tech/headers
[x-forwarded-client-cert]: https://www.envoyproxy.io/docs/envoy/latest/configuration/http/http_conn_man/headers#x-forwarded-client-cert

When we ended the last part, we kind of finished it on a low note, with errors
whenever we tried to send requests to the address service, even though we did
everything we were supposed to. So what happened?

If we step back a bit and think about the general concept of consent, the
approval we are looking for is for a *user* granting access to specific type of
*PII* to a *specific service*. But looking at our integration with the consent
middleware, we never actually specified the name of the service trying to access
the information, so it makes sense that things wouldn't work.

So OK, we have part of the answer, but how do we specify this? There was nowhere
in the configuration where we could provide this information to the middleware.
And, even if there were, how would the address service know which client is
making the call to it in the first place?

## Enter XFCC

Surprisingly, the answer is that we didn't actually do anything wrong. The
errors we are seeing stem from the fact that we are running all these services
locally rather than on Agora. You see, when services are deployed in Agora,
communication between them is handled by Istio and Envoy, which set a specific
header on requests called [X-Forwarded-Client-Cert][x-forwarded-client-cert]
(or XFCC, for short) that contains the identity of the service making the
request, and this is what the consent middleware is looking for. However, when
we made our cURL requests (or requests from the food delivery service) locally,
we didn't have Envoy to set the header for us, nor did we set the header
ourselves, so the middleware could not figure out where the request was coming
from and failed. And indeed, if we observe the address service logs we can see
that it says:

```
2023/04/12 13:59:04 failed to find XFCC elements in 'X-Forwarded-Client-Cert' header
2023/04/12 13:59:04 Missing clientID from the X-Forwarded-Client-Cert header
```

So let's fix that! But remember, this is only a "work-around"
for using everything locally, you won't need to do this when deploying your
service to Agora.

So, we know the name of the header we need to set, but what value should we set
for it? There are a number of ways to do this, but for now let's use the
easiest: Copying! Agora has a service deployed in it called httpbin, that can
introspect HTTP requests, responses, and more, so let's use that and see what a
"normal" request has its XFCC set to. Open a new browser tab, and visit
[this URL][httpbin-headers]. You should see something similar to this:

```json
{
  "headers": {
    ...
    "X-Forwarded-Client-Cert": "By=spiffe://cluster.local/ns/httpbin/sa/default;Hash=07c206e25f57acd52fadec09079f0a98d5f4b2266a991b6e5031418417bb1186;Subject=\"\";URI=spiffe://cluster.local/ns/city-ingress/sa/ingressgateway",
    ...
  }
}
```

We won't go into much detail here about what exactly that all means, since we
can copy most of the header as-is, but there is one part that is important to
us: `spiffe://cluster.local/ns/city-ingress/sa/ingressgateway`. This identifies
the service making the request as coming from "inside the cluster"
(`cluster.local`), from the `city-ingress` namespace, using the `ingressgateway`
service account. To adapt this to the food delivery service, recall in the 
second part of the codelab that we set a service mapping entry for it in the 
Consent service.
Let's refresh our memory what the entry was (and verify that it's still there!):

```
❯ curl http://localhost:3000/v2alpha/admin/service_mapping

{"services":[{"service_name":"FooDelivery","clients":[{"client_id":"food-delivery-service"}]}]}
```

Ah, right! We set the user-facing service name to `FooDelivery`, and the
"cluster" client name to `food-delivery-service`. So let's try the request we
made at the end of the last part, but this time set the XFCC header to simulate
a request coming from the food delivery service:

```
❯ curl -X POST http://localhost:8081/address \
    -H 'Content-Type:application/json'       \
    -H 'X-Forwarded-Client-Cert:By=spiffe://cluster.local/ns/httpbin/sa/default;Hash=07c206e25f57acd52fadec09079f0a98d5f4b2266a991b6e5031418417bb1186;Subject="";URI=spiffe://cluster.local/ns/food-delivery-service/sa/default' \
    -d '{"user_id":"e39eb5fe-bd8f-11ed-afa1-0242ac120002","address":"3-chōme-2-1 Nihonbashimuromachi, Chuo City, Tokyo 103-0022"}'
```

And it worked! Hurray! Next, let's make sure we also add the header in the food
delivery service. To do that, we'll first have to instantiate an explicit HTTP
request instead of using `http.Get`, so the code will be a bit longer:

```go
req, err := http.NewRequest(
    http.MethodGet,
    fmt.Sprintf("http://localhost:8081/address/%s", order.UserID),
    nil)
if err != nil {
    _, _ = fmt.Fprintf(w, "Food order failed: Could not create request. error: %v", err)
    w.WriteHeader(http.StatusInternalServerError)
    return
}
// Only set this header to work around the fact we are not running in Agora.
req.Header.Set("X-Forwarded-Client-Cert",
    "By=spiffe://cluster.local/ns/httpbin/sa/default;Hash=07c206e25f57acd52fadec09079f0a98d5f4b2266a991b6e5031418417bb1186;Subject=\"\";URI=spiffe://cluster.local/ns/food-delivery-service/sa/default")

resp, err := http.DefaultClient.Do(req)
if err != nil {
    _, _ = fmt.Fprintf(w, "Food order failed: Could not get address. error: %v", err)
    w.WriteHeader(http.StatusInternalServerError)
    return
}
if resp.StatusCode >= 300 {
    _, _ = fmt.Fprintf(w, "Food order failed: Could not get address. status: %s", resp.Status)
    w.WriteHeader(http.StatusInternalServerError)
    return
}
```

OK, good. Let's test this, and try to order a cheeseburger (the sushi was good,
but we don't want to eat the same thing day after day):

```
❯ curl -X POST http://localhost:8082/feedme \
    -H 'Content-Type:application/json'      \
    -d '{"user_id":"e39eb5fe-bd8f-11ed-afa1-0242ac120002","food":"Cheeseburger"}'
Food order failed: Could not get address. status: 403 Forbidden
```

I think I'm going to cry, why isn't it working?!

...

Well, isn't it, though? Think back to what we are trying to achieve - we want
the address service to check for consent from the user before returning their
address back to the food delivery service. Since we didn't grant consent to the
food delivery service, the address service is (correctly!) responding with an
error!

But we've learned our lesson by now - before we start celebrating, let's first
check that this is, indeed, the problem. So let's grant consent, and then try
to order our cheeseburger again. Recall that the "user-friendly" name of the
food delivery service is `FooDelivery`, and that we need consent for the data
attribute `CITY_ADDRESS_ID`, and now we can:

```
❯ curl -i -X POST "http://localhost:3000/v2alpha/consents" \
    -H "Content-Type: application/json" \
    -d '{ "user_id": "e39eb5fe-bd8f-11ed-afa1-0242ac120002", "service_name": "FooDelivery", "data_attributes": [ "CITY_ADDRESS_ID" ] }'

HTTP/1.1 204 No Content
Date: Wed, 12 Apr 2023 05:56:36 GMT

❯ curl -X POST http://localhost:8082/feedme \
    -H 'Content-Type:application/json'      \
    -d '{"user_id":"e39eb5fe-bd8f-11ed-afa1-0242ac120002","food":"Cheeseburger"}'

Food ordered! It will be delivered to "3-chōme-2-1 Nihonbashimuromachi, Chuo City, Tokyo 103-0022"
```

Hurray! It's done! We can have lunch _and_ respect the user's consent. Everybody
wins!

## What did we learn?

Now that everything works, it's a good time to look back and think about what we
learned. We started out writing two very simple services that together make sure
residents of our city are well-fed. We soon realized, however, that our services
did not respect the user's privacy, and went on to fix that.

We took a small detour to look at how consent is set up in Agora in general, and
registered our service. Then, with all the preparations out of the way, we
proceeded with the main integration task - adding the middleware to the service
serving PII. This involved a little bit of "plumbing", but the important bit was
in implementing the callback deciding who the user is and what the data
attributes are for which to check consent.

Finally, in this part of the code lab, we did some troubleshooting to
understand why our service "broke", and realized that this is actually just a
side-effect caused by us running everything locally, and that our code and
integration were basically already working correctly.

That was a lot to go through, well done! So...

## What's next?

Well, that's an excellent question! Why don't you tell us what else you'd like
to see? Stop by [#wcm-org-agora-ama][ama-channel] and let `@agora-personal-data` know!

Specifically, the team is constantly working on making the integration with
consent simpler, and your input on what would be useful is super valuable in
helping us prioritize our work :)
