# id_logging

One common crate to add logging/tracing capabilities to application in the identity stack.

## How to use

Initializing the tracing stack is done through the `init` method, which should be called when your application initializes.

```rust
id_logging::init(&config);
```

This method takes as argument a struct which should implement the `LogConfig` trait. This method will setup tracing providers, which need to be properly shutdown. This is done by calling the `shutdown` method when your application is shutting down.

```rust
id_logging:shutdown()
```
