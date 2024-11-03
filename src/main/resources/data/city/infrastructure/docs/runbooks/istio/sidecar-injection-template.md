# Istio Injection Template
Istio have a mechanism to inject sidecars with a custom template. By default, it injects a sidecar to a pod with a template called `sidecar`. For example, you can see the `sidecar` template from ConfigMap `istio-sidecar-injector-1-16-3` in `infrastructure/k8s/local/istio-system/istiod-1-16-3.yaml`.

## Step 1: Write your template
You can add your custom template at `sidecarInjectorWebhook` in `infrastructure/k8s/{cluster}/bin/istiod-values.yaml` (Helm values). For example, a simple template `hello` taken from https://github.com/istio/istio/blob/master/manifests/charts/istio-control/istio-discovery/values.yaml:
```
sidecarInjectorWebhook:
  templates:
    # Your template name goes here.
    hello: |
      metadata:
        labels:
          hello: world
```
Please note that the template MUST be a string. Once you updated the config, generate Istio manifest files by running a script `infrastructure/k8s/{cluster}/bin/istiod-1-16-3` (in case of Istio `v1.16.3`). Then, the new manifest files will be available at `cityos/infrastructure/k8s/{cluster}/istio-system/istiod-1-16-3.yaml`.

In addition, you can write some conditions inside the template here with Golang template as well. You can learn useful conditioning and syntax example from the built-in templates in `istio-sidecar-injector-1-16-3` ConfigMap. 

For example, add a new label if a pod has a label named `job-name`
```
sidecarInjectorWebhook:
  templates:
    hello: |     
      {{- if isset .ObjectMeta.Labels "job-name" }}
      metadata:
        labels:
          hello: world
      {{- end }}
```
**NOTE**: `{{- if isset .ObjectMeta.Labels "job-name" }}` checks if there is a label `job-name` in a pod.

The next example template adds a container named `envoy-sidecar-helper` if there is a container named "pgbackrest".
```
sidecarInjectorWebhook:
  templates:
    reaper: |
      # Iterates all containers and looks for a container named `pgbackrest`. 
      {{- $hasPgbackrestContainer := false }}
      {{- range $index, $container := .Spec.Containers }}
        {{ if eq $container.Name "pgbackrest" }}
          {{ $hasPgbackrestContainer = true }}
        {{ end }}
      {{- end }}
      # If that container is found, a sidecar container will be added. 
      {{- if $hasPgbackrestContainer }}
      spec:
        containers:
        - name: envoy-sidecar-helper
          image: paskalmaksim/envoy-sidecar-helper:v0.0.5
          args:
          - -log.level=DEBUG
          - -container=pgbackrest
          - -envoy.ready.check=false
          - -envoy.endpoint.ready=/ready
          - -envoy.port=15020
          env:
          - name: POD_NAME
            valueFrom:
              fieldRef:
                fieldPath: metadata.name
          - name: POD_NAMESPACE
            valueFrom:
              fieldRef:
                fieldPath: metadata.namespace
      {{- end }}
```

Here are some useful resources to learn Go templating.
- [Customizing OC Output With Go Templates](https://cloud.redhat.com/blog/customizing-oc-output-with-go-templates)
- [Official Documentation - Go template](https://pkg.go.dev/text/template)

## Step 2: Generate Istiod files
**NOTE:** Please note that the version number might be different depending on a cluster.
Once you finish writing the template, you need to generate new Istiod files by running `istiod-{version}` script. For example, `cityos/infrastructure/k8s/local/bin/istiod-1-16-3` for a local cluster. The script generates a YAML file for Istiod at `infrastructure/k8s/local/istio-system/istiod-1-16-3.yaml`. Then, deploy this new generated YAML file.

## Step 3: Use your template
There are 2 options to inject your custom template to pods.

### Option 1: Update pod annotation (not recommend)
**WARNING:** Your desired template set by annotation is overriden if your target namespace is labelled with `istio.io/rev: default`.

Unfortunately, only 1 template can be applied by separating with commas and you can annotate a pod with the following:
```
apiVersion: v1
kind: Pod
metadata:
  annotations:
    sidecar.istio.io/inject: "true"
    inject.istio.io/templates: hello
  name: your-pod
spec:
  ...
```

You will get the below error message, if you try to add comma-separated multiple templates.
```
The Pod "debug" is invalid: metadata.labels: Invalid value: "sidecar,hello": a valid label must be an empty string or consist of alphanumeric characters, '-', '_' or '.', and must start and end with an alphanumeric character (e.g. 'MyValue',  or 'my_value',  or '12345', regex used for validation is '(([A-Za-z0-9][-A-Za-z0-9_.]*)?[A-Za-z0-9])?')
```

### Option 2: Update a default template config (recommended)
**WARNING:** The `sidecar` template MUST be added in the list of templates to preserve the default Istio sidecar default.

We can update a list of default templates at `sidecarInjectorWebhook.defaultTemplates` key in `infrastructure/k8s/{cluster}/bin/istiod-values.yaml`. For example, to inject `hello` to all pods:
```
sidecarInjectorWebhook:
  # Add "sidecar" and your template in this array.
  defaultTemplates: ["sidecar", "hello"]
  templates:
    hello: |
      metadata:
        labels:
          hello: world
```
**NOTE** This is a recommended option since Istio templating becomes transparent and there is no need to change your deployment configuration. In a template, just write conditions to match with your deployment (ex. label and container name).
