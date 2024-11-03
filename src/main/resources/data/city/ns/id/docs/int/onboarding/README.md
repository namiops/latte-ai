# 🎉 Welcome to Agora Identity Team! 🎉

## Introduction

This document complements the existing getting started document available here: [TN-0101 Agora Team: Getting Started](https://go/agora-newhire). 

After following the tutorials there, you might notice that currently we have several clusters: `lab`, `lab2`, and `dev`. 
From the intranet or through the VPN, you can access the homepage of each cluster available here: [`lab`](https://id.agora-lab.woven-planet.tech/), [`lab2`](https://id.agora-lab.w3n.io/), and [`dev`](https://id.cityos-dev.woven-planet.tech/).

We also have a list of technical documents related to identity team to help you understand about our services available here: [Identity Technical Notes Index](http://go/idtn).

## Development

Some details to help you develop our services are available here: [Development](./development.md).

## Local Deployment

To be able to test the features you develop, you might need to deploy your own local cluster.

### Pre-requisite ⚙️

1. Initial setup using [TN-0101 Agora Team: Getting Started](https://go/agora-newhire).
2. Setup EC2 instance using [TN-0101 Agora Team: Getting Started](https://go/agora-newhire).
3. Clone [city repository](https://github.com/wp-wcm/city) in your EC2 instance. 
4. If you are going to deploy to [gen1 environment](#gen1-environment), additionally, you need to setup Minikube on your EC2 instance ([Troubleshooting and FAQs](../troubleshooting_and_faq.md)).

### Gen1 Environment

1. The tutorial to deploy your own local cluster in gen1 is available here: [Local Cluster Deployment](./gen1/local_cluster_deployment.md).

2. After making some changes to a certain service you might need to build a new image and deploy the recently-built image to your existing local cluster. 
   1. The tutorial to build and deploy a new image is available here: [New Image Deployment](./gen1/new_image_deployment.md).

3. Finally, to be able to access your local cluster deployed in your EC2 instance from your local machine, you need to establish a connection between your local machine, your EC2 instance, and minikube (where your local cluster is deployed). 
   1. The tutorial to access your local cluster from your local machine is available here: [Local Cluster Access](./gen1/local_cluster_access.md).

### Speedway Environment

1. The tutorial to deploy your own local cluster in speedway is available here: [Local Cluster Deployment](./speedway/local_cluster_deployment.md).

3. To be able to access your local cluster deployed in your EC2 instance from your local machine, you need to establish a connection between your local machine, your EC2 instance, and kind (where your local cluster is deployed). 
   1. The tutorial to access your local cluster from your local machine is available here: [Local Cluster Access](./speedway/local_cluster_access.md).

2. After making some changes to a certain service you might need to build a new image and deploy the recently-built image to your existing local cluster. 
   1. The tutorial to build and deploy a new image is available here: [New Image Deployment](./speedway/new_image_deployment.md).

## Materials

Some of the materials that might help you understand about our technologies.

### Rust

1.  [The Rust Programming Language Video Series](https://www.youtube.com/playlist?list=PLai5B987bZ9CoVR-QEIN9foz4QCJ0H2Y8)
2.  [The Rust Programming Language Website](https://doc.rust-lang.org/book/)
3.  [The Rust Programming Language, 2nd Edition Book](https://learning.oreilly.com/library/view/the-rust-programming/9781098156817/)
4.  [Programming Rust, 2nd Edition Book](https://learning.oreilly.com/library/view/programming-rust-2nd/9781492052586/)

### Istio

1.  [Fundamentals for Istio Workshop by Solo.io](https://www.credly.com/org/solo-io/badge/fundamentals-for-istio-by-solo-io)
2.  [Istio in Action Book](https://learning.oreilly.com/library/view/istio-in-action/9781617295829/)
