<!--
Pull request title example: ac-be/[{prefix}] {ticket number} {title} 
You can find the list of prefixes in `docs/review_policy/review_policy.md`.
-->

## Ticket

- https://jira.tri-ad.tech/browse/CISAM-XXX
<!-- Write JIRA ticket URL -->

## Description

<!-- Describe the main purpose of this pull request. -->

## How to check

<!-- Describe procedures of the verification, expected results, etc. -->

## Review point

- Ensure that the request/response schema matches the corresponding API specification.

## Checklist

<!--
Checkbox rules:

- [x] <- Verified
- [ ] <- NOT verified
- [-] <- Skipped
-->

- [ ] My code follows the [coding standards and conventions](https://security.woven-planet.tech/application-security-guidelines/secure-coding-best-practices/).
- [ ] My code follows the [coding rules in this project](https://github.com/wp-wcm/city/blob/main/projects/ac-access-control/backend/docs/coding_rules/coding_rules.md).
- [ ] My changes have been tested and verified to work.
- [ ] I have updated the documentation where necessary.
  - [ ] The request/response schema matches the corresponding API specification.
  - [ ] I removed the notation `[NOT IMPLEMENTED]` from the corresponding API specification.
- [ ] I have updated environment variables where necessary.
- [ ] Coverage rate after code change meets test policy.

<!--
If the coverage rate is lower than the test policy value,
Please describe the target directory, coverage rate, and the reason for reflecting the code in this state in Issues.
This is not required for directories for which the coverage ratio is below the test policy value at the time before the code change.
-->

## Issues

None
<!-- If there are any issues, remove the `None` above and describe them. -->
