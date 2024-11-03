# Consent Integration

[consent-config-definition]: https://github.com/wp-wcm/city/blob/dc5e03c78b622bafe434b28197057b3fe260577f/ns/privacy/consent/middleware/go/http/middleware.go#L31

Let's continue on our journey to support consent for our toy food delivery and
address services. In the previous part of the code lab, we made sure our
"environment" was set up correctly, so now we can go ahead and actually
implement the consent integration!

!!! note

    The setup we made in the previous part is not persistent. If you restarted
    the Consent service containers since then, you should make sure to quickly 
    run through the configuration part again.

## Wiring up the consent middleware

We can use the Consent middleware as a shared library and import it in our project,
so let's start modifying the address service code from previous parts to integrate
the middleware with its code.

The first thing we need is to import it:

```go
import (
    ...
    consent "github.com/wp-wcm/city/ns/privacy/consent/middleware/go/http"
)
```

Next, to prepare to plug in a middleware, let's create an explicit server MUX
and set the handlers on that, instead of relying on the default one:

```go
mux := http.NewServeMux()
mux.HandleFunc("/address", address)
mux.HandleFunc("/address/", address)
fmt.Printf("address_service listening on port :8081\n")
_ = http.ListenAndServe(":8081", mux)
```

Now comes the interesting part, we need to create an instance of the consent
middleware. To do that, we use the `consent.CheckRequestHandler` function - it
takes a `ConsentConfig` as a parameter, and returns the middleware that will
wrap our MUX.

We will look at how to configure the consent middleware in a minute, but first
let's create it with "dummy" config values and hook it up:

```go
// We add this at the top of `main()`
config := consent.ConsentConfig{}
consentMiddleware, err := consent.CheckRequestHandler(config)
if err != nil {
    panic(err)
}
...
// And then update the `ListenAndServe` line
_ = http.ListenAndServe(":8081", consentMiddleware(mux))
```

Good! Shall we try to run the code and see what happens? Spoiler: Nothing good.

```sh
# in ns/privacy/consent_101/03_consent_integration (or your copy):
❯ bazel run address_service
```

You'll get some output like this:

```
INFO: Analyzed target //ns/privacy/consent_101/03_consent_integration/address_service:address_service (0 packages loaded, 0 targets configured).
INFO: Found 1 target...
Target //ns/privacy/consent_101/03_consent_integration/address_service:address_service up-to-date:
  bazel-bin/ns/privacy/consent_101/03_consent_integration/address_service/address_service_/address_service
INFO: Elapsed time: 1.273s, Critical Path: 1.08s
INFO: 3 processes: 1 internal, 2 darwin-sandbox.
INFO: Build completed successfully, 3 total actions
INFO: Running command line: bazel-bin/ns/privacy/consent_101/03_consent_integration/address_service/address_service_/address_service
panic: missing ParseSubjectsFunc in the provided ConsentConfig

goroutine 1 [running]:
main.main()
        ns/privacy/consent_101/03_consent_integration/address_service/main.go:26 +0x170
```

The actual error can be a bit difficult to spot, but if you look closely you'll
find: `panic: missing ParseSubjectsFunc in the provided ConsentConfig`

Ah, that's to be expected - we didn't actually configure our consent middleware
properly, so `CheckRequestHandler` is returning an error. Let's fix that!

## Consent middleware configuration

Our code currently passes an empty `ConsentConfig` to `CheckRequestHandler`, and
we want to fix that, but before we jump in and start populating the config's
fields, let's take a look at its [definition][consent-config-definition]:

```go
type ConsentConfig struct {
    ConsentServiceGRPCURL string                    // gRPC URL of consent service (<host>:<port>)
    SkipConsentCheckFunc  SkipConsentCheck          // function returning decision if a consent check should be skipped for provided request
    ParseSubjectsFunc     ParseConsentCheckSubjects // function returning required for consent check data based on a provided request
    OmitResponseFunc      OmitResponse              // function returning decision if consent check result should be disregarded (forcing service handler to be called)
    LogErrorFunc          func(err error)           // function used for logging any failure during the consent check
}
```

