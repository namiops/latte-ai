# Weather Caution Service

This microservice is responsible for collecting weather data and storing them to the database and the storage.

## External Access

This service accesses to JMA web-site to get the weather data.

## Configuration

## Build and run the project with Bazel

```bash
bazelisk test --cache_test_results=no --test_output=errors //projects/weather-service/weather-caution-service/...
bazelisk run //projects/weather-service/weather-caution-service/cmd/weather-caution-service -- -c=$(pwd)/projects/weather-service/weather-caution-service/local/config/config.toml
```
