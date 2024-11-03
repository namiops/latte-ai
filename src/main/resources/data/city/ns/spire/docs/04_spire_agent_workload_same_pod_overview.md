# Running SPIRE Agent and workload in the same Pod

**Caution:**
This guide provides a setting close to the Agora environment and is not just a hands-on article. We adopt `x509pop`, which is also used in Agora as an authentication method for SPIRE.

## Background
In traditional setups, SPIRE Agent, workload, and Envoy Proxy operate in separate Pods. However, running SPIRE Agent and workload in the same Pod offers several benefits:

- By placing the SPIRE Agent, Envoy Proxy, and workload in the same Pod, all containers can communicate through localhost. This significantly simplifies communication between each container, making troubleshooting easier.
- Positioning the SPIRE agent and workload in the same Pod makes it clear which resources are associated with a specific SPIFFE ID. This centralizes the management of SPIFFE IDs, enhancing operability.
- A single Pod contains both the SPIRE Agent container and the Envoy Proxy container. Through the Envoy container, the workload (application) can communicate with other services running in Agora using mTLS without worrying about mTLS itself.

**Target Audience:**
- Those who have already read the [SPIFFE/SPIRE tutorial](https://developer.woven-city.toyota/catalog/default/component/spire/docs)
- Those who do not want to make major changes to existing workloads
- Those who want to simplify and manage the number of resources related to SPIFFE

### Note
When adopting this method, you need to set the node attestator to [x509pop](https://github.com/spiffe/spire/blob/main/doc/plugin_server_nodeattestor_x509pop.md).
`x509pop` is a method by which the SPIRE Agent attests itself to the SPIRE Server using a public key certificate and its corresponding private key. In this method, the SPIRE Server verifies the identity of the SPIRE Agent based on a trusted public key list. Therefore, you need to prove that the SPIRE Agent has the correct private key.
Each SPIRE agent needs to have unique identification information (a token for `join_token`, a unique key-pair for `x509pop`). This is because it is difficult to assign a unique value to multiple agents with a `join_token` if multiple agents are launched within each node.

## Procedure
Please follow the steps below.

1. Copy `city/ns/spire/docs-k8s/spire-envoy-integration` directory into your own directory:
```
cp -r <city-repo-dir>/ns/spire/docs-k8s/spire-envoy-integration <your-team-folder>/
cd <your-team-folder>/spire-envoy-integration/
```
Please replace the namespace with your team's namespace.

2. Each service team should create a CA certificate (public key) and a private key.
Please refer to [this](https://developer.woven-city.toyota/catalog/default/component/spire/docs/04a_spire_agent_workload_same_pod_x509pop_cert_generation/) for how to generate them.
Note that this certificate needs a digital signature. Rename your CA certificate (public key) and private key to `agent-x509pop.crt.pem` and `agent-x509pop.key.pem`, respectively, and place them in the certs/secret folder.

3. To allow the agent to verify the trustworthiness of the server, obtain the server's CA certificate from the Agora team. Please inquire in the AMA channel. This server CA certificate needs to be included in the [spire-bundle-configmap-dev.yaml](https://github.com/wp-wcm/city/blob/main/ns/spire/docs-k8s/spire-envoy-integration/spire-bundle-configmap-dev.yaml).

4. Request the Agora Team to include your CA certificate (`agent-x509pop.crt.pem`) in the trust list.Please inquire in the [AMA channel](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7). This can be done by creating a pull request in city repository with a title such as "Add <Your Team's Name> CA certificate to trust list" and providing the details in the description. Once your request is approved and merged, your CA certificate will be set as a trusted CA certificate in Spire Server.

5. After the registration of the CA certificate on the SPIRE server is completed, register the SPIFFE ID.
When registering, please specify the parentId in the form of spire-agent-x509pop-${POD_NAME} for each workload.
Please refer to [this](https://developer.woven-city.toyota/catalog/default/component/spire/docs/02b_hands_on_create_registration_entry/) for how to register the SPIFFE ID.  
A PR combining changes like [this one](https://github.com/wp-wcm/city/commit/5e0cd500149f88e2d443c84051aff3605309de9e) and [this one](https://github.com/wp-wcm/city/commit/5d83be8e46751cade6dc15ef956c256c346f9d01) is required to add the spiffeid.The two links represent an initial attempt and a subsequent correction to address an error. 

6. Execute the kubectl apply command to apply the manifest file. This deploys a Pod containing the SPIRE agent and the Envoy proxy on the necessary nodes.
```
kubectx minikube # Please switch the context to the environment where your team's k8s manifest will be applied. Here, we are temporarily setting it to minikube.

kubectl apply -k ./certs 
kubectl apply -k .
```

7. After you have completed steps 1 through 6, you can verify the correct operation of your application by examining the logs. The curl container, deployed in the same Pod, sends a request to the Envoy Proxy container. This request is then securely communicated to other services running in Agora via the Envoy Proxy container using mTLS. You can check the logs by executing the following command:
```
kubectl -n <your-team-namespace> logs spire-envoy-integration-0 -c curl
```

If the output includes `URI=spiffe://spire.cityos-dev.woven-planet.tech/ns/spire-sample/sa/httpbin-client`, this indicates that your application is functioning correctly and the setup is complete!

## Deep Dive

Here we will go into detail about the contents and meaning of specific configuration files.

- city/ns/spire/docs-k8s/spire-envoy-integration/spire-envoy-integration.yaml

This yaml file contains two containers, the SPIRE Agent and Envoy Proxy. The SPIRE Agent retrieves the SVID from the SPIRE Server, and the Envoy Proxy uses the SVID obtained from the SPIRE Agent to communicate with other services over mTLS.

This file uses a StatefulSet rather than a Deployment. This is because a StatefulSet creates Pods in order from 0, generating Pods with stable names. As a result, the SPIFFE IDs of the SPIRE Agent are also assigned in a stable manner.
The volumeClaimTemplates: [] at the end of the snippet indicates that this StatefulSet does not have its own persistent volume. This means that this StatefulSet does not share states between pods, but instead uses this to stabilize pod names.
```
apiVersion: apps/v1
# This isn't really a _stateful_ workload (see `volumeClaimTemplates` is empty).
# We need stable pod names so that the Agent's SPIFFE IDs are also assigned stably.
kind: StatefulSet
metadata:
  name: spire-envoy-integration
  namespace: spire-envoy-integration-ns
spec:
  # ...omitted...
volumeClaimTemplates: []
```

The initContainer:gen-cert generates a certificate for each agent based on a CA certificate. Each pod (SPIRE agent) generates a certificate based on a CA certificate when it starts. Also, each agent's parentId is in the form of `spire-agent-x509pop-${POD_NAME}`. When registering workloads, please specify this parentId.
```  
    spec:
      initContainers:
      - name: gen-cert
      containers:
      - name: spire-agent
      - name: envoy
```

`gen_cert.sh`: This script uses Cert-manager to generate certificates for each agent.
`cert.yaml.tpl`: This is a template for a Certificate resource that the `gen_cert.sh` script uses to request new certificates from Cert-manager.

```
kind: ConfigMap
metadata:
  name: init-gen-cert
  namespace: spire-envoy-integration-ns
data:
  gen_cert.sh: |
  # ...omitted...
  cert.yaml.tpl: |
  # ...omitted...
```

- city/ns/spire/docs-k8s/spire-envoy-integration/certs/secret/kustomization.yaml

A rootCA is created as a Kubernetes Secret based on the CA certificate (public key) and private key prepared by each service team. Each agent's certificate is created based on this rootCA.
```
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: cert-manager
secretGenerator:
  - name: agent-x509pop-root
    files:
      - tls.crt=./agent-x509pop.crt.pem
      - tls.key=./agent-x509pop.key.pem
```

- city/ns/spire/docs-k8s/spire-envoy-integration/certs/spire-cluster-issuer.yaml

Certificates for each SPIRE Agent are issued based on the set Root CA.
```
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: spire-agent-x509pop-issuer
spec:
  ca:
    secretName: agent-x509pop-root
```

## Conclusion
The above is a description of the method for running a SPIRE agent and workload within the same Pod. This configuration significantly simplifies communication between containers and centralizes the management of SPIFFE IDs, resulting in easier system operation and troubleshooting.
