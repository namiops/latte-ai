# Proximity Service [![CircleCI](https://circleci.com/gh/artsy/README.svg?style=svg)](https://circleci.com/gh/artsy/README)

Welcome to Proximity Service in Woven City! If you're a new team member, we're excited to have you!


# Concepts

## REST API Server

## Experiment Integration

## Decisioning

Inherent uncertainty about the location prediction and uncertainty about when the next measurement will be available.

### Decision Theory

Which is less about accuracy and more about costs and benefits of decisions; something more likely relevant and tangible.

## Abstraction Layer

### Heuristics

Collecting how streets, sidewalks, corners, crossings, pathways, corridor-mazes, subways, highways are being used.
The angle is mostly historical data about traffic; taking into account multi-seasonality. 

### Statistics

Reasoning about labeling, relying on built features (derived data), and ML models that identify patterns, behavior, intent, purpose and anomalies.


#### Location Prediction

- Probabilistic Location Prediction Method
- Short-term location prediction

- Gaussian-distributed prediction (using Kalman filter)
- Particle filter
- Unscented Kalman filter

## Config

## Providers

### Standards

#### GeoJSON

[RFC-7946: The GeoJSON Format](https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.6)

### Tile 38

## Docker

### Compose

Composing the provider (Tile 38) and the Rust REST API Server with a shared volume.

### Volumes

#### Config

Provider configuration scripts and data.

#### Pre-Recorded

Pre-recorded Lat/Long data needed for CLP replay with external reset, replay control.