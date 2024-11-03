## Karpenter

### Testing and Examples
- [Spinning new node in each AZ](resources/new-node.yaml)
  - Refs: https://github.com/aws/karpenter/issues/2572

### FAQ
#### How does karpenter choose provisioner?
Refs: https://github.com/aws/karpenter/issues/1570
```
The Provisioner spec with name "default" is just an arbitrary name and not treated any differently than other provisioners.
When Pod requirements and constraints can be satisfied by more than one Provisioner spec, Karpenter will sort them alphabetically and choose the first one. 
```

### Errors

#### IAM Credentials for Launching Spot Instances
- Refs: https://github.com/aws/karpenter/issues/504
- Error:
```
"The provided credentials do not have permission to create the service-linked role for EC2 Spot Instances."
```
- Fix: create spot service linked role (in tf)
```
resource "aws_iam_service_linked_role" "spot" {
  aws_service_name = "spot.amazonaws.com"
}
```

#### Duplicate NotReady node object with EC2 resource based naming
- Refs:
  - https://github.com/aws/karpenter/issues/2872
  - https://github.com/aws/karpenter/issues/2892
- Error: Duplicate entry in Kubernetes node list, without fully qualified domain name, that stays as NotReady.
```
NAME                                                  STATUS     ROLES    AGE   VERSION
i-013c547aa6e1b0d81.ap-northeast-1.compute.internal   Ready      <none>   8d    v1.24.7-eks-fb459a0
i-017460c41d67808f5                                   NotReady   <none>   69m
i-017460c41d67808f5.ap-northeast-1.compute.internal   Ready      <none>   69m   v1.24.9-eks-49d8fe8
i-03ece42d095d52c7f                                   NotReady   <none>   69m
i-03ece42d095d52c7f.ap-northeast-1.compute.internal   Ready      <none>   69m   v1.24.9-eks-49d8fe8
i-050b1f8e5775cdcea.ap-northeast-1.compute.internal   Ready      <none>   8d    v1.24.7-eks-fb459a0
i-087e098ae2d69e6c1.ap-northeast-1.compute.internal   Ready      <none>   8d    v1.24.7-eks-fb459a0
i-0f2f8665ae246f1ae                                   NotReady   <none>   68m
i-0f2f8665ae246f1ae.ap-northeast-1.compute.internal   Ready      <none>   67m   v1.24.9-eks-49d8fe8
```
- Fix: Need to set global setting to `aws.settings.nodeNameConvention: ip-name`. As long as subnet has `Resource name DNS A record` enabled.

#### not authorized to perform: pricing:GetProducts
- Refs:
  - https://github.com/aws/karpenter/issues/2172
  - https://toyotaglobal.enterprise.slack.com/files/U02LPHZ84VA/F04MJ34R0JJ/image.png
- Error:
```
ERROR    controller.aws.pricing    updating on-demand pricing, AccessDeniedException: 
User: arn:aws:sts::303349233740:assumed-role/KarpenterIRSA-agora-cluster-sandbox-20230126011124388600000005/1675304008676660289 
is not authorized to perform: pricing:GetProducts with an explicit deny in a service control policy
```
- We can either fix it or ignore this error, because
```
If Karpenter is unable to access the pricing api, it will continue with static pricing data in the binary. 
So should still work fine, but if it's possible to allow it to query the pricing api, that would be best so it can have 
the most up-to-date information on new instance types launches.
```

#### Webhook Errors
- Refs:
  - https://github.com/aws/karpenter/issues/2902
- Error:
```
2023-02-03T02:19:46.783Z        ERROR   webhook.DefaultingWebhook       Reconcile error {"commit": "c4a4efd-dirty", 
"knative.dev/traceid": "0c045462-f54f-425f-a26f-074093f4d6ca", "knative.dev/key": "karpenter/karpenter-cert", 
"duration": "33.864584ms", "error": "failed to update webhook: Operation cannot be fulfilled on 
mutatingwebhookconfigurations.admissionregistration.k8s.io \"defaulting.webhook.karpenter.sh\": the object 
has been modified; please apply your changes to the latest version and try again"}
```
- According to the GH issue, we can ignore this.
