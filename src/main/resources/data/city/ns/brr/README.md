# Basic Users and Residents Register (BURR, formerly BRR) v1

[[Atlas](http://go/consent-burr-atlas)]
[[Service Page](../service-page/docs/burr/README.md)]
[[API](../brr/api/brr.yaml)]
[[BURR Core v2](../burr)]

The Basic Users and Residents Register (BURR) (formerly BRR = Basic Residents Register)
is an API for Woven inventor and
operator services to utilize personal information about Woven City residents
and users. This service will only collect and store information about residents
who have opted-in to share their information.

Opting-in allows residents to better take advantage of various services
and utilities Woven City will provide, for a more engaging tailored experience.

## Using the BURR

If you're a service developer and want to use the BURR, you can find
documentation aimed at BURR consumers [here](http://go/burr).

## For BURR developers

### Updating the generated API server code with `oapi-codegen`

You must have Docker installed for this.

Run the `generate_coreapi_code_from_oapi_spec.sh` script in the `scripts` subdirectory.
This will (re)generate all relevant code from the OAPI spec files we have.

You can run this script from any working directory inside (inside your repo clone),
it automatically works in the right subdirectories.

### Updating the generated mocks with `mockery`

You must have Docker installed for this.

Run the `generate_coreapi_mocks.sh` script in the `scripts` subdirectory.
This will (re)generate all mocks we have.

You can run this script from any working directory inside (inside your repo clone),
it automatically works in the right subdirectories.

### Running the service
```shell
bazel run //ns/brr/coreapi/cmd/server
```

Send POST requests to `http://localhost:8080/core/v1alpha/person/{personId}` with JSON request body and try [BURR v1 APIs](https://developer.woven-city.toyota/catalog/default/api/brr-api/definition)

### BURR Grafana Dashboard

You can see the BURR dashboard for the dev cluster [here](http://go/burr-dashboard)
and for the lab cluster [here](http://go/burr-dashboard-lab).

For general information on Grafana dashboards in Agora, see
[the documentation](https://developer.woven-city.toyota/docs/default/component/grafana-tutorial)
in the dev portal.

The BURR dashboard JSON source is located in [`infrastructure/k8s/common/brr/grafana-config-0.1.0`](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/infrastructure/k8s/common/brr/grafana-config-0.1.0).

#### Updating the BURR dashboard

The general procedure is:

* Edit the dashboard in the Grafana UI.
  * When referring to the BURR's deployment namespace in queries, always use
    the `$burr_namespace` variable instead of hard-coding the value. We
    currently have two deployments (namespaces `brr` and `brr-b`), and by using
    the variable we can switch the dashboard between them with a dropdown.
* Export the dashboard JSON with "for sharing externally" option:
  * On the dashboard, click the "share" icon next to the dashboard name.
  * In the dialog, switch to the "Export" tab, and turn on the "Export for
    sharing externally" option.
  * Click "Save to file" to download the JSON.
* Replace the content of `.../grafana-config-0.1.0/brr-dashboard.json` with the
  content of the file you just downloaded
* Check the Git diff for any unexpected changes, and make adjustments if
  necessary. For example:
  * You may have changed the time range (`.time`) or refresh interval
    (`.refresh`) of the dashboard without knowing this would be saved. Reset it
    to the committed values unless you want to change it.
  * The `.version` value (near the bottom) was probably exported as whatever
    internal version Grafana currently has of the board. This value isn't
    significant for us, feel free to set it to the next higher number from the
    committed value (e.g. if last committed version was `11`, set it to `12`).
* Update the `agora_version` in `.../grafana-config-0.1.0/grafanadashboard-brr.yaml`
  to the same (next higher) version you used in `brr-dashboard.json`.
* Update the change log in `.../grafana-config-0.1.0/README.md` to explain what
  you're changing. Use the same version number as in the YAML and JSON files above.
* Commit your changes and open a PR.
