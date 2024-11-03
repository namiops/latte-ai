# Class-Sync-Dispatcher (namespace: xrl-dev)
This application is about Publish/Subscribe Client(Backend) used for MQTT/AMQP of XR Light Backend. 

## Overview
![Alt text](image.png)

## Development enviroment on local PC

### Launch docker container on minikube cluster for debug of minifest files


- Prepare minikube cluster following [this document](https://docs.google.com/document/d/1oP957pyAGjR3WJrCazAsB_uf_bLCu5WHffOg70wemH0/edit#heading=h.1wvf9vezz9tp), if windows, [this document](https://docs.google.com/document/d/1PHlrxT7J4LgpVLo_04aAfxaW9f552FH5vG8QRwsypM0/edit#heading=h.i8l0os7b3zpg). If you want to build a minikube environment on Ubuntu 20.04 accessed remotely from a Mac PC, refer to [this document](https://docs.google.com/document/d/1GU1yliRe3EcPdNXFMLIkOVv4_1IuyK07RKFlP7IqS1s/edit?usp=sharing).

- Build Image

      % bazel run //projects/xr-light/class-sync-dispatcher/app:csd_image.load

- Check `bazel/projects/xr-light/class-sync-dispatcher/app:csd_image` is exists.

      % docker images
      gcr.io/k8s-minikube/kicbase                           v0.0.40     c6cc01e60919   6 months ago   1.19GB
      projects/xr-light/class-sync-dispatcher/app           csd_image   738f2980aa9c   54 years ago   10.3MB

- Start minikube cluster, add image to minicube cache, and cache reload

      % minikube start
      % minikube image load //projects/xr-light/class-sync-dispatcher/app:csd_image.load
      % minikube cache reload

- Run the following command. The RabbitMQ Broker, Redis, and Vault Client will launch on minikube cluster.

      % kubectl apply -k projects/xr-light/class-sync-dispatcher/k8s/local/

- Run the following command. Check RabbitMQ, Redis, or CSD working.

      $ kubectl -n csd-local get pods
      NAME                               READY   STATUS    RESTARTS         AGE
      couchdb-0                          1/1     Running   37 (3m58s ago)   18d
      csd-app-955f69867-9j24b            1/1     Running   2 (3m18s ago)    7d21h
      rabbitmq-broker-0                  1/1     Running   13 (3m58s ago)   9d
      rabbitmq-broker-77b77fdd59-t6fx2   1/1     Running   20 (3m58s ago)   10d
      rfr-csd-redis-0                    1/1     Running   36 (3m58s ago)   18d
      rfr-csd-redis-1                    1/1     Running   35 (3m58s ago)   18d
      rfr-csd-redis-2                    1/1     Running   37 (3m58s ago)   18d
      rfs-csd-redis-6b864656f-jl8lh      0/1     Running   35 (3m58s ago)   18d
      rfs-csd-redis-6b864656f-nmn7g      0/1     Running   35 (3m58s ago)   18d
      rfs-csd-redis-6b864656f-qzjc5      0/1     Running   35 (3m58s ago)   18d
      vault-server-0                     1/1     Running   36 (3m58s ago)   18d

- After debugging finishes, stop minikube cluster

      minikube stop


## Communication by MQTT/AMQP

### Available message formats
      Protocol Buffers

### Configuring RabbitMQ Broker(local)
      # create user
      kubectl -n csd-local exec -it \
      rabbitmq-broker-server-0 \
      -- rabbitmqctl add_user myuser myuser

      # config vhost permissions
      kubectl -n csd-local exec -it \
      rabbitmq-broker-server-0 \
      -- rabbitmqctl set_permissions -p / myuser ".*" ".*" ".*"

      # add administrator permission
      kubectl -n csd-local exec -it \
      rabbitmq-broker-server-0 \
      -- rabbitmqctl set_user_tags myuser administrator

### MQTT

- Port forward to RabbitMQ Broker(local)

      kubectl port-forward --address 0.0.0.0 service/csd-mosquitto 1883:1883
      Forwarding from 0.0.0.0:1883 -> 1883



- access RabbitMQ Broker by python script

      c.f. tools/mqtt_message_tool.py


### AMQP
- Port forward to RabbitMQ Management(local)

      kubectl -n csd-local port-forward --address 0.0.0.0 service/rabbitmq-service 15672:15672


- create broker resources by rabbitmqadmin commmand

      # Create Exchanges

      # Create Queues

      # Create Bindings


- access RabbitMQ Broker by python script
      
      c.f. (JSON only) tools/amqp_message_tool.py

## Development enviroment on monorepo

Refer to [this URL link](https://github.com/wp-wcm/city/tree/main/docs/development/go#readme) and check updated version all the time.


### Run unit tests

WIP
