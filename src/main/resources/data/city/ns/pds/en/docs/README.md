# Personal data storage

## Run PDS in your local machine.

1. If you use VScode for debugging, the following config might helpful

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "Launch",
            "type": "go",
            "request": "launch",
            "mode": "auto",
            "program": "${workspaceFolder}/main.go",
            "envFile": "${workspaceFolder}/.env",
            "args": []
        }
    ]
}
```

2. You can run PDS with `docker compose` or `minikube`. Please check out the instructions below sections. 

3. Create the database with `test_request/pds.postman_collection.json`
    - Import collection into Postman. [More details from here](https://learning.postman.com/docs/getting-started/importing-and-exporting-data/).
    - Set collection variable as follows. Note: If you want to choose a different database name, you have to change env variables to `local-k8s/couch-db-deployment.yaml` and `local-k8s/pds-deployment.yaml`.
      - `PDS_DATABASE_NAME=pds_database`
    - Run Create a new database PDS request from the collection.

## Run local environment with K8s

1. Start minikube
`minikube start`

2. Build bazel and create gazelle templates
`bazel build`
`bazel run //:gazelle`

3. Push the image to docker registry
```bash
bazel run //ns/pds:push 
```

4. Create required Kubernetes resources
Please make sure you are using the expected tags in deployment manifests.
`kubectl apply -f local-env/k8s`

5. Access to PDS via minikube IP or Forward port into your localhost
```bash
$kubectl get services pds
NAME   TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
pds    NodePort   <IP ADDRESS>    <none>        8095:<NODE PORT>/TCP   147m
$ minikube ip
<MINIKUBE IP>
$curl <MINIKUBE IP>:<NODE PORT>
# OR
$kubectl port-forward svc/pds 8095:8095
Forwarding from 127.0.0.1:8095 -> 8080
Forwarding from [::1]:8095 -> 8080
$curl 127.0.0.1:8095
```

6. You may want to create a database via steelcouch
```bash
$kubectl exec <PDS POD NAME> -- curl -X PUT http://admin:password@steel-couch:15984/pds_database
{"ok":true}
```

## Run local environment with docker compose

1. Run docker-compose up

```bash
docker-compose -f local-env/docker/docker-compose.yaml up
```

## Test endpoints with Postman
Postman will visualize API, and also it could be used as test cases

### Run sequential test 

#### Run test in local cluster

##### Pre condition
- You need to run PDS and PDS with the following commands.
  `kubectl -apply -k ns/privacy/k8s`
  `kubectl -apply -k infrastructure/k8s/local/pds`

- You need to install "testkube" in your local cluster machine.
   https://kubeshop.github.io/testkube/installing/

- You need to inject testkube name space with Istio.
   `kubectl label namespace testkube istio-injection=enabled --overwrite`

- Restart deployment pods or kill pods

```bash
$kube get deployment -n testkube
NAME                                   READY   UP-TO-DATE   AVAILABLE   AGE
testkube-api-server                    1/1     1            1           21h
testkube-dashboard                     1/1     1            1           21h
testkube-minio-testkube                1/1     1            1           21h
testkube-mongodb                       1/1     1            1           21h
testkube-operator-controller-manager   1/1     1            1           21h
```

- `kubectl rollout restart deployment -n testkube`

##### Run test in K8s cluster
Note: At this moment we having trouble with Istio injection and resolving this issue with OSS community.
https://github.com/kubeshop/testkube/issues/1761

1. Create pds integration test in your local cluster
   `kubectl testkube create test --file ns/pds/tests/pds_contract_test.json --type postman/collection --name pds-test`

2. Run test
   `kubectl testkube run test`

3. Check result
   `kubectl testkube get execution 6103a45b7e18c4ea04883866`

In case If you want to make sure SteelCouch is working fine. You can run tests against "steelcouch" `local-env/test_request/steel_couch_api.json`
Additionally, you create dummy data with "steelcouch" collection as follows.
`newman run local-env/test_request/pds.postman_collection.json --folder create_resources`

#### Local machine test
In case you need to run tests in your "local environment" you need to install "newman".
Note: PDS K8s domain name used for PDS endpoint in the test. For "local environment" you may want to change it.

