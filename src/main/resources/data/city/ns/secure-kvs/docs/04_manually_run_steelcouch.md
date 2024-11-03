# Manually Run Steelcouch (Not recommended)

Steelcouch is an encrypting proxy that sits in front of CouchDB.

Steelcouch is currently beta software.
It may be used for development but not in production.
Every attempt will be made to avoid breaking changes.

## Usage

Steelcouch requires a configuration file.
See the [manual](MANUAL.md) for details.

### Run locally

```sh
$ bazel run //ns/secure-kvs/steelcouch -- -c /absolute/path/to/config.yaml
```

_Note_: `bazel run` sets the working directory to somewhere in the Bazel sandbox,
so passing a relative path usually won't work. It is recommended to specify the
absolute path to the config file instead.


### Build a container image

```sh
$ bazel build //ns/secure-kvs/steelcouch:image
```

### Push the container image

```sh
$ bazel run //ns/secure-kvs/steelcouch:push
```

The image is currently pushed to [the company Artifactory](https://artifactory-ha.tri-ad.tech/ui/repos/tree/General/docker%2Fwcm-cityos%2Fcore%2Fsteelcouch).

## Launch locally using Docker

```sh

cd /path/to/ns/secure-kvs/steelcouch/docs

# Place a key file that has enough length for the specified one by "ikm-length" in the configuration
$ echo $RANDOM | sha512sum | head -c 64 > steelcouch/root.key

$ ls -al steelcouch
total 16K
drwxrwxr-x  2 hoka hoka 4.0K Sep  2 09:21 .
drwxr-xr-x 38 hoka hoka 4.0K Sep  2 09:30 ..
-rw-rw-r--  1 hoka hoka   64 Sep  2 09:28 root.key
-rw-rw-r--  1 hoka hoka 1.5K Sep  2 09:21 .steelcouch

$ docker network create secure-kvs

# Launch CouchDB using the official container image
$ docker run --network secure-kvs -d --rm --name couchdb -e COUCHDB_USER=admin -e COUCHDB_PASSWORD=password -p5984:5984 couchdb
# (optional) if the image has not been downloaded yet
$ docker pull docker.artifactory-ha.tri-ad.tech/wcm-cityos/core/steelcouch:main-d22755cd-16303
# Launch Steelcouch
$ docker run --network secure-kvs --init --name steelcouch --rm -d -v $(pwd)/steelcouch:/steelcouch -p15984:15984  docker.artifactory-ha.tri-ad.tech/wcm-cityos/core/steelcouch:main-d22755cd-16303 --config "/steelcouch/.steelcouch"
```

Now we can put a document with encrypting through Steelcouch.

```sh
# Create a database
$ curl -X PUT http://admin:password@localhost:5984/demo
{"ok":true}

# Put a document through Steelcouch
$ curl -X PUT http://admin:password@localhost:15984/demo/e01 -d '{"motto": "I love gnomes"}'
{"ok":true,"id":"e01","rev":"1-6b0c2e4e72b797dcfc9c79c53d864fad"}

# The stored document is encrypted
$ curl -X GET http://admin:password@localhost:5984/demo/e01
{"_id":"e01","_rev":"1-6b0c2e4e72b797dcfc9c79c53d864fad","jwe":"eyJraWQiOiJlSjV4cUhwUkZDekxLQWdFZkctVnNzYTlmQkV5UXJIWDRYbFF3T0kxaWQ0IiwiZW5jIjoiQTI1NkdDTSIsIl9vcmlnaW4iOiJ0ZXN0IiwiX2RvbWFpbiI6ImVwb2NoIiwiX2hhc2giOiJTSEEyXzUxMiIsIl9kaWdlc3QiOiJKOGRHY0syM1VIWDYwRmpWenE5N0lNVG5lR3lEdXVpakwySnZsNEt2Tk1talBDQkc3MkQ5S25oNDAzamluLXlGR0FhNzJhWjRlUE9wOGMya2d3ZGpfUSIsImFsZyI6ImRpciJ9..m2VN7xLIHUlN2QsB.2AFg0iwo4NiKD4A6egPYT6EuDyAAyK5BUA.jpHdVm8yAJYIY_IEdlWcSg"}

# Get a decrypted document through Steelcouch
$ curl -X GET http://admin:password@localhost:15984/demo/e01
{"motto":"I love gnomes","_id":"e01","_rev":"1-6b0c2e4e72b797dcfc9c79c53d864fad"}
```

## Configuration

Steelcouch is configured with a YAML file.
By default, it will try to read ".steelcouch", "~/.steelcouch"
and "/etc/steelcouch".
This may be overridden with the "--config" command line option.

### Configuration File Format

```yaml
# The network address on which steelcouch listens.
# Default: 127.0.0.1:15984
listen: 127.0.0.1:15984

# The URL of the backing couchDB instance.
# Default: http://localhost:5984
couchdb: http://localhost:5984

# How the origin of a request is determined.
# Supported values: xfcc-uri, {static: somestring}
# Default: xfcc-uri
origin: xfcc-uri

# The cipher to use when encrypting.
# Supported values: A128GCM, A256GCM
# Default: A256GCM
cipher: A256GCM

# The hash to use for integrity.
# Supported values: SHA2_512, SHA3_512
# Default: SHA2_512
hash: SHA2_512

# The domain to use when creating keys.
domain: epoch

# Key resolver configuraion
resolver:

  # The resolver defines one or more domains.
  # Domain names should be meaningful.
  domains:

    epoch:
      # A HKDF key source.
      type: hkdf

      # The underlying hash.
      # Supported values: sha2-512, sha3-512
      # Default: sha2-512
      hash: sha2-512

      # Input key material domain, id and length.
      # This is resolved to a key on demand.
      # The default length is 0, meaning use the underlying hash size.
      ikm-domain: root
      ikm-id: root.key
      ikm-length: 64

    root:
      # A file key source.
      type: file

      # The directory under which key files are stored.
      prefix: /steelcouch

  # Capacity of resolver cache, in keys.
  capacity: 65536

  # Lifespan of key in the cache, in seconds.
  lifespan: 300
```

## Key Management

It is best practice to generate session keys from a HKDF domain,
keyed from another domain.

Once used, domain names, configurations and related stored keys
should be backed up and must never be modified.
If this information is lost,
previously encrypted documents will be unrecoverable.

It is best practice to include a datestamp in each domain name.

A file domain may be used to import secrets mapped onto the filesystem from
elsewhere.
