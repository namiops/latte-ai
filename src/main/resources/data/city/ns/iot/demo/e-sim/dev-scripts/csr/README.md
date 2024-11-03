# CSR Related Scripts

This folder contains programs and scripts for generating and validating CSR on eSim.

## Prerequisites

The commands in this folder are intended to be executed on an *Ubuntu* machine. Please ensure the following:

1. Follow the [development environment setup guide](https://github.com/wp-wcm/city/blob/feature/esim-iotsafe/ns/iot/demo/e-sim/memo/for_meeting_0621.md) to correctly configure your development environment.
2. Ensure the USB dongle containing the eSim card is attached to your machine, and the IoT Safe applet on your eSim card is updated to the latest version.

## Order

The order for the excuting the scripts should be:
1. `get_command_for_generating_csr/main.go`
2. `call_esim_to_generat_csr.sh`
3. `read_csr_from_esim.sh`
4. `obtain_cert_from_vault_lab2.sh`
