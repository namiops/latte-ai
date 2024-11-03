## CVM API Tests
### Postman script
The CVM API postman scripts can be executed, against CVM deployment in local as follows:

```sh
npm install -g newman
cd /path/to/ns/cvm
newman run tests/postman/cvm.postman_collection.json -e tests/postman/cvm.postman_local_environment.json
```

TODO: Integrate with CI pipeline.
