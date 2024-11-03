# Manifests for Testing Drako

This folder contains some manifest files to be used in development for Drako.

We are doing this way so we have more flexibility in local to develop for now.

At some point we will make a standard deployed with flux so anyone can develop.


## Setup

In order to run those tests, first make sure your local CRD is up to date by running

```
bazel run //ns/id/drako_data:drako_data_crd_gen | kubectl apply -f -
```

It goes without saying, but make sure you are running this to your local environment
only! Shared clusters must be updated via flux.

Next you must have DPS properly configured. For that, apply the `//infrastructure/k8s/local/data-privacy`
manifests to your local environment; then make sure to port-forward the port
8083 for the data-service pod when it comes online. With that said, run the
following commands to populate it with required data:

```bash
curl -I --request POST http://localhost:8083/admin/service_mapping/Drako/drako-test
curl -I --request POST http://localhost:8083/admin/data_mapping/PERSONAL_DATA/PERSON_PRIMARY_NAME
curl -I --request POST http://localhost:8083/admin/data_mapping/PERSONAL_DATA/EMAIL_ADDRESS
```

Once the CRDs are up to date and DPS is setup, you can apply the policies on this folder.

They will create the `drako-test` namespace, which you can access directly via browser at
https://drako-test.woven-city.local/ . You can also run our `//ns/id/drako_e2e:drako_e2e`
target in bazel to run oauth tests. A better solution is being rolled out using
testkube instead.

## Access denied?!

When trying to access https://drako-test.woven-city.local/ via browser it is expected to
have access denied at this stage if you didn't do anything else. Why so? Because your user
has not consented access to PERSONAL_PRIMARY_NAME and EMAIL_ADDRESS to drako-test. To fix
that, expose the port 8081 from the data-privacy service and:

```bash
curl -I --request POST http://localhost:8081/consents/user/alice/kind/PERSONAL_DATA/party/Drako
```

Assuming you are using the `alice` test user.

To revoke the above consent, you can use `DELETE` request method

```bash
curl -I --request DELETE http://localhost:8081/consents/user/alice/kind/PERSONAL_DATA/party/Drako
```