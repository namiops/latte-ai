# SDLC Phases

## Overview

Within Woven City we use the following Software Development Life Cycle. This maps roughly to various stages of deployment within the City OS platform.

![SDLC Overview](./assets/secure-sdlc-for-woven-city.png)

During the Business Planning Phase all development should take place in the **DEV** environment. As a project moves past the project proposal gate development MUST move to **Staging / Production**

## Storing Sensitive Data

[Storing Sensitive Data](./storing-sensitive-data.md)

## Development Security Requirements

> The first rule of security is to have fun

Development is an area where developers are given lots of tools to explore and test new ideas and concepts. As such security requirements in this area are generous compared to other areas of our deployment infrastructure.

### Additional Data Privacy Requirements in Development Environment

[Data Classification Policy](https://security.woven-planet.tech/information-security-policy/data-classification-policy/)

Information categorized at level L3 or L4 **MUST NOT** be used in the development environment

Anonymized Data (L2) **SHOULD NOT** be used in development.

If you need to test user data for development the recommended method is to generate a synthetic data set where possible and where not please reach to the Woven City Privacy Team ([Slack @Kana Oshimi](https://woven-by-toyota.slack.com/team/U01T1M9UG2W))

## Production Security Requirements
All woven city managed code in Production and Staging should have Snyk and CodeQL deployed into their build systems during the alpha stage of development before proceeding to the Beta stage. 

## More information
<Link to big Woven City SDLC Guide>
