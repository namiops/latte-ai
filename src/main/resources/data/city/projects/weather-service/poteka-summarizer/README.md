# POTEKA Consumer

This microservice is responsible for summarizing weather data provided by POTEKA Service and storing summarized data to the database.
The weather data is published every minutes, so this service summarize them to make them easy to use for clients.

## Configuration

## Build and run the project with Bazel

```bash
bazelisk test --cache_test_results=no --test_output=errors //projects/weather-service/poteka-summarizer/...
bazelisk run //projects/weather-service/poteka-summarizer/cmd/poteka-summarizer:poteka-summarizer.binary -- -c=$(pwd)/projects/weather-service/poteka-summarizer/local/config/config.toml
bazelisk run //projects/weather-service/poteka-summarizer:poteka-summarizer
```
