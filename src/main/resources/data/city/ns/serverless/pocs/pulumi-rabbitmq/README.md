# RabbitMQ on ECS

## What?

This is a Pulumi stack that will deploy scalable RabbitMQ to an existing ECS, networking is also omitted.

## Why?

This is a part of an external RabbitMQ PoC: https://wovencity.monday.com/boards/5855285561/pulses/6525524155

## How?

1. Create an ECS cluster: https://docs.aws.amazon.com/AmazonECS/latest/developerguide/create-ec2-cluster-console-v2.html
2. Build and push `<YOUR_ACCOUNT_ID>.dkr.ecr.ap-northeast-1.amazonaws.com/rabbitmq:latest` image from a `./docker` folder.
3. Modify `./app/Pulumi.dev.yaml` to include your AWS account ID.
4. Run `pulumi up`.

Note: an ECS instance role will require this inline policy:
```
{
	"Version": "2012-10-17",
	"Statement": [
		{
			"Effect": "Allow",
			"Action": [
				"autoscaling:DescribeAutoScalingInstances",
				"autoscaling:DescribeAutoScalingGroups",
				"ec2:DescribeInstances"
			],
			"Resource": "*"
		}
	]
}
```

## Refs:
- https://www.pulumi.com/docs/clouds/aws/get-started/begin/
- https://www.pulumi.com/docs/clouds/aws/guides/ecs/
- https://aws.amazon.com/ecs/
