# Kubectl setup

Once the setup is completed, you have a read-only view of your tenant, and one of the ways to interact with your tenant is through [kubectl](https://kubernetes.io/docs/reference/kubectl/).
We have [Minikube 101](/catalog/default/component/minikube-tutorial) and [Namespace 101](/catalog/default/component/namespace-tutorial) that covers how to use kubectl if you are unfamiliar with this tool, but in this document, we'll focus on getting access to the tenant.

Make sure the following tools are installed:

* [kubectl](https://kubernetes.io/docs/tasks/tools/#kubectl)
* [kubelogin](https://github.com/Azure/kubelogin)

Add a user config for Agora Dev to your `~/.kube/config` using the following commands:

```sh
cat <<EOF > /tmp/kubeconfig
apiVersion: v1
kind: Config
current-context: dev
clusters:
  - cluster:
      certificate-authority-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUM1ekNDQWMrZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBREFWTVJNd0VRWURWUVFERXdwcmRXSmwKY201bGRHVnpNQjRYRFRJeU1ETXdPVEEyTWpNME9Wb1hEVE15TURNd05qQTJNak0wT1Zvd0ZURVRNQkVHQTFVRQpBeE1LYTNWaVpYSnVaWFJsY3pDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBTHZ4CnFjRVFZcVZYUVpBWmswYUVPbUZaOE05bW5GRXNLQVlJekRTK2xyaFhWMGtNYmZBKzBzZWNVcm1TSVdJbzk4VGQKZThDMVV5R0kzV2NhQW9tUFhMdzdQSDcvOERIZk9HeUcvbGZmRDNyQXhSeWJyUHRjZEdFckc2L0xVdkdRSzRmaApZY0Q4Z2s0UnhpdlRRNWlod1k1VVU2b2VkWm9BMGxOSlNhZmthU0hNUGMwMDJwL05BUThiY1NsSS9EbXIrdkVuCnRHY1g4djBzeTdsRDBoa3Q2eEtCeXdYMG9KZ2hzKzIzRzJqbm1wcjJJNk5VRW43K2VZcGlBNUdPbldBcS81SVcKbVdPcU85WDFWWjNxUE01cVhVODNoUWwxUitZci9vVitXQkE0VHVFb043R1d1Z09PaG8wME5PZVFkOFNvRHozaApGQ0JEVDhrUHhCMnFXeDhMNzVrQ0F3RUFBYU5DTUVBd0RnWURWUjBQQVFIL0JBUURBZ0trTUE4R0ExVWRFd0VCCi93UUZNQU1CQWY4d0hRWURWUjBPQkJZRUZDUlZtZlQyZzBvWnk2dVcvdnd5d0ZoczloWmpNQTBHQ1NxR1NJYjMKRFFFQkN3VUFBNElCQVFCUFpkQTJKR1g0OVdpTkVMN2JTdXhWbFphems1YUtKZEc1TDJydmcwUUFuekMxUCtieApWYm9aemJPVU5nZlhtUFlWQ0toTUVldld4b29XWnpFaHU4OXBmSGdUbGovZ1Y5WjlZSHpLeDJMSHIvcTBJcGhWCm16aFF5WFhqMGhYOG9wcUtjWjdBR28wRmVwNzJVYm1odzViVEV3RVNXNXZjRm5Tek5Ub2xvNnluU0VpRVRmTDgKWDg2QWtpVFNxV0RVR0JyRlhOZnc2cGJEMC9IVEVCZWhoZ2M0dDl3N0xxRGJaYkZMUmZHUEhCU1plNENoWXcxTQp3NFhJd2xPK0d3M3ZFYUdpaTVOTG5NeGV3bC9uekg2Qlg1VGJhZFpaalEwL2FIdzJ2RUk3Sno1MW1Zdk9HRWtGCkJyb0szdjhvd25hOGU3QTZaNGd3c1R3MmY5L0VNZ0xpMHNmRgotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==
      server: https://D787076D86DBFBA2A25A04BBB4FDB483.gr7.ap-northeast-1.eks.amazonaws.com
    name: dev
contexts:
  - context:
        cluster: dev
        user: cityos-dev-aad-login
    name: dev
users:
  - name: cityos-dev-aad-login
    user:
        exec:
            apiVersion: client.authentication.k8s.io/v1beta1
            args:
              - get-token
              - --environment
              - AzurePublicCloud
              - --server-id
              - f3570a5e-edb2-4e67-9fe9-a92cbc267c6c
              - --client-id
              - f3570a5e-edb2-4e67-9fe9-a92cbc267c6c
              - --tenant-id
              - 8c31bd04-5a9a-4aa1-8bfa-c1edfe26a6a0
            command: kubelogin
            env: null
            provideClusterInfo: false
EOF
```

```sh
export KUBECONFIG=${HOME}/.kube/config:/tmp/kubeconfig
mkdir -p ~/.kube
kubectl config view --flatten > ~/.kube/config.new
mv ~/.kube/config ~/.kube/config.bak
mv ~/.kube/config.new ~/.kube/config
```

!!! Tip "Tabbed Contents"
    `kubectl` has a concept of [context](https://kubernetes.io/docs/tasks/access-application-cluster/configure-access-multiple-clusters/). The above steps configured a new kubectl context `dev` to interact with your dev Agora tenant.

    You can use `kubectl` that you already installed or [kubectx](https://github.com/ahmetb/kubectx) to switch context to `dev`.

    === "Using kubectl"

        ```sh
        # Check list of contexts and your current context
        kubectl config get-contexts

        # Switch to `dev`
        kubectl config use-context dev
        ```

    === "Using kubectx"

        (In a new shell sessions)

        ```sh
        # Check list of contexts and your current context
        kubectx

        # Switch to `dev`
        kubectx dev
        ```

If you're not in the office network, make sure that you're using imras.ts.tri-ad.global as a portal for the company VPN. You can configure it in the settings of GlobalProtect under the General tab.

Make an api request with kubectl in order to log in for the first time. Make sure to replace `<namespace>` with the namespace (=tenant name) of your project.

If prompted, follow it to nagivate to the auth page and enter the code.

```
$ kubectl get sa -n <namespace>
To sign in, use a web browser to open the page https://microsoft.com/devicelogin and enter the code XXXXXXXXX to authenticate.
```

Expected output:
```bash
NAME      SECRETS   AGE
default   1         xxx
```

If you get the expected output in the last step, your setup has been done correctly.

## Congratulations

At this point you should have accomplished the following things:

* Learned how to request tenant creation and member registration.
* Learned how to set up kubectl to interact with your Agora tenant, so you can confirm the onboarding setup is completed successfully.

## Where to Go Next

- [Welcome page](/welcome) to follow through the Agora journey
