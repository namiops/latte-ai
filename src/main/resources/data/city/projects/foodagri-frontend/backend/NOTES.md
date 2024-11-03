1. Creating secret from env file

```bash
kubectl create secret generic  aws-creds --from-env-file=.env.secret
```
