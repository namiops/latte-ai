# sourcer-sample

```sh
kubectl -n dbx-poc exec -it deploy/sourcer-sample -- /bin/sh

# Inside the container
curl -v -X POST http://localhost:9876 -H "topic_name: koh-test-topic" --data '{"test":"message"}'

# In case you want to specify the message key
curl -v -X POST http://localhost:9876 -H "topic_name: koh-test-topic" -H "message_key: abcd" --data '{"test":"message"}'
```

## Usage from other Pods/Namespaces

Currently an Service is given to the Vector container of sourcer-sample so that other Pods/Namespaces can potentially make use of it for testing out the data-sourcing experience.

```sh
curl -v -X POST http://vector.dbx-poc.svc:9876 -H "topic_name: koh-test-topic" --data '{"test":"message"}'
```

It is highly possible that this will not last long. More preferred deployment method is the sidecar pattern.
