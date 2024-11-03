# Simple Example

This is a simple PHP script that prints out the username provided by
drako and nothing else at root, but also prints the user id on /id.php.

It is built using Dockerfile as an easy example of how to work with
drako. Also contains a Makefile to make building simpler.

More advanced examples, like a simple chat being currently built,
are fully integrated with bazel and CI/CD.


# How to use this?

A few steps are needed:

1. make sure you have a build available of this dockerfile image in your
  local cluster.
1. edit ./manifests/01-basic.yaml so the deployment can find the image.
1. apply manifests one by one, seeing the expected behavior listed here


# 01-basic.yaml

We start with creating the basic kubernetes structures. A namespace, 
service, service account and deployment. Once applied, our pod should
be running:

```bash
❯ kubectl -n example get pods
NAME                          READY   STATUS    RESTARTS   AGE
simple-app-6457f9d6fd-f42rk   2/2     Running   0          4m40s
```

# 02-virtual_service.yaml

With this we expose the app via https://example.woven-city.local.

# 03-keycloak_client.yaml

Drako requires you to have a KeycloakClient with the same name of the
namespace created. This is the last dependency before we start playing
with Drako policies.

This client is created inside the `id` namespace and is usually managed
by the CityService operator.


# 04-authorization_policy.yaml 

Now we need an authorization policy. This is also usually managed by the
CityService operator. It tells istio that your service uses drako as the
authorizer.

However, only by setting this will cause all the requests to 403. Reason
being that we still don't have a drako policy and binding to describing
how to authorize.

# 05-drako_policy_binding.yaml

This file contains a simple policy: using `Legacy` authentication mode
(aka sessions), and the AllowAll policy.

Now, refreshing the page the message should change to include
the username.

# 06-drako_groups.yaml

Now let's apply a simple group policy. This create a DrakoGroup called
`admins` inside the `example` namespace with only one member (`bob`)
and sets our binding to only accepts admins to connect.

Now it will fail.

# 07-bind_to_path.yaml

Now let's say you want to make sure that for `/` any logged user can access.
For this we can change the binding to use the path.

After doing this `/` is accessible but `/id.php` is not accessible by anyone.

# 08-admin_path.yaml

This makes a change where `/`` is accessible by any logged in user, but `/id.php`
requires being part of the `admins.example` (meanmin `admins` group in `example`
namespace).

After this user `alice` has access to `/` but not `/id.php`.

# 09-alice_as_admin.yaml

To demonstrate how groups work, let's add `alice` to the `admins.example` group.

From this moment user `alice` has access to both endpoints.