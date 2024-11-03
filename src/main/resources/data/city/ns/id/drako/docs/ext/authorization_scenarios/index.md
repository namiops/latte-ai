# Overview

This document describes several authorization scenarios and provides guidance on how to use Drako to implement scenarios.

## Scenarios

- [Allow requests to a service from certain users only](limit_users/scenario.md)
- [Allow requests to certain hosts](certain_hosts/scenario.md)
- [Allow requests to certain HTTP paths and/or HTTP methods](http/scenario.md)
- [Allow requests to certain gRPC paths](grpc/scenario.md)
- [Allow requests to a service from another service outside the cluster](service_outside_cluster/scenario.md)
- [Allow requests to a service from another service inside the cluster](service_inside_cluster/scenario.md)
- [Allow requests to a service from other services inside and outside the cluster](service_inout_cluster/scenario.md)
- [Allow requests from a frontend or mobile application](auth_frontend_mobile/scenario.md)
- [Allow requests from a specific frontend app in WovenApp to access a particular backend app](wovenapp_frontend_backend_access/scenario.md)
- [Allow requests if the user ID in the request path is the same as of the authenticated user](mapped_user/scenario.md)
- [Allow CORS requests](cors/scenario.md)
- [Allow requests without authentication](no_authentication/scenario.md)
