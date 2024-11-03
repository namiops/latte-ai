# walkthrough
## Generate CSR
1. Obtain an access token
2. Get packages list
3. Get package info
    * Required parameters
        * packageName
        * packageAID
        * otaVersion
        * appletInstanceAID
4. Update package
    * Set STORE DATA command globally
    * method,path
        * PUT /remoteconfig/package/update/{packageName}/{packageAID}/{otaVersion}/{appletInstanceAID}
    * **Execute only once** when the IOTSAFE CAP file is uploaded. // Currently being modified so that this command is no longer necessary.
5. Set STORE DATA command for CSR generation
    * 
6. Check the status of the package
    * GET /remote-file-management-config/get
## Sample APDU Commands
* All commands are set by a pair of INSTALL(E6) and STOREDATA(E2).
* First INSTALL(E6) command can be omitted because Applet Console automatically makes it.
* Sample commands
* CREATE SLOT AND UPDATE SHORT SIZE FILE
    * **APDU1-1**: 80E280001E741c731370726f766973696f6e696e675f7365637265748301002002000a00
    * **APDU1-2**: 80E6200016000010A000000115700000000000004953410200000000
    * **APDU1-3**: 80E280000C770A3031323334353637383900
    * Detail
        * **APDU1-1**: 80E280001E741c731370726f766973696f6e696e675f7365637265748301002002000a00
            * 80: CLS
            * E2: INS
            * 80: P1
            * 00: P2
            * 1E: Lc
            * 741c731370726f766973696f6e696e675f7365637265748301002002000a: DATA field
                * 74: IoTSA_TLV_INS_CREATE_FILE_SLOT
                * 1c: LENGTH
                * 73: FILE Label
                * 13: LENGTH
                * 70726f766973696f6e696e675f736563726574: FILE NAME(provisioning secret)
                    * 83: File ID
                    * 01: 00
                    * 20: File size(Tag)
                    * 02: Length
                    * 000a: File size(Value)
            * 00: Le
        * **APDU1-2**: 80E6200016000010A000000115700000000000004953410200000000
            * 80: CLS
            * E6: INS
            * 20: P1
            * 00: P2
            * 16: Lc
            * 000010A0000001157000000000000049534102000000: Data Field(GlobalPlatform Card Specification section 11.5.2.3)
                * 00: Length of Load File AID
                * 00: Load File AID
                * 10: Length of Security Domain AID
                * A0000001157000000000000049534102: Security Domain AID 
                * 00: Length of Load File Data Block Hash
                * 00: Length of Load Parameters field
                * 00: Length of Load Token 
            * 00: Le
        * **APDU1-3**: 80E280000C770A3031323334353637383900
            * 80: CLS
            * E2: INS
            * 80: P1
            * 00: P2
            * 0C: Lc
            * 770A303132333343536373839: Data Field
                * 77: IoTSA_TLV_INS_UPDATE_FILE
                * 0A: File size
                * 303132333343536373839: File content(0123456789)
            * 00:Le
