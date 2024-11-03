# Postgres Operator Runbook

<!-- TOC -->

- [Introduction](#Introduction)
- [Backup](#Backup)
- [Restore](#Restore)
- [Shutdown and Restart](#shutdown-and-restart")

<!-- /TOC -->

| System            | Owner                              | Path to system                            | System Enabled in Environments |
| ----------------- | ---------------------------------- | ------------------------------------------| ------------------------------ |
| Postgres Operator | City Platform, City OS, Infra Team | [/infrastructure/k8s/common/cityos-system](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/infrastructure/k8s/common/cityos-system/) | local, lab, dev |

# Introduction
Because of its complexity, this runbook has been broken down into separate
files based on topic.

# Backup
[Backup](pgo-backup-and-restore/README.md#Backup)

# Restore
[Restore](pgo-backup-and-restore/README.md#Restore)

# Shutdown and Restart
[Shutdown and Restart](shutdown-restart-db-cluster.md)
