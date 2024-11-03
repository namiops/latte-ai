# read_csr_from_esim.sh

## Overview

`read_csr_from_esim.sh` is a bash script to read from eSim, convert it from DER to PEM format, and display its contents in a human-readable format. The script allows you to specify an output directory for the generated files. If no output directory is specified, the files are saved in the current directory.

## Prerequisites

Please [refer this](https://github.com/wp-wcm/city/blob/feature/esim-iotsafe/ns/iot/demo/e-sim/memo/for_meeting_0621.md) to set up the tools needed for reading CSR. 
- `pkcs11-tool`: A command-line utility to manage and interact with PKCS#11 tokens.
- `OpenSSL`: A robust, full-featured open-source toolkit for the Transport Layer Security (TLS) and Secure Sockets Layer (SSL) protocols.

## Usage

### Running the Script

1. **Make the script executable** (if not already):
   ```bash
   chmod +x read_csr_from_esim.sh
   ```

2. **Run the script**:
   - To output the files to the current directory:
   ```bash
   sh read_csr_from_esim.sh
   ```
     
   - To specify an output directory:
   ```bash
   sh read_csr_from_esim.sh /path/to/the/directory
   ```
     