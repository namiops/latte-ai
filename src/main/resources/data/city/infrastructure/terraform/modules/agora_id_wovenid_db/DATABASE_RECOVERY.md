# Database Recovery

Database recovery is the process of recovering or re-establishing a database to a previous state by using a backup. 

## Strategy

1. Provision new aurora cluster with the latest SAFE snapshot(the snapshot should not be broken)
2. Switch the target to the new cluster on the RDS proxy

## Context

You have already provisioned the broken database cluster like this.
Let's say you have main.tf in `terraform/environments/prod/accounts/platform-internal/id`.

```tf

module "broken_db" {
  source = "path/to/agora_id_wovenid_db/db"
  network = {
    ...
  }
  db = {
    ...
  }
}

module "db_proxy" {
  source = "path/to/agora_id_wovenid_db/proxy"
  network = {
    ...
  }
  db = {
    cluster_identifier = module.broken_db.cluster_identifier
    secret_arn         = module.broken_db.secret_arn
  }
}

```

## Operation

### You will submit the PR with the changes on main.tf

```tf

module "broken_db" {
  source = "path/to/agora_id_wovenid_db/db"
  network = {
    ...
  }
  db = {
    cluster_identifier = "wovenid-db-cluster-broken"
    ...
  }
}

+ data "aws_db_cluster_snapshot" "latest_snapshot" {
+   db_cluster_identifier = module.broken_db.cluster_identifier
+   most_recent           = true
+ }
+ 
+ module "new_db" {
+   source = "path/to/agora_id_wovenid_db/db"
+   network = {
+     ...
+   }
+   db = {
+     cluster_identifier = "wovenid-db-cluster-recovery"
+     snapshot_identifier = data.aws_db_cluster_snapshot.latest_snapshot.id
+     ...
+   }
+ }

module "db_proxy" {
  source = "path/to/agora_id_wovenid_db/proxy"
  network = {
    ...
  }
  db = {
-    cluster_identifier = "wovenid-db-cluster-broken"
+    cluster_identifier = "wovenid-db-cluster-recovery"
-    secret_arn         = module.broken_db.secret_arn
+    secret_arn         = module.new_db.secret_arn
  }
}

```

Please add the outputs of `terragrunt plan` or `terraform plan` to the PR description.

Run `terragrunt apply` or `terraform apply` after receving the approval from the relevant code owners.
