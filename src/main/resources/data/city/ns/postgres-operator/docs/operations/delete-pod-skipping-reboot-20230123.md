# Recovery operation for pods that skipped the reboot operation

During the incident related to the DCS communication error on 19 Jan 2023, we applied the reboot operation to the PosgreSQL cluster that faced the issue, but some of the replica instance didn't reboot. 

The operation aims to recover the imcomplete restart of the PostgreSQL clusters. 
And the document doesn't aim to make the manual unofficial deletion operation that we decided to take as a common administrative operation.
If we face some issue that the same manual operation becomes an option to solve it, we need to make a decision case by case including the risk of the unofficial operation.


Planned operation date: 23 Jan 2023 \
Engineer: keiji.hokamura

## Cause

Based on the code reading of the PGO operator and Patroni, we understand PGO's reboot is handled with the following procedure.

1. A person adds the label "postgres-operator.crunchydata.com/role" to the target PG cluster
2. PGO operator sets the label to the target StatefulSet and its pod template.
3. The operator deletes the pod for replica instances manually.
4. The operator triggers Patroni's switchover to demote the original leader and promote one of replicas
5. After confirming the original leader pod doesn't have `postgres-operator.crunchydata.com/role: master`, the operator deletes the pod of the original leader 

The label `postgres-operator.crunchydata.com/role` is managed by Patroni, and it updates the label during the switchover at procedure 4.
But due to the DCS communication issue (communication issue between Patroni and Kubernetes API) caused by the bug of the service account token rotation, we can assume the update of the label didn't corrently finished. 

The related source codes are the following.

- https://github.com/zalando/patroni/blob/v2.1.1/patroni/dcs/kubernetes.py
- https://github.com/CrunchyData/postgres-operator/blob/ed7ee9b71dece7004efbcfcd659870f37ff149b5/internal/controller/postgrescluster/instance.md
- https://github.com/CrunchyData/postgres-operator/blob/ed7ee9b71dece7004efbcfcd659870f37ff149b5/internal/controller/postgrescluster/controller.go
- https://github.com/CrunchyData/postgres-operator/blob/ed7ee9b71dece7004efbcfcd659870f37ff149b5/internal/controller/postgrescluster/patroni.go

I got the commit `ed7ee9b71dece7004efbcfcd659870f37ff149b5` for PGO via `git grep "5\.0\.4" $(git rev-list origin/master) docs/config.toml | head -1`.

We confirmed the inconsistent situation with one of the PG cluster.

```
# The pod with the issue is "postgresql-instance1-j9z8-0"
(base) ➜  cityos git:(hoka-operation-reboot-issue-3834774684) kubectl get po -n id --selector postgres-operator.crunchydata.com/cluster
NAME                                              READY   STATUS      RESTARTS   AGE
...
postgresql-instance1-796s-0                       5/5     Running     0          31h
postgresql-instance1-grkz-0                       5/5     Running     0          31h
postgresql-instance1-j9z8-0                       4/5     Running     0          87d
...

# The pod is "replica"
(base) ➜  cityos git:(hoka-operation-reboot-issue-3834774684) kubectl exec -it -n id postgresql-instance1-796s-0 -- patronictl list 
+-----------------------------+---------------------------------------------+---------+---------+----+-----------+
| Member                      | Host                                        | Role    | State   | TL | Lag in MB |
+ Cluster: postgresql-ha (7078859053406498940) -----------------------------+---------+---------+----+-----------+
| postgresql-instance1-796s-0 | postgresql-instance1-796s-0.postgresql-pods | Leader  | running | 10 |           |
| postgresql-instance1-grkz-0 | postgresql-instance1-grkz-0.postgresql-pods | Replica | running | 10 |         0 |
| postgresql-instance1-j9z8-0 | postgresql-instance1-j9z8-0.postgresql-pods | Replica | running |  9 |     26592 |
+-----------------------------+---------------------------------------------+---------+---------+----+-----------+

# The pod's label is still one for leader.
(base) ➜  cityos git:(hoka-operation-reboot-issue-3834774684) kubectl get po -n id postgresql-instance1-j9z8-0 -o json | jq '.metadata.labels."postgres-operator.crunchydata.com/role"'
"master"
```

The PGO operator doesn't use the label for routing the requests, so it hasn't been causing a wrong routing.

## Decision making on operation

We decided to manually delete the unhealthy read replica pods as the recovery operation.

