# how to develop on local

## Local Development using Tilt

This works both on amd64 and arm64 environment.

### What is Tilt?

The tool to make it easy to develop kubernetes codes.

- [Tilt | Kubernetes for Prod, Tilt for Dev](https://tilt.dev/)
- [Install | Tilt](https://docs.tilt.dev/install.html)

Customized the following sample file for `Tiltfile` in this project

- [tilt-extensions/Tiltfile at master · tilt-dev/tilt-extensions](https://github.com/tilt-dev/tilt-extensions/blob/master/kubebuilder/Tiltfile)

### start minikube

#### (option 1) w/ ctlptl
This is not must but `ctlptl` should be used for faster live updates.
W/o ctlptl, live updates would be slower and often fails. (We can update the image manually by clicking the button, though) 
- [tilt-dev/ctlptl: Making local Kubernetes clusters fun and easy to set up](https://github.com/tilt-dev/ctlptl)

after installing `ctlptl`, start the minikube as follows:

```shell
ctlptl create cluster minikube --registry=ctlptl-registry --kubernetes-version=<YOUR_K8S_VERSION>

## e.g.
ctlptl create cluster minikube --registry=ctlptl-registry --kubernetes-version=v1.21.11
```

#### (option 2) w/o ctlptl

minikube is necessary to be started.
```minikube_start.sh
 minikube start --memory=6144 # 2GB default memory isn't always enough. If your pc does not have enough memory, this should be set 4096
```

### export environment variables

First download go binaries to `./bin` such as `controller-gen` with the following command:

```shell
make install
make test
```

Then please set the `PATH`.
Using direnv is easier, but if you cannot use direnv, use the `export` command.

ref: [direnv – unclutter your .profile | direnv](https://direnv.net/)

##### with direnv

The following command should be executed once.

```shell
direnv allow
```

##### without direnv

```export_env.sh
# using `Makefile`, the binary files (controller-gen, kustomize, manager, setup-envtest) will be placed in `./bin`
export PATH="./bin:$PATH"
```


### Start the kafka-operator and related resources


```tilt_up.sh
$  tilt up
Tilt started on http://localhost:10350/
v0.30.6, built 2022-08-04

(space) to open the browser
(s) to stream logs (--stream=true)
(t) to open legacy terminal mode (--legacy=true)
(ctrl-c) to exit
```

Access the tilt UI on　your browser
- http://localhost:10350
Access the Kafka UI on　your browser
- http://localhost:8080

Edit the yamls in `./config/samples/` and check the log of operator!


### Clean resources
`minikube delete` might be the easiest way but you can clean as follows:

```clean.sh
tilt down
kubectl delete ns kafka
kubectl delete ns cert-manager
``` 
