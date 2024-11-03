# Setting the scene

[address_service_code]: https://github.com/wp-wcm/city/blob/main/ns/privacy/consent_101/01_no_consent/address_service/main.go
[consent_101_code]: https://github.com/wp-wcm/city/blob/main/ns/privacy/consent_101
[food_service_code]: https://github.com/wp-wcm/city/blob/main/ns/privacy/consent_101/01_no_consent/food_service/main.go

As a first step, we will start by setting up two services that communicate with
each other and use PII, but without any concern for consent. Then we will see
how to configure consent and add support for consent checks to our code in the
next parts of the code lab.

For our example, we will simulate a food delivery service (`food_service`), that
reads the user's home address (PII) from a separate service that manages it
(`address_service`).

The example code can be found [here][consent_101_code], in folders named after
the corresponding code lab part. Let's look at the important files under the
`01_no_consent` folder there:

```
.
├── address_service
│   ├── BUILD           - Bazel build file
│   └── main.go         - Code for the address service
├── food_service
│   ├── BUILD           - Bazel build file
│   └── main.go         - Code for the food service
└── requests.http       - HTTP request examples (if you're using IntelliJ, you
                          can run these directly from inside the IDE!)      
```

## Building and running the code

This codelab can currently only be built and run as part of the WCM `city`
monorepo, using the monorepo's build tool, Bazel.

To follow along and run the examples, you must have a local clone of the
monorepo, and you must have set up Bazel so you can build (parts of) the
monorepo.

If you want to follow along incrementally by modifying files yourself, you
can use the files in the `01_no_consent` folder as a starting point. You can
either edit them in place, or make a copy of that whole folder to work in
(the copy needs to be inside your clone of the monorepo to work!).

In the instructions for running Bazel commands in this codelab, we will use
"local" target specifiers, so you have to run them in the folder of the
corresponding codelab part, or in the folder where you modify the code.

## Address service

The address service code is [here][address_service_code]. Let's go through it
and see what it does:

`UserAddress` is a simple struct we will use to deserialize requests, containing
the user's ID and their address. Of course, in a "real" situation, this would
have been structured differently, but for our example this is enough.

```go
type UserAddress struct {
	UserID string `json:"user_id"`
	Address string `json:"address"`
}
```

The service's entry point doesn't do much - it sets up handlers for paths
starting with `/address`, and serves them on port 8081. Since this is an
example, there is no error handling (or logging) here.

```go
func main() {
	// Golang's default mux implementation differentiates between paths containing trailing slashes
	// and ones that don't, so we simply register both ¯\_(ツ)_/¯
	http.HandleFunc("/address", address)
	http.HandleFunc("/address/", address)
	fmt.Printf("address_service listening on port :8081")
	_ = http.ListenAndServe(":8081", nil)
}
```

In a "real" service, we would use a database to store the address information.
For our needs, though, let's just use an in-memory map. :)

```go
var addressMap sync.Map
```

This is our endpoint handler, registered earlier in `main`. It doesn't do a
whole lot - it checks whether the request was a GET or a POST, and then either
looks up or stores the user's address in `addressMap`. As with `main`, we don't
really do much error checking here to keep the code simple.

```go
func address(w http.ResponseWriter, req *http.Request) {
    case http.MethodGet: // GET /address/:id
        wovenID := strings.TrimPrefix(req.URL.Path, "/address/")
        address, _ := addressMap.Load(wovenID)
        userAddress := UserAddress{
            UserID: wovenID,
            Address: fmt.Sprintf("%v", address),
        }
        encoder := json.NewEncoder(w)
        _ = encoder.Encode(userAddress)
    case http.MethodPost: // POST /address
        decoder := json.NewDecoder(req.Body)
        var address UserAddress
        err := decoder.Decode(&address)
        if err != nil {
            w.WriteHeader(http.StatusBadRequest)
            _, _ = fmt.Fprintf(w, "decoding failed: %s", err.Error())
            return
        }
        addressMap.Store(address.UserID, address.Address)
    }
}
```

Let's test it out. In your clone of the monorepo, run the following command in
a shell (or use your favorite IDE with Bazel integration to run this target):

```sh
# in ns/privacy/consent_101/01_no_consent (or your copy):
❯ bazel run address_service
```

Now let's make some requests from another terminal (or, if you're using
IntelliJ, you can use the requests in `requests.http`):

