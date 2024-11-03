# Bazel 101

This directory is for the executable code that is part of the Bazel 101 Tutorial, under the `tutorial` namespace in
Agora. This code is meant to help show off some features in regard to using Bazel in Agora.

The code is heavily commented to help with understanding a very basic use of Bazel, and how it might work with your
application. 

## How to use this Directory
This directory contains a very small and basic example of a Go application that is using Bazel to build itself. This is
meant to be a small example of how Bazel is used, how you get it to run, and the various things you can do with 
it. For this example the three main aspects of using Bazel we want to emphasize is:
* How to build a dependency on Bazel
* How to build an executable binary on Bazel
* How to build a container image (Docker) on Bazel

## So Why Containers?

### Pretend you're back in 199x...

You're a hot shot developer, you've just been hired to work on a new project for a new hit software product. This product
takes data, does something with the data, and produces other data. Groundbreaking! So much money is in your future! 
You start work right away on the project and because you're such a badass at your job, your code is all ready to go. 
It's clean, it's efficient, it works well, it's been tested, heck, you even documented it to perfection.
This puppy works great, and you're confident that it's ready to go, so you copy your code over to the production server
and..

it doesn't work.

Huh? Well that can't be right: you made sure that everything ran correctly: startup, shutdown and everything between.


### The difficulty of 'environments'
As it turns out, after some digging, you find that you built your software on a Windows box, but the box meant to run
the development server is running on a version of RedHat Linux (We'll call this the _RHL Box_ for reference). The RHL Box
has some configuration that denies a few things that your code was able to reach with ease because it was running on your 
development laptop with root or elevated privileges. 

You think this is an easy enough issue to solve, but turns out there would be other issues that are there: you're using a certain 
library (DLL) that is not going to be available in a Linux flavor, or that is missing some features you're using. This
is just the tip of the iceberg

At this point you're wondering how you can make a copy of your machine on the RHL Box and then that way you can just run 
the same code as if the RHL Box is your own box. VMs are one way to accomplish this, but there are additional considerations 
there with management of the VMs. There is one other way...Containers

![img](./assets/dockermeme.jpg)


### What Docker, Bazel, or Containers do for you
At the heart of it. An **image** is your executable in a transportable form, capable of moving from machine to machine, 
with the working and proven theory being that the raw executable itself can run on any underlying hardware because, its 
already been compiled and is ready to work. A **Container** is an instance of your executable running, with given configuration 
that allow it to talk to the underlying system its running on Container tools like **Docker and Bazel** help you make 
these executables by telling ahead of time:
* What files I need to compile
* Are there any configuration files I need to work with?
* What's the executable script?
* Are there any arguments that need to be passed in?

These steps can be automated and made _deterministic_ which means you can predict your code's behavior more readily, and
know that your build is built the same way each and every time. 


## So why Bazel?
[Bazel](https://bazel.build/) is a build tool that lets you work on projects or code that is made up of multiple languages
and build fast. Bazel is designed with a 'monorepo' in mind where, multiple builds are happening at any given time, and 
the faster those builds can be resolved the faster the turnaround for deployment, testing and more. In addition, Bazel
like Docker allows for one to create images that can be used in containers


