<!--
Pull request title example: traffic-signal/[{prefix}] {ticket number} {title} 
You can find the list of prefixes in `docs/review_policy/review_policy.md`.
-->

## Ticket

- https://jira.tri-ad.tech/browse/TRAFFICSYS-XXX
<!-- Write JIRA ticket URL -->

## Description

<!-- Describe the main purpose of this pull request. -->

## How to check

<!-- Describe procedures of the verification, expected results, etc. -->

## Checklist

<!--
Checkbox rules:

- [x] <- Verified
- [ ] <- NOT verified
- [-] <- Skipped
-->

- [ ] My code follows the [coding standards and conventions](https://security.woven-planet.tech/application-security-guidelines/secure-coding-best-practices/).
- [ ] My code follows the [coding rules in this project](https://github.com/wp-wcm/city/blob/main/projects/traffic-signal/docs/coding_rules/coding_rules.md).
- [ ] My changes have been tested and verified to work.
- [ ] I have updated the documentation where necessary.
- [ ] I have updated environment variables where necessary.
- [ ] All environment variables in deployment.yaml are string type.
- [ ] I have increased agora version to be written [here](https://github.com/wp-wcm/city/blob/61885bd5999ab54ced1ab26bac329f346ebdaa9f/infrastructure/docs/runbooks/observability/grafana-dashboards.md#:~:text=%22agora_version%22%3A%20%221%22%20%20%20%20%20%20%20%23%20%3C%2D%2D%20Increase%20this%20version%20when%20you%20change%20your%20json), because we have updated json file for grafana dashboard.
- [ ] Coverage rate after code change meets test policy.

<!--
If the coverage rate is lower than the test policy value,
Please describe the target directory, coverage rate, and the reason for reflecting the code in this state in Issues.
This is not required for directories for which the coverage ratio is below the test policy value at the time before the code change.
-->

## Issues

<!-- Describe issues if there are. -->
