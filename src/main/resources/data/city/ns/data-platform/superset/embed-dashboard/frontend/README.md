# Superset embedded dashboard - frontend

## What does this do?

This is a demonstration page that uses the Superset Embedded SDK (https://github.com/apache/superset/tree/master/superset-embedded-sdk)

## How do I use it?

Set the following env vars in .env or in the compilation environment:

- `REACT_APP_DASHBOARD_ID` the ID of your dashboard, retrieved from the 'Embed' action in the Superset UI after you create it.
- `REACT_APP_SUPERSET_DOMAIN` the base URL to the Superset installation, e.g. `https://superset.agora-lab.w3n.io/`

then run the application with `yarn start` for a development environment, or `yarn build` to package the application for deployment.
