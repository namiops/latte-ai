# Data Sourcing Sample

## Database Connection

In order to connect this sample application to the database in OPR plane, apply secrets to your cluster.
The below is an example.


```secrets.yaml
apiVersion: v1
kind: Secret
metadata:
  name: dsc-opr-database
type: Opaque
data:
  db_host: ********
  db_port: ********
  db_user: ********
  db_password: ********
  db_name: ********
```


```sh
kubectl apply -f ./secrets.yaml
```