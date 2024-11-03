# set_provision_secret.sh

## Overview

`set_provision_secret.sh` is a bash script to set a provision secret for a eSim through eSimOS.

## Prerequisites

- `curl`
-  Your Sim Card's IoT SAFE applet is updated to the latest version.

Ensure you have the following environment variables set:
- `ESIMOS_EMAIL`: Your email for loging in [eSimOS](https://agora.sim-applet.com/).
- `ESIMOS_PASSWORD`: Your password for authentication.

## Usage

```bash
./set_provision_secret.sh <PKG_ID> <SIM_ID> <MODE> [PROVISION_SECRET]
```

`PKG_ID`: Package ID running in the eSIM, see [here](https://wovencity.monday.com/docs/6521230505?blockId=55d80e99-50b5-4b72-9d74-fd8dc6375511) 

`SIM_ID`: SID ID of the eSIM, see [here](https://wovencity.monday.com/docs/6521230505?blockId=55d80e99-50b5-4b72-9d74-fd8dc6375511)

`MODE`: `CREATE`, `UPDATE` or `DELETE`.
<br> `CREATE` will create the provision secret. This mode should be used if the provision secret does not exist in eSIM.
<br> `UPDATE` will update the provision secret. This mode should be used if the provision secret already exists in the eSIM.
<br> `DELETE` will delete the provision secret.

`PROVISION_SECRET`: The provision secret to create/update in the eSIM.

### Running the Script

1. **Make the script executable** (if not already):
   ```bash
   chmod +x set_provision_secret.sh
   ```

2. **Run the script**:
   - To update the provision secret
   ```bash
   ./set_provision_secret.sh 12 3 UPDATE MySecret
   ```

   - To create the provision secret
   ```bash
   ./set_provision_secret.sh 12 3 CREATE MySecret
   ```

   - To delete the provision secret
   ```bash
   ./set_provision_secret.sh 12 3 DELETE
   ```