```bash
# TODO: test script or post man collection might help lot in this case?
$ npm install -g newman
# brew install newman
$ newman run tests/pds_contract_test.json
┌─────────────────────────┬───────────────────┬──────────────────┐
│                         │          executed │           failed │
├─────────────────────────┼───────────────────┼──────────────────┤
│              iterations │                 1 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│                requests │                 9 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│            test-scripts │                13 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│      prerequest-scripts │                11 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│              assertions │                 6 │                0 │
├─────────────────────────┴───────────────────┴──────────────────┤
│ total run duration: 440ms                                      │
├────────────────────────────────────────────────────────────────┤
│ total data received: 1.4kB (approx)                            │
├────────────────────────────────────────────────────────────────┤
│ average response time: 21ms [min: 5ms, max: 103ms, s.d.: 29ms] │
└────────────────────────────────────────────────────────────────┘
```

## Run PDS in the local cluster

## How to put test data for PDS (Temporary solution, to be removed)

1. Get admin credentials

```bash
# Use CouchDB as client (temp solution)
$kubectl exec -it db-agora-kvs-pds-pds-secure-kvs-1 /bin/bash -n pds

# Check username and password
$echo $COUCHDB_PASSWORD
<adminPassword>
$echo $COUCHDB_USER
<adminUser>

# Create database through the SteelCouch if it's not exist
# In case if you want to change DB name please also change into pds.yaml file
$curl -X PUT http://$COUCHDB_USER:$COUCHDB_PASSWORD@steelcouch-agora-kvs-pds-pds-secure-kvs:5984/pds_database

{"ok":true}
```

2. Put data and retrieve data from PDS

```bash
# Add dummy data through the SteelCouch
$curl -X PUT http://$COUCHDB_USER:$COUCHDB_PASSWORD@steelcouch-agora-kvs-pds-pds-secure-kvs:5984/pds_database/5ebc50b8-96ad-401d-a470-c487bddba4fb__vehicle -d '{".color":"#3f304b","registrationNumber":0,"motorIdNumber":0,".holder":"5ebc50b8-96ad-401d-a47^Cc487bddba4fb",".document_type":"vehicle",".created":1656492147}'
{"ok":true,"id":"5ebc50b8-96ad-401d-a470-c487bddba4fb__vehicle","rev":"1-2546bd4347b003430b4291b25b36d6e2"}

# Add dummy data through the SteelCouch
$curl -X PUT http://$COUCHDB_USER:$COUCHDB_PASSWORD@steelcouch-agora-kvs-pds-pds-secure-kvs:5984/pds_database/5ebc50b8-96ad-401d-a470-c487bddba4fb__workaddress -d '{ "_id": "912f88de-0b61-4e7c-93b0-a85272c140ed__workaddress", ".holder": "912f88de-0b61-4e7c-93b0-a85272c140ed", ".document_type": "workaddress", "attributes": { "state": "Tokyo", "city": "Nihonbashi", "street": "3-2-1 Muromachi Mitsui Tower", "other": "16F" }, ".attributes": { ".country": "Japan" }, ".created": "1648801800000", ".updated": "1652149222000" }'
{"ok":true,"id":"5ebc50b8-96ad-401d-a470-c487bddba4fb__vehicle","rev":"1-2546bd4347b003430b4291b25b36d6e2"}

# Access to PDS get woven id list
$curl pds/holderlist
```

## TODO

1. Delete docker env once PoC server is ready. `local-env/docker`
2. Add more test cases including corner cases.
3. Create PDS API test cases `local-env/test_request/pds.postman_collection.json`
4. Change the structure of storing data to CouchDB.
   Currently, reserved attributes and users' dynamic attributes are at the same levels. like updated_date, =holder and work_address.
   This situation serializing dynamic attributes requires a bit of ugly static code.
   `client/couch_client.go:240`
   It would be better if we store user attributes to 1 level nested json structure to become more structured and easy to serialize.
   ```json
   {
    ".updated": "",
    "attributes": [
      {
        "encrypted_attr": "encrypted_value"
      },
    ],
    ".attributes": [
      {
        "plain_attr": "plan_value"
      },
    ],
   }
   ```
   But one issue could be when we need to search by attribute key or value nested search might be slow.
5. Validation logic should be implemented, especially for required attributes.
   - Reserved attribute values
   - CouchClient side required values for document creation
6. Update package name and change structure for project to make it more convenient.
   - `message` package is not matching with `documents.go`
   - `server` folder stores `pds` package
