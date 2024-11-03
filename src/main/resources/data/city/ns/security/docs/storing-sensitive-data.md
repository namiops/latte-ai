# Storing Sensitive Data

### Steelcouch (Encryption Proxy for Key-Value Store)
Steelcouch is our proxy service that sits in front of our [CouchDB](https://couchdb.apache.org/) database. The main purpose
of Steelcouch is to provide a barrier between sensitive data and the requester. Steelcouch does this by providing 
encryption per record, minimizing the attack surface. 

If you'd like to learn more please feel free to access [steelcouch](../../../ns/secure-kvs/steelcouch/docs/README.md) from the main list, or search 'steelcouch' in the 
search bar above

### Vault 
Agora is also making use of [Vault](https://www.vaultproject.io/) which has become a de-facto standard in solving a tricky 
issue in regard to secrets and sensitive data inside a cluster: where to store said secrets and how to make sure they're 
only accessed when they're needed. Vault provides a flexible tool that Agora is using to leverage our Kubernetes clusters 
to authenticate services, allow them to use secrets, and inject them into their services directly without needing to declare
secrets in places that are more easily attacked.

If you'd like to learn more please feel free to access [vault](../vault/docs/README.md) from the main list, or search 'vault' in the
search bar above