The first field, `ConsentServiceGRPCURL`, is optional. If left empty, the middleware
will automatically connect to the Consent service endpoint in the cluster the service is
running in. However, we aren't actually running in a cluster, so we should
probably set this value ourselves to point to our running Docker container.

Next up is `SkipConsentCheckFunc`. This allows the service to specify that certain
clients should be ignored, and consent shouldn't be checked for their requests.
You may be thinking this sounds dangerous and, indeed, it is. This setting is
only here for debugging purposes, or to allow services to enable consent for
their clients gradually, but be aware that this should *NEVER* be used in
production (and, once we actually have a production environment, will also be
enforced).

Going slightly out of order, let's talk about `OmitResponseFunc` next.
Like `SkipConsentCheckFunc`, this provides the service with a
fine-grained way to ignore consent decisions on message-by-message basis. As with
`SkipConsentCheckFunc`, this is only intended for development and gradual rollout
purposes, but should NOT be used in a production environment.

Finally, we have the most important part of the configuration,
`ParseSubjectsFunc`. As the comment suggests, this is a function that, given a
request, should return the affected user IDs and the types of data (i.e. data
attributes) that are being accessed by it. Now we can understand why our service
panicked when we tried to run it - the consent middleware doesn't know which
user the request is for or which information, so the address service has to
provide that information.

But what format does the function return the information in? If we follow the
definitions, we end up at:

```go
type ConsentCheckSubjects struct {
	UserID    string   // User's Woven ID who is a subject of consent check in an undergoing request
	DataAttrs []string // Data attributes annotated with an infotype which is involved in an undergoing request
}
```

We see exactly the two things we expected the function to report back - the
user ID and a slice containing the data attributes.

Now that we know what the different fields of `ConsentConfig` mean, it should be
fairly easy. Let's start with the `ParseSubjectsFunc`:

```go
func consentParseSubjectFunc(req *http.Request) (consent.ConsentCheckSubjects, error) {
	var dataAttrs []string

	if req.Method == http.MethodGet {
		dataAttrs = []string{"CITY_ADDRESS_ID"}
	}

	return consent.ConsentCheckSubjects{
		UserID:    strings.TrimPrefix(req.URL.Path, "/address/"),
		DataAttrs: dataAttrs,
	}, nil
}
```

So what are we doing here? We are returning an object where `UserID` is set to
the path segment following `/address/` in the URL and, for GET requests, we set
the `DataAttrs` field to "CITY_ADDRESS_ID", the type of information our endpoint
is serving.

Now, to wire it all up together, replace the line creating the empty config
with:

```go
config := consent.ConsentConfig{
    ParseSubjectsFunc: consentParseSubjectFunc,
    ConsentServiceGRPCURL: "localhost:3001",
}
```

And now if we try to run our service again:

```sh
# in ns/privacy/consent_101/03_consent_integration (or your copy):
❯ bazel run address_service
```

Voila, everything works!

```
INFO: Analyzed target //ns/privacy/consent_101/03_consent_integration/address_service:address_service (0 packages loaded, 0 targets configured).
INFO: Found 1 target...
Target //ns/privacy/consent_101/03_consent_integration/address_service:address_service up-to-date:
  bazel-bin/ns/privacy/consent_101/03_consent_integration/address_service/address_service_/address_service
INFO: Elapsed time: 0.933s, Critical Path: 0.74s
INFO: 3 processes: 1 internal, 2 darwin-sandbox.
INFO: Build completed successfully, 3 total actions
INFO: Running command line: bazel-bin/ns/privacy/consent_101/03_consent_integration/address_service/address_service_/address_service
address_service listening on port :8081
```

Or does it? Let's try setting the user's address, like we did all the way back
in the first part of the code lab...

```
❯ curl -X POST http://localhost:8081/address \
    -H 'Content-Type:application/json'       \
    -d '{"user_id":"e39eb5fe-bd8f-11ed-afa1-0242ac120002","address":"3-chōme-2-1 Nihonbashimuromachi, Chuo City, Tokyo 103-0022"}'
Internal Server Error
```

:(

What happened? Give me some time to debug it, and we'll figure it out in the
[next part of the codelab](04_xfcc_whats_that.md). :)
