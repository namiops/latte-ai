# Superset

## How to use Alerts and Reports function

Agora Superset supports [Alerts and Reports function](https://superset.apache.org/docs/installation/alerts-reports/).
You can receive alerts and reports based on your data and custom SQLs.

- Click `Settings` in the top-left and go to `Alerts & reports` page
  - if you cannot see that, please reach out to @agora-data-orchestration at [#wcm-org-agora-ama](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7) 
- Click `+Alert` (or Click `+Report`) and set the configurations as you like
 - **NOTE** Set `Slack` for `Notification method`. (`Email` is not supported at the moment (February　2024).)
- Go to the target Slack channel and type `/invite @agora-superset-<env>-alerts-reports` e.g. `/invite @agora-superset-dev1-alerts-reports`
