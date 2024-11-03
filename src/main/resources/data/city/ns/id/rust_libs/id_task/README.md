# id_task

This crate provides a more flexible way to run asynchronous tasks using tokio.

Some features include:
- Getting a `CancellationToken` (from `tokio_util::sync`) for allowing graceful
shutdown of processes.
- `Task`: Trait for running cancellable tasks.
- `RestarterTask`: A `Task` that restarts another task given a retry policy.
- `ConcurrentTaskSet`: A `Task` that runs multiple tasks concurrently and waits
for them to succeed. If a sub task ends (successfully or not) before the
cancellation token is fired, all tasks will be gracefully cancelled via the
child cancellation token.

# Examples

Please check the [examples](examples) directory for examples on how to use this
library.
