# Local Cluster Access

1. Follow the steps described in [How to access to Keycloak hosted on your EC2](/ns/id/keycloak/docs/int/keycloak_setup.md#how-to-access-to-keycloak-hosted-on-your-ec2). 
 <br> Note that when you configure your `/etc/hosts` in EC2 instance, the right-hand side should contain all the hosts that you want to access from your local machine.
    ```/etc/hosts
    <EXTERNAL-IP> id.woven-city.local observability.woven-city.local testkube.woven-city.local
    ```
2. You should be able to access services deployed in your local cluster from your local browser 🎉
3. Here are some links that you might want to check out:

    | Destination   | Link                                               |
    |---------------|----------------------------------------------------|
    | Keycloak      | https://id.woven-city.local/                       |
    | Observability | https://observability.woven-city.local/            |
    | Grafana       | https://observability.woven-city.local/grafana/    |
    | Jaeger        | https://observability.woven-city.local/jaeger/     |
    | Kiali         | https://observability.woven-city.local/kiali/      |
    | Prometheus    | https://observability.woven-city.local/prometheus/ |
    | Testkube      | https://testkube.woven-city.local/tests            |
