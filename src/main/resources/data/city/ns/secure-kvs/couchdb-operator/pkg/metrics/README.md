(A work in progress!)

# CouchDD Operator Metrics

## Common labels
These labels are applied by the operator to all metrics that are logged in the operator.

| Value             | Description                          |
|-------------------|--------------------------------------|
| cluster-namespace | The namespace of the couchdb cluster |
| cluster-name      | The couchdb cluster name             |

## couchdb_op_snapshot_current_count
Gauge that indicates the amount of current snapshots.

### couchdb_op_snapshot_current_count{status="x"}
Label that indicates the status of the counted snapshots.

| Value          | Description                                         |
|----------------|-----------------------------------------------------|
| Available      | The amount of available snapshots                   |
| Pending        | The amount of pending snapshots                     |

## couchdb_op_snapshot_activity
A counter that increases while a snapshot is active. If the counter stops changing, the snapshot no longer exists.

### couchdb_op_snapshot_activity{status="x"}
Label that indicates the status of the snapshot.

| Value          | Description                                         |
|----------------|-----------------------------------------------------|
| Available      | The amount of available snapshots                   |
| Pending        | The amount of pending snapshots                     |

### couchdb_op_snapshot_activity{snapshot_name="x"}
Label that indicates the name of the snapshot

## couchdb_op_dbcluster_status
Gauge. Indicates the current status of the pod. Possible values 0: error, 1: no error.

| Value | Description |
|-------|-------------|
| 0     | Error       |
| 1     | No Error    |

### couchdb_op_dbcluster_status{ctype="x"}
This label maps to [cluster types](../../api/v1alpha1/couchdbcluster_types.go#L127).