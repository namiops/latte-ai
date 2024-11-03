# Keycloak(WildFly)からKeycloak(Quarkus)への切り替え方法について
このドキュメントでは、Keycloak(WildFly)からKeycloak(Quarkus)への移行方法について説明します。

## 構成
Deploymentにより、Keycloakのインスタンスが作成されます。
このインスタンスは[external Keycloak](https://www.keycloak.org/docs/17.0/server_installation/#_external_keycloak)によって管理されます。
Realmはimport-realmからjsonファイルを読み込みます。
Clientは、KeycloakClientリソースによって管理されます。

## 切り替え方法
各環境ごとに、infrastructure/k8s/{env}/id/kustomization.yamlを変更する必要があります。

1点目は、keycloak-0.1.0をコメントアウトして無効化します。
これは、keycloak-0.1.0とkeycloak-0.2.0で使用するリソース名が一致するためです。

次に、KeycloakRealmへのPatch hitをコメントアウトして無効化します。
これは、external keycloakでインスタンスを管理する場合、Realmの編集ができないためです。

最後に、configMapGenerator部分のコメントアウトを有効化します。

infrastructure/k8s/{env}/id/kustomization.yaml
```
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - _namespace.yaml
  # - ../../common/id/keycloak-0.1.0
  - ../../common/id/keycloak-0.2.0
  - ../../common/id/postgres
  - ../../common/id/secure-kvs-0.1.0
  - ../../common/id/sts-0.1.0
  - ../../common/id/ums-0.1.0
  - ../../common/id/external-authorizer-0.1.0
  - keycloak-users.yaml
  - namespace-clients.yaml
images:
  - name: alpine/k8s:1.21.5
    newName: docker.artifactory-ha.tri-ad.tech/alpine/k8s
    newTag: 1.21.5
  - name: alpine
    newName: docker.artifactory-ha.tri-ad.tech/alpine
patches:
  # - path: patches/keycloak-realm/authentication-flows.patch.yaml
  #   target:
  #     kind: KeycloakRealm
  #     name: keycloak-realm-woven
  #     namespace: id
  # - path: patches/keycloak-realm/client-scopes.patch.json
  #   target:
  #     kind: KeycloakRealm
  #     name: keycloak-realm-woven
  #     namespace: id
  # - path: patches/keycloak-realm/master-realm.patch.json
  #   target:
  #     kind: KeycloakRealm
  #     name: master
  #     namespace: id
  # - path: patches/keycloak-realm/roles.patch.json
  #   target:
  #     kind: KeycloakRealm
  #     name: keycloak-realm-woven
  #     namespace: id
  - path: patches/keycloak-virtual-service.patch.yaml
  - path: patches/postgres-patch.json
    target:
      kind: PostgresCluster
      name: postgresql
      namespace: id
  - path: patches/secure-kvs.patch.yaml
configMapGenerator:
  - name: woven-realm-config
    behavior: merge
    files:
    - patches/keycloak-realm/woven-realm.json
```

## Keycloak(WildFly)とKeycloak(Quarkus)の差異
- keycloak adminユーザーのID/PWが違います。 admin/adminでアクセスできます。
- userのimportはしてません。(unmanagedのKeyclaokで管理をしているため、CR:KeycloakUserを使用してUserを作成することが出来ません。)
- External Authorizerとの動作確認はとれていません。

## 未解決の事象
- External Authorizerの動作確認時に、keycloakのログに以下のような警告が表示されます。詳しくはJiraに記入しています。
```
2022-11-14 07:51:08,356 WARN  [org.keycloak.events] (executor-thread-45) type=PERMISSION_TOKEN_ERROR, realmId=c163b2ae-8263-429f-bcb5-da7d1cd217c7, clientId=httpbin, userId=null, │
│  ipAddress=127.0.0.6, error=invalid_token, reason='HTTP 500 Internal Server Error', auth_method=oauth_credentials, audience=httpbin, grant_type=urn:ietf:params:oauth:grant-type:u │
│ ma-ticket, permission=4ebaf285-33c9-4c0e-89c4-8774efe9eda0, client_auth_method=client-secret
```
https://jira.tri-ad.tech/browse/CITYPF-2217

