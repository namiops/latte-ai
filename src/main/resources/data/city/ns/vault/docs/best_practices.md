# Recommended Best Practices For Vault Use

The following is some recommendations from the Agora Team for the use of Vault
and how to make it easier for you to maintain and use.

## Secrets In Vault

The following are a few guidelines for using secrets in Vault

### Use Namespaces Sparingly

Vault Namespaces are primarily used to delineate and separate administrative
boundaries in Vault. If you are a team that is considering asking for a new
namespace, consider if the new namespace will be used by members in an existing
namespace, and, if so, consider an addition to your existing secret schema,
roles, policies, and other access controls

### Use Naming Conventions For Secrets, Accounts, Policies, Etc.

For Vault, all secrets can be accessed via policy. These policies can be as
fine or as coarse-grained as one team wishes. A team can easily end up with
numerous policies, secrets, and accounts to tie to both.

To try and alleviate this concern, it's recommended to try and use clear labels
for your secrets, policies, roles, and service accounts. For example a
potential template might be

```
# For secrets
<namespace>_<service>_<secret>

# For policies
<namespace>_<service>_<secret>_policy

# For roles
<namespace>_<service>_<secret>_role

# For service accounts
<namespace>_<service>_secrets
```

Please keep in mind that for Vault, you are only allowed 64 characters,
alphanumeric (`[a-zA-Z0-9]`) and underscores (`_`).

### Consider The Use Case Or Type Of Secret You Are Storing

Vault in Agora is primarily meant for secrets meant to be used by **services**.
For individual users, Vault is not meant to be the primary vehicle for secrets
management. For individual users, Agora has other systems in place for
authorization and authentication.

Some example cases for use with Vault that should be considered are:

* User accounts (for things like AWS that your service uses for connection)
* Database passwords
* Administrative credentials
* Client ID
* API Keys

### For Certain Secrets, Determine If An Engine Can Be Used

Vault provides
several [Secret Engines](https://www.vaultproject.io/docs/secrets)
that can provide an added benefits at times. For example, use of a Postgres
Secret Engine can allow dynamic credentials, making access to the database
safer by having ephemeral users.

If you feel you could leverage a secret engine in a particular way, please feel
free to either refer to our documentation for potential examples to get you
started, or, consult the Agora Team for any recommendations on how to get
started.

### Organize Secrets Via A Schema

Secret Engines in Vault are per Vault Namespace, per namespace a user can have
multiple secret engines of the same type, with different paths. For example a
KV Secret Engine at path `kv` is different from a KV Secret Engine at path `KV`

For avoiding confusion per namespace, a schema is helpful to organize secrets
for a service to help differentiate types and environments. For example, one
could use a schema that has a KV Engine per environment with secrets then
separated by type:

```
secret
├── kv-dev
│ ├── databases
│ │ └── DB1
│ └── users
│     ├── User1
│     └── User2
├── kv-prod
│ ├── applications
│ │ └── App1
│ ├── databases
│ │ ├── DB1
│ │ └── DB2
│ └── users
│     ├── User1
│     └── User2
└── kv-stg
```

An alternative example here separates by type of secret, then environment:

```
secret
├── machine
│  ├── dev
│  │    ├── DB
│  │    │   ├── DB1
│  │    │   └── DB2
│  │    └── Apps
│  │         ├── App1
│  │         └── App2
│  ├── test
│  │    ├── DB
│  │    │   ├── DB1
│  │    │   └── DB2
│  │    └── Apps
│  │         ├── App1
│  │         └── App2
│  └── prod
│       └── etc
└── users
   ├── accounts
   │    ├── user1
   │    └── user2
   └── development
        ├── user1
        └── user2
```

## Service Accounts

Service Accounts are the main way to allow your services to authenticate with
Vault. Please try to follow a clear naming convention to help with auditing and
clarity of who is using what.