<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Agora Storage](#agora-storage)
  - [Supported Databases](#supported-databases)
  - [Supported Storage](#supported-storage)
  - [FAQs](#faqs)
    - [Can I use a database that is not supported by Agora?](#can-i-use-a-database-that-is-not-supported-by-agora)
    - [For databases supported by Agora, is redundancy supported?](#for-databases-supported-by-agora-is-redundancy-supported)
    - [Are there any recommendations or frequently used products for databases in Agora?](#are-there-any-recommendations-or-frequently-used-products-for-databases-in-agora)
    - [Are databases available on Agora provided in a container or on a virtual machine?](#are-databases-available-on-agora-provided-in-a-container-or-on-a-virtual-machine)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Agora Storage

| Last Update | 2024-04-25                        |
|-------------|-----------------------------------|
| Tags        | Data, Storage, Redis, CouchDB, S3 |

## Supported Databases

* [Postgres](postgres.md)
* Redis
* [CouchDB](secure-kvs.md)

## Supported Storage

* S3

## FAQs

### Can I use a database that is not supported by Agora?

Yes, you can. Other databases that are not directly supported by Agora can be
considered for deployment and management by other teams. The only requirements
for deployment to Agora is compliance with Woven City's Privacy and Security
Policies, and the usage of the database does not violate its license. If you
have any questions about the use of another database, please feel free to
contact us.

### For databases supported by Agora, is redundancy supported?

For SecureKVS, Agora provides a database cluster managed by Agora on request for
a team. The management of redundancy, backup, restore and other features is
managed by the Agora team.

For PostgresSQL and Redis, Agora manages the tools (Kubernetes Operators) for
each database solution, and each team is individually responsible for
deployment, backups, restore, and other redundancy using the provided tools.

For more information about the PostgresSQL operator and how to work with it,
please refer to
the [operator documentation](../../../ns/postgres-operator/docs/docs)

### Are there any recommendations or frequently used products for databases in Agora?

This is highly dependent on the application being deployed by your service team.
Agora is more than happy to discuss this with your team and make recommendations
for you based on your application requirements.

SecureKVS and PostgresSQL have been in operation for over a year and are being
used by multiple teams on Agora. We believe these two services provide a good
starting point for most teams, with the added benefit of being able to knowledge
share with other application teams.

### Are databases available on Agora provided in a container or on a virtual machine?

All database solutions in Agora are currently deployed via containers for use
inside of Kubernetes clusters. For SecureKVS, the cluster is managed by Agora,
providing a managed solution for teams. For PostgresSQL and Redi, the management
tools provided by Agora are used by each team to manage their own databases,
including backups and sizing. 