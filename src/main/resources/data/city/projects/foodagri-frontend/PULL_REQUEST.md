# Pull Request Checklist

Thank you for your contribution to the project! Before submitting this Pull Request, please make sure you have:

- Description of changes :
- [ ] JIRA Link :
- [ ] Attached proof for changes listed in ticket above ( for Frontend mainly )
- [ ] Checked if any API changes have been made, and updated the FE code accordingly?
- [ ] Tested the changes on both mobile and desktop to ensure cross-platform compatibility?
- [ ] Ensured that you have at least 80% coverage for Types and unit tests for the files you've modified?
- [ ] Added E2E test cases for new functionalities or updates?
- [ ] Thoroughly reviewed your own code to catch and fix any issues or errors?
- [ ] Checked if the local development setup needs to be updated based on your changes?
- [ ] Does any new ENV vars need to be added/removed ? Please list here and share secrets on Slack via Password sharing app
- [ ] Not using any package with a GPL License
- [ ] Does PR contains any changes in "order" table or model? If yes please recheck superset dashboards (Post in Slack woven-ec-eng-team for questions/help)
- [ ] Added new Graphql endpoint ? Make sure permissions are updated
- [ ] In case of model/db structure was changed, don't forget to run `pnpm run generateDbDoc` to update auto-generated doc
