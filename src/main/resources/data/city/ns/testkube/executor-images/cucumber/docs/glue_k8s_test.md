# K8s Glue Documentation

## K8s Common Steps
#### Client Configuration
- Purpose: 
   - Sets the timeout for all Kubernetes client operations.
- Glue pattern
   - `Given("^Kubernetes timeout is {time} milliseconds")`
- Example
   - `Given Kubernetes timeout is 5000 milliseconds`
### Set Namespace
- Purpose: 
   - Connect to a specific namespace.
- Glue pattern
   - `Given("^Kubernetes namespace {name}$")`
- Example
   - `Given Kubernetes namespace my-test`

## Pod Steps
### Verify Pod Exist
- Purpose: 
   - Verify if a pod exists.
- Glue pattern
   - `Then("^verify pod ([^\\s]+) exists$")`
- Example
   - `Then verify pod bdd-test-pod exists`
### Verify Pod State
- Purpose: 
   - A Kubernetes pod has a state and is in a phase (e.g. running, stopped). You can verify the state with an expectation.
- Glue pattern
   - `Given("^Kubernetes pod {name} is running/stopped$")`
- Example
   - `Given Kubernetes pod my-test-pod is running`
### Delete Pod 
- Purpose: 
   - Delete a pod.
- Glue pattern
   - `When("^delete pod ([^\\s]+)")`
- Example
   - `When delete pod bdd-test-pod`

## Operate Service Resource
### Create Service 
- Purpose: 
   - Create a service.
- Glue pattern
   - `Given("^create Kubernetes service {name}$")`
- Example
   - `Given create Kubernetes service bdd-test-service`
### Delete Service 
- Purpose: 
   - Delete a service.
- Glue pattern
   - `Given("^delete Kubernetes service {name}$")`
- Example
   - `Given delete Kubernetes service bdd-test-service`

## Operate Secret
### Create a Secret 
- Purpose: 
   - Create a secret in the current namespace.
- Glue pattern
   - `Given("^create Kubernetes secret {name}$")`
- Example
   - `Given create Kubernetes secret {name}`
|     `{property} | {value} |`
### Delete a Secret 
- Purpose: 
   - Delete a secret in the current namespace.
- Glue pattern
   - `Given("^delete Kubernetes secret {name}$")`
- Example
   - `Given delete Kubernetes secret {name}`

## Common Resource
### Create a Resource 
- Purpose: 
   - Create a resource.
- Glue pattern
   - `When("^create Kubernetes test resource$")`
- Example
   - `When create Kubernetes test resource`
