### How to deploy new karpenter helm chart

```bash
export KARPENTER_VERSION="0.22.1"
export JFROG_TOKEN="123abc"

git clone --depth 1 -b v"$KARPENTER_VERSION" https://github.com/aws/karpenter
cd karpenter/charts
rm *.tgz
unlink karpenter/crds
rm -rf karpenter/crds
tar -czf karpenter-"$KARPENTER_VERSION".tgz karpenter
curl -X PUT -u "vincent.carlos@woven-planet.global:JFROG_TOKEN" \
  -T "karpenter-$KARPENTER_VERSION.tgz" \
  "https://artifactory-ha.tri-ad.tech/artifactory/helm-local/vince/karpenter/karpenter-$KARPENTER_VERSION.tgz"
```
