CityOS CI Environment
======================

## Kubectl Setup

This environment is integrated with Woven Planet's Azure Active Directory via an Azure AD Oauth App.

In order to get kube api access you must request access from the CityOS team.

Make sure the following tools are installed:
* [kubectl](https://kubernetes.io/docs/tasks/tools/#kubectl)
* [kubelogin](https://github.com/Azure/kubelogin) -- You won't use this command directly in this set up process but it's used behind the scene.

Merge [kubeconfig](kubeconfig) to your `~/.kube/config` using the following commands:

```
# Move to the directory this README.md is placed
$ cd <...>/ci
$ export KUBECONFIG=${HOME}/.kube/config:$(pwd)/kubeconfig
$ mkdir -p ~/.kube
$ kubectl config view --flatten > ~/.kube/config.new
$ mv ~/.kube/config ~/.kube/config.bak
$ mv ~/.kube/config.new ~/.kube/config
```

Switch to the `ci` context

Using kubectl
```
$ kubectl config use-context ci
```

or

Using kubectx
```
$ kubectx ci
```

If you're not in the office network, make sure that you're using `imras.ts.tri-ad.global` as a gateway for the company VPN. You can configure it in the settings of GlobalProtect.

Make an api request with kubectl in order to log in for the first time. Make sure to replace `<namespace>` with the namespace of your project.

```
$ kubectl get sa -n <namespace>
To sign in, use a web browser to open the page https://microsoft.com/devicelogin and enter the code XXXXXXXXX to authenticate.
```

Expected output:
```bash
$ kubectl get sa -n <namespace>
NAME      SECRETS   AGE
default   1         31d
```
