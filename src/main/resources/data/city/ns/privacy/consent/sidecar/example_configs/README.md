# Example checker configurations

This folder includes example Checker configurations as well as "work in progress" files.
Checker needs some kind of configuration to know where to look for user ids and data attributes in the https contexts in order to prepare parameters for the consent checks.
One of the planned ways to express that is through istio-like configuration that would be loaded on checker/sidecar initialization.

## Work in progress

As there are multiple scenarios where user ids and data attributes could be included in the http contexts we didn't finalize configurations for all of them.
We have focused on scenarios that we know support will be required in the close future (like BURR use cases).

In order to avoid confusion please avoid using `wip` labeled files as a valid source of how the configuration should look like for your service.