The manual pod deletion isn't one documented in the PGO official document,
and triggering the reboot of the target PostgreSQL clusters is another option to restart the pods following the official PGO administration procedure.
But we decided to take the unofficial operation based on the following reasons.

- ~~The cluster reboot includes the reboot of the primary database pod with switchover, and it might bring a small down time.~~
Actually, we watched the pod deletion cause a reboot targeting another database instance pod during the PGO operator's reconciliation process.
So, regarding the small downtime for a switchover process, there is no difference over the both approaches.
- The cluster reboot needs temporary stopping Flux reconciliation for the target namespaces, and it affects service teams' development.
- We already took the manual operation before, and we didn't find any further issues by it.

### Details of pod deletion

PGO use a single node StatefulSet with `OnDelete` update strategy to control for each database instance pod to control their lifecycle, and the operator manually delete a pod when it needs to reboot the pod.
And all target pods have already been demoted to `Replica`. ~~also I confirmed that the rest of the operator logic is calling the deletion API for the target pod.
So, we think the deletion isn't problematic one.~~

~~We tested the operation targeting the database cluster in the `testing` namespace and confirmed that it was done without any impact to the running leader instance.~~

We saw the PGO operator triggered the pod reboot targeting another database including the leader one during its reconciliation process when we delete a database instance pod.
When a leader instance pod is rebooted, a switchover is triggered before the deletion.
So, the operation may bring a small downtime for the switchover.

We haven't fully figured out when the reboot is triggered, and we'll keep checking the behavior,
but the reboot might be triggered based on the StatefulSet's update revision and one put in the pod's labels.
We'll check it further in [this ticket](https://wovencity.monday.com/boards/3650470399/views/86217656).

## Operation manual

### Targets

| namespace | pod name |
| --- | --- |
| data-privacy | postgresql-instance-rwqf-0 |
| id | postgresql-instance1-j9z8-0 |
| woven-passport | payment-hub-db-payment-hub-db-1-9q6r-0 |

After we confirmed the switchover in the pod deletion operation targeting the database in the `data-privacy` namespace, 
we adjusted the time for the operation with the Agora ID team and the Woven Passport team to minimize the affects of the possible switchover downtime.

### Commands

#### `data-privacy` namespace

```
# Confirm the target pod
$ kubectl get po -n data-privacy --selector postgres-operator.crunchydata.com/instance

# Confirm the target isn't the current actual leader
$ kubectl exec -it -n data-privacy postgresql-instance-rwqf-0 -- patronictl list

# Confirm the deletion command
$ kubectl delete po -n data-privacy postgresql-instance-rwqf-0 --dry-run=client

# Execute the command
$ kubectl --as sudo --as-group=aad:0f158ca2-948a-4d79-83b1-f21380bd16aa delete po -n data-privacy postgresql-instance-rwqf-0 --dry-run=client

# Monitor the update
$ kubectl get po -n data-privacy --selector postgres-operator.crunchydata.com/instance -w
```

During the operation, a switchover happened.

#### `id` namespace

```
# Confirm the target pod
$ kubectl get po -n id --selector postgres-operator.crunchydata.com/instance

# Confirm the target isn't the current actual leader
$ kubectl exec -it -n id postgresql-instance1-j9z8-0 -- patronictl list

# Confirm the deletion command
$ kubectl delete po -n id postgresql-instance1-j9z8-0 --dry-run=client

# Execute the command
$ kubectl --as sudo --as-group=aad:0f158ca2-948a-4d79-83b1-f21380bd16aa delete po -n id postgresql-instance1-j9z8-0 --dry-run=client

# Monitor the update
$ kubectl get po -n id --selector postgres-operator.crunchydata.com/instance -w
```

During the operation, a switchover happened.

#### `woven-passport` namespace

```
# Confirm the target pod
$ kubectl get po -n woven-passport --selector postgres-operator.crunchydata.com/instance

# Confirm the target isn't the current actual leader
$ kubectl exec -it -n woven-passport payment-hub-db-payment-hub-db-1-9q6r-0 -- patronictl list

# Confirm the deletion command
$ kubectl delete po -n woven-passport payment-hub-db-payment-hub-db-1-9q6r-0 --dry-run=client

# Execute the command
$ kubectl --as sudo --as-group=aad:0f158ca2-948a-4d79-83b1-f21380bd16aa delete po -n woven-passport payment-hub-db-payment-hub-db-1-9q6r-0 --dry-run=client

# Monitor the update
$ kubectl get po -n woven-passport --selector postgres-operator.crunchydata.com/instance -w
```

During the operation, a switchover didn't happen.
