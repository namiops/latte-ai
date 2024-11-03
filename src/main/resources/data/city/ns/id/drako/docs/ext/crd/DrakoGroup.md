# DrakoGroup

DrakoGroup is a versatile tool for managing and establishing user groups within
Drako. It allows for the creation of a user list comprising multiple users, thus
streamlining group control and user management.

## Usage

### API usage

We provide APIs to manage DrakoGroup resources. Please check the [Drako Polis documentation](/docs/default/Component/drako_polis-service)
for more details.

### DrakoGroup

The primary purpose of DrakoGroup's structure is to manage users within the
group. Here is an example YAML configuration illustrating how a group is set up
using DrakoGroup:

```yaml
apiVersion: woven-city.global/v1alpha2
kind: DrakoGroup
metadata:
  name: <group-name>
  namespace: <namespace>
spec:
  description: <group description>
  userList:
    - userId: <user-1>
    - userId: <user-2>
    - userId: <user-n>
```

#### DrakoGroup Configuration Variables

| Variable           | Type   | Description                                                                      |
|--------------------|--------|----------------------------------------------------------------------------------|
| metadata.name      | String | Defines the name of the group.                                                   |
| metadata.namespace | String | Specifies the namespace where the group will be created.                         |
| spec.description   | Array  | Group description (optional) |
| spec.userList      | Array  | List of users that are part of the group, specified as `- userId: <wovenId>`. |

Figuring a user's Woven ID can be done using [BURR](https://developer.woven-city.toyota/catalog/default/api/burr-core-v2/definition#/Person%20API/get_persons_search). The API supports searching by name or email. Below is an example of searching for a user by email in the preprod (dev2) environment

```bash
$ curl -i "https://burr.agora-dev.w3n.io/core/v2alpha/persons/search?searchKey=emailAddress&searchText=taro.drako@woven-planet.global"
HTTP/2 200
content-type: application/json
date: Fri, 28 Jun 2024 06:50:20 GMT
x-envoy-upstream-service-time: 32
server: istio-envoy

{"items":[{"wovenId":"c73c0167-10cb-48d1-b37b-7ea6e9234160","basicInfo":{"name":{"normative":{"primaryName":"Drako","givenNames":["Taro"]},"phonetic":{"primaryName":"ドラコ","givenNames":["タロウ"]},"latin":{"primaryName":"龍","givenNames":["太郎"]}},"dateOfBirth":"-625-01-01","emailAddress":"taro.drako@woven-planet.global","phoneNumber":"+8011223344"}}],"totalItemsCount":1,"totalPagesCount":1}
```

### DrakoPolicy

[See Group in DrakoPolicy](./DrakoPolicy.md#group)

### DrakoPolicyBinding

[See DrakoPolicyBinding](./DrakoPolicyBinding.md)
