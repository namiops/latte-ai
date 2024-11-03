Cordon and drain a node
=======================

# Pre-requirements
This Run-book assumes that you are already authenticated to the relevant cluster
and that you have sufficient permissions.

# unschedule the application(s)
Follow the guide in [2-shut-down-application.md](Shut down an application runbook) to first stop all affected applications.

# Drain the node

Replace //NODE NAME// with the name of the node you want to drain.

```sh
kubectl drain --ignore-daemonsets --delete-emptydir-data <NODE NAME>
```

# Optionally reschedule the application again

Resume the suspended namespaces in flux to get the application(s) rescheduled.
