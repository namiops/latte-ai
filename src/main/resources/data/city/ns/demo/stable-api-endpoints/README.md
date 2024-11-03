# Stable API Endpoints

This directory contains the source code and infra code to help provide teams a
good working example of how to handle API service development. This code tries
to follow the following practices that are outlined in the following documents:

* [TS-1: Stable API Endpoints](https://docs.google.com/document/d/1sCITTcRqHwT3rk96fI5DsXqiGlLZfJe6c2U38h33Vko/edit#heading=h.gj1vzxxalsuk)
* [Engineering Council on Stable API Endpoints](https://docs.google.com/document/d/1zRX34ya0DdWwfsKzHUa7sfHB3kOEc76snjL3P6zPu5c/edit#heading=h.g8iuc4gzpf6k)

## Contents

* [api](./api) contains the API Specification Documentation
* [cmd](./cmd) contains the source code. You can check out the example code
  itself here, as well as see a good way to organize your routing for different
  versions of your service
* [k8s](./k8s) contains infrastructure examples of how a service developer could
  deploy the demo application, with rationale for both

## Motivation

The following is meant to provide a type of scenario with the APIs and why one
might switch or version, what sort of enhancements a developer might make, and
how it would warrant a new API version

### V1

Meant to be a sort of prototype and convey a Proof of concept of an API. When
starting, an MVP is more viable and helps a developer to get initial feedback on
the usability of the API service. V1 is the 'rough draft' from the developer's
POV: they need to get something out the door and to work on enhancements and
other features while V1 is working.

### V2

Shows a step forward after feedback. In V1, `/cookies` conveys little
information to the user, who needs to know how to call the API. From there the
developer wants to keep the prior API working, as there are teams that have
learned to adapt to use V1, and might not switch over immediately. This is
thankfully not a fully public API yet, but if it were, V1 would have to be
supported forever, regardless.

V2 is taking feedback and adapting the response to allow users to see more
information and make the API more usable. In addition, the bakery now serves
donuts. For parity Donuts V1 delivers the same for Cookies V2 to avoid making
the same mistakes twice.