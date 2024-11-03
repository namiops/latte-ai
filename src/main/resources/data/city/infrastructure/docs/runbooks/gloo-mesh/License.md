## Update License

- Get the license from lastpass
- Update the license in management cluster

```bash
export GLOO_GATEWAY="abc123"
export GLOO_MESH="abc123"
export CONTEXT="lab2-mgmt-east"

kubectl apply secret generic license-keys \
  -n gloo-mesh \
  --context $CONTEXT \
  --from-literal=gloo-gateway-license-key="$GLOO_GATEWAY" \
  --from-literal=gloo-mesh-license-key="$GLOO_MESH"
```
