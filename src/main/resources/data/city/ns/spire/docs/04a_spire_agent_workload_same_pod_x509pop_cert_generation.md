# SPIRE x509pop Node Attestation: x509pop Certificate Generation Guide
This document serves as a guideline for generating X.509 certificates specifically for the SPIRE x509pop node attestation method. x509pop is one of the node attestation methods in SPIRE that utilizes X.509 certificates to prove the identity of SPIRE agents. More details can be found [here](https://github.com/spiffe/spire/blob/main/doc/plugin_server_nodeattestor_x509pop.md).

## Introduction
Node attestation within SPIRE is a critical process to verify the identity of individual agents, and the x509pop authentication method utilizes X.509 certificates specifically generated for this purpose. This document explains how to generate x509pop-specific certificates to accurately authenticate SPIRE agents within the cluster.

## x509pop Certificate Generation
Follow the steps below to generate x509pop certificates:

1. Create a configuration file for OpenSSL. This file holds the settings needed for certificate creation.
```
cp /etc/ssl/openssl.cnf ./local-x509pop-openssl.cnf
```

2. Append to the configuration file information about the certificate to be generated.
```
cat <<EOF >> ./local-x509pop-openssl.cnf
[ x509pop ]

basicConstraints = CA:TRUE
keyUsage = digitalSignature, keyEncipherment, keyCertSign
EOF
```

3. Use OpenSSL to generate a new x509pop certificate and its corresponding private key.
```
openssl req -config ./local-x509pop-openssl.cnf -x509 -sha256 -nodes -days {VALIDITY_PERIOD} -newkey rsa:2048 -subj '/OU={YOUR_ORGANIZATION_UNIT}/CN={YOUR_COMMON_NAME}' -extensions x509pop -keyout {YOUR_PRIVATE_KEY_FILENAME} -out {YOUR_CERTIFICATE_FILENAME}
```
Here, replace {VALIDITY_PERIOD}, {YOUR_ORGANIZATION_UNIT}, {YOUR_COMMON_NAME}, {YOUR_PRIVATE_KEY_FILENAME}, and {YOUR_CERTIFICATE_FILENAME} as appropriate.

##  Caveats
- The generated certificate has a validity period of the specified number of days. You need to renew the certificate before its expiration date.
- Private keys are sensitive information. Ensure they are appropriately stored and not unnecessarily exposed.
