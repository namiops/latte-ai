# Weather Portal

This application is a front end service for Weather Service.  
It contains html, Javascript, css, svg files.

## svg files

The svg files are from JMA [https://www.jma.go.jp/bosai/forecast/img/(weather code).svg]  
The latest weather code list is available by putting "Forecast.Const.TELOPS" in console.

## Build and run the project with Bazel

```bash
bazel run //projects/weather-service/weather-portal:weather-portal
```
