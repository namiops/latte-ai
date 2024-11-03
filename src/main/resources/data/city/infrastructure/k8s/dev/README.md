CityOS Dev Environment
======================

## Kubectl Setup

This environment is integrated with Woven Planet's Azure Active Directory via
an Azure AD Oauth App.

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

Merge [kubeconfig](kubeconfig) to your `~/.kube/config` using the following commands:

```sh
# Move to the directory this README.md is placed
cd <...>/dev
export KUBECONFIG=${HOME}/.kube/config:$(pwd)/kubeconfig
mkdir -p ~/.kube
kubectl config view --flatten > ~/.kube/config.new
mv ~/.kube/config ~/.kube/config.bak
mv ~/.kube/config.new ~/.kube/config
```

Switch to the `dev` context

Using kubectl
```sh
kubectl config use-context dev
```

or

Using kubectx
```sh
kubectx dev
```

If you're not in the office network, make sure that you're using
`imras.ts.tri-ad.global` as a gateway for the company VPN. You can configure it
in the settings of GlobalProtect.

Make an api request with kubectl in order to log in for the first time. Make
sure to replace `<namespace>` with the namespace of your project.

Expected output:
```bash
$ kubectl get sa -n <namespace>
NAME      SECRETS   AGE
default   1         31d
```
