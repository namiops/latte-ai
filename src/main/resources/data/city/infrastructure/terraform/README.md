# List of AWS Accounts

* [List of AWS Accounts](#list-of-aws-accounts)
  * [Legacy Accounts](#legacy-accounts)
  * [Bastion hosts](#bastion-hosts)
  * [lab2](#lab2)
  * [dev2](#dev2)
  * [prod](#prod)

## Legacy Accounts

| Env. | Account ID   | Account Name                 | Purpose                            | Terraform code                                   | EKS OpenID Connect provider URL                                                   |
| ---- | ------------ | ---------------------------- | ---------------------------------- | ------------------------------------------------ | --------------------------------------------------------------------------------- |
| ci   | 093116320723 | services-default-dev-KPgvGid | Cluster to host ci related service | [accounts/093116320723](./accounts/093116320723) | https://oidc.eks.ap-northeast-1.amazonaws.com/id/EC3F12B7F55F1F1BB65A2BF88088FCC1 |
| lab  | 370564492268 | services-default-dev-Z278lss | Agora lab environment              | [accounts/370564492268](./accounts/370564492268) | https://oidc.eks.ap-northeast-1.amazonaws.com/id/B90F0B0CEE92F6F9416AAC091567BE24 |
| dev  | 835215587209 | services-default-dev-Kosk7HY | Agora dev environment              | [accounts/835215587209](./accounts/835215587209) | https://oidc.eks.ap-northeast-1.amazonaws.com/id/D787076D86DBFBA2A25A04BBB4FDB483 |

## Bastion hosts

| Account ID   | Account Name       | Purpose       | Terraform code                                 | EKS OpenID Connect provider URL |
| ------------ | ------------------ | ------------- | ---------------------------------------------- | ------------------------------- |
| 028081328740 | ag-bh-lab2-nKr4UIZ | Bastion hosts | [environments/bastion](./environments/bastion) | N/A                             |

## lab2

| Account ID   | Account Name                     | Region         | Purpose                                      | EKS OpenID Connect provider URL                                                   |
| ------------ | -------------------------------- | -------------- | -------------------------------------------- | --------------------------------------------------------------------------------- |
| 716975162005 | ag-infradev-qiWMRsA              | ap-northeast-1 | Transit gateway for traffic between clusters | N/A                                                                               |
| 303349233740 | agora-ng-t1-FYf3i90              | ap-northeast-1 | GlooMesh management east cluster             | https://oidc.eks.ap-northeast-1.amazonaws.com/id/705DC20C6A45D3B94B427D0C5F97F919 |
| 936984728316 | lab2-mgmt-west-COSaPg0           | ap-northeast-3 | GlooMesh management west cluster             | https://oidc.eks.ap-northeast-3.amazonaws.com/id/AB8C6849C7DFF457F5EE8C754BDF9ADA |
| 726643106163 | lab2-ng-w1e-pHgwdU0              | ap-northeast-1 | GlooMesh worker1 east cluster                | https://oidc.eks.ap-northeast-1.amazonaws.com/id/BB10AEB9816CBAA448049007EE602E19 |
| 479064346756 | lab2-ng-w1w-rxWz8iH              | ap-northeast-3 | GlooMesh worker1 west cluster                | https://oidc.eks.ap-northeast-3.amazonaws.com/id/1375F995FD28A053A41264B71C41D82C |
| 892017347218 | agora-lab2-buckets1-east-Vd4mXUn | ap-northeast-1 | Agora Storage owned account                  | N/A                                                                               |

The Terraform code for all accounts is under [environments/lab2](./environments/lab2)

## dev2

| Account ID   | Account Name                     | Region         | Purpose                                      | EKS OpenID Connect provider URL                                                   |
| ------------ | -------------------------------- | -------------- | -------------------------------------------- | --------------------------------------------------------------------------------- |
| 712742136960 | agora-dev2-transit-JTZH3LD       | ap-northeast-1 | Transit gateway for traffic between clusters | N/A                                                                               |
| 414060382968 | agora-dev2-mgmt-east-z0nJHzO     | ap-northeast-1 | GlooMesh management east cluster             | https://oidc.eks.ap-northeast-1.amazonaws.com/id/75D4E33465656F705F4175A4F9D7E03C |
| 390341593940 | agora-dev2-mgmt-west-Wg4hwq3     | ap-northeast-3 | GlooMesh management west cluster             | https://oidc.eks.ap-northeast-3.amazonaws.com/id/CDDB2625B1A305AAAB77475359481070 |
| 074769536177 | agora-dev2-worker1-east-gdu08tO  | ap-northeast-1 | GlooMesh worker1 east cluster                | https://oidc.eks.ap-northeast-1.amazonaws.com/id/6650625D283964F0BAC2BC1AE56FE0E9 |
| 822968620977 | agora-dev2-worker1-west-QP5nRSD  | ap-northeast-3 | GlooMesh worker1 west cluster                | https://oidc.eks.ap-northeast-3.amazonaws.com/id/B113C0BC8C0031C8995FA64C78CF1E90 |
| 459197079065 | agora-dev2-mlops1-east-mcXRWC3   | ap-northeast-1 | MLOps1 east cluster                          | https://oidc.eks.ap-northeast-1.amazonaws.com/id/D33E8136E42754BB9B1D5DC616E80A3B |
| 604625330049 | agora-dev2-mlops1-west-PeFG6Ed   | ap-northeast-3 | MLOps1 west cluster                          | N/A                                                                               |
| 975049951479 | agora-dev2-ci-east-YGbL7QC       | ap-northeast-1 | CI east cluster                              | https://oidc.eks.ap-northeast-1.amazonaws.com/id/FD92B4CE1D7B3B9C824726BA0779A0AA |
| 471112803776 | agora-dev2-buckets1-east-DnYaItH | ap-northeast-1 | Agora Storage owned account                  | N/A                                                                               |

The Terraform code for all accounts is under [environments/dev2](./environments/dev2)

## prod

| Account ID   | Account Name                           | Region         | Purpose                                                 | Owners               |
| ------------ | -------------------------------------- | -------------- | ------------------------------------------------------- | -------------------- |
| 533267018370 | agora-prod-transit-AabsneC             | ap-northeast-1 | Transit gateway and Terraform states of all accounts    | Infra                |
| 975050319137 | agora-aws-marketplace-services-iJdfaBo | ap-northeast-1 | AWS marketplace services                                | Infra                |
| 058264466652 | agora-prod-storage-valet-owh2pxw       | ap-northeast-1 | Storage and data services                               | Data                 |
| 905418299040 | agora-prod-mlops1-east-9U1kDus         | ap-northeast-1 | MLOps1 east cluster                                     | Infra                |
| 211125476557 | agora-prod-platform-internal-qJ0rMuw   | ap-northeast-1 | Agora internal services                                 | Service and Identity |
| 713881797477 | agora-prod-direct-connect-HhqjAQ9      | ap-northeast-1 | Integration with Direct Connect from Woven City Network | Infra                |


- The Terraform code for all accounts is under [environments/prod](./environments/prod)
- To get access to each account, add your email to a list in [environments/prod/bootstrap/aws_sso/config.yaml](./environments/prod/bootstrap/aws_sso/config.yaml)
