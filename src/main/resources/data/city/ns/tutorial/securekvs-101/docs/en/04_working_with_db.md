# Working with DB
Alright, it's time to see the Secure KVS in action.

## Looking at the data through Steelcouch
We can use the CouchDB built-in dashboard to view the data. If you remember, Steelcouch is just a proxy, so it forwards all the requests it can't handle to CouchDB, and the one it can it does handle. With a combination of both, we can view the decrypted data using the CouchDB dashboard.

First, we need to find the user name and password. We can use the helpful commands from the Helm Chart before:

```shell
$ kubectl get secrets db-agora-kvs-test-secure-kvs --namespace securekvs-101 -o=jsonpath='{.data.adminUsername}' | base64 -d
admin
$ kubectl get secrets db-agora-kvs-test-secure-kvs --namespace securekvs-101 -o=jsonpath='{.data.adminPassword}' | base64 -d
{some randomly generated string}
```

To access it, we need to port-forward the Steelcouch service like so:

```shell
$ kubectl port-forward service/steelcouch-agora-kvs-test-secure-kvs -n securekvs-101 5984:5984
```

Now we can go to the link `localhost:5984/_utils/` and login using the credentials we found before. From here, we can go to the `todo-items` database and look at the entry we made. For example:

```json
{
  "_id": "d838d8ea-828d-4327-b99e-3b0339fece24",
  "Item": "this will be encrypted!",
  "CreatedAt": "2022-10-05T02:40:01.470738318Z",
  "_rev": "1-28ab6c558aae56604ea1808d8ee75a84"
}
```

## Seeing the encrypted data on CouchDB
Following the same principle we learned in the previous step, we can also see the encrypted value. Simply port-forward the CouchDB's endpoint service:

```shell
kubectl port-forward service/db-agora-kvs-test-secure-kvs -n securekvs-101 5984:5984
```

Now, if we go to the `localhost:5984/_utils/` and login, we will see that the calls don't get passed through Steelcouch, so the data is just an encrypted string. Like so:

```json
{
  "_id": "d838d8ea-828d-4327-b99e-3b0339fece24",
  "_rev": "1-3aefb31b23a312d83454152d879d3ac4",
  "jwe": "eyJraWQiOiJublExenlMRW9LN2lmSW53bHNhLXBWMHpoSFJ5Uk9CeFZRbkt5UFhMakN3IiwiZW5jIjoiQTI1NkdDTSIsIl9vcmlnaW4iOiJ0ZXN0IiwiX2RvbWFpbiI6ImVwb2NoLTIwMjIwNDAxIiwiX2hhc2giOiJTSEEyXzUxMiIsIl9kaWdlc3QiOiJKOGRHY0syM1VIWDYwRmpWenE5N0lNVG5lR3lEdXVpakwySnZsNEt2Tk1talBDQkc3MkQ5S25oNDAzamluLXlGR0FhNzJhWjRlUE9wOGMya2d3ZGpfUSIsImFsZyI6ImRpciJ9..wLyiYIu1IyGD3g3C.g_VXiJQ5I-rQxdnXTGIkBSKYP4vQlXgz77zw31WPMmk35-qcHSzTN8WuCUMKHV2CUl9ghrdYIslnEd8NJJUsG9XgGY9P7Vlc7MKNzuNFNQ.uGbNwqF9oRX2Ai2UtEqCww"
}
```

With this, hopefully, you have enough info to use Secure KVS for your needs.