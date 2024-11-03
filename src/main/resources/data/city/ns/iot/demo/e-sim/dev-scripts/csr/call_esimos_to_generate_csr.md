# call_esimos_to_generate_csr.sh

## Overview

`call_esim_to_generat_csr.sh` is a bash script to ask eSim to generate CSR via the Agora ESIMOS API. The script authenticates using provided credentials to obtain an access token and uses it to send APDU commands to a specified SIM.

## Prerequisites

- `curl`
- `jq`: JSON processor.
-  Your Sim Card's IoT SAFE applet is updated to the latest version.

Ensure you have the following environment variables set:
- `ESIMOS_EMAIL`: Your email for loging in [eSimOS](https://agora.sim-applet.com/).
- `ESIMOS_PASSWORD`: Your password for authentication.

You will also need to set the correct `PKG_ID` and `SIM_ID`. Details can be found [here](https://wovencity.monday.com/docs/6521230505?blockId=55d80e99-50b5-4b72-9d74-fd8dc6375511).
Example below:
```bash
PKG_ID=12
SIM_ID=3
```

## Usage

### Running the Script

1. **Make the script executable** (if not already):
    ```bash
    chmod +x agora_apdu_config.sh
    ```

2. **Run the script with an APDU value**:
    ```bash
    sh call_esimos_to_generate_csr.sh <APDU>
    ```
    Replace `<APDU>` with the actual APDU value you want to send. Example below:
    ```bash
    sh call_esimos_to_generate_csr.sh 80E28000B97db774013150b23081af310b3009060355040613024a50310e300c06035504081305546f6b796f31133011060355040a130a576f76656e2043697479310f300d060355040b1306436974794f533141303f06035504031338746573742d6465766963652d32343037303130322e717a2d746573742d312e746573742e696f742e61676f72612d6c61622e77336e2e696f3127302506092a864886f70d01090116187573657240776f76656e2d706c616e65742e676c6f62616c00
    ```
