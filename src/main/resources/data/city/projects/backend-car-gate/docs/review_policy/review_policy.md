# Review policy

When modifying code or documents in a project,
a PR review by reviewers is required before merging it into main branch.

## Pull Request Rule

When issuing a PR, the title should be written as follows.  

For development in monorepo, the title of the PR should begin with the name space name of the development project + /.  

Normally, changes to code and documentation should have a JIRA ticket tied to the work,
so include the Jira issue key in the title of the Pull Request.
PR titles should be prefixed with a prefix. (Prefixes should be written in [] brackets.)
Prefixes should be assigned in the following classifications generally basically.

| prefix | means |
|---------|--------|
| fix  |   Bug fix |
| hotfix | Critical (urgent) bug fixes |
| feat | Add new feature |
| update | Update feature |
| change | Specification changes to existing functions |
| refactor | Refactoring |
| docs | Add or update documentation |
| test | Add or modify test code |
| chore | Update only those generated automatically by builds and scripts |

If there is no prefix that applies, please find the most appropriate prefix and assigned it.

Below is an example of a PR title.

```name_space/[fix] xxxx fixed summary```
("name_space" is name space name of this project. "xxxx" is JIRA's ticket number.)

Please use the following template for what to include in your PR.
[PR Template](https://github.com/wp-wcm/city/projects/backend-car-gate/pull_request_template.md)

The template includes a checklist.
Please review and check these contents before submitting your PR.

### *About the Reviewer*

Reviewers should be determined through communication within the team as appropriate. The following is preferred whenever possible.

Assign a development person for this system who has submitted a PR. When modifying documents related to external specifications, please include the people who will be using this system as reviewers.

If you have one developers for this system, please assign at least one other reviewers. Please assign someone who knows the specifications of the system as much as possible, and someone who is familiar with the Backend if it is Backend, and someone who is familiar with the Frontend if it is Frontend.

## Timing of merge into main branch

The timing for merging into the main branch should be subject to the following conditions.  

- If it is source code, it must be approved by at least one of the reviewers.
- In the case of specifications, at least one person who is developing this system and one person who will use this system, respectively, must approve the specifications.
- If the document is important to the overall project, it should be approved by all reviewers.  
(Such documents should request a [collective review](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=182945460).)

## Update Timing of Monorepo's Container Image

The container image in agora is not updated simply by merging the Monorepo project into Main branch.  

A PullRequest for the container update is automatically generated and must be Approve.  

Basically, the person who merged into the Main branch should Approve.  
(Surely the person will want to check the behavior of the container image.)  

This is no problem to do blindly when updates are needed.  

But, do not Approve if you want to refrain from updating immediately, for example, when the system is in use.

**Note: Any changes to k8s settings will be reflected at the main branch merge stage.  
If the introduction of this setting affects the behavior of the container image before the update, merging into the main branch should be done at the same time as the container image update.**
