# the Operation of AT Commands (AT+CCHO, AT+CCHC, AT+CGLA)

To send APDU commands to the IoTSAFE applet, the LTE communication module must support the AT+CCHO, AT+CCHC, and AT+CGLA commands. The procedure to verify compatibility is as follows.

1. Insert the SIM with the IoTSAFE applet installed into the LTE communication module and connect it to a PC or similar device.

1. Prepare an environment where AT commands can be entered (such as a terminal software).

1. Execute the AT+CCHO command.
   - `XXXXXXXX` will output a different value (session ID) each time it is executed.

    ```sh
    # Select the IoTSAFE applet and open a logical channel
    AT+CCHO="A0000001157000000000000049534102"
    +CCHO: XXXXXXXX

    OK
    ```

1. Execute the following AT commands in sequence. Replace `XXXXXXXX` with the value output by AT+CCHO.

    ```sh
    # Send the Compute Signature - init command to the IoTSAFE applet
    AT+CGLA=XXXXXXXX,36,"002A00010D840101A1010391020001920104"
    +CGLA: 4,"9000"

    OK
    # Send the Compute Signature - update command (data to be signed) to the IoTSAFE applet
    AT+CGLA=XXXXXXXX,78,"002B8001229E202CF24DBA5FB0A30E26E83B2AC5B9E29E1B161E5C1FA7425E73043362938B9824"
    +CGLA: 4,"6142"

    OK
    # Receive the response (digital signature) of the above command
    AT+CGLA=XXXXXXXX,10,"00C0000000"
    +CGLA: 136,"334073B3A470B67ADD1B954EAC15CFFB4F6BA77B53D91E35E1244526621D97C385240E2656458895A4F99A73B7E5263C4073C98A4B0D66AC71005871694660B9EC9C9000" 
                                                               
    OK                                                                                                                                                    
    # Close the logical channel
    AT+CCHC=XXXXXXXX
    OK
    ```

1. If all of the above AT commands can be executed (resulting in OK) and the response lengths are the same as above, it can be determined that the LTE communication module used for verification supports the AT commands (AT+CCHO, AT+CCHC, AT+CGLA).