* CREATE SLOT AND UPDATE LONG SIZE FILE
    * **APDU2-1**: 80E28000267424731b696f74736166655f636c69656e745f6b6579706169725f636572748301022002022200 // same as APDU1-1
    * **APDU2-2**: 80E6200016000010A000000115700000000000004953410200000000 // same as APDU1-2
    * **APDU2-3**: 80E20100F6778202222d2d2d2d2d424547494e2043455254494649434154452d2d2d2d2d0a4d4949425a5443434151796741774942416749554c75655939347a344b41596234357351793657445a38494974647377436759494b6f5a497a6a3045417749770a4a6a456b4d434947413155454177776255484a70646d46305a534244515342536232393049475a766369424a6231525451555a464d4234584454497a4d5449770a4e6a417a4e4463314d316f5844544d7a4d5449774d7a417a4e4463314d316f774a6a456b4d434947413155454177776255484a70646d46305a534244515342530a6232393049475a766369424a6231525451555a00
    * **APDU2-4**: 80E20101F6464d466b77457759484b6f5a497a6a3043415159494b6f5a497a6a30444151634451674145562b2b68664748310a714a395655626559686a31637a42453749684b4276764f374e6133586b4561354f42696d504a3943394462422f377379436447476568434635716f39307479680a4d316271436a7332734c446752364d594d4259774641594456523052424130774334494a6247396a5957786f62334e304d416f4743437147534d343942414d430a413063414d455143494452514635734c4c37696c5830304f4559677171656f46344c755a76664b6f6c31386955714f30732f3136416942783766492b77562b410a2f554a7a5100
    * **APDU2-5**: 80E281023A73344733796663383342376c695773397633643039727a654e463236413d3d0a2d2d2d2d2d454e442043455254494649434154452d2d2d2d2d0a00
    * Detail
        * **APDU2-3**: 80E20100F6778202222d2d2d2d2d424547494e2043455254494649434154452d2d2d2d2d0a4d4949425a5443434151796741774942416749554c75655939347a344b41596234357351793657445a38494974647377436759494b6f5a497a6a3045417749770a4a6a456b4d434947413155454177776255484a70646d46305a534244515342536232393049475a766369424a6231525451555a464d4234584454497a4d5449770a4e6a417a4e4463314d316f5844544d7a4d5449774d7a417a4e4463314d316f774a6a456b4d434947413155454177776255484a70646d46305a534244515342530a6232393049475a766369424a6231525451555a00
            * 80: CLS
            * E2: INS
            * 01: P1
            * 00: P2(chunk1)
            * F6: length(246 byte)
            * 7782..: Data field
            * 00:Le
        * **APDU2-4**: 80E20101F6464d466b77457759484b6f5a497a6a3043415159494b6f5a497a6a30444151634451674145562b2b68664748310a714a395655626559686a31637a42453749684b4276764f374e6133586b4561354f42696d504a3943394462422f377379436447476568434635716f39307479680a4d316271436a7332734c446752364d594d4259774641594456523052424130774334494a6247396a5957786f62334e304d416f4743437147534d343942414d430a413063414d455143494452514635734c4c37696c5830304f4559677171656f46344c755a76664b6f6c31386955714f30732f3136416942783766492b77562b410a2f554a7a5100
            * 80: CLS
            * E2: INS
            * 01: P1
            * 01: P2(chunk2)
            * F6: length(246 byte)
            * 464d..: Data field
            * 00: Le
        * **APDU2-5**: 80E281023A73344733796663383342376c695773397633643039727a654e463236413d3d0a2d2d2d2d2d454e442043455254494649434154452d2d2d2d2d0a00
            * 80: CLS
            * E2: INS
            * 81: P1(final chunk)
            * 02: P2(chunk3)
* DELETE OBJECT
    * **APDU3-1**:80E2800005760383010000
    * Delete a file
        * **APDU3-1**:80E2800005760383010000
            * 80:CLS
            * E2:INS
            * 80:P1
            * 00:P2
            * 7603830100: Data field
                * 76: DELETE FILE SLOT
                * 03: Length
                * 83: File ID(Tag)
                * 01: Length
                * 00: File ID(value)
            * 00: Le
* SELECT OBJECT AND UPDATE FILE
    * **APDU4-1**: 80E280001A7518731370726F766973696F6E696E675F73656372657483010000
    * **APDU4-2**: 80E6200016000010A000000115700000000000004953410200000000 // same as APDU1-1
    * **APDU4-3**: 80E2800022772058723770417931547a7542344d3955686d5657434835766932334438306a366c00
    * Detail
        * **APDU4-1**: 80E280001A7518731370726F766973696F6E696E675F73656372657483010000
            * 80: CLS
            * E2: INS
            * 16: P1
            * 75: P2
            * 18: Lc
            * 75: IoTSA_TLV_INS_SELECT_OBJECT
                * 18: Length
                * 73: File label(tag)
                * 13: length
                * 70726F766973696F6E696E675F736563726574: ascii code of "provisioning_secret"
                * 83: File ID(tag)
                * 01: length
                * 00: File ID
            * 77: IoTSA_TLV_INS_UPDATE_FILE
                * 20: Length
                * 58723770417931547a7542344d3955686d5657434835766932334438306a366c: Xr7pAy1TzuB4M9UhmVWCH5vi23D80j6l
            * 00: Le
