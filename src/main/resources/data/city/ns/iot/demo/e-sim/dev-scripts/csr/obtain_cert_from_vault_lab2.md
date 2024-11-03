# obtain_cert_from_vault_lab2.sh

## Overview

The `obtain_cert_from_vault_lab2.sh` script is to obtain a certificate from Lab2's Vault using a CSR. This script is only used for test if your CSR is valid or not. Upon successful execution, the obtained certificate will be stored in `crt.pem` in the working directory.

## Prerequisites

- `kubectl` and access to lab2 environment.
- `jq`: JSON processor.

## Usage

```bash
sh obtain_cert_from_vault_lab2.sh <CSR_PATH>
```
`CSR_PATH`: (Optional) Path to the CSR file. If not provided, the script defaults to `./csr.pem`.

