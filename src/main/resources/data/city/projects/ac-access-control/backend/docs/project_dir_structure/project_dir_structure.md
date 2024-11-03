# Access control system's namespace and project directory structure in monorepo

## Namespace

Namespace of access control system is following

`ac-access-control`

## Project directory structure

The directory structure of the Access Control System is as follows.

```text
directory                       description

city
   └ projects
       └ ac-access-control      Access control system's top directory
           ├ backend            Access control backend top directory
           |  ├ internal        Common code referenced by all back-end projects
           |  ├ auth            Access control auth service 
           |  |  └ api-server   Access control auth service Web API server project
           |  ├ management      Access control device management service
           |  |  ├ api-server   Access control device management Web API server project
           |  |  └ worker       Access control device management worker project
           |  └ log             Access control log service
           |     └ api-server   Access control log service Web API server project
           |
           └ frontend           Access control frontend top directory
              ├ surveillance    Device management and entry/exit Log system UI project
              └ management      Access control management system UI project
```

For the backend, create a folder for each service,
Deploy the projects that comprise each service under each service folder.
