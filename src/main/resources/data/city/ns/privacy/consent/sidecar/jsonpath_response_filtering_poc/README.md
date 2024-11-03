# Redacting json using JSONPath POC

This is just a small POC exploring how we could use JSONPath for redacting parts of the responses.
Using JSONPaths presented in checker configurations we could find the pieces of personal information in responses 
and redact if response from the consent service says so.

For example checker configurations see [example_configs](../path_parser/example_configs)

## How to run
This app is excluded form the root cityos workspace and is only intended to be run with cargo as it's not intended be maintained or reused in the future.

Once in a `/jsonpath_response_filtering_poc`:

```cargo run```

## JSONPath crates checked

While there are couple options when it comes to JSONPath crates we could use, only one allowed to modify the root json after querying it with JSONPath.

- [jsonpath_lib](https://crates.io/crates/serde_json_path)

Other crates didn't mention anything about json modifications, and made it not possible by returning immutable object after querying.
We are trying to follow up about this problem with these crates by github issues.

As examples:

- [jsonpath-rust](https://crates.io/crates/jsonpath-rust)
  - With the [find_slice](https://docs.rs/jsonpath-rust/0.3.3/jsonpath_rust/struct.JsonPathFinder.html#method.find_slice) method
  - [Issue about it](https://github.com/besok/jsonpath-rust/issues/48)

- https://crates.io/crates/serde_json_path
  - And all the [NodeList methods](https://docs.rs/serde_json_path/0.6.3/serde_json_path/struct.NodeList.html)
  - [Issue about it](https://github.com/hiltontj/serde_json_path/issues/66)
