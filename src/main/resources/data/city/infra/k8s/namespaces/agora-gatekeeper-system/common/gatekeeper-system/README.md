## What's the difference between constraints-templates-0.x.x, constraints-legacy-0.x.x and constraints-agora-0.x.x

- constraints-templates-0.x.x is source of code for both - old and new environments
- constraints-legacy-0.x.x is used in old environment
- constraints-agora-0.x.x is used in new environment. 

## What if I want to add custom constraints for my service

- you should create custom folder for your service under `infra/k8s/namespaces/agora-gatekeeper-system/common/gatekeeper-system`. 
- under your folder create 2 sub-folders `constraint-templates` and `constraints`. 
- for every constraint create file with <constraint name> under `constraints` sub-folder and folder with <constraint name> under `constraint-templates`. 
- nice to have `sample` folder under <constraint name>  `constraint-templates` sub-folder and reference to it in `kind: Suite` suite.yaml for test with `gator` tool.

Example on `id`
`infra/k8s/namespaces/agora-gatekeeper-system/common/gatekeeper-system/id`
- `infra/k8s/namespaces/agora-gatekeeper-system/common/gatekeeper-system/id/constraint-templates`
    - keycloakclient-id -> folder with source code

        - samples -> folder for test samples

            - constraint.yaml - specific constraint implementation for test suite

            - allowed1.yaml - allowed example for test suite (those could be multiple then add allowed2, allowed3 ... number in the end of the name)

            - disallowed1.yaml - disallowed example for test suite (those could be multiple then add disallowed2, disallowed3 ... number in the end of the name)

        - kustomization.yaml - refers to list of templates ( `- template.yaml`)

        - suite.yaml - test suite file refers to resources in samples directory ( `- template.yaml`)

        - template.yaml - source code of constraint

    - kustomization.yaml -> refers to list of constraint templates (`- keycloakclient-id`)

- `infra/k8s/namespaces/agora-gatekeeper-system/common/gatekeeper-system/id/constraints`

    - keycloakclient-id.yaml  -> Constraint with specific implementation for usage on envs

    - kustomization.yaml -> refers to list of constraints (`- keycloakclient-id.yaml`)