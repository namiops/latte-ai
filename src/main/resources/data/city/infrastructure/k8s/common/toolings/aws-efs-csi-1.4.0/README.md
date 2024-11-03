## How to generated this

1. Add the helm repo
```bash
helm repo add aws-efs-csi-driver https://kubernetes-sigs.github.io/aws-efs-csi-driver/
```
2. Update  helm  local repo
```bash
helm repo update
```
3. Generate templates
```bash
helm template aws-efs-csi-driver aws-efs-csi-driver/aws-efs-csi-driver --namespace=toolings
```