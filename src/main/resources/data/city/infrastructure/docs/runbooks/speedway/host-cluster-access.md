# SMC host cluster access

These are instructions on how to grant a team permissions to access their namespaces on the host cluster. This is assuming that their namespaces have already been created.

* To create a new group, go to **USER GROUPS** in our **Woven City - Shared** project [https://portal.tmc-stargate.com/projects/76](https://portal.tmc-stargate.com/projects/76). Currently only Wilson, Ming, Nico, Spencer, Vincent and Wojtek have permissions to **CREATE GROUP**. So you could potentially ask any one of these to create a group and populate with members for you. You need to create group first before you can add users. Then you could action the next part. All groups start with **wcmshrd-** and to keep with convention, we will include agora so **wcmshrd-agora-**.

* Once the group has been created, you will need to grab the UUID of the group. Let's use group [WCMSHRD-AGORA-STORAGE](https://portal.tmc-stargate.com/projects/76/usergroup/3212) as an example. The link address of **MANAGE IN AZURE AD (deprecated)** is https://portal.azure.com/#blade/Microsoft_AAD_IAM/GroupDetailsMenuBlade/Members/groupId/f87c2d01-3045-4a8f-a3da-c7b68b732a44. So the UUID of the group to use later is `f87c2d01-3045-4a8f-a3da-c7b68b732a44`.

* Fork Git repository [mtfuji-namespaces](https://github.tri-ad.tech/TRI-AD/mtfuji-namespaces) and you could follow pull request https://github.tri-ad.tech/TRI-AD/mtfuji-namespaces/pull/1255 as an example.

* Message in [#kubernetes](https://toyotaglobal.enterprise.slack.com/archives/C02JB3YLR1U) channel in Slack with your pull request details and wait for their approval and subsequent merge. Then wait 2 hours for the changes to take effect.

* Afterwards, you can then show a member of this newly created group on how to download the kubeconfig file via https://portal.tmc-stargate.com/mtfuji, set their `KUBECONFIG` environment variable to this file and then show use of `--context` and `--as`.
