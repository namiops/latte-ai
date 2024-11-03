# Local Cluster Deployment

## Local Deployment

1.  In your EC2 instance, create a new branch on the `city` repository.
2.  If you would like to use the current branch for deployment, you can skip this step. Otherwise, please write 
    the name of the desired branch to the file `ns/id/utils/local/.argocd-branch-name.txt`
3.  Commit and push your changes to the remote branch.
4.  Configure these environment variables in your shell config (`~/.zshrc` or `~/.bashrc`).
    <br> Note: You can check the shell installed on your vm specified [here](/infrastructure/terraform/environments/bastion/common.yaml) while requesting the EC2 instance from infra team. Your shell config could be located at `~/.zshrc` or `~/.bashrc`.
    1. GITHUB_USERNAME=`<Your github username>`
    2. GITHUB_TOKEN=`<Your github personal access token>`
5.  Run this command to reload your shell config:
    ```shell
    source ~/.zshrc
    # or
    source ~/.bashrc
    ```
6.  Run the bootstrap script from anywhere in the `city` repository.
    ```shell
    $(bazel info workspace)/ns/id/utils/local/local-speedway.sh
    ```
7.  Congratulations 🎉 You are done with the deployment to the local cluster.
    To check the progress of the deployment you need to access https://argocd.woven-city.local/ from your browser.
    Please follow the steps available [here](local_cluster_access.md).
8.  After you finish with the testing, run the following command to delete the local cluster:
    ```shell
    kind delete clusters agora-speedway-local
    ```

## Commands

Here are some useful commands to get started with.

| Command                                | Description                                                                                                                                                                                                                      |
|----------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `kubectl config get-contexts`          | To get the current context. It should display asterisk (*) in `kind-agora-speedway-local` indicating it is the current context.                                                                                                                   |
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
