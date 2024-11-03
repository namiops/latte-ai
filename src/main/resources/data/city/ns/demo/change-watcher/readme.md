## Change Watcher
A service that demonstrates how to watch changes on SKVS documents.<br>

### Sequence
Currently, a use case specific to device shadow documents in _iot_topology_ is implemented.
![sequence](./docs/sequence.png)


### Development Setup
1. SKVS is a dependency, it can be setup by either [port forwarded from the lab](https://github.com/wp-wcm/city/tree/main/ns/iot/iota#script-to-setup-dependencies-for-local-development) or [spin-up SKVS docker containers](https://github.com/wp-wcm/city/tree/main/ns/secure-kvs/steelcouch#launch-locally-using-docker).
2. Add the below to the launch.json, and start in a VS Code debugger.
```json
        {
            "name": "Launch shadow demo",
            "type": "go",
            "request": "launch",
            "mode": "auto",
            "program": "demo/change-watcher/cmd/main.go",
            "args": [
                "--secure-kvs-url",
                "http://localhost:15984",
                "--secure-kvs-dbname",
                "iot_topology",
                "--watch-changes-tenants",
                "rapid-prototyping-clp",
            ]
        }
```
