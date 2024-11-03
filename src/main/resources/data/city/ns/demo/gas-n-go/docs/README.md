# Gas 'N Go (Agora Serverless Demo)

Welcome! This demo code showcases the use of Serverless in Agora. In this demo
code you will learn about the following

* How Serverless Works In Agora
* The Tools Behind Serverless
* What You Can Do With Serverless

## Contents

* [What Is Serverless](#what-is-serverless)
    * [What You Can Do With Serverless](#what-you-can-do-with-serverless)
* [What We're Building](#what-were-building)
* [What You'll Need](#what-youll-need)
* [Let's Start](#lets-start)

## What Is Serverless

Serverless is an execution model for your applications. With Serverless, the
main purpose is to "use as you go" where, as a developer you only use compute
when you need to use compute. With Serverless applications developers are not
concerned with:

* Hardware Capacity
* Server Configurations
* Server Management
* Server Maintenance
* Fault Tolerance
* Scaling

Serverless also provides a few other benefits to the developer

* **Costs**
  * Serverless can be more cost-effective than having a set quantity of
    servers which most of the time, are being underutilized
  * Serverless is also sometimes described as *"pay as you go"*
* **Speed**
  * Serverless functions are often simple and do one thing or one action. This
    reduces the complexity of the function and allows developers to make
    their applications in the forms of "Steps" where, functions are called in
    sequence or parallel

### What You Can Do With Serverless

There are many things you can do with Serverless, but some common use cases are:

* **Build and API Application**
  * Your API endpoints could be serviced by a series of serverless functions
    instead of a persistent service application
* **Data Processing And Analysis**
  * Data Events or triggers could call serverless functions, that allow your
    applications to process, transform, or store data for further analysis
    by your application down the line
* **Batch Processing**
  * Batch jobs or batch processing could be done as a series of serverless
    functions
* **Event Ingestion**
  * Handling events from a queue, or a message source, could be handled by a
    serverless function

## What We're Building

For our exercise we're pretending to be a gas station or a fuel stop in Woven
City. However, in Woven City, we don't have gas, but rather batteries. Batteries
are the main driver for our technology along with hydrogen cells. So Gas 'N Go
helps by managing the various orders for cells to be either delivered, or,
picked up to be recycled.

![overview](./assets/overview.png)

The way this works for our basic scenario is to have a call that sends the
request to a serverless, which is listening on an exposed API endpoint. When
called, the serverless is triggered, performs a few actions, and then calls
another serverless instance, which in turn does another action that sends a
message out to a given Slack channel

## What You'll Need

The Materials inside of this demo assume you are using Agora, which means you're
on the monorepo, or at a minimum using Agora's CI/CD. These materials could be
used without Agora but your mileage will vary.

These materials take advantage of the following parts of Agora

* Pre-prod Environment
  * We're taking advantage of Gloo Mesh with our setup, you can read up on
    Gloo Mesh [here](https://docs.solo.io/gloo-mesh-enterprise/latest/getting_started/)
  * You can also try Gloo Mesh on your own with this
    live [workshop](https://github.com/solo-io/solo-cop/tree/main/workshops/gloo-mesh-demo)
* Bazel
  * You can read more about Agora's use of Bazel [here](https://github.com/wp-wcm/city/tree/main/docs/development/bazel)
* Knative
  * You can read more about Agora's setup [here](https://github.com/wp-wcm/city/blob/main/infrastructure/docs/runbooks/knative/serving/onboard-guideline.md)
* Agoractl
  * You can find more about this [here](https://developer.woven-city.toyota/docs/default/Component/agoractl-tutorial)
* Zebra
  * You can find more about Zebra [here](https://developer.woven-city.toyota/docs/default/Component/zebra-service)

For the use of the Slack call, you'll need to do the following:

* Create a Slack App
    * You can find more information about this [here](https://api.slack.com/start/apps)
* Set up a Slack Channel

## Let's Start

First we'll go over setting up our [environment](environment.md)

## Contact Us

If you have any feedback or questions about this document, please feel free to
reach out to the Agora Team at the [#wcm-org-agora-ama channel](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7) 
