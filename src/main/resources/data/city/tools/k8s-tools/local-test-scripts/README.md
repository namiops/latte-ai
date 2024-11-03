# What is it?

Script is meant to test RBAC Model inside `cityos-system`/`rbac` for DEV Cluster.

# How to use?

```bash
$ ./test-rbac-model.sh NAME_OF_ROLE
```

> **Please note**: NAME_OF_ROLE should be a name of YAML file with prefix `cityos-role`

# How it works?

1. Create NAME_OF_ROLE
2. Create base role
3. Show if aggregation in NAME_OF_ROLE is working correctly
3. Delete base role
4. Create all base roles
5. Show if aggregation in NAME_OF_ROLE is working correctly
6. Delete all base roles
7. Delete NAME_OF_ROLE


# How to interpret rules

```
clusterrole.rbac.authorization.k8s.io "cityos-base-secret-view" deleted
clusterrole.rbac.authorization.k8s.io/cityos-base-view created
Name:         cityos-role-platform-admin
Labels:       <none>
Annotations:  rbac.authorization.kubernetes.io/autoupdate: true
PolicyRule:
  Resources                                    Non-Resource URLs  Resource Names  Verbs
  ---------                                    -----------------  --------------  -----
  bindings                                     []                 []              [get list watch]
  events                                       []                 []              [get list watch]
  limitranges                                  []                 []              [get list watch]
  namespaces/status                            []                 []              [get list watch]
  namespaces                                   []                 []              [get list watch]
  ```

In `PolicyRule` we have 4 columns:
- Resources
- Non-Resource URLs
- Resource Names
- Verbs

Validation is:

Check if defined RBAC from specific clustrerrole appeared in these columns with only specific verbs.