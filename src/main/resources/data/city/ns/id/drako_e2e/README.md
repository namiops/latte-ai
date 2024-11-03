# Drako e2e

A set of end to end tests for drako, more specifically for the Oauth2 and 
SingleUseToken authentication modes.


## Why

Drako contains a relatively simple implementation that depends on the
integration with highly complex systems to properly work. Although
unit tests will make sure we have a good quality overall when adding new
changes, it doesn't validate if our assumptions about the integrations
are valid.

Having those end to end tests can help us validate the implementation.

## Why not session

Priority now (2023/04/05) is to add tests to Oauth2 and SingleUseToken
because we don't have a simple working example of this.

## How does it work

It simply create a few scenarios and call a specifically crafter set
of httpbin instances in the drako-test namespace. This is namespace
is configured using the files in //ns/id/drako/manifests.

## Image

There is target to create an image as well. The goal is to eventually
run this as part of our automated testing.