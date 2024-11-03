# Create a tenant

Tenants can be added to IoTA by Agora IoT admins. If you are not an admin contact the team using the dedicated [#wcm-org-agora-services](https://woven-by-toyota.slack.com/archives/C042AQ2TU4A) slack channel.

??? Info "Admin Flow"
  
    We will use the [agoractl-iota](https://developer.woven-city.toyota/docs/default/component/agoractl-tutorial/plugins/01_agoractl_iota/) plugin to automatically create a new tenant in Keycloak. This [demo](https://drive.google.com/file/d/1R-uKpKOEVbLorXLfyjGOnzBH073YdwgV/view?usp=drive_link) demonstrates how to create and manage the tenant.

??? Info "Self-onboarding (Gen3 production cluster aka Speedway)."

    In order to self-onboard to the production cluster please use [the tenant-creation stack](https://developer.woven-city.toyota/docs/default/component/iota-service/Tasks/onboarding/#system).

??? danger "Manual way to create tenant"

    Groups and devices can be added by anyone with a Woven ID that has _tenant_ permissions.  However, there is currently
    no way to add those permissions automatically.  There is also currently no way to automatically create a new tenant in
    Keycloak.  Both of these actions must be done manually by logging into the [dev](https://id.cityos-dev.woven-planet.tech/auth/) or [lab](https://id.agora-lab.woven-planet.tech/auth/) cluster's Keycloak instance using an account that has admin
    privileges.  For security reasons, how to obtain the admin login credentials is not mentioned here.

    #### Adding a new tenant in Keycloak

    Tenants are based on Keycloak _resources_.  Having access to a tenant's resource gives a particular user the rights
    to provision devices on that tenant.  To create a new tenant resource in Keycloak:

    1. Login to Keycloak and select "Groups" from the "Woven" realm
    2. Click the "New" button and and give the group a name in the form of iot-<tenant_name>-group
    3. Click the "Save" button
    4. Select "Clients" from the "Woven" realm
    5. Search for the "iota" client and select it to display its configuration
    6. Select the "Authorization" tab; A set of authorization related tabs will appear below it
    7. Select the "Resources" tab in the new tabs that have appeared and click the "Create" button
    8. Give it a name in the form iot-<tenant_name>-tenant and click the "Save" button
    9. Select the "Policies" tab and in the "Create Policy..." dropdown select "Group"
    10. Give it a name in the form of iot-<tenant_name>-policy and add the iot-<tenant_name>-group from the Groups list
    11. Click the "Save" button
    12. Select the "Permissions" tab and in the "Create Permission..." dropdown select "Resource-Based"
    13. Give it a name in the form of iot-<tenant_name>-permission
    14. Next to the "Resources" field, click the "Select a resource..." dropdown and search for the tenant name from step #5.
    15. Next to the "Apply Policy" field, click the "Select existing policy..." dropdown and search for the policy name from step #7.
    16. Click the "Save" button

    At this point, you have created a resource for the desired <tenant_name> and have created an access policy and
    permission for granting access to that resource, as well as a group associated with the resource.

    #### Adding a new vhost in RabbitMQ

    While the new tenant is now available for use via Keycloak, it must also be created in RabbitMQ, where it is called a
    "vhost".  To create a new vhost in RabbitMQ we use the topology operator as follows:

    1. Add a vhost to the bottom of the [vhosts](https://github.com/wp-wcm/city/blob/392bcc3510ec8ea8b474d1c1fecc344df52a81e2/    infrastructure/k8s/dev/iot/rabbitmq-vhosts.yaml#L71. file

    ```
    apiVersion: rabbitmq.com/v1beta1
    kind: Vhost
    metadata:
      name: <namespace>-vhost
      namespace: iot
    spec:
      name: <namespace>
      rabbitmqClusterReference:
        name: rabbitmq
    ```

    2. Open a PR and once merged you should see the vhost appear in the [management console](#accessing-rabbitmq-management-ui). (example     [PR](https://github.com/wp-wcm/city/pull/3614))

    Without this step, a user will be able to create groups and devices in their tenant, but the provisioning step will fail
    and they won't be able to send or receive messages via RabbitMQ.

    #### Granting a user's Woven ID access to the tenant

    Access is granted to the resource created above by adding the user's Woven ID account to the group that was created.  To
    do this:

    1. Login to [lab](https://id.agora-lab.woven-planet.tech/auth/) cluster's Keycloak instance  with username and password from [here]   (#retrieve-keycloak-client-secret)
    2. Select "Users" from the "Woven" realm
    3. Search for the user (or add user) for which access to is be granted and select it to display its configuration
    4. Select the "Groups" tab
    5. Select the group created in the above set of steps from the list on the right.  It will be named iot-<tenant_name>-group and click     the "Join" button

    !!! Success "Done!"
        The user with the selected Woven ID can now use his or her Keycloak user name and password to create and delete
        groups and devices under <tenant_name> (but not under other tenants).  To enable access to other tenants, simply add
        the desired tenant's group to the user's list of groups.  To revoke access to the user, remove the appropriate
        tenant's group from the user's Woven ID.
