# How Steelcouch Works

Steelcouch is a [CouchDB](https://couchdb.apache.org/) encrypting proxy.

It is useful for storing confidential information in CouchDB without
having to worry about encrypting or decrypting records on the client side.
Each document is encrypted with a unique session key.

## Synopsis

```
USAGE:
    steelcouch [OPTIONS]

FLAGS:
    -h, --help       Prints help information
    -V, --version    Prints version information

OPTIONS:
    -c, --config <config>    Sets configuration path
```

## Description

Steelcouch intercepts HTTP PUT requests with paths with of the form {db}/{doc}
and encrypts the document before passing it on to CouchDB.
Similarly, it decrypts responses to HTTP GET requests.
If a document appears not to be encrypted it is passed back to the client as is.

All other requests are transparently proxied.

## Supported CouchDB APIs by Steelcouch

**Notes:**
The information in this section is on April 25th, 2024.

Here is the CouchDB APIs that Steelcouch supports encryption for storing data and decryption for getting them.

Encryption

* [PUT /{db}/{doc}](https://docs.couchdb.org/en/3.2.2-docs/api/document/common.html#put--db-docid)

Decryption

* [GET /{db}/{doc}](https://docs.couchdb.org/en/3.2.2-docs/api/document/common.html#get--db-docid)
* [POST /{db}/\_find](https://docs.couchdb.org/en/3.2.2-docs/api/database/find.html#post--db-_find)
* [POST /{db}/\_bulk_get](https://docs.couchdb.org/en/3.2.2-docs/api/database/bulk-api.html#post--db-_bulk_get)

Other API calls are just passed through to CouchDB.
The APIs such as [DELETE /{db}/{docid}](https://docs.couchdb.org/en/3.2.2-docs/api/document/common.html#delete--db-docid) that don't include documents in the request or the response are available on Steelcouch, 
even though they aren't listed above.
But the APIs such as [POST /{db}/\_bulk_docs](https://docs.couchdb.org/en/3.2.2-docs/api/database/bulk-api.html#db-bulk-docs) aren't supported by Steelcouch.

If your application is using an unsupported API and it's urgently necessary for the development timeline, 
please let us know in the Slack message for the onboarding.

## Document Encryption

Steelcouch only knows how to encrypt JSON objects.
Other JSON values are rejected as an error.

Object members having a key which starts with an underscore ('\_')
or a plus ('+') are special, and are left untouched.
The former are reserved by CouchDB, and the latter may be used by an
application for indexing purposes.

All other object members are encrypted and encoded as a compact JWE
serialization, which is stored in the object under the key "jwe".
In addition, a hash of all fields starting with a period is stored in
the JWE protected header so that their integrity may be checked at decrypt time.

## Bugs

Steelcouch supports encryption of [document attachments](https://docs.couchdb.org/en/stable/api/document/attachments.html#put--db-docid-attname). However, Steelcouch doesn't (yet) support attachment encryption when the attachment is specified through __attachments_ field.
