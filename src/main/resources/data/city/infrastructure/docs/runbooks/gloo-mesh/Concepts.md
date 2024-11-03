# Basic Concepts

<!-- vim-markdown-toc GFM -->

* [Bootstrapping clusters](#bootstrapping-clusters)
  * [Requirements](#requirements)
  * [Network architecture management-worker clusters](#network-architecture-management-worker-clusters)
* [Workspaces](#workspaces)
  * [Service isolation](#service-isolation)
  * [Import Export Behavior](#import-export-behavior)
* [Traffic Routing](#traffic-routing)
  * [North-south Routing](#north-south-routing)
  * [East-west Routing](#east-west-routing)
* [Gloo Mesh Behind The Scenes](#gloo-mesh-behind-the-scenes)

<!-- vim-markdown-toc -->

## Bootstrapping clusters

### Requirements

This is a **simplified overview** of bootstrapping clusters using cert-manager.

```mermaid
%%{init: {'theme': 'neutral', "flowchart" : { "curve" : "basis" } } }%%
flowchart LR
    subgraph "management cluster"
        subgraph "gloo mesh NS"
            MH(KubernetesCluster: worker-cluster)
            MA(Service: gloo-mgmt-server)
            MB(Secret: relay-token-sercret)
            subgraph "CA"
            MC(Issuer: relay-root-ca) --- MD(Certificate: relay-root-ca)
            end
            MD --- ME(Issuer: relay-root-ca-selfsigned) 
            ME --- MF(Certificate: gloo-mesh-mgmt-server)
            ME --- MG(Certificate: worker-cluster)
        end
    end
    
    subgraph WC ["worker-cluster"]
        subgraph "gloo mesh NS"
            WA(distribute here) -.-  WB(Service: gloo-agent)
        end
    end

    MA -.- LA(Load Balancer)
    MB -.- |secret to join for first time| WA
    MF -.- |for MTLS| WA
    MG -.- |for MTLS| WA
    WB ==> |join| LA
    MH -.- |register| WC
```

### Network architecture management-worker clusters

```mermaid
%%{init: {'theme': 'neutral', "flowchart" : { "curve" : "basis" } } }%%
flowchart TD
    subgraph "management cluster"
        subgraph "gloo mesh NS"
            MA(gloo-mgmt-server) --- MC(gloo-metrics-gateway)
            MA --- MD(redis)
            MB(gloo-mesh-ui) --- MD
            MB --- ME(prometheus)
            MC --- ME
        end
        
    end
    MA -.- LA(Load Balancer)
    MC -..- LB(Load Balancer)
    
    subgraph "worker cluster"
        subgraph "gloo mesh NS"
            WA(gloo-agent)
        end
    end
    WA --> |GRPC 9900| LA
    WA --> |Healthchecks 8090| LA
    WA --> |TCP 4317| LB
```

## Workspaces

- Workspace is a logical grouping of namespaces across multiple clusters, providing more flexible grouping than a namespace.
  - A workspace can be mapped to multiple namespaces, but a namespace can only belong to one workspace.
- There are two custom resources involved: [Workspace](https://docs.solo.io/gloo-mesh-enterprise/main/reference/api/workspace/) and [WorkspaceSettings](https://docs.solo.io/gloo-mesh-enterprise/main/reference/api/workspace_settings/).
  - Workspace should be deployed in the gloo-mesh namespace in the management cluster.
  - WorkspaceSettings should be deployed in one of the namespaces it selects.
    - The namespace where WorkspaceSettings is being deployed is called **root config namespace**.
    - **Global WorkspaceSettings** can be set by creating a resource in the gloo-mesh namespace of the management cluster named `global`.

<img src=https://docs.solo.io/gloo-mesh-enterprise/main/img/wksp-ov3.svg width="70%" height="70%">

As shown in the image, workspace does not necessarily have to share the same name as a namespace. It can include multiple namespaces that span accross selected clusters.

### Service isolation

- Service isolation can be enabled in WorkspaceSettings.
- Service isolation restricts workspace visibility of all of [these resources](https://docs.solo.io/gloo-mesh-enterprise/main/reference/api/workspace_settings/#workspacesettingsspec-workspaceobjectselector-typedobjectselector-objectkind).
  - Behind the scenes, service isolation will create [Authorization Policy](https://istio.io/latest/docs/reference/config/security/authorization-policy/).
  - To trim sidecar config to the workspace, set `trimProxyConfig: true`. This creates [Sidecar](https://istio.io/latest/docs/reference/config/networking/sidecar/) resource in each namespace for the Workspace.
- Service isolation can be enhanced by [AccessPolicy](https://docs.solo.io/gloo-mesh-enterprise/main/reference/api/access_policy/), which is a wrapper that could generate PeerAuthentication and AuthorizationPolicy.

### Import Export Behavior

- At a high level overview, each workspaces that wants to share CR needs to explicitly define `importFrom` and `exportTo`, requiring **both workspaces to agree to the relationship**.

|    Resource Type    | Service Isolation On |      Service Isolation Off      |
|:-------------------:|:--------------------:|:-------------------------------:|
| ROUTE_TABLE         |  need import/export  |        need import/export       |
| SERVICE             |  need import/export  | **does not need import/export** |
| VIRTUAL_DESTINATION |  need import/export  |        need import/export       |
| EXTERNAL_SERVICE    |  need import/export  |        need import/export       |
| API_DOC             |  need import/export  |        need import/export       |
| GRAPHQL_*           |  need import/export  |        need import/export       |
| EXTERNAL_WORKLOAD   |  need import/export  |        need import/export       |

References:
- https://docs.solo.io/gloo-mesh-enterprise/main/concepts/multi-tenancy/
- https://www.solo.io/blog/workspaces-and-multi-tenancy/
- https://github.com/solo-io/solo-cop/tree/main/blogs/workspaces

## Traffic Routing

North-south routing and east-west routing are terms used to describe the direction of data traffic within a network.  
  
North-south routing refers to the traffic that flows between the internal and external networks.  
[The north-south section below](#north-south-routing) describes traffic specifically coming from north to south.  
  
On the other hand, east-west routing refers to the traffic that flows within the network, cluster to cluster traffic in this case.

### North-south Routing

```mermaid
%%{init: {'theme': 'neutral', "flowchart" : { "curve" : "basis" } } }%%
flowchart TD
    AA(Inbound Request) --> BA(Load Balancer)
    AA --> BB(Load Balancer)
    BA --- CA
    BB --- CB
    
    subgraph "Gloo Custom Resources"
        subgraph Gateway
            CA(Ingress Gateway) --- DA(Virtual Gateway)
            CA --- DB(Virtual Gateway)
            CB(Ingress Gateway) --- DC(Virtual Gateway)
        end
        subgraph Routing
            DB --- EA(Route Table)
            EA ---|can delegates| EB(Route Table)
        end
        subgraph Destination
            EA ---- FA(Virtual Destination)
            EA --- GA(External Service)
            EA --- HA(Cloud Provider<br>i.e. AWS Lambda)
        end
    end
    
    FA --- IA(K8S Service)
    IA --- JA(App)
    GA --- JB(App)
    HA --- JC(App)
```

- [VirtualGateway](https://docs.solo.io/gloo-mesh-enterprise/main/reference/api/virtual_gateway/)
  - VG should be attached to the ingress gateway. This means:
    - Port mapping must [matches](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/gloo-mesh/federation-demo/intramesh_route/virtualgateway-bookinfo.yaml#L14) the gateway.
    - The workspace where VG is deployed must have visibility to the Ingress Gateway Service.
  - When VG is successfully deployed, [Gateway](https://istio.io/latest/docs/reference/config/networking/gateway/) object will be created.
  - RouteTable must also be bound to this resource. This means:
    - Workspace where VG is deployed must have visibility to RT.
- [RouteTable](https://docs.solo.io/gloo-mesh-enterprise/main/reference/api/route_table/)
  - Define routing rules after VG.
  - VirtualService resource will be generated in selected clusters and will be visible only to defined workspace.
  - RT supports three types of routes: HTTP, TCP, and TLS.
- [Virtual Destination](https://docs.solo.io/gloo-mesh-enterprise/main/reference/api/virtual_destination/)
  - ServiceEntry and DestinationRule resources will be generated in selected clusters and will be visible only to defined workspace.
  - VD forwards request to one of the backing services **randomly**.
  - **If more than one service per cluster is selected, VD will be invalid and will not be translated**.
  - To forward to the local cluster service only, a FailoverPolicy and/or OutlierDetectionPolicy must be configured.
- [External Service](https://docs.solo.io/gloo-mesh-enterprise/main/reference/api/external_service/)
  - ServiceEntry resource will be generated in selected clusters and will be visible only to defined workspace.
  - IP addresses for ES can be resolved via DNS or provided statically using [ExternalEndpoint](https://docs.solo.io/gloo-mesh-enterprise/main/reference/api/external_endpoint/)

### East-west Routing

```mermaid
%%{init: {'theme': 'neutral', "flowchart" : { "curve" : "basis" } } }%%
flowchart LR
    subgraph "management cluster"
        M1(RootTrustPolicy:<br>To Allow MTLS trust<br>between worker clusters)
    end
    
    subgraph WC1 ["worker-cluster-1"]
        subgraph WC1X [Workspace X]
            WC1A(request) ==> WC1B(Service: frontend)
        end
        subgraph WC1GW [Workspace gateways]
            WC1C(east-west-gateway)
        end
    end

    subgraph WC2 ["worker-cluster-2"]
        subgraph WC2X [Workspace X]
            WC2A(Service: backend)
        end
        subgraph WC2GW [Workspace gateways]
            WC2B(east-west-gateway)
        end
        WC2B ==> WC2A
    end

    M1 -.- WC2B
    M1 -.- WC1C
    WC1X -.- |same workspace| WC2X
    WC1GW -.- |same workspace| WC2GW
    WC1C --- AA(Load Balancer)
    AB(Load Balancer) ==> WC2B
    WC1B ==> |MTLS: 15443| AB
```

- Each east-west gateway exposes itself through LoadBalancer.
- In this example, before the frontend service reaches the LB, the sidecar looks for RT in the workspace, which then looks for VD.
- By default, each workspace will use the east-west gateway with label `"istio": "eastwestgateway"`, which is defined in WorkspaceSettings.
- As shown in the graph, **RootTrustPolicy** is necessary for work clusters to trust each other and establish MTLS in the east-west gateway.

## Gloo Mesh Behind The Scenes

Gloo Mesh mostly is a wrapper of Istio CR. Following table will show generated Istio resources behind the scenes:
| Gloo Resource Name              | Property                | Generated Istio Resources                      |
| -------------------------       | ------------------      | -------------------------------------------    |
| AccessPolicy                    |                         | AuthorizationPolicy<br>PeerAuthentication      |
| ExternalService                 |                         | ServiceEntry                                   |
| GatewayLifecycleManager         |                         | IstioOperator                                  |
| IstioLifecycleManager           |                         | IstioOperator                                  |
| RouteTable                      |                         | VirtualService                                 |
| ProxyProtocolPolicy             |                         | EnvoyFilter                                    |
| VirtualDestination<sup>*1</sup> |                         | DestinationRule<br>ServiceEntry<br>EnvoyFilter |
| VirtualGateway                  |                         | Gateway                                        |
| WorkspaceSettings               | serviceIsolation        | AuthorizationPolicy                            |
|                                 | trimProxyConfig         | Sidecar                                        |
|                                 | exportTo<sup>*2</sup>   |                                                |
|                                 | importFrom<sup>*3</sup> |                                                |
|                                 | federation              | ServiceEntry<br>EnvoyFilter                    |

- *1 EnvoyFilter is generated in eastwest gateway to allow request directed to configured host
- *2 Copy Istio resources from this workspace to other workspace
- *3 Copy Istio resources from other workspace to this workspace
