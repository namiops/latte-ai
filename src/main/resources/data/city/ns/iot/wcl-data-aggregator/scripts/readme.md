## Steps to run

1. Download the latest iotactl binary for your platform from [artifactory](https://artifactory-ha.tri-ad.tech/ui/native/wcm-cityos/ns/iot/iota/cmd/iotactl/), rename the file to `iotactl` and move it to a path accessible from the terminal.

2. Create a config file (`~/.iota/dev.toml`) in the home directory as below.
```
tenant = 'test'
idp-url = "https://id.cityos-dev.woven-planet.tech"
service-url = 'https://iot.cityos-dev.woven-planet.tech'
client-id = 'iota_client'
```

3. Execute the script.

```bash
bash ./test-events.sh dev
```

**Notes**

1. The test script will open the browser for SSO login. The login should be successfully completed because the access token is required as part of the request authentication, for the commands executed by the `iotactl`.



2. Successful execution would look like:
```
bash test-events.sh lab
Set active context to "lab"
adding group test-gates-1711701099000...ok
adding device testing-device...ok
provisioning device testing-device...ok
sending a test event to topic test-gates-1711701099000/device/testing-device/event
with message 
{
  "timestampMs": 1711701099000,
  "event": 
{
  "nfcAuthenticationError_enter": {
    "sort": "detected",
    "detail": "Note additional information here."
  },
  "nfcAuthenticationError_exit": {
    "sort": "resolved"
  }
}

}...ok

++++Clean up the environment++++


Deleting group "test-gates-1711701099000"

WARNING!  Deleting a group will result in the deprovisioning and deletion of all devices
contained within the group; this is a non-reversible action. The group "test-gates-1711701099000" contains
1 device(s).

Are you absolutely sure that you wish to delete this group? Y/N 
Deleted group "test-gates-1711701099000" successfully
```