* IoTSA_TLV_INS_SET_CSR // CN=device0.local
    * **APDU5-1**: 80E28100217d1f740131501a30183116301406035504030c0d646576696365302e6c6f63616c00
        * Detail
            * 80: CLS
            * E2: INS
            * 81: P1
            * 00: P2
            * 21: Lc
            * 7d: IoTSA_TLV_INS_SET_CSR
                * 1f: Length
                * 74: Private key label
                * 01: Length
                * 31: ascii code of "1"
                * 50: CSR subject
                * 1a: length
                * 30183116301406035504030c0d646576696365302e6c6f63616c // value
                    - Specifies the subject element of CertificationRequestInfo in RFC2986 as ASN.1 DER-encoded data
                    ```
                    3018:
                      30: Tag indicating SEQUENCE
                      18: Length of this SEQUENCE (24 bytes)
                    3116:
                      31: Tag indicating SET
                      16: Length of SET (22 bytes)
                    3014:
                      30: Tag indicating SEQUENCE
                      14: Length of SEQUENCE (20 bytes)
                    0603550403:
                      06: Tag indicating OBJECT IDENTIFIER (OID)
                      03: Length of OID (3 bytes)
                      550403: OID value (this means 2.5.4.3, Common Name)
                    0c0d646576696365302e6c6f63616c:
                        0c: Tag indicating UTF8String
                        0d: Length of this UTF8String (13 bytes)
                        646576696365302e6c6f63616c: Value of UTF8String ("device0.local")
                    ```
            * 00: Le

* CN=device0.local
`80E28100217d1f740131501a30183116301406035504030c0d646576696365302e6c6f63616c00`
* CN=d0
`80E28100167d14740131500f300d310d300b06035504030c02643000`
* CN=d0,O=o0
`80E28100237d21740131501c301a310d300b06035504030c026430310d300b060355040a0c026f3000`
* CN=device0.local0, Org=My Organization
`80E281003b7d39740131503430323116301406035504030c0d646576696365302e6c6f63616c31183016060355040A0c0F4d79204f7267616e697a6174696f6e00`


30 21                          -- SEQUENCE, 長さ33バイト
  31 0b                        -- SET, 長さ11バイト
    30 09                      -- SEQUENCE, 長さ9バイト
      06 03 55 04 03           -- OBJECT IDENTIFIER (2.5.4.3, Common Name), 長さ3バイト
      0c 02 64 30              -- UTF8String, 長さ2バイト ("d0")
  31 0b                        -- SET, 長さ11バイト
    30 09                      -- SEQUENCE, 長さ9バイト
      06 03 55 04 0a           -- OBJECT IDENTIFIER (2.5.4.10, Organization), 長さ3バイト
      0c 02 6f 30              -- UTF8String, 長さ2バイト ("o0")




80E281003b
7d397401315034
3032
    3116
        3014
            0603550403
            0c0d646576696365302e6c6f63616c

    3118
        3016
            060355040A
            0c0F4d79204f7267616e697a6174696f6e
00





* IoTSA_TLV_INS_SET_CSR // CN=device0.local, O=My Organization
    * **APDU6-2**: 80E281003b7d1f740131503430443118301606035504030c0d646576696365302e6c6f63616c31283026060355040A0c104d79204f7267616e697a6174696f6e




- Structure when adding CN and O:
    ```
    30 xx                   ; SEQUENCE, length 68
       31 18                ; SET, length 24
          30 16             ; SEQUENCE, length 22
             06 03 55 04 03 ; OBJECT IDENTIFIER, length 3, 2.5.4.3 (Common Name)
             0c 0d 64 65 76 69 63 65 30 2e 6c 6f 63 61 6c ; UTF8String, length 13, "device0.local"
       31 28                ; SET, length 40
          30 26             ; SEQUENCE, length 38
             06 03 55 04 0A ; OBJECT IDENTIFIER, length 3, 2.5.4.10 (Organization)
             0c 10 4d 79 20 4f 72 67 61 6e 69 7a 61 74 69 6f 6e ; UTF8String, length 16, "My Organization"
    ```