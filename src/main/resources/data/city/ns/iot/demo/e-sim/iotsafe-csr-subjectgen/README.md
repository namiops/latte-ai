## iotsafe-csr-x509subjectgen.py

This is a script to generate a subject for Certificate Signing Request (CSR) and X.509 certificates.
The subject is represented as X.501 Name and encoded in form of ASN.1 DER.

It also encodes STORE DATA command for our IoT Security applet to request on-card CSR generation.

Please see RFC2986 and X.501 for technical details.

## usage

1. Install dependencies and setup virtualenv

```sh
$ sudo apt install python3 python3-poetry
$ poetry install --no-root
```

2. Encode X.509 subject and STORE DATA commands.
APDU command generated (`80e28100..`) can be used to request on-card generation of CSR.


```sh
$ poetry run python3 iotsafe-csr-x509subjectgen.py --commonName device0.local  --countryName JP --stateOrProvinceName Tokyo  --organizationalUnitName HQ --organizationName myCompany --emailAddress hq@mycompany.com --output subject.der
Subject:    C=JP,ST=Tokyo,O=myCompany,OU=HQ,1.2.840.113549.1.9.1=hq@mycompany.com,CN=device0.local
            30773116301406035504030c0d646576696365302e6c6f63616c311f301d06092a864886f70d01090116106871406d79636f6d70616e792e636f6d310b3009060355040b0c02485131123010060355040a0c096d79436f6d70616e79310e300c06035504080c05546f6b796f310b3009060355040613024a50

STORE DATA commands:
# generate CSR, private key label="1"
80e28100807d7e740131507930773116301406035504030c0d646576696365302e6c6f63616c311f301d06092a864886f70d01090116106871406d79636f6d70616e792e636f6d310b3009060355040b0c02485131123010060355040a0c096d79436f6d70616e79310e300c06035504080c05546f6b796f310b3009060355040613024a50 00
```
