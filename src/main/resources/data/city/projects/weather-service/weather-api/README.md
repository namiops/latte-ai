# Weather API Service

The Weather API microservice is responsible for exposing an API for clients to get weather data.

## Configuration

## Run the project with Bazel

* Run as binary with local config file (from monorepo root)

```bash
bazelisk test --cache_test_results=no --test_output=errors //projects/weather-service/weather-api/...
bazelisk run //projects/weather-service/weather-api/cmd/weather-api -- -c=$(pwd)/projects/weather-service/weather-api/local/config/config.toml
```
