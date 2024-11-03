# RDS Cluster Instances Scale-up

## Scale-up Strategy

### Update Instances One by One
Modify the `instance_class` for each instance individually, and apply the changes sequentially with a delay or interval between each update.

### Sequential Updates
Only one instance is updated at a time, while the others continue to serve traffic. This reduces the risk of a complete outage and maintains database availability.

### Failover Capabilities
Aurora clusters are designed for high availability, with automatic failover between instances. If you update a single instance at a time, the cluster can promote a replica to be the primary instance if needed, maintaining continuity.

### Load Balancing
During the update, the remaining instances can handle the workload. If your cluster is properly configured with read replicas and balanced workloads, the impact on performance should be minimal.


## 1. Before Scale-up

Let's say you have 3 instances that you want to scale-up by modifing `instance_class` from `db.t3.medium` to `db.t3.large` for each.


```tf
module "agora_id_wovenid_db" {
  source = "/path-to/modules/agora_id_wovenid_db"
  network = {
    ...
  }
  db = {
    instances = [
      {
        instance_class = "db.t3.medium"
        identifier = "wovenid-db-0"
      },
      {
        instance_class = "db.t3.medium"
        identifier = "wovenid-db-1"
      },
      {
        instance_class = "db.t3.medium"
        identifier = "wovenid-db-2"
      }
    ]
    cluster_parameter = {
      force_ssl = "1"
    }
  }
  vault = {
    ...
  }
}
```

## 2. Scale-up for wovenid-db-0

Modify `instance_class` of `wovenid-db-0`

```diff
module "agora_id_wovenid_db" {
  source = "/path-to/modules/agora_id_wovenid_db"
  network = {
    ...
  }
  db = {
    instances = [
      {
-       instance_class = "db.t3.medium"
+       instance_class = "db.t3.large"
        identifier = "wovenid-db-0"
      },
      {
        instance_class = "db.t3.medium"
        identifier = "wovenid-db-1"
      },
      {
        instance_class = "db.t3.medium"
        identifier = "wovenid-db-2"
      }
    ]
    cluster_parameter = {
      force_ssl = "1"
    }
  }
  vault = {
    ...
  }
}
```

Check the changes

```shell
tf plan
```

Apply the changes

```shell
tf apply
```

## 3. Scale-up for wovenid-db-1 and wovenid-db-2 sequentially

Follow Step2 to update the rest of the instances sequentially


