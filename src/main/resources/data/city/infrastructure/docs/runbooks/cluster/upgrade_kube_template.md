# Template to announce Kubernetes upgrades

## How to use
This is a template you _can_ use to C&P the announcement to Slack.

Set the variables in the script
  - `$CLUSTER_NAME` : `LAB`,`CI`,`DEV`
  - `$K_VERSION` : _Kubernetes Version_ e.g. `1.23`
  - `$DATETIME` : _Date and Time of the upgrade_
  - `$REASON` : _Add the reason for the upgrade or blank_

## Template

  - edit `upgrade_kube_template.sh`
  - change variables as described above
  - run `upgrade_kube_template.sh`
  - copy output to slack `#wcm-agora-announcement`
