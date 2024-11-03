# Step 2: Deploy our app

Now that we have pds and privacy services running in our cluster we will deploy a go app that will do some simple accesses of data using pds.

## Before deployment

Before we go further let's discuss the privacy service from a practical perspective in our deployment. The following config maps no longer exist (as they were used to populate data prior to a working version of the Data Privacy Service), but are useful to illustrate how services, data-kinds, and user information interacts inside the DPS.

```yaml
  config.yaml: |
    "_clientMap":
      birthday-service-ai-client: birthday-service
      birthday-service-frontend: birthday-service
      oidc-rp: demo-service
      calorie-counter: weightloss-service #NEW
    "_dataMap":
      birthday:write: personal-details
      birthday:read: personal-details
      personal-details:read: personal-details
      weightloss_data:read: personal-details #NEW
      weightloss_data:write: personal-details #NEW
    "_userConsentMap":
      tal:
        personal-details:
          - birthday-service
      atsushi:
        personal-details:
          - birthday-service
      healthy_user: #NEW
        personal-details: #NEW
          - weightloss-service #NEW
```

The _clientMap includes a client name with a service that is associated with it. The _dataMap includes individual resources mapped to data-kinds. Resources with permission for that data-kind can be accessed by the client to read or write that data according to the mapping. The _userConsentMap has a woven-id, along with multiple data-kinds, and the service that they map to.

Rather than having the data populated at start up, as the DPS is now functioning, this data is added by POSTing to the endpoints of the DPS.

```shell
curl -X POST http://data-privacy-admin.data-privacy/admin/service_mapping/weightloss-service/calorie-counter
curl -X POST http://data-privacy-admin.data-privacy/admin/data_mapping/personal-details/weightloss_data:read
curl -X POST http://data-privacy-admin.data-privacy/admin/data_mapping/personal-details/weightloss_data:write
curl -X POST http://data-privacy-fe.data-privacy/consents/user/healthy_user/kind/weightloss_data:read/party/weightloss-service
curl -X POST http://data-privacy-fe.data-privacy/consents/user/healthy_user/kind/weightloss_data:write/party/weightloss-service
```

Although in our setup this is accomplished through golang. The setup endpoint below takes care of setting up these calls.

## Deploy Calorie Counter

First lets go to the **project root**

```shell
$ cd cityos/ns/tutorial/pds-101/calorie-counter
```

Let's deploy the Calorie Counter application.

```shell
$ kubectl apply -f ./kubernetes
namespace/calorie-counter created
deployment.apps/calorie-counter created

$ kubectl get pods -n calorie-counter
NAME                              READY   STATUS    RESTARTS   AGE
calorie-counter-cf74ddcfd-sz5ld   0/1     Running   0          11s
```

Once our pod is `Running` lets try and send some messages. First we will use `port-forward` to expose the service from minikube. Make sure to have another shell open because port-forward stays in the foreground.

```shell
$ kubectl port-forward <pod_name> -n calorie-counter 8080:8080
Forwarding from 127.0.0.1:8080 -> 8080
Forwarding from [::1]:8080 -> 8080
```

Our app is _very_ simple, we will store a user's data, in this case calories eaten in the day, and TDE (Total Daily Expenditure, basically the number of calories you need to each in a day) and can fetch that data as well. As well as another GET endpoint where you can get the number of calories your have eaten so far in the day. There is also an administrative endpoint that we will be using to set up the permissions that are necessary for the service in the first place.

First let's get that administrative endpoint out of the way.

```shell
$ curl -X POST http://localhost:8080/setup
```

Lets send a `curl` to send a POST request to the pod

```shell
$ curl -X POST http://localhost:8080/tde -d '{"tde":"1900","caloriesEaten":"1500"}'
```

And some GET requests as well

```shell
$ curl -X GET http://localhost:8080/tde
{"caloriesEaten":"1500","created":"0","document_type":"weightloss_data","holder":"healthy_user","id":"healthy_user__weightloss_data","tde":"1900","updated":"0"}

$ curl -X GET http://localhost:8080/caloriesLeft
You can still eat 400 calories today.
```

`What if I want to get another user's data? Or different data from that same user? What if I'm a different client trying to access the same user's data?`

All of these are controlled by the url we are accessing, or the certificates we are passing in.

[Here](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/ns/tutorial/pds-101/calorie_counter/internal/http/gin.go) you can see the go code used to define the gin server for our calorie_counter app. The healthy_user is the user we are requesting data for, evident in the path. Also the kind of data (weightloss_data, like we defined above in the map) is in the path as well. Finally the client that is accessing the data is supplied in the certificate (as weightloss-client)

Finally there is a teardown endpoint that will remove the previous access we have added

```shell
$ curl -X POST http://localhost:8080/teardown
```

## Congratulations

We now have a basic understanding of PDS conceptually, how to deploy it to a local cluster and access data within the PDS.
