# BURR Consent Info Tool

Extracts and prints consent-related information from OpenAPI documents.

## Description

This tool prints the data attributes & consent-related annotation info from an 
OpenAPI document (OAD).

This helps get an overview of the data attributes needed for each endpoint, and
the information can be used to update the docs.

This tool can either use the main API spec baked into the coreapi service 
(default behavior), or alternatively read any OAD YAML file specified as an 
argument.

The output is sorted by endpoint path and method, so it's reproducible across 
runs and should make it easy to go through the items.

The output contains info on whether a "skip consent check" annotation is present,
and if so, what the stated reason is.  
It also prints the comments that go into the descriptions in the YAML file; if 
the consent check is active, this includes the parameter used as the data 
subject ID, and the list of data attributes.

## Usage

It's easiest to run this command using Bazel, since that will load and compile
everything that's necessary.

To just run the tool with default settings:

```shell
bazel run //ns/brr/coreapi/cmd/consent_info_tool:consent_info_tool
```

If you want to pass any arguments to the tool, you have to include a separate
`--` before the arguments to tell Bazel to stop looking for its own arguments
and pass them to the tool instead.

For example, to show the help output:

```shell
bazel run //ns/brr/coreapi/cmd/consent_info_tool:consent_info_tool -- --help
#                            note the separate extra double-dash --^^
```

Or to manually specify an OAD file:

```shell
bazel run //ns/brr/coreapi/cmd/consent_info_tool:consent_info_tool -- --filename /path/to/spec.yaml
```
