
# Install CLI

## iotactl

The Agora IoT command line tool, [iotactl](https://artifactory-ha.tri-ad.tech/ui/native/wcm-cityos/ns/iot/iota/cmd/iotactl), allows you to manage your fleet of devices, set their status and perform operations such as provisioning and credentials rotations.

## Useful Commands

### Help

You can see a help screen that lists the commands that are accepted by iotactl using the following command:

```bash linenums="0"
iotactl --help
```

To obtain more detailed help about a particular command, use the name of the command with the --help flag:

```sh linenums="0"
iotactl <command> --help
```

Commands can have subcommands associated with them. For instance the _add_ command has _group_ and _device_ subcommands.
Again, detailed help can be obtained for the subcommands:

```sh linenums="0"
iotactl <command> <subcommand> --help
```

So for instance, to obtain help about adding a new device to Agora:

```sh linenums="0"
iotactl add device --help
```

Most commands and subcommands require a number of flags to specify such things as the name of the group and tenant on
which to perform the command. The required flags are always explicitly listed with the command's help. And if you miss a
flag when executing a command, the command will let you know and refuse to continue execution.

### Initialization and context configuration

The CLI allows you to run from multiple clusters you have access to, and can help you switching from a `lab` environment to a `dev` or production environment. This can be configured running 

#### iotactl init

This will prompt you for the most often used flags (tenant, group, username and password) and will save your responses
in a context configuration file. It will also ask you for the name of the file to save to, enabling you to have
multiple context configurations if desired. The context configuration will be saved in the file `~/.iota/<name>.toml`
by default, but it is possible to specify an extension such as .json or .yaml if one of those formats is preferable.

!!!Warning 
    Passwords can also be added to this file, but this is not encouraged.

#### iotactl set context
Once you have saved a configuration in a context configuration file, that context becomes active. If you would like to
switch to a previously generated context configuration, use the _set context_ command.
In this case, <context> is the name of the context (without extension) that you gave when prompted by the _init_
command.
```sh linenums="0"
iotactl set context <context>
```

#### iotactl get context

To find out the currently active context, use the _get context_ command.

As can be seen from above, many arguments are required for the creation of groups and devices, particularly for
creating devices. This can be arduous to type every time and using passwords on the command line could represent a leak
as they are saved in the shell's command history. To simplify this, iotactl supports the concept of "configuration
contexts", which are a set of flag values that are placed into a configuration file that is read at startup. For
instance, you might have a context configuration file that looks like the following:

```sh linenums="0"
tenant="bobs-robots"
username="bob"
password="bob12345"
# group="delivery-robots"
```

At startup, these values are read, so that any values found in here no longer need to be passed in command line flags.
We can thus simplify command invocations as following.

Old command (without context configuration):

```sh linenums="0"
iotactl add device <device> --group <group> --tenant <tenant>
```

New command (with context configuration):

```sh linenums="0"
iotactl add device <device>
```

!!! Note
    The `tenant` variable to be input is different from what is stored in Keycloak(stored as `iot-*-tenant`). So if your tenant name is `example`, you should input `example` instead of `iot-example-tenant`.

### Single Sign-On

`iotactl` management commands need a JWT token and this will be retrieved via SSO. A user with a Woven ID and the right scopes can run commands after logging in via Keycloak after running

```sh linenums="0"
iotactl login
```

In general, running this operation manually is not necessary as each command will check if a valid token is stored in a credentials file and, in negative case, start the SSO flow. Users will be prompted with a browser login form and redirected to a success page if the credentials are valid. 

!!! Note
    Valid authentication is not necessarily equal to authorization to run the command and this can still fail if the user is trying to run management commands on a tenant they have no access to.

!!! Info
    Occasionally you might receive the error below as response to a login attempt.
    ```
    2024/08/09 16:56:21 Unable to TokenSource():
    oauth2: "invalid_grant" "Session not active"
    ```
    To solve it go to ~/.iota and delete the .<environment>_credentials file for the cluster you are using. For example for prod ~/.iota/.prod_credentials

### Fleet management

!!! Warning
    The management commands need a JWT in order to work correctly and this will be retrieved via SSO in a browser flow. Thus, these should not be called from a device on which displays are not installed and instead be executed from the operator's laptop.

#### iotactl add group

Adding a group is as simple as running:

```sh linenums="0"
iotactl add group robots --tenant mytenant
```

This will generate a *provisioning secret* to be stored somewhere safe, ideally on each device HSM, so that devices can provision and refresh their own certificates by themselves.

#### iotactl add device

Adding devices to it is as simple as running:

```sh linenums="0"
iotactl add device <device> --group <group> --tenant <tenant>
```

#### iotactl delete device

Once a device has been added to the Agora device registry, it can be removed again using the _delete device_ command.
If it has been provisioned, then it will automatically be deprovisioned as well. Like the _deprovision_ command, this
will invalidate any certificates and message broker credentials that were previously issued.

```sh linenums="0"
iotactl delete device <device> -g <group> -t <tenant>
```
This command cannot be undone, however a device can be re-added at this point simply by running the _add device_ command
again.


#### iotactl delete group

!!! Warning

    Deleting a group is a destructive operation also for its devices as these will be deprovisioned and deleted in cascade. Devices deleted will also be deprovisioned so simply re-adding will not grant provisioning and credentials validity and the provisioning flow needs to be executed again.

This command will deprovision and delete all devices that belong to a group.

```sh linenums="0"
iotactl delete group <group> -t <tenant> 
```

Like the _delete device_ and _provision_ commands, this command cannot be undone. However, unlike those commands, which
operate on a single device, this command operates on all devices in the specified group. So if the group contains 1000
provisioned devices, *all* of those devices will be deprovisioned and deleted! Be absolutely sure that this is what
you want to do before running this command.

### Provisioning

!!! Success "Best practice: run this on the device!"
    The provisioning command will need the *provisioning secret* obtained while adding a group to a tenant to be sent or embedded in a device HSM, but it will not require JWT or single sign on and the suggested best practice is to run the command from the device as it will generate private and public key as well as the CSR necessary to retrieve certificates and keys.

#### iotactl provision <device>

```sh linenums="0"
iotactl provision <device> --group <group> --tenant <tenant> --provision-secret <secret>
```

This is the command that will return the certficates and keys that are used for accessing Agora (they are saved to the `~/.iota/<CLUSTER>/<TENANT>/<GROUP>/` directory), as well as the username and password that are required for accessing the message broker within Agora.

All the files will be saved with the device name as prefix, if everything goes well you should find the following files in the `~/.iota/<CLUSTER>/<TENANT>/<GROUP>/` folder:

- `<deviceName>_broker.json`. The data broker credentials. This will contain tenant, user and password.
- `<deviceName>_deviceSecret.txt`. Used to refresh the certificate or to deprovision the device.
- `<deviceName>_ca.pem`. The CA chain to specify for TLS connection.
- `<deviceName>_crt.pem`. The device certificate returned as response to the CSR.
- `<deviceName>_key.pem`. The key generated locally by iotactl.
To print these credentials to terminal you can use `--expose` in the provisioning command, but this should be avoided in production environment.

#### iotactl deprovision

The opposite of _provision_, this commands deprovisions a device and puts it back in the same state that it was in
after completion of the _add device_ command. Afer executing this command, the certificates issued by the previous
provisioning and the credentials for the message broker are no longer valid.

```sh linenums="0"
iotactl deprovision <device> --group <group> --tenant <tenant> -d <secret>
```

The secret passed in here is the device secret that was saved in `<deviceName>_deviceSecret.txt` upon provisioning.
This command cannot be undone, however a device can be reprovisioned at this point simply by running the _provision_ command again.

#### iotactl refresh provision-secret

If you have forgotten the provisioning secret that was created when adding a group, or if it has been compromised, it
can be recreated using this command.

```sh linenums="0"
iotactl refresh provision-secret <group> --tenant <tenant> --username <username> --password <password>
```

This will generate a new secret that can be used with the _provision_ command. Devices that have already been
provisioned will still be provisioned, but they will not be able to refresh their credentials and will need manual update of the secret if they have been provisioned in bulk.

All future provisioning that occurs in \<group> will need to use the new secret. This is the case regardless of whether the device being provisioned was created before or after that secret was refreshed.

#### iotactl refresh certificate

If the certificate for your provisioned device is expired or will be expiring soon, you can refresh it using this command. After this command completes, your previous certificate will be revoked and will no longer be valid.

Note this command will not work if your device's current certificate has already been revoked. You will need to reprovision your device.

You can refresh your certificate by using either `--device-secret` or `--provision-secret`. The device secret that should be passed after  `--device-secret` is saved in `<deviceName>_deviceSecret.txt`, which is generated upon provisioning.

Generally, it's recommended to use `--device-secret` over `--provision-secret` for refreshing certificates. This is because the device secret is unique to each device, whereas the provisioning secret is shared among devices within a group. If the provisioning secret is revoked or refreshed for some reasons, all devices in the group will be blocked or need a new provisioning secret to refresh their certificates.  However, if the devices use their own unique device secrets, they would not be affected.

Please note that it's not possible to pass both `--device-secret` and `--provision-secret` to the command at the same time.

```sh linenums="0"
iotactl refresh certificate <device> --group <group> --tenant <tenant> --device-secret <device-secret>
iotactl refresh certificate <device> --group <group> --tenant <tenant> --provision-secret <provision-secret>
```

The new certificate and key files will be written to the same location as the  `provision` command, so your previous files will be overwritten. The broker credentials and device secret, however, will not change.

!!! Note
    As of Jul 23, 2024, we have extended the functionalities of the deprovisioning secret to include tasks beyond deprovisioning. As part of these updates, we are renaming `--deprovision-secret` flag to `--device-secret` flag in `iotactl` command and `~/.iota/<CLUSTER>/<TENANT>/<GROUP>/<deviceName>_deprovisioning.txt` to `~/.iota/<CLUSTER>/<TENANT>/<GROUP>/<deviceName>_deviceSecret.txt`. Please be aware that you may encounter some inconsistencies in terms of file names.

### Upgrade CLI 

It is highly recommended to keep the CLI binary up-to-date.
The `upgrade` command supports to replace binary file itself to the latest version.

```sh linenums="0"
iotactl upgrade
```

Note: This command will require the write permission to the file system.

## Useful Options

```bash  linenums="0"
-o, --output <output type>
```

This flag is available on `get*` and `get-shadow*` commands. By adding this flag, the output will be the expected output type. Currently only `json` is available.

??? Example

    ```sh linenums="0"
    ./iotactl get groups -o json
    ```

    Response format is like these:

    === "Success case"

        Content type of under "resp" will be different depending on commands.

        ```json
        {
            "status": "success",
            "response": [
                {
                    "+name": "test"
                },
            ]
        }
        ```

    === "Failure case"

        ```json
        {
            "status": "failed",
            "error": "error details"
        }
        ```

