# Powerporter Backend

## Prerequisite

Set your working directory to here (`projects/ems-kit/v2h/backend/main`).

## Local environment

### Run backend

```shell
$ export V2H_NEXTDRIVE_API_KEY={API Key}
$ go run main.go
```

### Start charging in the browser

1. Connecting a VPN
2. Replace deviceId
3. Open the Chrome console and run the following code

```js
document.body.insertAdjacentHTML("beforeend", `<form id="TEST" action="http://localhost:8000/v2h/api/v1/devices/{deviceId}/charge" method="post"></form>`)
document.getElementById("TEST").submit()
```