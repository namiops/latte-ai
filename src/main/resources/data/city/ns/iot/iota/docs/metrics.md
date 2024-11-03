# Metrics
Devices that are provisioned with IoTA can publish time series metrics to a dedicated MQTT topic. Those metrics will then be pushed to Mimir and viewable in Grafana.

![Metrics architecture](diagrams/metrics.png)

# Setup

## Tenant configuration
Before sending metrics over MQTT, some additional setup needs to be performed for your tenant. Please contact the Agora IoT admins to perform this setup. Note that this only needs to be performed once per tenant, so additional devices you add after this will be supported as well.

## Payload Format
The payload could be either OTLP format or any JSON.

### OTLP
Example of [OTLP payload](https://github.com/wp-wcm/city/blob/main/ns/iot/iota/docs/resources/metrics.json), accepts both OTLP JSON or protobuf encoded.

### Any JSON
When sent as plain JSON, some additional configuration is required to extract Prometheus metric out of the payload. Here's an example:

#### Metrics mapping configuration
In order for your metrics to be imported correctly into Mimir, you must define a mapping in YAML that specifies which fields in your JSON body should be treated as metrics.

##### JSON body sent by device
```json
{
  'temperature': 16,
  'humidity': 31,
  'time_s': 1697682357,
  'pressure': [
    {
      'reading': 29,
      'time_ms': 1697682357001
    },
    {
      'reading': 30,
      'time_ms': 1697682357501
    }
  ],
  'test_label': 'sample label'
}
```

##### YAML mapping configuration
```yaml
- name: temperature
  type: value
  path: $.temperature
  epochTimestampPath: $.time_s
  labels:
    test_label: $.test_label
- name: humidity
  type: value
  path: $.humidity
  epochTimestampPath: $.time_s
  labels:
    test_label: $.test_label
- name: pressure
  type: object
  path: "$.pressure[*]"
  epochTimestampMsPath: $.time_ms
  labels:
    test_label: $.test_label
  values:
    reading: $.reading
```

We use [JsonPath](https://github.com/json-path/JsonPath) to extract fields from the JSON body.

`name`: the name of your metric that will be used in Mimir

`type`: `value` if the JsonPath query targets a value, `object` if the query targets a JSON array

`path`: the JsonPath query to the metric in the JSON body

`epochTimestampPath` / `epochTimestampMsPath`: the JsonPath query for the epoch timestamp of the metric. You must provide the timestamp in either seconds (`epochTimestampPath`) or milliseconds (`epochTimestampMsPath`). If using an `object` type metric, this path should be relative to the objects inside the array.

`labels`: optional labels

`values`: if `type` is `object`, `values` defines a list of values and the corresponding JsonPath queries. The name of the value will be appended to `name` and used as the full metric name. The JsonPath should be relative to the object.

# Usage
To send metrics, your device should send a JSON body to a topic using MQTT.

The default topic that is configured during setup is `<group name>/<device name>/metrics`. For example, if your group name is `home` and your device name is `living-room-light`, the topic would be `home/living-room-light/metrics`.

If you wish to configure a different topic to push to Mimir, such as shadow or logs, please contact the Agora IoT admins.

# Grafana
In Grafana, there will be a separate Mimir data source defined per tenant, so make sure to set your data source correctly.
