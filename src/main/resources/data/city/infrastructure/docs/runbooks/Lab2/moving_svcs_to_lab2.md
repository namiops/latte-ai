**Table of Contents**
<!-- vim-markdown-toc GFM -->

* [Checklist for deployment to Lab2](#checklist-for-deployment-to-lab2)
* [Purpose of this document](#purpose-of-this-document)
* Steps:
    * [Step-1: Adding service manifests files](#step-1-adding-service-manifests-files)
    * [Step-2: Cluster management](#step-2-cluster-management)
    * [Step-3: Worker configuration](#step-3-worker-configuration)
* [Debugging](#debugging)


# Checklist for deployment to Lab2

you can find the checklist deployment to lab2 [here](https://docs.google.com/document/d/1ztPBlKPiG-SwPfsEWOZ3RZtzWF-FoEcu7QtjAehJHu8). Please feel free to make a copy for your own usecase.

# Purpose of this document

> This document is specifically for agora platform engineers to move their services to Lab2 environment

The purpose of this document is provide the necessary people, a runthrough/walkthrough on how to move their resources to lab2. This document also aims to explan "why we are creating these files" and "where to look if something goes wrong with your service in lab2". 

# Steps
## Step-1: Adding service manifests files

In this document we will use the example of [Developer portal](https://developer-portal.agora-lab.w3n.io/).

1. We need to confirm that all our service manifests files are present in **infrastructure/k8s/common/\<namespace>/\<version>**. You can take a look at the folder for Developer portal [here](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/developer-portal).

2. Make sure that the above folder contains a **kustomization.yaml** which lists all the yamls in this folder. The purpose of this folder is that we can reuse the same code for lab2 and don't have to copy paste. ([example](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/developer-portal/kustomization.yaml))

3. If you are using bazel/zebra, files target is added to **infrastructure/k8s/common/zebra_files.bzl**. [Here](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/zebra_files.bzl) is the link to the file. 

## Step-2: Configuration in the management cluster

In this step we will be setting up our own workspace in Lab2. Lab2 environment uses gloo-mesh which has a concept of workspaces. With Gloo workspaces, we can group all our team's resources across clusters and namespaces. Depending on settings, our resources become available to other resources in the workspace automatically. 

<!-- ![Alt text](./assets/gloo-workspace-1.svg =100x100) -->
<p align="center"><img src="./assets/gloo-workspace-1.svg" width="600" /></p>

1. We will define a **Workspace yaml** file in the folder **infrastructure/k8s/environments/lab2/clusters/mgmt-east/gloo-mesh/** by the name "workspace-\<service-name>.yaml". This file will create a new new workspace for your team in lab2. You can copy paste the contents from other files and udpate the name of your workspace. ([example](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/gloo-mesh/workspace-developer-portal.yaml)) 

2. This Workspace file will be referenced in the **kustomization.yaml** file present in the **infrastructure/k8s/environments/lab2/clusters/mgmt-east/gloo-mesh/** folder. You need to add your "workspace-\<service-name>.yaml" to ([here](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/gloo-mesh/kustomization.yaml))

Now, our next steps would be to set up namespace, WorkspaceSettings, RouteTable, VirtualDestination. Let's look at them one by one

* **namespace** - self-explanatory
* **WorkspaceSettings** - workspace settings let's you configure your workspace with several different configurations like Service Isloation, east-west gateway selection, import or export from other workspaces and a bunch of different things. (You can find more about about setting workspaces in the official documentation [here](https://docs.solo.io/gloo-mesh-enterprise/latest/concepts/multi-tenancy/workspace-configuration/)).
* **RouteTable** -  This is used for defining routing rules. This resource creates VirtualService in selected clusters and will be visible only to defined workspaces.
* **VirtualDestination** - VD forwards request to one of the backing services randomly. It generates ServiceEntry and DestinationRule Resources. 

<br/><br/> 
<p align="center"><img src="./assets/routing-ov.svg" width="900" /></p>
<br/><br/> 

3. We need to define the above files in **infrastructure/k8s/environments/lab2/clusters/mgmt-east/\<namespace>** folder. You can reference the Developer portal folder [here](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/developer-portal).

    * The namespace itself in **_namespace.yaml**
    * A WorkspaceSetting in **workspacesettings-\<service-name>.yaml**
    * A RouteTable (Gloo Mesh API for VirtualService in Istio), if needed, is in **routetable-\<service-name>.yaml** (every service that requires outbound and inbound traffic needs to define a RouteTable)
    * A VirtualDestination (Gloo Mesh API for ServiceEntry and DestinationRule in Istio), if needed, is in **virtualdestination-\<service-name>.yaml**
    * A Kustomization file, listing the above files is in **kustomization.yaml**
<br/><br/> 


4. Now we need to reference the kustomization.yaml file in the above directory to files in **infrastructure/k8s/environments/lab2/clusters/mgmt-east/flux-system/kustomizations/services** folder so that FluxCD can pick up your manifest files and deploy them. An example for developer portal is present [here](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/flux-system/kustomizations/services). You can have a look at "developer-portal.yaml" and "kustomization.yaml" files.


    * Create a FluxCD Kustomization file for the service in **\<service-name>.yaml**
    * A Kustomization file referencing that file, in **kustomization.yaml**
<br/><br/> 

5. If you’d like to expose your service to the company private network, add your host and workspace by host’s alphabetical order to VirtualGateway defined in:  **infrastructure/k8s/environments/lab2/clusters/mgmt-east/city-private-ingress/virtualgateway-default.yaml** ([here](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/mgmt-east/city-private-ingress/virtualgateway-default.yaml))

You can learn more about above concept [here](https://github.com/wp-wcm/city/blob/main/infrastructure/docs/runbooks/gloo-mesh/Concepts.md).

# Step-3: Configuration in worker clusters

In this step we will be setting up manifest files in worker clusters for our service. These resources will be deployed in lab2-worker1-east cluster.

1. Firstly, we  need to create a **kustomization.yaml** file in **infrastructure/k8s/environments/lab2/clusters/worker1-east/\<namespace>** folder which references all your service manifests files present in the **infrastructure/k8s/common/\<service-name>/\<version>** folder. ([example](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/developer-portal/kustomization.yaml)). 


2. Create a _namespace.yaml in **infrastructure/k8s/environments/lab2/clusters/worker1-east/\<namespace>/** (please note if you already have a namespace file defined in your **infrastructure/k8s/common/\<service-name>/\<version>** folder then there is no need to define it here)

3. A FluxCD kustomization file by the name \<service-name>.yaml exists at **infrastructure/k8s/environments/lab2/clusters/worker1-east/flux-system/kustomizations/services/** folder which refrences the kustomization file created in 1st point above. ([example](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/flux-system/kustomizations/services/developer-portal.yaml)) 

4. Update the kustomization.yaml in  **infrastructure/k8s/environments/lab2/clusters/worker1-east/flux-system/kustomizations/services** refers to your \<service-name>.yaml kustomization created in point 3 above. ([example](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/lab2/clusters/worker1-east/flux-system/kustomizations/services/kustomization.yaml))

5. Make sure all container images are patched to refer to **Artifactory** in the kustomization.yaml

6. If [Flux image automation](https://github.com/wp-wcm/city/blob/main/docs/technical_notes/cd.md?plain=1#L77) is desired:
    * Configuration is in place at infrastructure/k8s/environments/lab2/clusters/worker1-east/flux-system/automations/\<service-name>-automation.yaml

    * Image repositories and policies are in place at: infrastructure/k8s/environments/lab2/clusters/worker1-east/flux-system/images/\<service-name>.yaml


With all these files in place create a PR and merge it. 

# Debugging

If the services isn't deployed as planned, it can be due to several factors. Some of the problems are listed below

* There are some orphaned yaml files present that are not refrenced in the kustomization file. Please make sure that all the services that we are deploying are properly mentioned in the respective Kustomization files. 

* Firstly, make sure that the Gloo Mesh resources we deployed in lab2-mgmt-east cluster in step-2: Cluster Management are up and working properly without any errors, this includes Workspace, WorkspaceSettings, RouteTable, VirtualDestination(*if required), etc. 

* Check the resources deployed for your service in lab2-worker1-east cluster. These include all the resources from namespace to your service deployment file.

* Lab2 uses gloo-mesh which forces us to use IPv6 for our applications. Please make sure that your application has enabled IPv6 and is able to listen IPv6 addresses




