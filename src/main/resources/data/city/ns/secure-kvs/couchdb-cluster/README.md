CouchDB cluster
----------

This folder includes files to build container images that are used for a CouchDB cluster deployment in [the Secure KVS deployment](/infrastructure/helm/secure-kvs).


### Steps to build CouchDB dev image
1. Clone [apache/couchdb-docker](https://github.com/apache/couchdb-docker.git) repository.
2. A few changes are required on the [dev](https://github.com/apache/couchdb-docker/tree/main/dev) path.
    1. By default main branch is checked out, so specify the required commit [here](https://github.com/apache/couchdb-docker/blob/main/dev/Dockerfile#L78).
    2. The [docker-entrypoint.sh](https://github.com/apache/couchdb-docker/blob/main/dev/docker-entrypoint.sh) in the dev assumes that the container is always started as root. If the container needs to be started as non-root, copy docker-entrypoint.sh from a stable version, for example, 3.3.2.
3. Build the image.
    ```sh
    bash -x build.sh version dev
    ```
4. Tag the image appropriately and push it to the artifactory.
    ```sh
    docker tag apache/couchdb:amd64-dev docker.artifactory-ha.tri-ad.tech/wcm-cityos/secure-kvs/apache/couchdb:amd64-dev-3.3.2-<commit>
    docker push docker.artifactory-ha.tri-ad.tech/wcm-cityos/secure-kvs/apache/couchdb:amd64-dev-3.3.2-<commit> 
    ```
5. Update the sha digest of the couchdb dev image in the [WORKSPACE](../../../WORKSPACE) file.
