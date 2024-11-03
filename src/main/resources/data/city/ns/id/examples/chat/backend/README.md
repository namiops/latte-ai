# chat backend

This is a demo project showcasing how to setup drako for authorization.

It is a chat application implementing a simple set of APIs. It contains
the following endpoints:

* POST /send => to post messages
* GET /profile => for fetching user profile information
* GET /feed => a feed with the messages

The service is stateless; meaning that whenever a new user connects no
previous message is shared. It is implemented using streams.

Access control is entirelly delegated to drako, and to access profile
information the chat service must have consent from users. This data
is managed by BURR, but thanks to the drako integration, the chat service
only contacts the data backend if consent has been granted.

## Overview

This backend uses in-memory store for storing the messages (which means the saved messages are gone once the backend restarted) since we would not need the full featured RDBS just for demo project.


## Coverage report

You may need to install lcov first.

```
./coverage_report.sh
```

## Why not a schema?

In the real world you would be using something like OpenAPI to specify
your schema. In this example, however, the goal was to make the
simplest code possible with lesser number of libraries and straightforward
build system.