# Distributed OpenTelemetry Tracing Project

This sample code shows the implementation of distributed tracing and logging using OpenTelemetry in Go. It consists of two services: a frontend and a backend, both instrumented with OpenTelemetry to provide end-to-end observability.

## Service Overview

### Frontend Service
The frontend service:
- Generates sample spans periodically.
- Sends HTTP POST requests to the backend service.
- Propagates the `traceID` and `spanID` to the backend service via HTTP header `Traceparent`.

### Backend Service
The backend service:
- Listens for incoming HTTP requests from the frontend.
- Extracts the `traceID` from the incoming request headers.
- Generates its own spans, which are automatically associated with the frontend's trace.

## Trace Propagation
This demo shows the flow of distributed tracing:
1. The frontend initiates a trace and generates a unique `traceID`.
2. This `traceID`is propagated to the backend via HTTP headers.
3. The backend extracts the `Traceparent` and creates child spans under the same trace.
4. Both services send their spans to the Telemetry Collector.
5. The result is a complete end-to-end trace of the request flow across services.

## Running the Services

1. Start the backend service:
   ```
   go run backend/main.go
   ```
   The backend will start listening for requests on `localhost:8081`.

2. In a new terminal, start the frontend service:
   ```
   go run frontend/main.go
   ```
   The frontend will begin generating sample spans and sending requests to the backend perioadically.

Both services will generate traces and logs that are sent to the configured Telemetry Collector. You'll see log output in both terminals indicating the flow of requests and the associated `traceID`s.

## Viewing Traces and Logs

To view the generated traces and logs, you'll need access to [Grafana Cloud](https://wcmagoraprod.grafana.net/explore?schemaVersion=1&panes=%7B%22pmk%22:%7B%22datasource%22:%22grafanacloud-traces%22,%22queries%22:%5B%7B%22refId%22:%22A%22,%22datasource%22:%7B%22type%22:%22tempo%22,%22uid%22:%22grafanacloud-traces%22%7D,%22queryType%22:%22traceql%22,%22limit%22:20,%22tableType%22:%22traces%22%7D%5D,%22range%22:%7B%22from%22:%22now-1h%22,%22to%22:%22now%22%7D%7D%7D&orgId=1). You can query the traces, spans, and attached logs by `traceID`.
