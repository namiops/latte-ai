# Helm Controller

> The Helm Controller is a Kubernetes operator, allowing one to declaratively
manage Helm chart releases with Kubernetes manifests.

Helm is a templating tool to make packages of manifests that are reusable.
Helm installs charts into Kubernetes, creating a new release for each
installation. And to find new charts, you can search Helm chart repositories.

While there are some uses of helm in Agora, helm is very opinionated and has a
tendency to hide errors. You are recommended instead to prerender the template
and ship the resulting manifest directly. Also if at all possible this
prerendering of upstream charts should be done with a script for repeatability.
