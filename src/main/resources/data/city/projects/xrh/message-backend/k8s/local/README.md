# How to run message-backend on local cluster

## Run deploy.sh
projects/xrh/message-backend/k8s/local/deploy.sh

----
## Update build files
bazel run //:gazelle

## Build the application image
bazel run //projects/xrh/message-backend/api:server_image.load

## Build rabbitmq image
docker build -t rabbitmq:3.11.7-management projects/xrh/message-backend/k8s/local/rabbitmq/

## Delete a service / deployment (Delete and Deploy again to update the pod with new image !)
kubectl delete -k projects/xrh/message-backend/k8s/local

## Load the application image into minikube environment
minikube image load projects/xrh/message-backend/api:server_image

## Load the rabbitmq image into minikube environment
minikube image load rabbitmq:3.11.7-management

## Create a service / deployment
kubectl apply -k projects/xrh/message-backend/k8s/local

## Expose the service port to the cluster port
kubectl port-forward --address 0.0.0.0 service/rabbitmq 15672:15672

kubectl port-forward --address 0.0.0.0 service/rabbitmq 1883:1883

kubectl port-forward --address 0.0.0.0 service/xrh-message-backend 8000:8000

kubectl port-forward --address 0.0.0.0 service/vault-server 8200:8200

----

## Set new image into the deployment / service container
kubectl set image deployment.apps/xrh-message-backend xrh-message-backend=docker.io/projects/xrh/message-backend/api:server_image

## Rollout the deployment
kubectl rollout restart deployment.apps/xrh-message-backend

## Switch to minikube docker
eval $(minikube docker-env)

## Switch back to host docker
eval $(minikube docker-env -u)

## Get logs of a pod
kubectl logs <podname>
