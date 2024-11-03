# Rust API client for social-connection-client

Users' social connection information shared by many services.

This API is under active development and the API may change.

See [go/burr](http://go/burr) for more info about BURR or reach out on
[our Slack channel](http://go/burr-slack).



## Overview

This API client was generated by the [OpenAPI Generator](https://openapi-generator.tech) project.  By using the [openapi-spec](https://openapis.org) from a remote server, you can easily generate an API client.

- API version: 0.1.0
- Package version: 0.1.0
- Build package: `org.openapitools.codegen.languages.RustClientCodegen`

## Installation

Put the package under your project folder in a directory named `social-connection-client` and add the following to `Cargo.toml` under `[dependencies]`:

```
social-connection-client = { path = "./social-connection-client" }
```

## Documentation for API Endpoints

All URIs are relative to *http://social-connection.brr.svc.cluster.local/social-connection/v1alpha*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DefaultApi* | [**connection_woven_id_get**](docs/DefaultApi.md#connection_woven_id_get) | **GET** /connection/{wovenId} | Returns a profile list of connected persons.
*DefaultApi* | [**connection_woven_id_target_woven_id_delete**](docs/DefaultApi.md#connection_woven_id_target_woven_id_delete) | **DELETE** /connection/{wovenId}/{targetWovenId} | Remove a mutual connection between persons.
*DefaultApi* | [**connection_woven_id_target_woven_id_post**](docs/DefaultApi.md#connection_woven_id_target_woven_id_post) | **POST** /connection/{wovenId}/{targetWovenId} | Create a mutual connection between persons.


## Documentation For Models

 - [Profile](docs/Profile.md)


To get access to the crate's generated documentation, use:

```
cargo doc --open
```

## Author


