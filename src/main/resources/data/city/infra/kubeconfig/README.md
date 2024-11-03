## vClusters kubeconfig files

### Requirements

You must have the below installed.

* [kubectl](https://kubernetes.io/docs/tasks/tools/#kubectl)

* [pinniped](https://get.pinniped.dev/cli) command-line tool

---

* Download the relevant kubeconfig file

* Set your environment variable `KUBECONFIG`

```bash
export KUBECONFIG=/path/speedway-x-vcluster-kubeconfig.yaml
```

* Then run a `kubectl` command

> [!IMPORTANT]
> If you are running this on a computer without graphical interface (for instance
> your EC2 instance) AND you have a terminal browser installed (e.g.  `lynx`),
> `kubectl` may will attempt to load the URL with that browser (which likely most
> likely can't be used to get any authorization code because of lack of JavaScript
> support). Therefore it is recommended to unset the `BROWSER` environment
> variable or set it to something safe while calling `kubectl` (e.g.
> `BROWSER=echo kubectl version`).

* You will then be prompted with something similar to the below where you can sign in via your browser

```bash
Log in by visiting this link:

    https://login.microsoftonline.com/00000000-0000-0000-0000-000000000000/oauth2/v2.0/authorize?access_type=offline&client_id=00000000-0000-0000-0000-000000000000&code_challenge=8OGdXXAVho5VASYT81bgiUVJVtDocI38Nsfm7ZzyAyI&code_challenge_method=S256&nonce=fafeec59892713cbdf4b95fe2cd59329&redirect_uri=http%3A%2F%2F127.0.0.1%3A23456%2Fcallback&response_mode=form_post&response_type=code&scope=email+offline_access+openid&state=a9e30a7330bdf790b0bdf4b3bc41ff21

    Optionally, paste your authorization code:
```

Once you have successfully logged in, you should see `you have been logged in and may now close this tab` in your browser and you now have access to the relevant cluster and to your team's namespaces via `kubectl`

> [!IMPORTANT]
> If you are using `kubectl` on one computer (e.g., your EC2 instance) and opening
> the link on a browser on a different computer (e.g., your laptop), your browser
> is probably going to fail on a URL like `http://127.0.0.1:12345/callback`.
> One possible solution to this is to create an SSH tunnel for redirecting
> whatever the computer where your browser is running back to the computer where
> your `kubectl` is running. You can create this tunnel by running the following
> on the browser computer:
>
> ```bash
> ssh -L 127.0.0.1:12345:localhost:12345 YOUR_USER@YOUR_KUBECTL_MACHINE
> ```
>
> You can close the SSH shell as soon as the login is successful.

---

### Troubleshooting

If you get the below output when attempting to log in:
```bash
Error: could not complete Pinniped login: failed to exchange token: unexpected HTTP response status 400
Unable to connect to the server: getting credentials: exec: executable pinniped failed with exit code 1
```

Please remove `~/.config/pinniped` and try to log in again ([issue](https://github.com/vmware-tanzu/pinniped/issues/774)).

---

Please reach out to us via [#wcm-org-agora-infra](https://app.slack.com/client/E01JD7LPQTB/C02USLDU1U3) if you have any questions, issues or you're not able to access your team's namespaces via `kubectl`.
