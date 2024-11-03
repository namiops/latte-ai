# Scheduler API Tests

## Postman script
The Scheduler API postman scripts can be executed, against Scheduler deployment in local as follows

```shell
npm install -g newman
cd /path/to/ns/scheduler
newman run tests/postman/scheduler-api.postman_collection.json -e tests/postman/scheduler-api.postman_local_environment.json
```


TODO: Integrate with CI pipeline.
