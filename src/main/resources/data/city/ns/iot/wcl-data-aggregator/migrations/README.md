### DB Migrations

```sh
kubectl config use-context <cluster>
password=`kubectl get secret -n iot fss-events-wcl-poc-pguser-events -ojson | jq -r .data.password | base64 -d`
kubectl port-forward <pg-leader-pod> -n iot 5432:5432 # check logs to find out the leader
psql --host=localhost --port=5432 -U events -d events -f /path/to/migrations/01_add_created_at.sql # password will be prompted
```
