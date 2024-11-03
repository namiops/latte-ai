# Agora Infrastructures 

* [Agora Infrastructures](#agora-infrastructures)
  * [AWS](#aws)
    * [AWS Accounts](#aws-accounts)
  * [Kubernetes Clusters](#kubernetes-clusters)
    * [Legacy environments (CI/Lab/Dev)](#legacy-environments-cilabdev)
    * [Lab2](#lab2)
  * [Infrastructure Documents](#infrastructure-documents)
  * [Useful Links for Infra team](#useful-links-for-infra-team)

## AWS

### AWS Accounts
Cloud resources in each AWS account are provisioned by Terraform as Infrastructure-as-Code (IaC).
We have multiple AWS accounts managing each environment, as listed in detail by [Terraform README](./terraform/README.md).

If you are in Agora Infra team, please make sure that you have access to all of the above AWS accounts.
To access each account, go to https://woven.awsapps.com/start#/.

## Kubernetes Clusters
Kubernetes resources in Agora are deployed by [FluxCD](fluxcd.io), a GitOps tool from CNCF projects.
Basically, Flux controllers pull manifest files from a git repository and apply them on Kubernetes (server-side apply).
More details can be found at [k8s/README.md](./k8s/README.md).

### Legacy environments (CI/Lab/Dev)
To deploy your resources to Agora legacy environments, check each cluster's folders in [k8s](./k8s).

### Lab2
To learn more about Agora Lab2 cluster, please refer to these documents:
- [Basic Concepts of Gloo Mesh](./docs/runbooks/gloo-mesh/Concepts.md)
- [Lab2 Onboard Guide](./docs/runbooks/Lab2/onboard.md)

To deploy your resources to Agora Lab2 environment, check each cluster's folders in [k8s/environments/lab2](./k8s/environments/lab2).

In addition, like the legacy environments, you can create a local next-gen cluster for testing your services before deploying them to Agora Lab2 environment.
You can follow [Local v2 environment](./k8s/environments/local/README.md) to start up the local cluster.

## Infrastructure Documents
Documents for managing infrastructures and services provided by Agora infra an service teams are available at [docs](./docs). Runbooks at [docs/runbooks](./docs/runbooks) serve as guidelines when you operate on each service. They include instruction and well-known issues. If you would like to share steps to set up a service, a guideline for fixing bug, steps to upgrade a service, etc, everyone is welcome to write documents here.

## Useful Links for Infra team
- [TN-0119 CityOS Cluster RBAC Model](https://docs.google.com/document/d/1W4IlG94MFOxLcZVwdwJJsj90AW2n4LZOxhO0mUET0fE/edit#heading=h.5qm13wuvtiz9)
- [TN-0156 Agora Kubernetes platform Gen 2 requirements](https://docs.google.com/document/d/1JwShhmE9GthB2TYb8ae7sPieecUKeWxOF77Ln35Y2f8/edit#heading=h.5qm13wuvtiz9)
- [TN-0208 Secure CI Pipeline](https://docs.google.com/document/d/1emnpST0-LfTue7eht3hqF3YwMuhLAuc37HO_-cCdrjw/edit)
- [TN-0247 Azure AD Automation on the Infra Protected CI Pipeline](https://docs.google.com/document/d/1XyLXG6b4PJIpZZkqZ4fYCe_NrFNCoNkWpHEoVPAVqNM/edit)
- [TN-0275 Monorepo Project M&M](https://docs.google.com/document/d/1KBvyzHHnHxFLqtvjrIwCutKNMUUF6_CyzaS6zhmHrQA/edit)
- [TN-0295 Serverless on Agora (Lambdas)](https://docs.google.com/document/d/1nrK_oFCBaHkJ-Z9UB7D4KwZAwLl79RWDYMO6G45jer4/edit#heading=h.mk1gkt19szgd)
