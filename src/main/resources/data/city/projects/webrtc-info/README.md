# webrtc-info (namespace: xrb)
This repository is about REST API used for WebRTC of XR Light Backend. 
Specification of Channel GET API and Token Generate API on Agora for WebRTC. 


## Development enviroment on local PC

### Launch docker container on minikube cluster for debug of minifest files


- Prepare minikube cluster following [this document](https://docs.google.com/document/d/1oP957pyAGjR3WJrCazAsB_uf_bLCu5WHffOg70wemH0/edit#heading=h.1wvf9vezz9tp), if windows, [this document](https://docs.google.com/document/d/1PHlrxT7J4LgpVLo_04aAfxaW9f552FH5vG8QRwsypM0/edit#heading=h.i8l0os7b3zpg)

- Build Image

      % bazel run //projects/webrtc-info/server:server_image.load

- Run the following command. Check `bazel/projects/webrtc-info/server:server_image` is exists.

      % docker images
      gcr.io/k8s-minikube/kicbase         v0.0.40        c6cc01e60919   7 weeks ago    1.19GB
      hello-world                         latest         9c7a54a9a43c   4 months ago   13.3kB
      bazel/projects/webrtc-info/server   server_image   300f9ffada10   53 years ago   227MB

- Start minikube cluster, add image to minicube cache, and cache reload

      % minikube start
      % minikube image load projects/webrtc-info/server:server_image
      % minikube cache reload

- Run the following command. The CouchDB, Vault, and REST API will launch on minikube cluster.

      % kubectl apply -k projects/webrtc-info/k8s/local/

- Run the following command. Check CouchDB, Vault, or REST API working.

      % kubectl get pods
      NAME                                  READY   STATUS    RESTARTS        AGE
      app-5b9cf84675-h5ssn                  1/1     Running   0               6m16s
      couchdb-0                             1/1     Running   1 (5h23m ago)   6h31m
      vault-server-0                        1/1     Running   1 (5h23m ago)   6h31m
      webrtc-info-broker-847bbc894d-bvjfj   1/1     Running   0               148m
      zookeeper-7476d958c8-dkmpn            1/1     Running   0               3h54m

- Port forward and access REST API by curl commmand

      kubectl port-forward --address 0.0.0.0 service/app 8001:8001
      Forwarding from 0.0.0.0:8001 -> 8001


      curl http://0.0.0.0:8001 
      {"message":"This is the head contents. Put some prefix to access APIs"}

      If you want to check other commands , see https://docs.google.com/document/d/1ub_bBBkmc2rfg2tI2s_qw9RX5eH68Zd23XoHAjavkXc/edit#heading=h.85tnxqtyu9c0

- Port forward and access Couch DB (URL: http://0.0.0.0:5984/_utils/index.html#login id:`admin`/ps:`password`)

      kubectl port-forward --address 0.0.0.0 service/couchdb 5984:5984
      Forwarding from 0.0.0.0:5984 -> 5984

- Port forward access Vault (URL: http://0.0.0.0:8200 token:`vault-plaintext-root-token`)

      kubectl port-forward --address 0.0.0.0 service/vault-server 8200:8200
      Forwarding from 0.0.0.0:8200 -> 8200

- After debugging finishes, stop minikube cluster

      minikube stop


## Development enviroment on monorepo

Refer to [this URL link](https://github.com/wp-wcm/city/blob/main/docs/development/python/README.md#binaries--docker-images) and check updated version all the time.

### Run with the docker image created via bazel build

      $ bazel run //projects/webrtc-info/server:server_image

### Run unit tests

      # run tests under the specified directory
      $ bazel test //projects/webrtc-info/server/<unit tests directory>


      # run tests with log outputs --test_output=all
      $ bazel test --test_output=all //projects/webrtc-info/server/<unit tests directory>

      # re-run tests ignoring cache
      $ bazel test --cache_test_results=no //projects/webrtc-info/server/<unit tests directory>

### Update python dependencies
      $ bazel run //:py_requirements.update
      $ bazel run //:gazelle_python_manifest.update

### Update BUILD.bazel

      $ bazel run //:gazelle