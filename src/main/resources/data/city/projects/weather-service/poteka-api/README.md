# POTEKA API

This microservice is responsible for providing weather data published by POTEKA Service.

## Configuration

## Build and run the project with Bazel

```bash
bazelisk test --cache_test_results=no --test_output=errors //projects/weather-service/poteka-api/...
bazelisk run //projects/weather-service/poteka-api/cmd/poteka-api -- -c=$(pwd)/projects/weather-service/poteka-api/local/config/config.toml
```
