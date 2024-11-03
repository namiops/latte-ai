# Features and functionalities

This section provides an overview of the core features or components within the Agora platform, as well as common workflows that you can execute from the [UI panels](https://agora-ui.agora-lab.woven-planet.tech/admin/).

## List of features

* [**Services**](#services)
  * [Notifications](#notifications)
  * [IoTA devices](#iota-devices)
  * [Xenia management](#xenia-management)
* [**Privacy**](#privacy)
  * [Consent management](#consent-management)
  * [Consent service mapping](#consent-service-mapping)
* [**Tools**](#tools)
  * [Icon library](#icon-library)

## Services

### Notifications

You can use the [Notifier UI](https://agora-ui.agora-lab.woven-planet.tech/admin/notifications) to add and configure notifications sent from your service to the end users via different available providers.

For more information on notification services on Agora, refer to our [documentation](https://developer.woven-city.toyota/docs/default/Component/notification-service/).

#### Setting up a notification

To send out a notification, you need to first create a template for it:

1. Upload your template in `.txt` format [here](https://github.tri-ad.tech/cityos-platform/notification-template-registry/).
2. Make a request for approval on the Agora services team [Slack channel](https://toyotaglobal.enterprise.slack.com/archives/C042AQ2TU4A).

Once your template has been approved, you can configure your notification by filling in the fields below.

_***Note:** Currently, our UI does not support saving your notification configurations or viewing your notification history. Any information you have entered will be lost if you hit **Refresh** or exit your browser while the current session is still running._

* **Name:** Enter a unique name for you notification.
* **Provider:** Select the provider through which you would like to send the notification.
* **Template:** Select the template you uploaded above.
* **Data:** Enter any required data parameter values for your custom template. 
* **Recipients:** Enter the CityOS ID(s) of the user(s) you would like to send the notification to.
  * Clicking **Notify me!** auto-populates this field with your own ID.

You can use the buttons in the **Live preview** function on the right to toggle between lock screen and content views on both iOS and Android.

Once you have filled out the above, click **Done** at the top of the page to send out the notification. You will see a confirmation page with the information you provided, as well as the date and time that your notification was sent.

To replicate these settings in another notification, click **Duplicate**.

### IoTA devices

The [IoTA UI](https://agora-ui.agora-lab.woven-planet.tech/admin/iota-devices) enables you to add and provision groups and devices under your tenant space.

To learn more about managing IoTA devices on Agora, refer to our [documentation](https://developer.woven-city.toyota/docs/default/Component/iota-service).

#### Before you start

In order to add or edit groups and devices, you need to be part of a tenant.

* If you don't already have an existing tenant account in Keycloak, make an account creation request on the Agora services team [Slack channel](https://toyotaglobal.enterprise.slack.com/archives/C042AQ2TU4A).
* If you are not subscribed to the corresponding tenant, send an access request to the Agora [services team](https://toyotaglobal.enterprise.slack.com/archives/C042AQ2TU4A) or [developer relations team](https://toyotaglobal.enterprise.slack.com/archives/C0415J5P1FD).

When you add a new group or device on the Agora UI, you become the device owner by default. After that, you can assign ownership to other users with the **Update device owner** function.

All users in the tenant account can freely edit and/or delete devices and groups.

#### Adding a device in a new group

1. Select your tenant account from the dropdown box at the top of the page.
2. Click **Create new group** and enter a unique name for your device group. Click **Confirm** to save the group.
3. Once the new group has been created, find the group name in the list and click on it.
4. On the **Group details** page, click **🗘 Secret** and refresh the group provisioning secret.
5. Copy the secret shown on the next page and store it securely for later use.
   * _**Note:** Make sure to do this, as the secret cannot be recovered once you close this window._
6. From the **Group details** page, click **Create new device**. Enter a unique device name and click **Confirm** to save it.

#### Adding a device to an existing group

1. Find the group name in the list and click on it.
2. On the **Group details** page, click **Create new device**. Enter a unique device name and click **Confirm** to save it.

#### Provisioning your device

When you add a new device, it is not authorized or connected to the IoTA network by default. To provision it for communications, follow the steps below:

1. Expand the device group and click the **Provision device** symbol next to the device name.
2. Enter the group provisioning secret and device expiration time in the respective fields, then click **Provision**.
3. You will be redirected to the confirmation page. Download the device certificate and upload it to your IoT device.
4. If you need to add or modify shadows for your device, see [this page](https://developer.woven-city.toyota/docs/default/Component/iota-service/Concepts/Fleet%20Management/02_shadow/#cli-commands-for-device-shadow) for example CLI commands you can use.
   * _**Note:** Group shadows will be applied to all devices within the group._

### Xenia Management

You can use the [Xenia Management UI](https://agora-ui.agora-lab.woven-planet.tech/admin/xenia-management) to update your IoT devices and device groups using MQTT. Any files you upload here will be stored securely in Amazon S3.

To learn how to make Xenia updates using the CLI tool, refer to our [documentation](https://developer.woven-city.toyota/docs/default/component/iota-service/Concepts/OTA/cli_tutorial/).

#### Before you start

You need to have registered your tenant, group, and device(s) on the UI before applying updates to them. For more information on how to do this, see:
* [IoTA devices: Before you start](#before-you-start-1)
* [Adding a device in a new group](#adding-a-device-in-a-new-group)
* [Adding a device to an existing group](#adding-a-device-to-an-existing-group)

_***Note:** Currently, our UI does not support saving your update configurations or viewing your update history. Any information you have entered will be lost if you hit **Refresh** or exit your browser while the current session is still running._

#### Update by adding a new release

1. Click **Create new distribution**.
2. Configure your update by filling in the fields below.

    * **Name:** Enter a unique name for this distribution.
    * **Tenant:** Select the tenant for the device that you want to update.
    * **Group:** Select the group for the device.
    * Select **Create new release**. 
    * **Create release:** Enter the name of your release file.
    * **Check sum:** Enter the SHA 256 checksum of your file (`sha256sum <YourFilename>.ext` on macOS).
    * **Content length:** Enter the size of your file in bytes.

3. Click **Generate upload URL**.
4. Copy the URL. Then, open a terminal and run `curl <YourUploadURL> --upload-file path/to/file.ext` to upload your file to the Amazon S3 bucket.
5. Once you have uploaded the URL to AWS, return to the UI and select the group/device(s) you want to update and click **Distribute**.

#### Update from an existing release

1. Click **Create new distribution**.
2. Configure your update by filling in the fields below.

    * **Name:** Enter a unique name for this distribution.
    * **Tenant:** Select the tenant for the device that you want to update.
    * **Group:** Select the group for the device.
    * Select **Use existing release**. 
    * **Select release:** Select the release file you want to use from the dropdown menu.

3. Finally, select the group/device(s) you want to update and click **Distribute**.


## Privacy

The [Privacy UI](https://agora-ui.agora-lab.woven-planet.tech/admin/privacy) enables you to configure consent grants and identities for your services.

For more information on Agora privacy services, see:
* [Consent management in Agora](https://developer.woven-city.toyota/docs/default/Component/consent-management-service/en/consent/)
* [PoC privacy checklist](https://docs.google.com/document/d/1jxI9gm398jMucFpKTpaksAqBBemtmSwo290ttpZt0PQ/edit#heading=h.d9rbuf43enco)

### Consent management

You can use the [Consent management UI](https://agora-ui.agora-lab.woven-planet.tech/admin/consent-grant-management/) to grant access to user data for various services.

_***Note:** To manage access consent, the admin for the service needs to first add the service and relevant attributes in the service mapping settings. (See [Consent service mapping](#consent-service-mapping) below for details.)_

* [**Users**](https://agora-ui.agora-lab.woven-planet.tech/admin/consent-grant-management/users) can select their services and grant/revoke consent to access data attributes within these services. 
* [**Admins**](https://agora-ui.agora-lab.woven-planet.tech/admin/consent-grant-management/authority) can grant/revoke access for other users by user ID.
  * To request admin privileges for your account, contact the Agora [developer relations team](https://toyotaglobal.enterprise.slack.com/archives/C0415J5P1FD).

### Consent service mapping

Admins can add services and data attributes (clients) on the [Consent service mapping UI](https://agora-ui.agora-lab.woven-planet.tech/admin/consent-service-management). Access permissions can then be configured through the [consent settings](#consent-management).
