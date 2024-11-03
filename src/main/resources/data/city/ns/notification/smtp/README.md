# Notification::SMTP

Builds an image hosting postfix with predefine set of configurations to facilitate
building a relay host for our clusters.

It listens in plain SMTP at default port 25 under the expecation it will be
managed by an istio service mesh 

## Configuration

The configuration is entirely done via ConfigMap in kubernetes. It is expected
to be mounted in "/config" and that each contained file is a valid configuration
file for postfix.

By default we provide a working `master.cf`, but you still need to provide a
good `main.cf` and likely the mapping files you want to use (e.g. aliases, 
credentials).

To have postmap run in your files, make sure they have the `.db` extension.

Lastly, the startup script will tail the file pointed by `MAILLOG_FILE` environment
variable (default to `/var/log/postfix.log`).

Example `main.cf` file:

```
relayhost = ${RELAYHOST}
smtp_use_tls = yes
smtp_sasl_auth_enable = yes
smtp_sasl_password_maps = hash:/etc/postfix/sasl_passwd.db
smtp_tls_CAfile = /etc/ssl/certs/ca-certificates.crt
maillog_file = /var/log/postfix.log
compatibility_level = 2
inet_interfaces = all
```

Example `sasl_passwd.db` file:

```
[hostname]:port username:password
```
