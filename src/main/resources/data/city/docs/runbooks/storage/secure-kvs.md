# Secure KVS

| Last Update | 2024-07-29             |
|-------------|------------------------|
| Tags        | Data, Storage, CouchDB | 

## Configuring Indexes

!!! Note
    This is an Agora Administrative Action. If you're not an admin, this is not
    for you

Use the example snippet to set up a index. Before running ensure:

* The appropriate context is being used
* The name of the Database is correct
* The JSON for your index is suited to your specific database and relative tables

```shell
k --as=agora-secure-kvs-<ENVIRONMENT>-admin exec agora-kvs-couchdb-0 \ 
-- bash -c 'curl -v -X POST \
'http://'$COUCHDB_USER':'$COUCHDB_PASSWORD'@localhost:5984/<COUCHDB_NAME>/_index' -d "<JSON_BODY>" -H "Content-Type: application/json"'
```

