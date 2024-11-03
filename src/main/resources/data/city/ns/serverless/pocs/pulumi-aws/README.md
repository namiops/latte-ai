# Remote Pulumi

## What?

This is a Pulumi stack example that works with AWS.

## Why?

It is a part of Pulumi PoC: https://wovencity.monday.com/docs/6076222940

## How?

1. Install and configure pulumi (`pulumi login --local`): https://www.pulumi.com/docs/clouds/aws/get-started/begin/
2. Create a user in your AWS sandbox account.
3. Attach S3 full access policy to it.
4. Add a login to an AWS user (key and secret).
5. Run `export AWS_ACCESS_KEY_ID="<YOUR_ACCESS_KEY_ID>" && export AWS_SECRET_ACCESS_KEY="<YOUR_SECRET_ACCESS_KEY>"`
6. Run `pulumi up`
