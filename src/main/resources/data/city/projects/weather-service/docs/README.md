# Weather Service

## Overview

- functions
  - Providing the weather data provided by JMA ([Japan Meteorological Agency][1]). Clients can get weather forecasts and warnings and etc.
  - Providing the weather data observed by the weather gauge installed in Woven City. Clients can get real-time weather data and forecasts in the next 3-36 hours.
- Restrictions for Usage
  - Since JMA is a Japanese organization, most of the data is distributed in Japanese. Basically, it is translated into English in this service, but long sentences such as headlines and very local location names are kept in Japanese.

## Links

- [confluence page](https://confluence.tri-ad.tech/x/04eFFg)
- [open APIs](https://developer.woven-city.toyota/catalog/default/api/weather-api/definition)
- [example web page](https://weather.cityos-dev.woven-planet.tech/weather-portal)

## Services

- [Weather API Service](https://github.com/wp-wcm/city/tree/main/projects/weather-service/weather-api/README.md)
- [Weather Caution Service](https://github.com/wp-wcm/city/tree/main/projects/weather-service/weather-caution-service/README.md)
- [POTEKA API Service](https://github.com/wp-wcm/city/tree/main/projects/weather-service/poteka-api/README.md)
- [POTEKA Consumer Service](https://github.com/wp-wcm/city/tree/main/projects/weather-service/poteka-consumer/README.md)
- [POTEKA Summarizer Service](https://github.com/wp-wcm/city/tree/main/projects/weather-service/poteka-summarizer/README.md)

[1]:https://www.jma.go.jp/
