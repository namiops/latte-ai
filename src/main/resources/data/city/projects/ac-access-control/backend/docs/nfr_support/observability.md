# NFR Observability Support Policy

## References

### Enabler backlog

Enabler backlog is the list of tasks to support NFR.
Support policies are written based on the backlog.  
[link](https://docs.google.com/spreadsheets/d/1Q_J-ZG32zZYueIsIqDXAI6xQhRJClczjtjcPc5ZF0jo/edit#gid=0)

### NFR-ADR documents

- [Observability](https://docs.google.com/document/d/17sJ1kz34g7i2mKDWYxx0XaaQBsEgPAgy-WKnZfEmftg/edit#heading=h.7rsf42327jx2)
  - [Logging](https://docs.google.com/document/d/13cBIn_ZjNeSfgvya5reRQB1h6fLRIyb1hksVZyqW5zc/edit)
  - [Tracing](https://docs.google.com/document/d/1mi6y4o33HLblaALV4niu5bcTwGLIUokqpoRnqK7oohM/edit)
  - [Metrics](https://docs.google.com/document/d/1oyaiFFV3xYEjwizzj7Ok7BusvfuDsoY3kyqvtPvJPcE/edit#)
  - [Alert](https://docs.google.com/document/d/1Uhwc0FWXkO_caOlRkimOU6uVg4AgW99IBdvZuLeuwws/edit#)

## Support policy

### Logging

This section describes when and what log must be put in each application.

#### Logging timings

The table shows the list of timings when each backend application must put a log to standard output.  
In the table, the `Format` corresponds to the title of each section described in [the Logging NFR-ADR](https://docs.google.com/document/d/13cBIn_ZjNeSfgvya5reRQB1h6fLRIyb1hksVZyqW5zc/edit#bookmark=id.6nf3ca3dj0am).

| Log output timing                                         | Format                           | Description                                                                                                                                                                                                                                                                                                | Logs will be output in                           | Notes                                                                                                                                                                                                         |
| :-------------------------------------------------------- | :------------------------------- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :----------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| When a server side error occurs                           | -                                | A server must output a log if there is no problem with the content of the request or message, but the server failed to process it. Example: an error that causes a 5xx response on an HTTP server. This item includes when a server receiving an error response from an external service like A3 and BURR. | The error handling section of each function call | Logs should be output immediately at the point where an error occurred. HTTP error responses must be logged in `application` layer because HTTP request module doesn't know which status code is as expected. |
| When sending an HTTP request and receiving the response   | Req/Res log                      | Immediately before sending a request, and immediately after receiving the response                                                                                                                                                                                                                         | An HTTP request module                           |                                                                                                                                                                                                               |
| When receiving an HTTP request and returning the response | Req/Res log                      | Immediately after receiving a request, and immediately before returning the response                                                                                                                                                                                                                       | An HTTP handler middleware                       |                                                                                                                                                                                                               |
| When sending an AMQP message                              | Event transmission/reception log | When sending a message                                                                                                                                                                                                                                                                                     | An AMQP sending module                           |                                                                                                                                                                                                               |
| When receiving an AMQP message                            | Event transmission/reception log | When receiving a message                                                                                                                                                                                                                                                                                   | An AMQP reception module                         |                                                                                                                                                                                                               |

The next table shows when each application must output logs.

| Log output timing                                         | Auth Service API Server | Management Service API Server | Management Service Worker |
| :-------------------------------------------------------- | :---------------------- | :---------------------------- | :------------------------ |
| When a server side error occurs                           | o                       | o                             | o                         |
| When sending an HTTP request and receiving the response   | o                       | o                             |                           |
| When receiving an HTTP request and returning the response | o                       | o                             |                           |
| When sending an AMQP message                              | o                       | o                             | o                         |
| When receiving an AMQP message                            |                         |                               | o                         |

#### Logging formats

Following to the logging NFR-ADR document, JSON is the expected log format.  
The following table shows the list of fields included in each log.  
The `o` denotes that the field must be included in the log.  
See the NFR-ADR document for the detailed explanation of each field.

| Field Names          | When a server side error occurs          | When sending an HTTP request and receiving the response | When receiving an HTTP request and returning the response | When sending an AMQP message | When receiving an AMQP message |
| :------------------- | :--------------------------------------- | :------------------------------------------------------ | :-------------------------------------------------------- | :--------------------------- | :----------------------------- |
| Timestamp            | o                                        | o                                                       | o                                                         | o                            | o                              |
| SeverityText         | o                                        |                                                         |                                                           |                              |                                |
| TraceId              |                                          | o                                                       | o                                                         |                              |                                |
| SpanId               |                                          | o                                                       | o                                                         |                              |                                |
| TraceFlags           |                                          | o                                                       | o                                                         |                              |                                |
| InstrumentationScope | *include if logs are put using a library | *                                                       | *                                                         | *                            | *                              |
| Body                 | o                                        |                                                         |                                                           |                              |                                |
| Attributes           | o                                        | See Req/Res log attributes                              | See Req/Res log attributes                                | See AMQP log attributes      | See AMQP log attributes        |
| Resource             | o                                        | o                                                       | o                                                         | o                            | o                              |

##### HTTP Req/Res log attributes

The table shows the list of fields defined in `Attributes`.  
See the NFR-ADR document for the detailed explanation of each field.  

| Field Names      | When sending an HTTP request and receiving the response | When receiving an HTTP request and returning the response |
| :--------------- | :------------------------------------------------------ | :-------------------------------------------------------- |
| http.methodA     | o                                                       | o                                                         |
| http.targetA     | o                                                       | o                                                         |
| http.host        | o                                                       | o                                                         |
| http.schema      | o                                                       | o                                                         |
| http.status_code | Only after receiving a response                         | Only before sending a response                            |
| http.latency     | Only after receiving a response                         | Only before sending a response                            |

##### AMQP log attributes

The table shows the list of fields defined in `Attributes`.  
See the NFR-ADR document for the detailed explanation of each field.  

| AMQP log attributes            | When sending an AMQP message | When receiving an AMQP message |
| :----------------------------- | :--------------------------- | :----------------------------- |
| messaging.systemmessaging      | o                            | o                              |
| messaging.destinationmessage   | o                            | o                              |
| messaging.status_code          |                              | Only in worker                 |
| messaging.latency              |                              | Only in worker                 |
| messaging.rabbitmq.routing_key | o                            | o                              |

#### Logging implementation

According to the [instrumentation status](https://opentelemetry.io/docs/instrumentation/), openTelemetry Go SDK doesn't support logging. Because of that, we may have to implement logging module by scratch.

### Tracing

Following to the Tracing NFR-ADR, each API Server will

- generate `trace context` and `span` when it receives a request
- put tracing information immediately after generating context, and immediately before returning the response
- set a unique `service.name` value, such as project name.

Management Worker is out of scope because it only communicates with devices and not with other servers.

#### Tracing implementation

We implement tracing feature using the library [opentelemetry-go](https://github.com/open-telemetry/opentelemetry-go).
Example implementation is found [here](https://github.tri-ad.tech/R-D-WCM/ps-ac-implementation-examples/tree/main/openTelemetry).

### Metrics

#### Configured metrics

The following table shows metrics monitored.

| Metrics                                        | Auth API Server | Management API Server | Management Worker | Notes                                           |
| :--------------------------------------------- | :-------------- | :-------------------- | :---------------- | :---------------------------------------------- |
| Error rate per                                 | o               | o                     | o                 | Error: 5xx or corresponding cases.              |
| Number processed by status                     | o               | o                     | o                 | count 2xx and 3xx, 4xx, 5xx cases respectively. |
| Transaction time per case                      | o               | o                     | o                 |                                                 |
| Throughput (processed message per a time unit) | o               | o                     | o                 |                                                 |
| Number of device error logs                    |                 |                       | o                 | Custom metrics                                  |

#### Metrics implementation

Following to the Metrics ADR, we will _publish metrics as Prometheus exporter and register for CityPF service discovery_.  
Please See also [the discussion in slack](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7/p1676434151840269).

>Kohta Natori
  5ヶ月前
Hello 
@Joseph / ジョセフ
, 
@Brad/ブラッド
, Thank you for the onboarding session.
I have a question about metrics.
If I want to see some metrics regarding HTTP requests of my services(like these, my team uses echo for backend) , same kind metrics have been scraped on Istio proxy or somewhere ? or do I need to configure something on my services ?

> Wojtek
  5ヶ月前
Hi 
@Kohta Natori
 -san, you just need to add annotations inside your Deployment/Statefulset, example of annotations https://github.tri-ad.tech/cityos-platform/cityos/blob/4ba37d132c1e61fd292a689f9b45ba0[…]8s/common/cert-manager/cert-manager-1.7.1/cert-manager.yaml

> Later on, we will provide ServiceMonitor feature, but this is not yet implemented on DEV cluster

### Alert

We will set multiple level of alerts based on the Service Level Objective (SLO) for each service mentioned in the [Availability ADR](https://docs.google.com/document/d/1ha_qkVQoboUuFl0u-nnDM1feW_PAVAeuYNohpclGuCk/edit#heading=h.o6tnh6m1pym9).
The below shows the list of alerts and their criteria.
Since the criteria depend on the SLO, the actual values are TBD.

| Alert                       | Fatal             | Error             | Warn | Interval | Notes                  |
| :-------------------------- | :---------------- | :---------------- | :--- | :------- | :--------------------- |
| Number of fatal alerts      | >= 1 fatal alerts | -                 | -    | 1min     |                        |
| Number of error alerts      | -                 | >= 1 error alerts | -    | 1min     |                        |
| Security-related            |                   |                   |      |          | TBD in ADR             |
| Safety-related              |                   |                   |      |          | TBD in ADR             |
| Audit-related               |                   |                   |      |          | TBD in ADR             |
| Error rate per              |                   |                   |      | 1min     |                        |
| Number processed by status  |                   |                   |      | 1min     | _Not needed?_          |
| Transaction time per case   |                   |                   |      | 1min     |                        |
| Throughput                  |                   |                   |      | 1min     | _Not needed?_          |
| Number of device error logs |                   |                   |      | 1min     | Only Management Worker |

#### Alert implementation

Alerts will be set according to the Alert ADR. The details are TBD.
