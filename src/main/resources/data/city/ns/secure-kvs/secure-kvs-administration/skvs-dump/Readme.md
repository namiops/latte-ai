### SecureKVS Dump

A minimal script to dump an SKVS database in a cluster into a file.
This file could be restored to SKVS database in the same or a different cluster.

#### Steps to execute

The script assumes that SKVS is available at localhost 5984
```bash
    bazel run //ns/secure-kvs/secure-kvs-administration/skvs-dump:skvs-dump -- --database <database name> --service-account <service account name> --namespace <namespace name> --backup-directory </path/to/backup/directory> --mode=<false|true> # false to backup, true to restore
```
