# Agora Speedway

Speedway is the codename for Agora's third generation environment for production, built on Stargate Multi-Cloud (SMC).
Up until now, the teams developing services in Agora have done so on `dev` (legacy, first generation platform) or `pre-prod` (second generation). The next step is for those services to run on Speedway.

This document details the steps necessary for a team to self-onboard to Speedway.

!!! warning
    Due to ongoing changes, please follow the instructions in this document carefully and check back periodically for new updates.
    If you have any questions, please reach out to us in the [Agora AMA channel](https://toyotaglobal.enterprise.slack.com/archives/C02CVJLTMJ7).

## Migration schedule from other environments

The current options for migrating environments are:

- Projects currently in the `dev` cluster will be migrated to Speedway no later than September 2024. If you want to migrate right away, you can move to `pre-prod` at your own convenience.
- Projects in `pre-prod` will be able to transition to Speedway no later than September 2024.
- New projects or projects not currently on Agora can onboard to `pre-prod` or directly to Speedway after September 2024.

You can refer to the [Speedway launch queue](https://wovencity.monday.com/boards/6862330327) for information on teams with a planned onboarding.

The deprecation plan for `dev` and `pre-prod` will be announced in the future. For now, services on these environments can continue as usual.

## Overview

Generally, migrating a project to Speedway includes the following:

1. Namespace request and creation
1. CityCD setup
1. Vault
1. Postgres
1. SecureKVS
1. KeycloakClient/WovenID setup
1. Deployments
1. AuthPolices, VirtualService, etc.
1. Gateway configurations and stable URLs

## First step: Namespace creation

Currently, you **cannot** create namespaces in Speedway on your own.

Before proceeding with anything else, please fill out [this request form](https://forms.monday.com/forms/e1faf754a33a501dd21dc83134510705?r=use1). The Agora Developer Relations (DevRel) team will help you to create your new Speedway namespace.

You can check the status on [this Board](https://wovencity.monday.com/boards/6889239500). Once the status changes to Done, proceed to [setting up CityCD.](./citycd.md#new-citycd-project-namespace-setup)

!!!info
    Because of SMC policy, namespaces will have a prefix of `agora-` in Speedway.

## Hashicorp Vault

Use of Hashicorp Vault in Agora has its own separate process.

To use Vault you need a Vault Namespace. Request a new Vault Namespace [here](https://wkf.ms/4cblwtS).

For more details on how to start using Vault in Agora please refer to the [Vault Documentation](/docs/default/Component/vault-docs)

## Next Steps

Once your request for a namespace has been completed, you can start by [setting up CityCD](./citycd.md#new-citycd-project-namespace-setup) for your project.

If you have already done so, depending on your project's architecture, please check the other pages in the left navigation menu for each corresponding service migration.
