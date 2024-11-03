# IoTSAFE applet provisioning policy

* The requirements for provisioning (writing secret keys, etc.) and remote management for the IoTSAFE applet are specified in the GSMA specification (IoT.04 - Common Implementation Guide to Using the SIM as a ‘Root of Trust’ to Secure IoT Applications, Clause 3.2.3.1).

* Provisioning (writing secret keys, etc.) and remote management are carried out using the APDU command "STORE DATA".

* The implementation status for each function is as follows.

|     | Provisioning and Remote Management by IoT Security Service                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | Implementation Status                                                   |
| --- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------- |
| G1  | The applet shall support key pair provisioning at the factory with or without certificates                                                                                                                                                                                                                                                                                                                                                                                                                                            | supported                                                               |
| G2  | The applet shall support OBKG requests from an administration server. <br/> The public key is returned to the administration server, with optionally CSR generated by applet. <br/><br/>(OBKG: On-Board key generation, a function to generate private keys on the IoTSAFE applet)                                                                                                                                                                                                                                                    | OBKG: supported, CSR is not generated by the applet                     |
| G3  | The applet shall support:<br/>* Injection of certificates from an administration server<br/>* Deletion and revoking of keys (private/public) and certificates from an administration server                                                                                                                                                                                                                                                                                                                                           | supported                                                               |
| G4  | 1. The applet shall allow the establishment of a remote management session at any time. <br/><br/> 2. During a remote management session, the applet shall not block requests from the IoT device middleware. <br/><br/>3. During a remote management session, the applet shall respond to the IoT device middleware with an error if a credential used for the requested operation is in the process of being updated. <br/>The applet shall make it possible for the device to know that a remote management session has completed. | 1: supported, 2: not confirmed (expected to be fine) 3: not implemented |

|     | Local Provisioning and Management (if supported)                                                                                                                                                                                                                             | Implementation Status                                                                                                    |
| --- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| H1  | The applet shall support OBKG requests from the device. <br/> The public key is returned to the device, optionally along with a certificate signing request.                                                                                                                 | supported                                                                                                                |
| H2  | The applet shall support:<br/>• Injection/storage of certificates from the device <br/>• Update/replacement of stored certificates that were generated by the device <br/>• Deletion of key pairs and certificates that were generated by the device                         | supported                                                                                                                |
| H3  | The applet shall ensure certificates are signed by a trusted CA before the certificate is stored within the applet.                                                                                                                                                          | Implementation on the applet is difficult, so it will not be implemented. This will be ensured by the management server. |
| H4  | If a CSR is generated at the applet, it shall be additionally signed using one of the remotely provisioned keys in the applet dedicated to this usage. CSR and attestation signature along with the reference of the key used for signing shall be returned in the response. | CSR is not generated by the applet, so it will not be implemented.                                                       |