# AWS Load Balancer Controller
==============================

```
curl -L https://github.com/kubernetes-sigs/aws-load-balancer-controller/releases/download/v2.4.6/v2_4_6_full.yaml | yamlfmt - | kubectl slice -o ./
kustomize create --autodetect
yamlfmt kustomization.yaml
sed -Ei 's/kube-system/toolings/' *.yaml
sed -Ei 's/your-cluster-name/YOU_FORGOT_TO_PATCH_CLUSTER_NAME' *.yaml
```
