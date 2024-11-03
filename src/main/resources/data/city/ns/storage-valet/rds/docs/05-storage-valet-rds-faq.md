# Storage Valet RDS - FAQ

This is a space for frequently asked questions about Storage Valet RDS.

#### Question 1: How do I access my provisioned Storage Valet RDS database instance?
When we create a database or table schema in PostgreSQL, how do we access it?
Do other teams access it from the shell of a pod (with a service account) that has access to RDS?

**Answer:**
The RDS offering from Agora storage valet, enables pods from within Speedway (running within SMC cluster) to resolve and access their respective RDS database instances.

Correct, currently teams are deploying a pod, with psql, and connecting from within the cluster (by exec into the pod with has the service account defined in the RDS config file). Or teams are using their application's ORM to build out the database schema.

A note regarding SSL, the connection will be rejected if it is not established using SSL (verify).

Please see [Accessing the RDS instance from your kubernetes pod](./02-storage-valet-rds-quickstart.md#accessing-the-rds-instance-from-your-kubernetes-pod) and [Pod deployment example](./02-storage-valet-rds-quickstart.md#pod-deployment).

---

#### Question 2: Where can I get the RDS database connection details?
Where can I find my RDS database connection string and user credentials?

**Answer:**
Please refer to the notes in
[RDS authentication introduction](./01-storage-valet-rds-introduction.md#rds-authentication),
[Connecting to your rds instance using psql](./02-storage-valet-rds-quickstart.md#connecting-to-your-rds-instance-using-psql) and
[Pod deployment example](./02-storage-valet-rds-quickstart.md#pod-deployment).

---

#### Question 3: I am facing SSL errors when trying to connect to my RDS instance?
When trying to connect to my RDS database using psql I get an SSL error.

**Answer:**
A connection to RDS will be rejected if it is not established using SSL. Please refer to the notes in [RDS connections requires SSL](./02-storage-valet-rds-quickstart.md#rds-connections-requires-ssl).

> ⚠ Note
> If you see the following error from the RDS proxy, your password may not be correct or might have been rotated:
>
> FATAL: This RDS Proxy requires TLS connections (SQLSTATE 28000)

Please also refer to [RDS connection troubleshooting checklist](#question-8-i-am-struggling-to-connect-to-my-rds-instance).

---

#### Question 4: Which version of PostgreSQL is being used?
PostgreSQL is one of the supported Storage Valet RDS database engines. Which version of Postgres is being used?

**Answer:**
Please see [Storage Valet RDS database engines](./01-storage-valet-rds-introduction.md#supported-engines) for the list of supported RDS database engines.

---

#### Question 5: Agora Storage Valet RDS Autoscaling?
Is it correct that auto-scaling (up and down) of CPU, memory, etc. is performed automatically by RDS(AWS) side?
Can we expect storage to scale automatically?

**Answer:**
Currently as part of the user config we provide the ability to define the sizing of the underlying database instance,
by changing the `instanceClass` configuration value
(please see [agoractl rds create](./03-storage-valet-rds-agoractl.md#onboarding--update-rds-configuration-agoractl-rds-create)).
This will enable you to scale up or down the instance class of your RDS instance. In this way we can vertically scale up the instance. AWS will manage the health of the underlying instance, but currently scaling vertically needs to be done by you.

In terms of autoscaling, storage autoscaling is a feature that can be can enabled. Currently is it not configured. Effectively RDS Storage Auto Scaling will monitor the actual storage usage, and scale up when the usage reaches the provisioned storage capacity.

---

#### Question 6: Agora Storage Valet RDS backups and restore?
Are database backups done automatically?

**Answer:**
Yes we have enable automated database backups (called
[snapshots](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_WorkingWithAutomatedBackups.html)
by AWS). These snapshot contain the RDS instance at a given point in time, and enable you to restore to a new RDS instance. The default backup retention is one day, and takes place during a default back up window. Both of these can be overridden and set based on your SLA and restore expectations (via the [agoractl rds create](./03-storage-valet-rds-agoractl.md#onboarding--update-rds-configuration-agoractl-rds-create) config file).
You may refer to
[this](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/environments/dev2/base/storage-valet-rds-dev3/configs/rds-sample.yaml)
RDS config file, to see how to modify the RDS backupWindow, backupRetentionPeriod, and maintenanceWindow.

At a SQL level, back up tools like pgdump or schema management tools like flyway can still be used to backup at the SQL level. We do not currently enable this. This would be up to your team. In summary, we provide RDS instance level backups, which snapshot the entire instance and the data at a given point in time.

Currently we are working on enabling the self-service restore process. There is a draft technical note (TN) about
[Storage Valet RDS backup and restores](https://docs.google.com/document/d/1sB1obxldXQs12-YK6wEJTm7BBaqwNHL2PwP5pKLqW2A/edit?usp=sharing)
which we are working on. For now, the Agora data storage team would support you in the restore and cut off process for your database instance.

---

#### Question 7: Agora Storage valet RDS read replicas?
Is it possible to create a
[read replica](https://aws.amazon.com/rds/features/read-replicas/)
of RDS and access to it?


**Answer:**
Currently no, please let us know any requirements that you have or are expecting so we can prioritize further work.
Please feel free to reach out the Agora Data team on the `#wcm-org-agora-storage` Slack channel with the `@agora-storage` mention.

---

#### Question 8: I am struggling to connect to my RDS instance?
I am unable to connect to my RDS instance? My application is unable to connect to RDS?
What should I check for?


**Answer:** RDS connection troubleshooting checklist:

* I have reviewed [FAQ Question 1](#question-1-how-do-i-access-my-provisioned-storage-valet-rds-database-instance)
* Are you able to confirm that your application is trying to establish a connection using SSL?
* Is your application connecting with TLS?
* From a pod within SMC cluster can you connect directly via `psql` to the RDS instance?
* Is the password you are using still valid?
* The RDS password string can contain non-URL escaped characters, are you handling this correctly?
* Can you confirm whether the external secrets operator is syncing the RDS secret correctly?
* Is there any extra log information from the the Istio side-cart container?
* Your issue maybe be on the Istio ServiceEntry: does the host and config match what is expected?

---

#### Question 9: Database not found?
My database is not found? When connecting to my RDS instance it says database not found (database does not exist)?

```sql
my_db=> \l
FATAL:  Request returned an error: database "my_db" does not exist.
```

**Answer:**
Storage Valet RDS does not currently create any additional databases beside the default postgres database.
In other words, the default database that ships with Storage Valet RDS is `postgres`.
Using the master user password teams can create new databases (as needed).
We have noted this as a possible future feature request.
Please feel free to reach out the Agora Data team on the `#wcm-org-agora-storage` Slack channel with the `@agora-storage` mention.

---

#### Question 10: Agora Storage valet RDS password rotation?
The storage team’s RDS password is regularly rotated. Are there any considerations we should take on the application side? For example, do we need to take any actions to handle environment variable updates or avoid connection errors?
Do we have a best practice for the secret rotation? Within our pod should we be mounting it as a file to pickup changes?

How often is the RDS password rotated?

Can I modify the external secret polling refresh interval?

**Answer:**

Mounted environment variables are not rotated, however established connections will not be terminated when a secret is rotated. This is only an issue if you disconnect and need to reestablish connection.
In other words, if your pod is loading the secret as environment variables, new values will not propagate unless the pod is redeployed.

How you handle re-connections are up to you.
For [example](https://kubernetes.io/docs/concepts/configuration/secret/#using-a-secret)
you could, mount the kubernetes secret to a file. Your application would then reference this
file when establishing (and re-establishing) the connection to RDS.
An example file-watcher implemented by iota-ota can be found
[here](https://github.com/wp-wcm/city/blob/979390dcfb1601a6d667373ba150a1c1d36c7ecb/ns/iot/iota-ota/main.go#L105-L155).
A further discussion related to fsnotify, Go, and Kubernetes ConfigMaps can be found
[here](https://martensson.io/go-fsnotify-and-kubernetes-configmaps/). The DevRel team has also created some example code for implementing a file-watcher, this can be found [here](https://github.com/wp-wcm/city/pull/43072).

An alternative, to mounting the kubernetes secret to a file, would be to rotate the pod so that the new environment variables will be mounted again correctly.
Other alternatives: integrate AWS SDK and read secret directly from AWS. Future option: we can move towards token-based IAM auth (this has not yet been tested and will also require having the AWS SDK as a library dependency).

By default, the RDS password is rotated every 7 days (this can be adjusted).

Yes, the external secret polling refresh interval can be set in your respective bazel target, please see example in [agoractl rds k8s](./03-storage-valet-rds-agoractl.md#generating-serviceaccount-and-serviceentry-manifests-agoractl-rds-k8s) and please review the command docs [here](./03-storage-valet-rds-agoractl.md#k8s-subcommand).
The default refresh interval is `"1h"`.

> ⚠️ Note:
>
> It is important to note that there is a balancing act between the polling rate and cost of API calls to AWS secrets manager. Try to optimise the refresh window to something that makes sense.
For example: if the password only rotates monthly, then a 30min refresh interval may be wasteful.

---

#### Question 11: Storage Valet RDS username?
Who creates the RDS username?

**Answer:**
The username is created by Storage Valet RDS and the name is specified by
you using the `agoractl rds create --username` option.

---

#### Question 12: How can I enable RDS encryption whilst minimising downtime?

The encryption change is a replacement change, so a new RDS instance will be created.
Agora Storage Valet RDS intends to support a blue-green deployment and cut-over approach.

**Answer:**
Currently we have the following process:

1. Scale down app deployment to 0 replicas
2. Back up database (for example using pgdump)
3. Validate backup with postgres pod/image
4. Update existing RDS config to have encryption=true (terraform apply); (RDS instance is replaced)
5. Once RDS is up, restore database using the pg dump backup
6. Validate restore (connect to db via pod and validate data)
7. Scale up app deployment

A reference runbook created by the BURR team can be found [here](https://docs.google.com/document/d/1L_OsUBNIarCxQdTOExZclaNul8-7cZDqrP3LttuH-9c/edit#heading=h.hx1y10kj03ma)

---
