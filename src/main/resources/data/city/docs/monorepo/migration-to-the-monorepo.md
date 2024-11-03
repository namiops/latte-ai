# Migration to the Monorepo

One of the first steps your team may need to make, is migrating your existing project into this monorepo. For most
teams, we hope for this to be a very simple process. There are two aspects to the migration:

- Adding your project files to the monorepo
- Integrating with bazel (the monorepo's build tool)

Initially, we will focus on getting your project into the monorepo to allow for just a quick shift in repository origin
with minimal interruption on your team's immediate workflow. Afterwards, more time and consideration can be placed into
adopting bazel.

## Setting Up GitHub Permissions

See [Setting Up Github Teams](./setting-up-github-teams.md) and [Creating a New Directory in the Monorepo](./creating-a-new-service.md#creating-a-new-directory-in-the-monorepo).

## Migrating the Project

### Commit History

The first step is deciding the importance of your project's commit history. Because most projects are so new, and
everything is rapidly changing, we encourage teams to not worry about maintaining the commit history as it will allow
migration to be easier. Also, it is important to remember that your old repository can be archived for historical needs.

If you would like to keep your commit history, please reach out to the maintainers of the monorepo to work out a
solution. This will only be accepted under special circumstances where there is clear value for this effort.

### Preparing Your Monorepo Project

See the [new directory creation guide](./new-project.md#creating-a-new-directory-in-the-monorepo) please.

### Adding Your Code

Once your team has had the initial PR merged for adding your directory, you can now move the code into the monorepo.
Just create a new PR copying all of your code into your project's directory. For your initial PR, you can leave
everything the same for your project. This should just be a migration of where your code is stored, but not how you
develop or manage dependencies.

From here forward, all changes in your project's directory can be approved and merged exclusively by your team. If you
have any questions, do not hesitate to reach out to the monorepo maintainers. Once your project is moved to the
monorepo, all team members will want to pull the new repository and start working from the new location.

Once the team is settled, the next stop of adopting bazel can begin.

## Adopting Bazel

To integrate with the CI/CD pipeline offered by the monorepo, you will need to add bazel support for your project. To do
this you can see information about already-supported language and tools in
the [Developing with Bazel](./../development/bazel/README.md) guide. This guide should give you the
information you need to get started on adding support for your project.

When switching to bazel, it is important to remember that dependencies are managed for the entire monorepo. This means
you will want to make sure that all of your dependencies are available to bazel. The details on managing dependencies
with bazel should be outlined for your specific language in the above guide.

After your project is fully integrated with bazel, you can delete any dependency tools local to your project, as they
should no longer be in use.
