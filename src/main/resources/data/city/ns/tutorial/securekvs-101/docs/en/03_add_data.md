# Adding data using the API
We will add data through the API so that later when we look at the database we can see some data inside.

To do that, we will need to port-forward the API to be able to access it. Let's use the API service to port-forward:

```shell
$ kubectl port-forward service/securekvs-101 -n securekvs-101 8080:8080
Forwarding from 127.0.0.1:8080 -> 8080
Forwarding from [::1]:8080 -> 8080
```

Currently, the API has three endpoints.

* You can add data by doing a `POST` request at `/TodoItem`.

```shell
$ curl http://localhost:8080/TodoItem -X POST -d '{ "text": "this will be encrypted!" }'
```

* You can view all data by doing a `GET` request at `/TodoItem`. 

```shell
$ curl http://localhost:8080/TodoItem -X GET
[
    {
        "_id": "d838d8ea-828d-4327-b99e-3b0339fece24",
        "Item": "this will be encrypted!",
        "CreatedAt": "2022-10-05T02:40:11.140294566Z"
    }
]
```

* You can view a specific item by doing a `GET` request at `/TodoItem/:id`.

```shell
$ curl http://localhost:8080/TodoItem/d838d8ea-828d-4327-b99e-3b0339fece24 -X GET
{
    "_id": "d838d8ea-828d-4327-b99e-3b0339fece24",
    "Item": "this will be encrypted!",
    "CreatedAt": "2022-10-05T02:40:11.140294566Z"
}
```