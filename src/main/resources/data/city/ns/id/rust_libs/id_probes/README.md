# id_probes

This crate provides [startup, liveness, and readiness](https://kubernetes.io/docs/concepts/configuration/liveness-readiness-startup-probes/) HTTP endpoints.
The user creates `Component`s via a `ComponentSet`. It's each `Component`'s responsibility to update its status accordingly and keeping it live.

Please see [example](example) for an example regarding the general structure.