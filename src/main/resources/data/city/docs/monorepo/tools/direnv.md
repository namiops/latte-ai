# Setting Up `direnv`

This guide will cover how to use `direnv` for development with multiple AWS profiles 
across multiple services.

## Configuring AWS Profiles

When using `aws-cli`, AWS profiles can be configured in many different ways. Most notably,
you can choose to use [environment variables](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-envvars.html)
or the `~/.aws/credentials` and `~/.aws/config` files through `aws configure`. (See [here](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html))

For the `direnv` setup to work, we will be using `aws configure`. When setting up a new aws
account, make sure to use `aws configure --profile <profile-name>` where it is recommended 
that the `<profile-name>` be the same as the name of your service.

## Utilizing `.envrc`

Once you have multiple aws profiles set up, you can easily switch between profiles by
setting the environment variable `AWS_PROFILE`. We can take advantage of this by using
`.envrc` files and `direnv` in each directory. 

In each service's root directory, there should be a `.envrc` file with the following line:

```shell
export AWS_PROFILE=<profile-name>
```

With this set, whenever you change to this directory, `direnv` will automatically load the
`.envrc` file and set the `AWS_PROFILE`. If the environment is not loading, you can manually
load the `.envrc` file by running:
```shell
direnv allow .
```

## Managing `.envrc` files

The `.envrc` file can be used for environment variables other than `AWS_PROFILE`, which means 
they are inherently sensitive. This is why `.envrc` files are ignored in the `.gitignore`, because otherwise, there is a risk
of pushing sensitive information to the repository.

It is recommended that each service provide a `.envrc_sample` file which contains the necessary
environment variables for development of that service, but not any values for those variables. 
Each developer would then copy that file and rename it to `.envrc` and fill in the necessary values
for development on that service. 

