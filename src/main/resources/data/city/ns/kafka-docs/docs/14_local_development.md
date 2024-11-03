# Kafka client app development

In Lab1/Lab2/Dev1 environment, [Managed Kafka - Amazon Managed Streaming for Apache Kafka (MSK) - AWS](https://aws.amazon.com/msk/?nc1=h_ls) is utilized.
The Kafka connection mode can be set to no TLS/SSL encryption option `PLAINTEXT` because mTLS connection is handled by Istio instead.

## Kafka client app development in Local

### When using FluxCD

If you are not familiar with FluxCD, the following guide might be helpful:
- [English version](https://developer.woven-city.toyota/docs/default/Component/agora-deployment-tutorial/en/03_deployment/)
- [Japanese version](https://developer.woven-city.toyota/docs/default/Component/agora-deployment-tutorial/ja/03_deployment/)

Here are the steps to deploy Kafka in Local using FluxCD: 

- Setup the local flux environment following [the documentation](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/local#prerequisites)
- Edit [the branch name](https://github.com/wp-wcm/city/blob/155bbae53ebb5f68929e479195481a9c0784f90d/infrastructure/k8s/environments/local/clusters/worker1-east/flux-system/sources/git-city.yaml#L9) to your branch name
- Uncomment the following lines to make them Flux targets:
  - [kafka](https://github.com/wp-wcm/city/blob/219f99727928774e7c8ccdd38a1f0668f98035a8/infrastructure/k8s/environments/local/clusters/worker1-east/flux-system/kustomizations/system/kustomization.yaml#L14)
  - [(if necessary) Apicurio schema registry](https://github.com/wp-wcm/city/blob/219f99727928774e7c8ccdd38a1f0668f98035a8/infrastructure/k8s/environments/local/clusters/worker1-east/flux-system/kustomizations/services/kustomization.yaml#L7) 
  -  [(if necessary) Kafka-admin (AKHQ)](https://github.com/wp-wcm/city/blob/219f99727928774e7c8ccdd38a1f0668f98035a8/infrastructure/k8s/environments/local/clusters/worker1-east/flux-system/kustomizations/services/kustomization.yaml#L10)
- Start Flux!
  ```shell
  cd <repo_root>/infrastructure/k8s/environments/local
  bin/bootstrap
  ```

### When not using FluxCD

It might take time to let Flux deploy all the resources because it deploys the resource that is not related to Kafka by default.
If you want to deploy them more quickly, you can deploy them by hand. See the [Local Kafka setup](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/kafka/local-only-0.0.1) for more details.
