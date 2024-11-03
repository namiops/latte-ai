# Local Pulumi

## What?

This is a Pulumi stack example that creates k8s manifests locally as a result of `pulumi up` command.

## Why?

It is a part of Pulumi PoC: https://wovencity.monday.com/docs/6076222940

## How?

1. Install and configure pulumi (`pulumi login --local`): https://www.pulumi.com/docs/clouds/kubernetes/get-started/begin/
2. Run `pulumi up`

### Details
By default, Pulumi applies manifests to a k8s cluster directly, but this contradicts our GitOps and CI/CD process.

Instead, we can redirect an output to a folder:
```go
		provider, err := kubernetes.NewProvider(ctx, "provider", &kubernetes.ProviderArgs{
			RenderYamlToDirectory: pulumi.StringPtr("FOLDER_NAME_HERE"),
		})
		if err != nil {
			return fmt.Errorf("kubernetes.NewProvider: %w", err)
		}
```
