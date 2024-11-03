# Migrating Services Between Agora Environments

This document is meant to help with some general guidelines with teams who are
moving between environments that Agora provides for our colleagues.

## Who This Document Is For

This document was initially created due to the Agora team's original cluster
(known as the learning cluster) reaching End Of Life (EOL) in September 2022.
The hope and idea of this document is to provide service teams guidelines to
watch out for when, and if they need to move their services to a new cluster.
This document can apply for a few scenarios such as:

* A cluster that the Agora team runs or maintains reaches EOL and will be
  dismantled or shut down, requiring services still on that cluster to be moved
* A service team is ready to move to next steps for service or application
  development, such as moving from a developer environment to a staging
  environment

This document is meant for service teams and colleagues who need to know or
want to have some best practices and guidelines that can help them with any
issues or problems in regard to their service setups, and how to migrate from
one environment to the next. 

## Guidelines and Practices for Migration

The following is meant to be a few guidelines, tips, and things to check when
you migrate your service or services from one cluster to another. While the
Agora team is working hard to make sure that migration is frictionless and with
minimal changes it does help to make sure and verify a few things in your
deployments.

### What Agora Environments Are

Agora has set up clusters via GitOps, which states that **the git repository is
the truth**. The Agora Team has made considerations on how to organize the
clusters in a way that helps make the infrastructure repeatable and composable.
Agora does this in a way that is mostly invisible to our colleagues and service
teams; from a service team's perspective, there is a low chance that anything
needs to be changed because the environments should be set up in the same
manner.

### Checking Hooks to Common Services in Agora

Several of the common services that Agora provides like Grafana/Loki for
Observability, Notifications, and Kafka for Orchestration and Interoperability,
should be setup in a similar manner across environments. Please make sure to
verify your setups with URLs, ports, and any additional configurations.

For our service bus (Kafka), each environment has a dedicated MSK cluster for
it. `learning` has a different MSK than `dev` and likely than in `staging`.
Topics that services depend on will need to be declared again in the new
environment. This can be done in the same way as before, by using a CityOsKafka
Custom Resource that declares a service's topics, and ACLs.

### Setting Up Tenancy For Services in Agora

Agora is set up to allow multi-tenancy inside the cluster, which is done for
security purposes. Tenancy allows Agora to help teams self-service their
resource deployments to the cluster. If you were never in the previous cluster
or set up as a tenant, the Agora team will need to set up your tenancy with the
appropriate controls and menifests. If you were set up as a tenant in the
previous cluster, the manifests should be similar, however, you will need to
re-verify that you are set up as a tenant in the new cluster. Please contact
the Agora team to help with this. 


### Secrets and Vault

Secrets are separated per environment in Agora. One secret in `learning` might
not necessarily be the same one needed in `dev` or future clusters. For some
secrets however there might be a shared configuration, such as an OAuth app
that can be used in multiple areas for a small example. In addition, each
environment needs to have a separate setup for using Vault's authorization
engines. 

If you need to enable a new configuration to move Vault secrets to the cluster,
it is currently recommended that you contact the Agora team for help on setting
up the proper engine. 