# Drako setup on Speedway

This document is meant to be a self service guide on configuring Drako on Speedway.

!!! Note
    Please note that the process for setting up Drako may change in future. We will constantly update this guide with the latest information.

For setting up Drako on Speedway, you would require some additional resources apart from Drako resources.
Please follow below steps to employ Drako as the external authorizer for your service in Speedway:

1. Add the labels `drako-buddy.woven-city.global/ignore: 'no'` to the `Service` that you want to protect with Drako under `/metadata/labels` ([example](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tenant-face-recog/speedway/prod/service.yaml#L10)) to let Drako Buddy auto-generate the `EnvoyFilter` for your service. Please refer to the [Drako Buddy documentation](https://developer.woven-city.toyota/docs/default/Component/drako-service/drako_buddy/) for guidance on how to define your `Service`, e.g.:

    ```yaml
    apiVersion: v1
    kind: Service
    metadata:
      name: <resource-name>
      labels:
        drako-buddy.woven-city.global/ignore: 'no'
    spec:
      ports:
        - name: http-<port-name>
          port: 8080
          protocol: TCP
      selector:
        <label-key-1>: <label-value-1>
        <label-key-2>: <label-value-2>
        <label-key-n>: <label-value-n>
    ```

2. Configure the `Sidecar` in your namespace to allow egress connections to Drako ([example](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-tenant-face-recog/speedway/prod/sidecar.yaml#L13)), e.g.:

    ```yaml
    apiVersion: networking.istio.io/v1beta1
    kind: Sidecar
    metadata:
      name: <resource-name>
    spec:
      egress:
        - hosts:
            - "./*"
            - "istio-system/*"
            - "agora-id-dev/drako-v1.agora-id-dev.svc.cluster.local" # for Speedway dev
            - "agora-id-prod/drako-v1.agora-id-prod.svc.cluster.local" # for Speedway prod
    ```

3. Currently, Drako in Speedway dev and prod is still using Keycloak in preprod environment while waiting for Keycloak in Speedway to be ready. Therefore, for Drako to work properly you need to deploy a `KeycloakClient` associated with your namespace to _preprod environment_. Make sure that the `clientId` defined in `KeycloakClient` is the same as the name of your namespace in Speedway ([example](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/face-recog/keycloakclient-speedway-agora-tenant-face-recog-prod.yaml#L8)). Please also make sure that you setup the correct `redirectUris`, `rootUrl`, and `webOrigins`. If you don't have any namespace yet in preprod environment, please reach out to Devrel on [#wcm-org-agora-devrel](https://toyotaglobal.enterprise.slack.com/archives/C0415J5P1FD) to help you create a namespace to deploy your `KeycloakClient`.

After the above setup is completed you can deploy your Drako resources, i.e., `DrakoGroup`, `DrakoPolicy`, and `DrakoPolicyBinding`. Please refer to the [Drako documentation](https://developer.woven-city.toyota/docs/default/component/drako-service/) for guidance on how to setup Drako resources for your services.

If you require an access token to test your endpoints use `x-auth-request-access-token` field in: <https://dev-echo-id-test.woven-city.toyota/> (dev) and <https://echo-id-test.woven-city.toyota/> (prod).

If you have any questions please reach out to Devrel on [#wcm-org-agora-devrel](https://toyotaglobal.enterprise.slack.com/archives/C0415J5P1FD).
