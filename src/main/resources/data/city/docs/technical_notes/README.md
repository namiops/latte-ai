# Technical Notes

These technical notes will explain how the monorepo is set up and the techincal details of its implementation.
Reading these notes are not required to use the monorepo, but will help in understanding how it works.
The purpose of these notes are to encourage developers to help improve the monorepo if they are interested
in doing so, and also provide some background in case anyone outside of the divison is interested.

### Table of Contents
* [Technical Decisions](#technical-decisions)
* [Bazel](/docs/technical_notes/bazel/bazel.md)
* [CI](/docs/technical_notes/ci.md)
* [Deployments](/docs/technical_notes/deployments.md)
* [Vault](/docs/technical_notes/vault.md)
* [Contribution](#contribution)

### Current Architecture

![Monorepo Structure](../images/infra-architecture.png)

### Technical Decisions

#### Why Monorepo?

See [Why Monorepo?](./why-monorepo.md)

#### Why not use X?

There are many tools that can help make developer's lives easier. Some are easier to implement than others.
Some are also easier to get approval to use than others. If you have a suggestion on a new tool, feel free
to talk to us in the `#cicd-backend-wcm` Slack channel. Just be aware that third party tools require security
approval to use, so it might take some time to implement if we decide to use it!

### Contribution

If you wish to contribute to the monorepo, feel free to contact us in `#cicd-backend-wcm` on Slack, open an issue,
or even just make a Pull Request with some changes. We are open to discussion and ideas!