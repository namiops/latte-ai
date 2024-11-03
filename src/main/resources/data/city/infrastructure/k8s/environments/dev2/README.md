CityOS Dev2 Environment
=======================

<!-- vim-markdown-toc GFM -->

- [CityOS Dev2 Environment](#cityos-dev2-environment)
    - [Kubectl Setup](#kubectl-setup)
    - [Sudo Access](#sudo-access)

<!-- vim-markdown-toc -->

### Kubectl Setup

This environment is integrated with Woven Planet's Azure Active Directory via an Azure AD Oauth App.
In order to get kube api access you must request access from the CityOS team.
Make sure the following tools are installed:

* [kubectl](https://kubernetes.io/docs/tasks/tools/#kubectl)
* [kubelogin](https://github.com/Azure/kubelogin) *(You won't use this command directly in this set up process but it's used behind the scene)*
  * Do not use Snap to install kubelogin as it will not be able to resolve DNS properly ([issue](https://github.com/Azure/kubelogin/issues/231))
  * If you are using Mac, you may run  `brew install Azure/kubelogin/kubelogin`
  * Verify your kubelogin version format with below, by running `kubelogin --version`

```sh
kubelogin version
git hash: v0.1.4/aed62b0077827211ca2e6f7422281f34e4221e98
Go version: go1.21.11
Build time: 2024-07-05T19:50:24Z
Platform: darwin/arm64
```

Create and merge the [kubeconfig](https://kubernetes.io/docs/concepts/configuration/organize-cluster-access-kubeconfig) for Dev2 to your `~/.kube/config` using the following commands:

```bash
# Move to the directory this README.md is placed
export KUBECONFIG=${HOME}/.kube/config
export KUBECONFIG=${KUBECONFIG}:$(pwd)/clusters/mgmt-east/kubeconfig.yaml
export KUBECONFIG=${KUBECONFIG}:$(pwd)/clusters/mgmt-west/kubeconfig.yaml
export KUBECONFIG=${KUBECONFIG}:$(pwd)/clusters/worker1-east/kubeconfig.yaml
export KUBECONFIG=${KUBECONFIG}:$(pwd)/clusters/worker1-west/kubeconfig.yaml
mkdir -p ~/.kube
kubectl config view --flatten > ~/.kube/config.new
mv ~/.kube/config ~/.kube/config.bak
mv ~/.kube/config.new ~/.kube/config
export KUBECONFIG=${HOME}/.kube/config
```

There will be three additional clusters on the contexts:

* `dev2-mgmt-east`
* `dev2-mgmt-west`
* `dev2-worker1-east`
* `dev2-worker1-west`

You can confirm if new contexts have been added using the following command 

```bash
$ kubectl config get-contexts
```

Switch to the `dev2-mgmt-east` context

Using kubectl

```bash
$ kubectl config use-context dev2-mgmt-east
```

or

Using kubectx

```bash
$ kubectx dev2-mgmt-east
```

If you're not in the office network, make sure that you're using `imras.ts.tri-ad.global` as a gateway for the company VPN. You can configure it in the settings of GlobalProtect.

Make an api request with kubectl in order to log in for the first time. Make sure to replace `<namespace>` with the namespace of your project.

```bash
$ kubectl get sa -n <namespace>
To sign in, use a web browser to open the page https://microsoft.com/devicelogin and enter the code XXXXXXXXX to authenticate.
```

Expected output:

```bash
$ kubectl get sa -n <namespace>
NAME      SECRETS   AGE
default   1         31d
```

### Sudo Access

In this environment, [CityOS platform engineer members](../../../terraform/accounts/835215587209/aad/config.auto.tfvars.json) can perform cluster admin impersonation.

```bash
$ kubectl --as sudo --as-group aad:<resourceName> auth can-i list <pods|services|etc> -n <namespace>
yes
```

This behavior is defined in `./clusters/<cluster-name>/kube-system/rbac/`[clusterrolebinding-cityos-platform-impersonate-admin.yaml](clusters/mgmt-east/kube-system/rbac/clusterrolebinding-cityos-platform-impersonate-admin.yaml).
