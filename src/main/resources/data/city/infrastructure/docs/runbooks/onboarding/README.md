# Agora Infra Onboarding

## Dev setup

Please follow the doc about Bazel installation first [Installing Bazel](../../../../docs/development/bazel/README.md) 

Then when you got Bazel installed you should follow the doc [Installing k8s tools](../../../../tools/k8s-tools/README.md)

## AWS setup

There is AWS link - https://woven.awsapps.com/start#/

You should have access to all accounts listed in [Accounts usage](../../../terraform/README.md). You could check what they are for in the doc.

## GitHub setup

There are 3 Github repositories you should have access to:

* https://github.com/wp-wcm/city - Brand-new Git which is Mono-Repo, for Agora Infra Team activities,
you probably use the 2 folders below
    - infrastructure 
    - infra
* https://github.tri-ad.tech/cityos-platform/EKS-provision-configuration/ - Legacy Git for EKS old Environments
* https://github.com/wp-wcm/protected-workflows - Protected workflows for Agora Infrastructure. You can see logs from protected CI jobs (ex. Terraform jobs) here.
## Useful Links Access

Go to below links and check if you have access or not. If you don't have access, 
ask Agora Infra Team Management to give you permissions.

* Vault - https://dev.vault.tmc-stargate.com/ui/vault/secrets (One for all ENVs)
* Grafana & Loki 
  - https://observability.cityos-dev.woven-planet.tech/grafana (DEV)
  - https://observability.agora-lab.woven-planet.tech/grafana (LAB)
  - https://observability.agora-ci.woven-planet.tech (CI)
  - https://athena.agora-lab.w3n.io (LAB2)
* Wiz - https://app.wiz.io/login
* AWS Legacy CI/CD Pipeline - https://concourse.tri-ad.tech/
* Monday Board (Agora Infra Tasks) - https://wovencity.monday.com/boards/3891277781/views/88312847 
* Agora Infra Consultations - https://app.gather.town/app/k27bKnjmMJcHxV5H/Agora