```
❯ curl -i -X POST http://localhost:8081/address \
    -H 'Content-Type:application/json'       \
    -d '{"user_id":"e39eb5fe-bd8f-11ed-afa1-0242ac120002","address":"3-chōme-2-1 Nihonbashimuromachi, Chuo City, Tokyo 103-0022"}'
HTTP/1.1 200 OK
Date: Tue, 22 Aug 2023 08:02:30 GMT
Content-Length: 0

❯ curl -X GET http://localhost:8081/address/e39eb5fe-bd8f-11ed-afa1-0242ac120002 \
    -H 'Accept:application/json'
{"user_id":"e39eb5fe-bd8f-11ed-afa1-0242ac120002","address":"3-chōme-2-1 Nihonbashimuromachi, Chuo City, Tokyo 103-0022"}
```

Hurray, it works! Next up, the food delivery service!

## Food delivery service

The food delivery service code is [here][food_service_code]. It's does a tiny
bit more than the address service, but I'm sure we can figure it out! ^_^

Similar to the address service, we start with a struct to model our food order
requests:

```go
type FoodOrder struct {
	UserID string `json:"user_id"`
	Food string `json:"food"`
}
```

Next, we have the same `UserAddress` struct we saw in the address service. In
a "real" service, we would probably generate both of those definitions from the
address service's OpenAPI spec, but for this code lab we are just going to copy
and paste the struct.

```go
type UserAddress struct {
	UserID string `json:"user_id"`
	Address string `json:"address"`
}
```

The `main` function looks very similar, almost like someone just copied it from
address service and changed the paths and the name of the handler:

```go
func main() {
	// Golang's default mux implementation differentiates between paths containing trailing slashes
	// and ones that don't, so we simply register both ¯\_(ツ)_/¯
	http.HandleFunc("/feedme", feedme)
	http.HandleFunc("/feedme/", feedme)
	fmt.Printf("food_service listening on port :8082")
	_ = http.ListenAndServe(":8082", nil)
}
```

And once again, the majority of the code is in the handler. In this case, we
only want to handle POST requests, but the logic for handling them is a bit more
complicated than in the address service.  
The handler performs 3 steps: First, it deserializes the request. It then
extracts the user ID, and uses it to GET the user's address from the address
service. Finally, it deserializes the response from the address service, and is
ready to place the food order with the restaurant. Uhmmm... We'll leave that
part as an exercise for the reader. ;-)

```go
func feedme(w http.ResponseWriter, req *http.Request) {
    switch req.Method {
    case http.MethodPost:
        decoder := json.NewDecoder(req.Body)
        var order FoodOrder
        err := decoder.Decode(&order)
        if err != nil {
            w.WriteHeader(http.StatusBadRequest)
            _, _ = fmt.Fprintf(w, "Food order failed: Failed to decode request. error: %s", err)
            return
        }

        resp, err := http.Get(fmt.Sprintf("http://localhost:8081/address/%s", order.UserID))
        if err != nil {
            w.WriteHeader(http.StatusInternalServerError)
            _, _ = fmt.Fprintf(w, "Food order failed: Could not get address. error: %s", err)
            return
        }

        decoder = json.NewDecoder(resp.Body)
        var address UserAddress
        err = decoder.Decode(&address)
        if err != nil {
            w.WriteHeader(http.StatusInternalServerError)
            _, _ = fmt.Fprintf(w, "Food order failed: Malformed response from address_service. error: %s", err)
            return
        }

        // This is where a "real" service would actually order the food

        _, _ = fmt.Fprintf(w, "Food ordered! It will be delivered to \"%s\"", address.Address)
    }
}
```

And that's it. But does it work? First, make sure the address service is still
running, and then run:

```sh
# in ns/privacy/consent_101/01_no_consent (or your copy):
❯ bazel run food_service
```

Assuming you still have the address entered earlier (you may have to POST it
again if you restarted the service!), you can now try to order some sushi:

```
❯ curl -X POST http://localhost:8082/feedme \
    -H 'Content-Type:application/json'      \
    -d '{"user_id":"e39eb5fe-bd8f-11ed-afa1-0242ac120002","food":"Sushi"}'
Food ordered! It will be delivered to "3-chōme-2-1 Nihonbashimuromachi, Chuo City, Tokyo 103-0022"
```

Hurray, food is on its way! Everything is right with the world again!

Except that we didn't check whether the user has consented to sharing their
home address with the food delivery service. That's not good - in Woven City we
want to give users and residents a choice in how their personal data is used,
so we need to do better. How? Let's continue to the [next section](02_consent_setup.md)
and find out!
