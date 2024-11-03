# Quickstart

### Environment status

| Cluster       | Status                                                                                                                           |
|---------------|----------------------------------------------------------------------------------------------------------------------------------|
| Speedway-prod | ![IoTA Prod](https://argocd.agora-dev.w3n.io/api/badge?name=agora-iot-prod&revision=true&showAppName=true&namespace=citycd-speedway-prod) |
| Speedway-dev  | ![IoTA Dev](https://argocd.agora-dev.w3n.io/api/badge?name=agora-iot-dev&revision=true&showAppName=true&namespace=citycd-speedway-dev)   |

### Prerequisites

Agora IoT platform (codename IoTA) allows teams to create groups of devices and provision them in order to communicate with the Agora cluster and its IoT message broker.
The following steps are prerequisites to set everything up:

1. **Add a tenant**. This step is necessary to configure a vhost in Agora's IoT message broker and keep separate each team's space. See more details in the dedicated [section](./Tasks/tenant.md).
<br />*Note:* on Gen3 (Speedway) production cluster, you can onboard yourself using [the tenant-creation stack](https://developer.woven-city.toyota/docs/default/Component/iota-service/Tasks/onboarding/#system).
    
2. [Download](https://artifactory-ha.tri-ad.tech/ui/native/wcm-cityos/ns/iot/iota/cmd/iotactl) iotactl CLI for your architecture and environment. Please be aware that the latest version of `iotactl` is compatible only with the Speedway environment. For Dev and Pre-prod environments, you should continue using the legacy versions of `iotactl`.
  
  | Cluster               | Mac (x86-64)         | Mac (Arm64)          | Windows (x86-64)     | Linux (x86-64)       | Linux (Arm64)        |          
  |-----------------------|----------------------|----------------------|----------------------|----------------------|----------------------|
  | [Speedway](https://artifactory-ha.tri-ad.tech/ui/native/wcm-cityos/ns/iot/iota/cmd/iotactl/v1/) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/v1/iotactl_v1_release_darwin_amd64-latest) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/v1/iotactl_v1_release_darwin_arm64-latest) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/v1/iotactl_v1_release_windows_amd64-latest.exe) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/v1/iotactl_v1_release_linux_amd64-latest) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/v1/iotactl_v1_release_linux_arm64-latest) |
  | [Dev and Pre-prod](https://artifactory-ha.tri-ad.tech/ui/native/wcm-cityos/ns/iot/iota/cmd/iotactl/) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/iotactl_release_darwin_amd64-latest) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/iotactl_release_darwin_arm64-latest) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/iotactl_release_windows_amd64-latest.exe) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/iotactl_release_linux_amd64-latest) | [Download](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/iotactl_release_linux_arm64-latest) |
 
  - verify it works with `iotactl --help`
  - you'll need a woven id (keycloak user) to authenticate with SSO
  - learn more about the CLI in the dedicated [documentation](./Tasks/iotactl.md)
    
3. **Add your user to the tenant**: this is an administrative task for now. Ask the admin team to add your authorized users to your newly created tenant.

!!! Note

    While using the CLI you will be redirected to SSO browser login to retrieve a JWT to pass to the backend, but this is not necessary for all the commands. The provisioning / deprovisioning, which are supposedly executed by the IoT device itself to self-enroll, will actually need respectively the provisioning and deprovisioning secrets obtained while creating a group and after the provisioning. This allows devices without display or input peripherals to use iotactl without user inputs.

### Onboard your IoT Device on Agora

To onboard your IoT device onto the Agora platform, there are three primary methods available. Primarily, you can use the Agora UI for a straightforward execution on-screen. However, if that doesn't cover your needs, consider utilizing the iotactl command-line tool or the IoTA API.

- Agora UI: Allows you to complete the setup via a web interface without any command-line operations.
- iotactl: Suitable for command-line operations on devices where the CLI is accessible.
- IoTA API: Use this when the command line is not an option, such as within smartphone applications.

You can access the Agora UI through the following URLs:

- For the pre-production environment: https://agora-ui.agora-dev.w3n.io/admin/iota-devices
- For the development environment: https://agora-ui.cityos-dev.woven-planet.tech/admin/iota-devices

#### Add a group to your tenant
##### Using the Agora UI:
1. Click `Create new group` at the top right, enter a group name, and create it.
2. Select the created group, click `Refresh group provisioning secret`, and save the secret displayed by clicking `Copy secret`.

##### Using iotactl:
Enter the following command in your terminal:
```console
iotactl add group <groupName> -t <tenant>
``` 
Save the provisioning secret somewhere safe. Ideally, in production, this should go on the device for self provisioning in a secure storage hardware component.(Learn more about credentials distribution and [bulk provisioning(10_certificates.md))
#### Add a device to your group
##### Using the Agora UI:
1. Within the created group, click Create device and enter a Device name to register the device.

##### Using iotactl:
```shell
iotactl add device <deviceName> -g <groupName> -t <tenant>
```  

#### Provision the device

!!! Warning
  
    This should be executed from the device itself in order to keep private key and public key always safely stored on the device they belong to. 

!!! Tip

    As a rule of thumb for testing purposes it's good strategy to set the --ttl of the certificate to 730 hours (1 month) to avoid issues while developing. You can extend the duration of the TTL but bear in mind it's bad practice to use longer durations like 6 months.

##### Using iotactl:
Run in the shell

```shell
iotactl provision <deviceName> -g <groupName> --ttl <certificate duration inhours> --provision-secret <secret>
```
This command generates some files:
  
- `<device>_broker.json` the RabbitMQ credentials to connect to the broker

- `<device>_ca.pem`, `<device>_crt.pem` and `<device>_key.pem`: the key andcertificates to establish mTLS and be able to connect to the Agora cluster. Thedefault TTL of the certificate is 168 hours. This can be overridden byspecifying `--ttl` flag along with the provision command.
    
- `<device>_deprovisioning.json`: a secret to pass to deprovision the currentdevice
  
- (*Test only!*) To print to screen all the info you can use the `--expose`flag. This will create the abovementioned files in any case.


!!! example "Full Example"

    If your tenant is `mecha-owners` and your group is `mecha`, in order to provision a `mecha-1 ` device

    ``` shell
    iotactl add group mecha -t mecha-owners 
    #returns provisioning secret secret12345xyz
  
    iotactl add device macha-1 -g mecha -t mecha-owners
  
    iotactl provision mecha-1 -g mecha --ttl 730 -t mecha-owners --provision-secret secret12345xyz
    ```

!!! Tip

    To avoid specifying some flags like `-t` all the time you can configure a toml file in your .iota folder as explained in [iotactl](./Tasks/iotactl.md)

##### Using the IoTA API
When using the IoTA API, you first need to generate a CSR (Certificate Signing Request) before provisioning the device. Select the appropriate API endpoint for your environment:

- For the pre-production environment:https://iot.agora-dev.w3n.io
- For the development environment: https://iot.cityos-dev.woven-planet.tech

Generate the CSR with the following commands:
```
mkdir /tmp/keys
step crypto keypair /tmp/keys/kp.pub /tmp/keys/kp.key --kty RSA --size 4096 --no-password --insecure
step certificate create --key /tmp/keys/kp.key --csr {deviceName}.{groupName}.{tenantId}.iot.[API Endpoint] /tmp/keys/csr.csr
```

Next, you'll need to send an API request to provision the device using the provisioning secret. Since API endpoints vary by environment, please select the appropriate one for your needs:
- For the pre-production environment:https://iot.agora-dev.w3n.io
- For the development environment: https://iot.cityos-dev.woven-planet.tech

For example, using the curl command for device provisioning involves including the device's CSR in the request body and specifying the provisioning secret in the HTTP header.
```
curl -X 'POST' \
  'https://iot.[API Endpoint]/tenants/{tenantId}/groups/{groupName}/devices/{deviceName}/provisioning' \
  -H 'accept: application/json' \
  -H 'x-provisioning-key: [Provisioning Secret]' \
  -H 'Content-Type: application/json' \
  -d '{
  "certRequest": {
    "csr": "-----BEGIN CERTIFICATE REQUEST-----\n[CSR Content]\n-----END CERTIFICATE REQUEST-----\n",
    "format": "pem",
    "ttl": 3600
  },
  "withBrokerAccess": true
}'
```

For more details on provisioning via API, refer to the [Device Provisioning API definition](https://developer.woven-city.toyota/catalog/default/api/iota-api/definition#/Device%20Provisioning/ProvisionDevice).

!!! Warning

    Deprovisioning a device implies that both broker credentials and certificate are revoked and existing MQTT or AMQP connections will be closed. 
      
#### Make your device talk to Agora

Example of a Python [client](https://github.com/wp-wcm/city/tree/main/ns/iot/demo/iotaclient) using the files above to talk to IoTA

Now you are all set and can start using features such as [device shadow](./Concepts/Fleet%20Management/02_shadow.md) to set, report and query your device state or deploy your services in Agora and consume from your own MQTT topics!

#### Device certificate automation

In order to avoid having to manually provision each device, keep track of their certificates' expiration, and periodically refresh them, a small utility program is provided. It is called IoTA Daemon, `iotad`, and you can read more about it in [the following document](./Tasks/iotad.md).

##### Using the IoTA API
For certificate renewal, the process mirrors provisioning in that a Certificate Signing Request (CSR) generation is necessary. Follow these steps to generate a new CSR:

Create a new CSR with the commands below:
```
mkdir /tmp/keys
step crypto keypair /tmp/keys/kp.pub /tmp/keys/kp.key --kty RSA --size 4096 --no-password --insecure
step certificate create --key /tmp/keys/kp.key --csr {deviceName}.{groupName}.{tenantId}.iot.[API Endpoint] /tmp/keys/csr.csr
```

To renew the certificate, send an API request with the provisioning secret. As API endpoints vary by environment, ensure you select the correct one:

- For the pre-production environment: https://iot.agora-dev.w3n.io
- For the development environment: https://iot.cityos-dev.woven-planet.tech

Use the following command to renew the device certificate:
```
curl -X 'POST' \
  'https://iot.[API Endpoint]/tenants/{tenantId}/groups/{groupName}/devices/{deviceName}/certificate/refresh' \
  -H 'accept: application/json' \
  -H 'x-provisioning-key: [Provisioning Secret]' \
  -H 'Content-Type: application/json' \
  -d '{
    "csr": "-----BEGIN CERTIFICATE REQUEST-----\n[CSR Content]\n-----END CERTIFICATE REQUEST-----\n",
  "format": "pem",
  "ttl": 3600
}'
```
For further details, please refer to the [API definition for Device Certificate Renewal](https://developer.woven-city.toyota/catalog/default/api/iota-api/definition#/Device%20Provisioning/RefreshDeviceCertificate).

### Conclusions
The fundamental flow to onboard IoT devices onto the Agora platform can be flexibly achieved using the Agora UI, CLI tools, or the REST API. The Agora UI enables intuitive configuration and management of devices. The CLI, distributed as a binary, allows for self-provisioning on devices. However, for scenarios where compatibility issues arise, a different method is preferred, or when Linux is unavailable (e.g., smartphone applications), building on top of our [REST API](https://developer.woven-city.toyota/catalog/default/api/iota-api/definition) is also a viable option!
