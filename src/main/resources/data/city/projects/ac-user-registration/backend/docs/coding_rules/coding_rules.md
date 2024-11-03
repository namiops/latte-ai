# *Directory structure and coding rules* <!-- omit in toc -->

## **Table of Contents** <!-- omit in toc -->

***

- [**Scope of this document**](#scope-of-this-document)
- [**About the programming language to be used**](#about-the-programming-language-to-be-used)
- [**Coding rules**](#coding-rules)
- [**Development tools**](#development-tools)
- [**Linter \& Formatter**](#linter--formatter)
- [**Directory structure**](#directory-structure)

## **Scope of this document**

***
This document describes the coding rules for the Access Control Backend.

## **About the programming language to be used**

***
The program that runs in the project is written in golang.

## **Coding rules**

***
Basic coding rules follow [effective go](https://go.dev/doc/effective_go).

Also as a supplement [CodeReviewComments](https://github.com/golang/go/wiki/CodeReviewComments).

The following is a description of what is not specified in the above rules.

- File names should be named with snake_case.
- The following Copyright header comments should be added to the created files.
  (The year("2023") should be change written the year in which the file was created.)

```text
/*
 * Copyright(c) 2023 TOYOTA MOTOR CORPORATION.
 */
```

- The comment description rules follow the [Go Doc Comment](https://go.dev/doc/comment) description.

- Basically, function parameters are passed by value, not by pointer.However, pointers should be passed in the following cases
  - If the structure contains a pointer
  - If the value of the structure needs to be changed within the function
  - If the data in the structure or variable is large.  
  Although specific sizes are not mentioned, large data such as image data should be passed as pointer passing.
- For other coding styles (e.g., naming functions and variables) that are not described in the effective go, please refer to the [style guide](https://google.github.io/styleguide/go/) for other coding styles (naming of functions and variables, etc.) that are not described in [effective go](https://go.dev/doc/effective_go).

## **Development tools**

***
Use [VSCode](https://code.visualstudio.com/)(Visual Studio Code) for development.
Install the following as an Extension of VSCode.

- [Go for Visual Studio Code](https://github.com/golang/vscode-go)
- [OpenAPI (Swagger) Editor](https://github.com/42Crunch/vscode-openapi)
- [asyncapi-preview](https://github.com/asyncapi/vs-asyncapi-preview)
- [Code Spell Checker](https://github.com/streetsidesoftware/vscode-spell-checker)
- [Markdown All in One](https://github.com/yzhang-gh/vscode-markdown)
- [markdownlint](https://github.com/DavidAnson/vscode-markdownlint)  

Also, when creating the OpenAPI yaml, please use the following tool.

- [Stoplight Studio](https://stoplight.io/studio)

## **Linter & Formatter**

***
Basically, rely on VSCode's Go extension.

[This link](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=222829129) for VSCode environment construction.

Build according to this document and install the go extension at [this link](https://marketplace.visualstudio.com/items?itemName=golang.go).

The following tools shall be used to check and ensure that no warning are raised.

- staticcheck

This is VSCode go extension default checker.

## **Directory structure**

***
The Top directory structure should be as follows

| directory              | Description                                                       |
| ---------------------- | ----------------------------------------------------------------- |
| .                      |                                                                   |
| internal/              | Deployment the code commonly used in any project                  |
| project1/<br>project2/ | Each project comprising this system                               |
| some<br>other          | Deployment the files other than code commonly used in any project |

The directory structure within each project is as follows
|     directory |              | Description                                                                                                                       |
| ------------: | ------------ | --------------------------------------------------------------------------------------------------------------------------------- |
|          doc/ |              | Documentation on I/F such as APIs and MQTT topics in this project                                                                 |
|      scripts/ |              | Deployment scripts for mockup generation, builds, etc.                                                                            |
|     internal/ |              | Deployment the code for this project                                                                                              |
|             ├ | application/ | Main logic in this project                                                                                                        |
|             ├ | async/       | Deploy code for MQTT/AMQP                                                                                                         |
|             ├ | cmd/         | Main application in this project                                                                                                  |
|             ├ | db/          | Implementation of the database                                                                                                    |
|             ├ | db/schema    | Describe of database schema. This schema should not be used from anything other than a store process that performs DB operations. |
|             ├ | handler/     | REST API's Input/Output processing in this project                                                                                |
|             ├ | message/     | Deploy code for request and response messages to and from external I/F(REST API etc.).                                            |
|             ├ | middleware/  | Deploy middleware to be applied when routed                                                                                       |
|             ├ | model/       | Define the structure to be used between each package in this project and, if necessary, the describe of validation.               |
|             ├ | mock/        | Deployment mockup code to be used for testing                                                                                     |
|             ├ | router/      | Deploy code related to middleware for routing REST APIs (Related to HTTP Server).                                                 |
|             ├ | service/     | Code for request to external service's API                                                                                        |
|             ├ | store/       | Describe CRUD implementation for DB records                                                                                       |
|             ├ | utils/       | Describe generic code used by any package in this project                                                                         |
|             └ | testutils/   | Describe generic code used by any package or module's test code in this project                                                   |
| some<br>other |              | Files needed for this project                                                                                                     |
