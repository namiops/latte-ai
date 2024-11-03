# Secure KVS sample application

Here are the intentions of this sample application.

*   Demonstrate CouchDB features, including its query and view mechanism.
*   (Not yet) have a test on write operation conflict handling.
*   Use it to check the effect of some administration for Secure KVS.

The sample application hosts REST APIs. The API design was inspired by [PDS](https://developer.woven-city.toyota/catalog/default/api/pds-api) and [BRR](https://developer.woven-city.toyota/catalog/default/api/brr-api). It's implemented in Python, and [FastAPI](https://fastapi.tiangolo.com/) is used as a Web application framework.

## How to locally run

TODO: Include Steelcouch in the instruction. Preparing a `docker-compose` file might be good to simplify the steps.

Here are the steps to run the sample application.

1.  Launch CouchDB.
2.  Set up a database and indexes.
3.  Insert the address master data.
4.  Launch the sample application.

### Launch CouchDB

We can launch it using the official Docker image.

```bash
$ docker run -d --rm --name couchdb -e COUCHDB_USER=admin -e COUCHDB_PASSWORD=password -p 5984:5984 couchdb
```

### Set up a database and insert the address master data

Both setup processes are implemented as a Bazel `py_binary` target. We can execute it as below.

```bash
$ bazel run //ns/demo/secure-kvs-sample:setup_database -- --host http://localhost --user admin --password password --database test --address-csv ns/demo/secure-kvs-sample/data/latest_address.csv --address-import-num 5000
```

Here is the manual of the setup program.

    usage: setup_database.py [-h] [--host HOST] [--port PORT] [--user USER] [--password PASSWORD] [--database DATABASE] [--address-csv ADDRESS_CSV] [--address-import-num ADDRESS_IMPORT_NUM]

    Before executing it, set up a CouchDB database for a PDS SKVS sample application. You need to make sure the specified database doesn't exist on the CouchDB instance.

    options:
      -h, --help            show this help message and exit
      --host HOST           The hostname of the CouchDB endpoint.
      --port PORT           The port number of the CouchDB endpoint.
      --user USER           The user name for the CouchDB database.
      --password PASSWORD   The password for the CouchDB database.
      --database DATABASE   The name of the CouchDB database
      --address-csv ADDRESS_CSV
                            You can get the latest data for Japan from http://go/japanese-address.
      --address-import-num ADDRESS_IMPORT_NUM
                            The number of the importing addresses. If the argument isn't specified, all addresses in the address CSV file will be imported.

### Launch the API server

The server is also defined as a `py_binary` target. We can launch it via `bazel run` like this.

```bash
SKVS_ENDPOINT=http://localhost:5984 SKVS_USER=admin SKVS_PASSWORD=password PDS_DATABASE=test bazel run //ns/demo/secure-kvs-sample
```

The configurations related to the CouchDB endpoint are passed through the environment variables.

## Open API document

After launching the server, we can see the Open API document through the following endpoint.

    http://<host>:8000/docs

## Run the client emulator

The sample application includes a client emulator implemented using [Locust](https://docs.locust.io/en/stable/index.html). The emulator is constructed with a user client who keeps registering and updating user information and a service client who keeps getting the registered information.

We can launch it using the `locust` CLI command after [installing Locust via pip](https://docs.locust.io/en/stable/installation.html).

```bash
$ locust -f ns/demo/secure-kvs-sample/src/locustfile.py --host http://localhost:8000
```
