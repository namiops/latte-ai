# Agora Identity Namespace
Welcome 🎉, if you want to use Agora Identity stack with your application please follow the material below.
- [Developer Portal Agora Identity System](https://developer.woven-city.toyota/docs/default/Component/id-homepage)


## Onboarding a new member to Agora Identity team
Welcome to the Team! 🎉
- Please start from [here](docs/int#readme).
- Also, please refer to markdown files located in `docs` folder of each project.


### Development
or to discover our documents please run below bazel commands.

#### Pre-requisite
NOTED THAT THE DOCUMENT NEED A PREREQUISITE `mkdocs` SETUP HERE IN [Backstage Documentation](/ns/developer/backstage/README.md) to install related dependencies.

```shell
$ cd ns/developer/backstage
$ pip install -c constraints.txt -r requirements.txt
```


#### Start mkdocs server
The easiest way is with bazel rule (this does not support hot reload)
```
# for general documentation
bazel run //ns/id:docs

# for Drako documentation
bazel run //ns/id/drako:docs

# for Keycloak documentation
bazel run //ns/id/keycloak:docs

```

- Please check the `BUILD` file in our namespace folder for more information about documentation!
- For maintaince and troubleshooting, see `ns/service-page`

#### For development purpose
If you need a hot reload on document update. Use this command to launch a mkdocs server (make sure you already setup Presequisite step)
```
$ mkdocs serve
```
