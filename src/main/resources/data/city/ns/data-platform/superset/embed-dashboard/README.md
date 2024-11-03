# Superset Embedded SDK usage example

This is a sample that uses [Superset Embedded SDK](https://github.com/apache/superset/tree/master/superset-embedded-sdk) using `guest_token`.

A backend server is required for token management, and a JS application is needed to use the Superset Embedded SDK. 

Please see ./backend and ./frontend for more specific details. 

While this is functional, there are several points to note regarding security:

- Generally speaking, the point of the embedded SDK and guest tokens is to make a dashboard publicly viewable without login.
- While it is possible to embed the dashboard in an application with authn/z around it, the underlying datasources (and the dashboard itself) have public permissions. Care should be taken here when dealing with potentially sensitive data.
- Similarly, while each guest token can have row-level security specified at issuance time, other Agora Superset users who have logins will have access to the full datasource and dashboard.

This code is a proof-of-concept. Please discuss your needs and business logic with the Agora Orchestration team for a more in-depth consultation on best practices and architecture to keep your project and data secure.
