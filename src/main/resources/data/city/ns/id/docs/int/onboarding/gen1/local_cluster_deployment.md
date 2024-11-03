# Local Cluster Deployment

## Local Deployment

1. In your EC2 instance, create and go to your own branch.
2. To make sure flux refers to your branch as the source of deployment configuration, change the branch specified in [infrastructure/k8s/local/flux-system/sources/git-city.yaml](/infrastructure/k8s/local/flux-system/sources/git-city.yaml) to your local branch.
    ```yaml
    spec:
      ref:
        branch: <your-branch>
    ```
3. Commit and push your changes to the remote branch.
4. Clone bootstrap script from [anders-pedersen/feature/bootstrap-linux](https://github.com/wp-wcm/city/tree/anders-pedersen/feature/bootstrap-linux/tools/local-setup) branch.
5. Copy the `bootstrap` script located at `tools/local-setup/bootstrap` to your branch with the same directory path.
6. Go to `tools/local-setup` directory and make the bootstrap script executable.
    ```shell
    chmod +x bootstrap
    ```
7. Run the bootstrap script.
    ```shell
    ./bootstrap
    ```
8. From the bootstrap menu, select `Configuration -->` to set up your environment variables needed for the bootstrap script to run.
   1. Specify `Minikube` in `Install k8s system` field.
9. Go back to the bootstrap main menu, select `Show exports config` and copy the export commands displayed to your shell config(`~/.zshrc` or `~/.bashrc`) (No need to exit the script, you can update the config in new terminal inside your VM).
   <br> Note: You can check the shell installed on your vm specified [here](/infrastructure/terraform/environments/bastion/common.yaml) while requesting the EC2 instance from infra team. Your shell config could be located at `~/.zshrc` or `~/.bashrc`.
   <br> Note: 1. Login to [artifactory](https://artifactory-ha.tri-ad.tech/ui/packages), click `Edit Profile` under your email address at right top corner.
   <br> 2. Generate `API Key` and copy its value to below variable `BST_ARTIFACTORY_TOKEN`.
   1. Update the export variable values copied. 
   2. BST_GITHUB_USERNAME=`<Your github username>`
   3. BST_GITHUB_TOKEN=`<Your github PAT token>`
   4. BST_ARTIFACTORY_USERNAME=`<your email address>`
   5. BST_ARTIFACTORY_TOKEN=`<token created on artifactory>`
   6. BST_DEFAULT_WORKDIR=`DIR where repo is clonned`
   7. Add below two as well, update the value based on how many resources you want to allocate:
      1. export MINIKUBE_CPUS=6; 
      2. export MINIKUBE_MEMORY=16000;
   8. Load the config file.
10. From the bootstrap main menu, select `Full Bootstrap` to configure your environment based on environment variables set up in the previous step.
11. We want to use the packages pre-installed by bazel which was installed when running the full bootstrap. Therefore, before going to the next step, make sure `kubectl` used is the one inside your `~/.local/bin`. From anywhere inside the `city` folder, run this command.
    ```shell
    which kubectl
    ```
    If it points to `kubectl` in the root bin, prioritize this path `~/.local/bin` when searching for packages. Put this export command in your shell config.
    ```shell
    export PATH="$HOME/.local/bin:$PATH";
    ```
12. Finally, from the bootstrap menu, select `Spin up environment` to start the deployment of the local cluster.
13. Congratulations 🎉 You are done with the deployment to the local cluster.
14. [Confirmation] Check if flux is tracking and picking up changes in your branch.
    ```shell
    flux get sources git city
    ```
   Note: The `REVISION` must point to your branch's commit.
   FAQ: If it does not point to your branch, follow suggestions in FAQ doc [here](/ns/id/docs/int/troubleshooting_and_faq.md).

## Commands

Here are some useful commands to get started with.

| Command                                | Description                                                                                                                                                                                                                      |
|----------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `kubectl config get-contexts`          | To get the current context. It should display asterisk (*) in `minikube` indicating it is the current context.                                                                                                                   |
| `flux get kustomizations`              | To get the kustomizations running in the current context. The `REVISION` column indicates the branch and hash commit applied by flux. To make sure the cluster is ready, wait for each kustomization to be ready (`READY=true`). |
| `kubectl get pods -A`                  | To get all pods from all namespaces. The `NAMESPACE` column indicates the namespace which each pod belongs to.                                                                                                                   |
| `kubectl get -n <namespace> pods`      | To get all pods that belong to a certain namespace.                                                                                                                                                                              |
| `kubectl get -n <namespace> pod <pod>` | To get a pod that belongs to a namespace. Append `-o yaml` at the end to get the pod's configuration file.                                                                                                                       |
| `kubectl get -n <namespace> services`  | To get all services that belong to a namespace.                                                                                                                                                                                  |
| `kubectl logs -n <namespace> <pod>`    | To get logs of a pod that belong to a namespace. Append `\| bunyan` at the end to beautify the log. Make sure that you have [bunyan](https://github.com/LukeMathWalker/bunyan/releases) installed in your EC2 instance.          |

## What's Next

* [Local cluster access](local_cluster_access.md)
* [New image deployment](new_image_deployment.md)
* [Help while development](/ns/id/docs/int/onboarding/development.md)
* [Troubleshooting and FAQ](/ns/id/docs/int/troubleshooting_and_faq.md)
