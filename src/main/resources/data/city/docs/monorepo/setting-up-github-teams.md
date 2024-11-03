# Setting Up Github Teams

We are trying to help manage team membership and permissions through Github Teams. This helps us group developers
and apply changes to groups at a time using a flexible configuration method that is already built into the system
that we are using.

## Creating a Team

Teams are created on the organization level. Under the @wp-wcm org, create a team and add whoever is on your team to
the team. If the members of your team are not in the organization, they need to be added to the org. This can be done
in the same step as adding them to the team. If your team member does not show up, then that means they are not part
of the Github Enterprise server and will need to be added by IT.

## Adding the team to the Monorepo

Currently, there is no automation to add teams to the monorepo. So, it will have to be requested manually. Please contact
the CI/CD team on Slack in the [wcm-cicd-support](https://toyotaglobal.enterprise.slack.com/archives/C02660CMJLT) channel.

## Establishing Code Ownership

To protect your own team's code and facilitate the workflow and ease of development, we highly recommend establishing
code ownership of your own code. To do this, just add your team as a codeowner in the [CODEOWNERS](https://github.com/wp-wcm/city/blob/main/CODEOWNERS) file when
creating a new directory. To assist in organization, please follow the formatting convention:

```plain
###############################################################################
# Project Ownership
###############################################################################

### <YOUR TEAM NAME> ###

your/directory/or/file @wp-wcm/your-github-team
your/other/directory/or/file @wp-wcm/your-github-team
```
