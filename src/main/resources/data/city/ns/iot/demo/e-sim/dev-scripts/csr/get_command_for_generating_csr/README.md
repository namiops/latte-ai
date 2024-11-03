# Sample Code For Getting the "Command for Generating CSR"

## Description

This is a sample Golang script that constructs the command for generating CSR.

## Requirements

- Golang

## Usage

### Input

You need to adjust the CSR subject, which is within the `main()` function, to fullfill your needs. 

### Output

The script will print out
- The command to generate CSR using a card reader and `gp.jar` ([Ref](https://github.com/wp-wcm/city/blob/feature/esim-iotsafe/ns/iot/demo/e-sim/memo/for_meeting_0621.md?plain=1#L22))
- The command to generate CSR using eSimOS and APDU ([Ref](https://github.com/wp-wcm/city/blob/feature/esim-iotsafe/ns/iot/demo/e-sim/memo/for_meeting_0709.md))

### Running the Script

1. Go to the folder of this README
2. Init the project by `go mod init <Mod_Name>` and `go mod tidy`
3. Adjust the CSR subject within the `main()` function based on your needs
4. Run the scripts by `go run .`
5. Copy the genearted command from the output
