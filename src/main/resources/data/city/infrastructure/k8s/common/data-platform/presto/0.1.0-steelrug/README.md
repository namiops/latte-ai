# steelrug-bundled presto

Currently a WIP, used for investigating the feasibility of transparent Parquet column-level encryption. 

Not ready for internal or external usage.

## Current status

* Able to encrypt/decrypt using a static hardcoded key
* Results in an error on query if the key is invalid, instead of masking the encrypted data
