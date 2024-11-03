# Agora Vault Support

| Last Update | 2024-08-05      |
|-------------|-----------------|
| Tags        | Vault, Security |

## Creating a Vault Namespace

All steps are under the Project [here](https://portal.tmc-stargate.com/projects/76)

1. Click `USER GROUPS`
2. Check if there is an existing group for the team. If not, click `CREATE GROUP` If there is a team proceed to **step 9**
3. Set the name of the group. the name should be of the template `wcmshrd-<team/service name>-vault-<admin/viewer>`. There is a character limit of 32 characters. **Lowercase letters, numbers, and hyphens only**: avoid numbers if possible
4. Set the description to have the following for better tracking of teams: Organization Name, and Service or Team Name. For Org Names refer to [go/org](https://go/org)
5. Click `CREATE`
6. Repeat steps 2-5 for the second group (`admin` or `viewer`)
7. Wait for the groups to be propagated. This can take upwards of 30 minutes
8. Once the groups are created, select the `admin` group and add members by their email address. The `viewer` group can be ignored for now, as this is just a requirement on SMC but never really used
9. Back on the [Project Page](https://portal.tmc-stargate.com/projects/76), select `RESOURCES` and click `CREATE RESOURCE`
10. Select `Vault`
11. Set the name of the namespace. SMC will "normalize" the name to their pattern of `ns_stargate/ns_<env>_<name>`. **Lowercase letters and numbers only**: avoid numbers if possible
12. Select all available environments (`Dev`, `Stg`, `Prod`)
13. Select the Admin and Viewer Groups created in Steps 2-5
14. Click `SUBMIT`
15. Done. Please let the team know that their request is processing and that they can verify on Vault UI after up to 30 minutes

## Adding a new member to an existing Vault namespace

All steps are under the Project [here](https://portal.tmc-stargate.com/projects/76)

1. Click `USER GROUPS`
2. Find the existing `admin` group and click it
3. Click `MEMBERS`
4. Click the `+` button in the top right of the table
5. In the pop-up window, enter the email addresses of the new members
6. Click `ADD USERS`
7. Done. Tell the team or members to wait for the group to be updated. This can take upwards of 30 minutes.
