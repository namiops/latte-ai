# Agora

This is the official Agora Team repository, and used to be known as CityOS

## Background

![Overview](./docs/assets/agora_ovewview.png)

Agora is a platform that is the "digital heart of the Woven City". Agora does its
best to provide "Self-service": empowering its users to deliver high-quality
software with minimal friction. To accomplish this, Agora provides common tools,
service discoverability, networking, security, identification, and many more
things. By providing these features, our service teams can focus on what they're
best at: providing top-quality software and technology to the Woven City.

## Mission

Create a trusted platform and ecosystem that empowers residents, emboldens
inventors, promotes collaboration and enables service delivery.

## Who we are

Please meet the team at the
[Agora Team Introduction](https://docs.google.com/presentation/d/1aIKvJaDbJr_8zY5uHReSadfkEPOAgkK9d1WtLjK4ndI/edit#slide=id.g13b5502b838_0_1)!

## What's new

Agora is developing fast! We perform regular updates at [What’s New in Agora?](https://docs.google.com/presentation/d/1wCqZCV_K_206Cyz8o_tfzmNpNd7CWhl0BJbIo_ujA8I/edit#slide=id.p)

## Where to start

If you are reading this page, it's assumed that you are an internal Agora developer. For you, it's best to browse this
documentation either on GitHub or using a Markdown viewer such as VS Code. This documentation contains "the works" -
that is, everything you need for both developing services to run on Agora, and for developing Agora itself.

If you are on a Woven City service team and are using Agora to host your project, you might be better off browsing the
documentation via the [Developer Portal](https://developer.woven-city.toyota/). The developer portal
contains only a subset of the information provided here, and should provide everything you need to develop services for
Agora, without having to know the internal details of Agora. Of course, if you find such details interesting, you are
welcome to continue reading!

We also provide [Technical Notes](https://docs.google.com/document/d/1cxwfUerDR3jJo7vtJCVPG6as8438bK7wPxeaasGoMww/edit?usp=sharing)
for sharing technical information within the Agora developer community. These provide extra information about Agora and
its services that complement the information here, and again, is targeted at internal Agora developers and not service
developers. If you find that you need to refer to a technical note in order develop a service for Agora, something is
wrong! Please let us know, and we will update the developer portal with the missing information.

For new internal Agora developers who are onboarding, the [Agora Team: Getting Started](https://docs.google.com/document/d/1hukqI4XoPXIgW809DNKkPm_qItLkLqtY4vyw7U1G7n8/edit#heading=h.t2u7xj5ws70q) technical note will help you to get up and
running, including non-technical related things that you need to set up to work with us. Please start from there.

## Directory Structure

### [ns](./ns/README.md)

Namespaces. Agora-related services are found under their respective namespace.

### projects

Similar to the ns directory, but for services that were imported from the backend repo.

### [infrastructure](./infrastructure/README.md)

Infrastructure as code, terraform, kubernetes, helm etc.

### infra

Similar to the infrastructure directory, but for services that were imported from the backend repo. These will
eventually be merged into the infrastructure directory, and the _infra_ directory will go away.

### [docs](./docs/README.md)

Home to many of Agora's documents. This will be a central repository for knowledge in regard to various topics like
tools, infrastructure, example code, and more.

### [tools](./tools/README.md)

Internal tools and configurations.

## Contact Us

* If you have questions about interacting with Agora that aren’t answered in
  above places, or have ideas or feature requests, we’ve set up [the Slack channel
  #wcm-org-agora-ama](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7)
  for you.
* You can also use [GitHub issues](https://github.tri-ad.tech/cityos-platform/cityos/issues/new/choose)
  for bug report and feature request.
