# POTEKA Consumer

This microservice is responsible for collecting weather data from POTEKA Service and storing them to the database and the storage.

## External Access

This service accesses to POTEKA to get the weather data.

## Configuration

## Build and run the project with Bazel

```bash
bazelisk test --cache_test_results=no --test_output=errors //projects/weather-service/poteka-consumer/...
bazelisk run //projects/weather-service/poteka-consumer/cmd/poteka-consumer -- -c=$(pwd)/projects/weather-service/poteka-consumer/local/config/config.toml
```
