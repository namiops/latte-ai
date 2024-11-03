# Benefits of Using This Monorepo

As we proceed to encourage adoption of the usage of this monorepo across various different teams and divisions, we would
like to provide an easily digestible document to better understand the experience of using the monorepo.

### Why Monorepo?

First, if you are interested in why we chose to use a monorepo, please see [Why Monorepo?](./why-monorepo.md).

### Benefits

* **Managed CI/CD**
  * Inside the monorepo, the CI/CD team will handle all of the hassle that is configuring the CI and CD so you don't have
    to worry about it. Setting up a proper development pipeline is a lot of work, and having every team reinvent the wheel
    for their own projects is not sustainable at scale, and so we can offer a flexible and robust pipeline for you.
  * If you are curious about the CI system, please see [the CI doc](./ci.md).
  * If you are curious about the CD system, please see [the CD doc](./deployments.md).
* **Managed Security**
  * The monorepo will offer security scanning and a secure system to develop in. Security is extremely important, especially
  when we are handling personal information. The CI/CD team will manage developer security for all the teams in the monorepo.
<!--  * TODO: Add Snyk and security documentation: https://jira.tri-ad.tech/browse/WCMDO-32 -->
* **Priority Support**
  * The CI/CD team's top priority is supporting teams in the monorepo. Because of that, any issues or requests brought up
  by teams inside the monorepo are very high priority for us. This allows you to offload a lot of DevOps and Platform overhead
  onto our team. 
* **Scalability**
  * As teams develop more and increase their ownership of more and more systems and code, it becomes much much harder for
  a team to manage and handle everything. We have designed and architected this monorepo to be scalable in terms of 
  development load and developers. This means it is easy to onboard and work with more and more members as well as the
  ever increasing amount of code each team will be producing.
* **Autonomy**
  * We have designed the monorepo to give as much autonomy to developers as possible (within consideration of security and
  development conveniences). Developers will have full control of all of their code, their configurations, and teams among
  many other freedoms. 