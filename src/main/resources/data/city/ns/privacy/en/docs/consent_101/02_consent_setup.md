# Configuring the "platform" to support consent

[data-mapping-sheet]: https://docs.google.com/spreadsheets/d/1UtdYCk7UoJ3-DdGQlytkcmpPPdYAv1jmQY1DYLIdIuE/edit

When we left off at the end of the previous part we had a fully functioning food
delivery service, so our users don't have to feel hungry anymore. But how else
did it make them feel? We never asked them if they agreed to share their address
with the food delivery service, and now they may feel surprised, or even lose
trust in the city as a whole. So let's fix that!

## Understanding the problem

The first step in solving a problem, is understanding it. In this case, we know
that the food delivery service's access to the user's information should have
been controlled and checked. But whose responsibility is this?

Should the food delivery service have checked before trying to access the
information? Should the address service enforce this when serving requests? Is
it Agora's responsibility to prevent this from happening? Or maybe it's the
user's "fault" that they didn't realize the food delivery service would need to
know their address?

Let's start with the easiest one to answer:

> maybe it's the user's "fault" [...]

It is _never_ the user's fault - even when the user is doing something wrong,
they are doing so because *we* designed a product/service that they
misunderstood.

> Should the food delivery service have checked [...]?

Although at first thought it may seem reasonable to place this responsibility on
the food delivery service, this becomes less realistic once you consider "bad
actors" and compromised services that will simply skip this step.

> Should the address service enforce this when serving requests?

This is the correct answer. As the actual service that stores the PII and shares
it with other services, it is the address service's responsibility to make sure
the users' preferences are respected. 

> Is it Agora's responsibility [...]?  

Or, in other words, if it's the address service's responsibility, what is the
platform's role here?

That's a great question, I'm glad you asked! Although in the end it is the
(address) service's responsibility to handle PII correctly and check consent
before sharing it with other services, Agora's consent tooling is intended to
make the process of managing, configuring and checking consent as easy as
possible. Let's see how that's done in practice!

## Registering with Consent Service

Following the main [consent documentation](../consent/README.md#data-attributes),
in order to manage consent for the user's home address, we need to use the
right "data attributes", whose use is defined in [TN-0055](http://go/tn-55)).
Following the link from that TN to the
[Consent Categorization sheet][data-mapping-sheet], it appears that there is already an
appropriate value for tagging residential addresses - `CITY_ADDRESS_ID`.

The next step is to tell the Consent service about our own service.
For that, let's run the Consent service locally so we can play around with it.

### Running the Consent Service locally

To run the consent service locally (inside a container), use the following command:

```shell
# run this in ns/privacy/consent_101, where the "docker-compose.yaml" file is:
docker compose up -d
```

This sets up the required database container, and starts the Consent service with
the right configuration to connect to it. We can now verify that the service is up and
running by accessing its liveness endpoint:

```
❯ curl -I localhost:3000/readyz

HTTP/1.1 200 OK
content-length: 0
date: Mon, 10 Jul 2023 07:57:43 GMT
```

(As with the previous part, if you're using IntelliJ or a similar IDE, you can
find these requests in the `requests.http` file and run them from the IDE.)

It returns 200, so we're good to go! Next, let's look at the service mapping.

## Service mapping

Now we know the the right data attribute for the user's street address,
so this is what we would have to ask the user access to. But which service
should we request access for? Referring back to the main
[consent documentation](../consent/README.md#service-mapping), we see that we
would need an entry in the service mapping, to represent the food delivery
service. So let's create one (for the creatively named FooDelivery service):

```
❯ curl -i -X POST "http://localhost:3000/v2alpha/admin/service_mapping" \
    -H "Content-Type: application/json" \
    -d '{ "service_name": "FooDelivery", "clients": [ {"client_id": "food-delivery-service"} ] }'

HTTP/1.1 204 No Content
Date: Mon, 27 Mar 2023 08:54:14 GMT
```

Looks like it's OK, but it can never hurt to check:

```
❯ curl http://localhost:3000/v2alpha/admin/service_mapping

{"services":[{"service_name":"FooDelivery","clients":[{"client_id":"food-delivery-service"}]}]}
```

And indeed, we see the entry for `food-delivery-service` in there. Success!

Now that we have things set up, let's move on to the [next section](03_consent_integration.md)
and integrate our services with the Consent service.
