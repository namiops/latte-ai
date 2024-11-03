# How to generate a map between GMO-PG and our own error codes

## Background

- We need to provide appropriate error information to users when error occurs.
- We need to hide GMO-PG error codes for the security point of view.

## Procedure

1. Move to `backend/dataImportScripts/gmoError/`
2. Create `gmoPaymentError.csv` that defines the relation between GMO-PG errors and own error codes. This file can be exported from this [spreadsheet](https://docs.google.com/spreadsheets/d/1BtrdfnUWTZtkqSxIoVk55ZUPxTDGJdPARGoncK1Rb84/edit#gid=903527686).

```csv:gmoPaymentError.csv
gmoErrCode,gmoDetailErrCode,errorInfo
E00,E00000001,PAYMENT_FAILED
E00,E00000002,PAYMENT_FAILED
E00,E00000003,PAYMENT_FAILED
E00,E00000010,PAYMENT_FAILED
...
```

- gmoErrCode: Error code included in error response from GMO-PG
- gmoDetailErrCode: Detail error code from GMO-PG
  - You can see the detailed information about gmoErrCode (like E00) and gmoDetailErrCode (like E00000001) from the [GMO documents](https://gmopg_docs:PF%cwa$GmCC@docs.mul-pay.jp/payment/credit/errorcode).
- errorInfo: Our own error information that is sent to FE when an error occurs

3. Execute `$ node csvtojson.js`, then `gmoPaymentError.json` will be created.

```json:gmoPaymentError.json
[
  {
    "gmoErrCode": "E00",
    "gmoDetailErrCode": "E00000001",
    "errorInfo": "PAYMENT_FAILED"
  },
  {
    "gmoErrCode": "E00",
    "gmoDetailErrCode": "E00000002",
    "errorInfo": "PAYMENT_FAILED"
  },
  {
    "gmoErrCode": "E00",
    "gmoDetailErrCode": "E00000003",
    "errorInfo": "PAYMENT_FAILED"
  },
  {
    "gmoErrCode": "E00",
    "gmoDetailErrCode": "E00000010",
    "errorInfo": "PAYMENT_FAILED"
  },
  ...
]
```

4. Move `gmoPaymentError.json` to `backend/utils/`.
