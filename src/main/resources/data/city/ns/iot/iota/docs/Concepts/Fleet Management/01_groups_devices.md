# Tenants, Groups and Devices

![Tenant topology](../../diagrams/tenant-groups.png)

## [Tenant](../../Tasks/tenant.md)
A tenant is the owner of one or more vhosts an has control over the resources created in it, both broker resources (exchanges, queues) and devices. A tenant can own multiple groups and a group be made of multiple devices. To keep data secure and private, vhosts MUST not exchange data directly through a shared user, but should use data pipelines to do so and this applies to both vhosts belonging to the same tenant and vhosts of different tenants.

![Multitenancy](../../diagrams/tenants-isolation.png)

## Group

A group is container of devices. It's used for logical grouping but also for bulk management. [Adding a group](../../Tasks/iotactl.md#iotactl-add-group) creates a provisioning secret and devices using this secret to provision themselves will belong to this group and this group only (unless a device is deprovisioned and added to a new group). 

Being in the same group allows also to block all the devices at once by deleting the group itself or update its status and see it applied to all the devices in cascade. (see [devices shadows](02_shadow.md) for more details)

!!! Success "Best practice"
    Use groups as containers for devices with the same characteristics. This will make easier update them in bulk or update them with one single call. 

## Device

An IoT device can be added to a group and a tenant in few [steps](../../index.md#onboard-your-iot-device-on-agora) and start sending data over MQTT or AMQP immediately after provisioning. 

A device MUST ensure encryption of its partition and disk to protect provisioning secrets and certificate keys and MUST NOT share these with other devices. 

A device is not a service or another program and is meant to be hardware (or a virtual device / simulator if you are developing) running outside the Agora cluster.

!!! Success "Best practice: MQTT topics"
    Although there are no restrictions on the topic on which a device can publish data as long as it's in the correct vhost, it is advised that devices in a group use paths and subpaths of a topic such as `<groupName>/<deviceName>` to make easier permission management, manage correctly the scope of a device and implement autosubscription and automations. An example can be seen in [device logging](../../03_devicelog.md#sending-logs-via-mqtt) where the reserved topic `<DeviceGroupName>/<DeviceName>/logs` is automatically subscribed by IoTA log collector to push device system logs to the observability stack.