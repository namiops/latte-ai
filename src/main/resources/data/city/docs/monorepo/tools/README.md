# Developer Tooling

For most services, we will be using the following tools, so please install them:
* [aws-cli](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html)
* [bazel](https://docs.bazel.build/versions/main/install.html)
* [docker](https://docs.docker.com/get-docker/)
* [kubectl](https://kubernetes.io/docs/tasks/tools/)
    * You may also want to install minikube from this link as well

In addition, the following are recommended, but not necessary

* [direnv](https://direnv.net/docs/installation.html)
    * If you are working on multiple services, it is recommended to configure the aws credentials for each service
      under different profiles. You can then configure your environment to automatically switch to each profile when
      you switch to the directory. These profiles are set through the `AWS_PROFILE` in each directory's `.envrc` file.
    * [direnv Setup Guide](./direnv.md)
* [Hashicorp Vault](https://learn.hashicorp.com/tutorials/vault/getting-started-install)
    * We are using Hashicorp Vault as a secrets storage. Naturally, being able to access it locally is important.
      Of course, you can do all of your secrets management from the browser, but the cli is recommended for those
      who prefer it.
    * [Woven Planet Vault](https://dev.vault.w3n.io:8200/ui/vault/auth?with=oidc)
        * Namespace: ns_dev/ns_wcm_infra/
    * [Hashicorp Vault Guide](https://github.tri-ad.tech/information-security/vault-tools/tree/main/guides)
* [jq](https://stedolan.github.io/jq/download/)
    * `jq` is a tool that assists in dealing with JSON format in the command line. Useful in various situations.
    
Depending on which language and which service you are developing, you will need different setups.
Check out each detailed guide here:

* [Go](/docs/development/go/README.md)
* [Java](/docs/java/README.md)
