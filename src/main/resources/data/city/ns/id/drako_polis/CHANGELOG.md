# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Added

### Fixed

### Changed

### Removed

## [0.6.0] 2024-08-26

### Added

- Protection for ArgoCD resources. If a DrakoGroup contains the
  label/annotation `argocd.argoproj.io/tracking-id`, DrakoPolis will reject all
  requests for modifications.

## [0.5.1] 2024-07-23

### Changed

- Upgrade dependencies: `drako_data` 0.12.0.

## [0.5.0] - 2024-06-27

### Fixed

- API to check if a user is part of a group (`GET /namespaces/{namespace}/groups/{group}/users/{username}`) now returns the proper HTTP 204 (No Content) instead of HTTP 200 (OK).

### Changed

- API to add users to groups (`POST /namespaces/{namespace}/groups/{group}/users`) now takes a `userId` instead of a `username`. It will also return a `userId` instead of a `username` upon success. 
- API to check if a user belongs to a group (`GET /namespaces/{namespace}/groups/{group}/users/{username}`) now takes `userId` (`GET /namespaces/{namespace}/groups/{group}/users/{userId}`) instead of `username`.
- API to delete a user from a group (`DELETE /namespaces/{namespace}/groups/{group}/users/{username}`) now takes `userId` (`DELETE /namespaces/{namespace}/groups/{group}/users/{userId}`) instead of `username`.
- API to list users belonging to a group (`GET /namespaces/{namespace}/groups/{group}/users`) now returns a list of users identified by their `userId` instead of their `username`.

## [0.4.0] - 2024-06-06

### Added

- New `GET /namespaces/{namespace}/users/{user}` API which returns all groups a user is part of for a given namespace ([reference](http://go/drakopolis-v1-rest-api)).

### Fixed

- Validation bug where groups with names longer than 32 characters could not be managed. The upper limit is now set to 253 characters to match Kubernetes limits.
- Exposure of `GET /namespaces/{namespace}/groups/{group}/users/{username}` API to the HTTP server. Before this fix, the API was not available to clients.

### Changed

- Modified `GET /namespaces/{namespace}/groups/{group}/users` API to support pagination ([reference](http://go/drakopolis-v1-rest-api)).
