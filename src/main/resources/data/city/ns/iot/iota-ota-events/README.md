# IoTA OTA(SUS) Events Consumer
This is the event consumer for iota-ota(SUS) that consume SQS event. Source of the event is S3 Notification on Release bucket. This event consumer host the following handlers to process the following logics:

* Slack notification handler : It consume S3 objectCreated event and send the slack notification to the tenant's slack channel.
* (TBD)Release DB Handler: Update Release information to DB
* (TBD)Auto distribute handler: Setting automate distribution

Design: https://wovencity.monday.com/docs/6210789947

# Handlers
## Slack notification handler
### Try it out in lab
* Upload the release e.g. via UI https://agora-ui.agora-lab.woven-planet.tech/admin/xenia-management/releases/create/test/ota-test-group
* Check slack channel e.g. #iot-ota-test to see slack message for the uploaded release

### Setup notes
* Template is created at https://github.tri-ad.tech/cityos-platform/notification-template-registry/blob/main/templates/sus-release-created.txt
* Slack webhook ID from this [slack incoming-webhooks app](https://woven-by-toyota.slack.com/apps/A0F7XDUAZ-incoming-webhooks)
* Configure the slack from [Xenia-setting](https://agora-ui.woven-city.toyota/admin/xenia-settings)

# Development
## Running the program locally in VSCode with sandbox account
Note that these instructions are for running against `lab2` using the `test-byob` tenant
1. Ensure your user is added to the `test-byob` tenant
1. Create 2 S3 buckets in your AWS Sandbox account, 1 to use as the shared bucket and 1 to use as the BYOB bucket for the tenant `test-byob`
1. Configure the Terraform variables defined in `local-terraform/variables.tf` (See [Terraform documentation](https://developer.hashicorp.com/terraform/language/values/variables#assigning-values-to-root-module-variables)
1. Apply the Terraform to create the AWS resources in your sandbox account
    ```bash
        cd local-terraform
        terraform plan
        terraform apply
    ```
1. Run the setup script to port forward to notification service and OTA in cluster
    ```sh
    ./setup-dep-local-env-lab2.sh up
    ```
1. Add the entry to `launch.json` e.g.
    ```json
    {
                "name": "Launch OTA Events",
                "type": "go",
                "request": "launch",
                "mode": "auto",
                "program": "iot/iota-ota-events/main.go",
                "args": [
                    "--notifier-url",
                    "http://localhost:18081",
                    "--config-provider-url",
                    "https://iota-config-provider-iot-lambda.agora-lab.w3n.io",
                    "--agora-ui-url",
                    "https://agora-ui.agora-lab.w3n.io",
                    "--ota-url",
                    "http://localhost:18082",
                    "--local",
                    "--sqs-config",
                    "{ \"test-byob\" : { \"QueueURL\" : \"<SQS QUEUE URL>\"}}", << Obtain this value from the AWS SQS UI
                    "--shared-sqs-config",
                    "{ \"QueueURL\" : \"<SQS QUEUE URL>\"}", << Obtain this value from the AWS SQS UI
                    "--ota-revision",
                    "iota-ota-00025" << change this according to what setup script say.
                    ],
                "env": {
                    "AWS_ACCESS_KEY_ID":"",
                    "AWS_SECRET_ACCESS_KEY":"",
                    "AWS_SESSION_TOKEN": "",
                },
            },
    ```
    * get the AWS credentials from `http://go/aws`


## Running the program locally in VSCode point to cluster's environment
If you have access to them then just update the launch.json point to lab's SQS resources.

example `launch.json`
```json
    {
                "name": "Launch OTA Events",
                "type": "go",
                "request": "launch",
                "mode": "auto",
                "program": "iot/iota-ota-events/main.go",
                "args": [
                    "--sqs-config",
                    "{ \"test-mimir\" : { \"QueueURL\" : \"https://sqs.ap-northeast-1.amazonaws.com/370564492268/iota-ota-lab-agora-test-mimir-events\"}}",
                    "--shared-sqs-config",
                    "{ \"QueueURL\" : \"https://sqs.ap-northeast-1.amazonaws.com/370564492268/iota-ota-lab-agora-shared-events\"}",
                    "--notifier-url",
                    "http://localhost:18081",
                    "--config-provider-url",
                    "https://iota-config-provider-iot-lambda.agora-lab.woven-planet.tech",
                    "--agora-ui-url",
                    "https://agora-ui.agora-lab.woven-planet.tech/"
                    "--ota-url",
                    "http://localhost:18082",
                    "--local",
                    "--ota-revision",
                    "iota-ota-00025" << change this according to what setup script say.
                ],
                "env": {
                    "AWS_ACCESS_KEY_ID":"",
                    "AWS_SECRET_ACCESS_KEY":"",
                    "AWS_SESSION_TOKEN": "",
                },
            },
```
