# Why Monorepo?

Japanese version is [here](./why-monorepo-ja.md).

### Development

Developing in a monorepo allows developers to offload the DevOps and development flow context to a centralized 
CI/CD/DevOps team. This makes it easier on teams for many reasons.

First, this means the initial barrier to entry for development is lower. There is less of a need for onboarding 
and team-specific domain knowledge. This increases our hiring range by not having to specify DevOps skills on our 
JD, allowing us to grow quicker.

In addition, this allows developers to only focus on development. For most developers, context switching between 
managing DevOps and developing applications reduces the efficiency of work. If the developer only needs to focus 
on development, then development becomes faster.

Also, having a singular, unified system, means that integration into the CityOS platform becomes easier as well. 
Currently, the CityOS platform is still developing the platform out, and it is incredibly hard for many teams to 
integrate, as there is not much documentation for users and the communication cost with the CityOS team is extremely 
high. If teams continue developing on their own and move in a different direction than the platform, the migration 
cost later becomes much larger and harder to do. 

### Scaling

As WCM grows larger and larger, and there are more and more teams working on more and more services, a multi-repo 
approach becomes increasingly hard to manage. This is because it becomes increasingly hard to track which codebases
interface with any other codebase. By keeping everything within a single monorepo, code search across the services 
becomes easier, and managing everyone’s usages also becomes easier.

In addition, it is much easier to onboard a new service into the architecture. This is because when a new service 
gets added to the monorepo, all of the CI/CD and development flow is already set up. There is no need to change the 
repo settings, or write your own CI/CD for your own repo. Also, having to set up the whole flow once means we can 
ensure that the singular flow is correct and efficient. Having everyone set up their own flow, especially when not 
all engineers are familiar with DevOps means that there is a larger chance to have a poor pipeline that is 
unoptimized and insecure. 

### Security

Keeping everything in a monorepo allows us to centralize the security management of all the services inside of the 
monorepo. Things like security scanning tools and security reviews can easily be incorporated into the development 
process through the CI and pull request reviews. At scale, this is much more efficient and manageable than a 
multi-repo approach.

When teams each have their own repo and are in charge of it, they have to keep and maintain the security of their 
code by themselves. This adds another burden on top of the many other factors that developers must consider when 
developing, and makes it easy to miss something. In terms of impact a breach in security is one of the most 
dangerous, and minimizing risk for security should always be a goal. 

### Dependencies

A monorepo approach makes managing dependencies easier across all teams. This can be quite a controversial topic 
in terms of how to manage dependencies in a monorepo, but the planned approach is to have global dependencies for 
all services in the monorepo.

Having global dependencies comes with a few side effects. The biggest one is dependency updates. When a team wants 
to update a dependency, they must update it for all the services in the monorepo. This has the possibility of 
breaking another service unintentionally.

However, the benefit is that if the global dependencies are managed by a central team, then it is much easier to 
ensure the dependencies are up to date and the code quality is maintained to a modern standard. One critical 
example of an application of this benefit is in the recent log4j security incident that affected software on a 
global scale. If an incident of a similar nature were to happen in the future, a global dependency management 
system would allow us to easily resolve any security issues regarding the security bug in the library immediately 
for all services in the monorepo. This can also be applied to other dependencies that could be losing support such 
as Python 2.7, older Java versions, etc.

As for the details of how to implement the global dependency system, there is ongoing discussion, 
and here is a technical note of a possible approach: [CityOS Dependency Management TN](https://docs.google.com/document/d/12HItRSxW7p7ckgsgMVqvPW7MEZHy_Pz0fott05RECHE/edit)

Comparing the cost of possible breaking updates to the benefit of better security and management of dependencies, 
I think that having a global dependency system in a monorepo is worth it. This is because we should be pushing 
towards having more up-to-date dependencies anyways. In addition, in a realistic case where there is an absolute 
need to use two different versions of a dependency, many dependency systems (i.e. go mod, cargo, etc.) can specify 
some “replace” directive to create an exception. This means, under the extreme circumstance where a service 
absolutely needs an older version, we can allow for that to happen. 

### Notes

#### Infrastructure costs and setup

One of the difficulties of monorepos is that they require a strong, dedicated team to manage the infrastructure 
around it to make it run smoothly. We plan on utilizing many strong tools and engineers to do this. Tools like 
Bazel allow us to develop a build system that is flexible across many languages into a single build system. This 
means we can support almost any language with a robust and fast build system. In addition, with the GitOps based 
CD system that the CI/CD team is working on with the CityOS team for deployments, we can set up an easy to use 
deployment flow.

#### Bazel

One big concern many teams have when moving to the monorepo is the fact that they would have to learn Bazel. This 
is true, but the requirement is to only learn Bazel on a surface level. Using Bazel to build has a pretty low 
barrier to entry, while setting up Bazel is quite hard. However, setting up and managing Bazel is handled by the CI/CD team, 
and will not be required of the application teams. In addition, if teams choose to be in their own repo, they would 
have to learn how to set up Github Actions, FluxCD, and all of the other knowledge needed for CI/CD. Compared to 
the surface level understanding of Bazel, the knowledge required for each individual repository is immense.

#### Autonomy

One concern that many teams have when moving to a monorepo is the loss of autonomy and control over their project.
For the project itself, each team will still retain full control of the project. This means that the deployment 
configurations, tooling, resources provisioned, and other aspects for the service will all still be under the 
control of the team. The only impact to the development would be sharing the main branch’s git history and repo’s 
pull requests with other projects, which are quite minimal in general.　If you have any other concerns about autonomy,
or ever feel as if your team could use more after integration, please reach out to the CI/CD team. Our goal is to 
allow for strong autonomy while reducing burden on application teams!

#### Power Users

We do not plan on having EVERY team in the monorepo. We expect some teams to become “power users”. This means that 
the team is already extremely knowledgeable about their own development systems and flow, and wish to utilize 
their unique position to have full autonomy of their own system. Most likely, we cannot support such unique 
requirements, and so they will be free to utilize their own repository. However, for most cases and most teams, 
this is not the case, and the majority of teams will be located in the monorepo. 

You may be eligible for being a power use if you know how to:
* manage github actions
* enable security related tooling (e.g. snyk)
* enable Continuous Delivery
* enable Continous Deployment
* know how to integrate into CityOS's cluster (if needed)
* wish to handle the communication between necessary teams (CityOS, Security, IT, etc.)

Otherwise, we greatly encourage you to integrate within the monorepo and allow the CI/CD team to handle these
actions for you!
