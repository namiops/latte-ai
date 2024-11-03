# FAQ/Troubleshooting Documentation

This documentation is sorted by type of common issues that have been picked up and documented to prevent it from happening again or to provide quicker response times.

## Problem: Integration Not Found

### Cause

The credentials cannot be found. By not having the proper credentials Backstage is unable to scrape the repo

### Symptom

The message in the logs looks something like this, note the `message`

```json
{
  "entity": "location:default/generated-563aa34864de32f795851e03edc5240f5944cfd7",
  "level": "warn",
  "location": "github-discovery:https://github.com/wp-wcm/city/blob/main/catalog-info.yaml",
  "message": "Processor GithubDiscoveryProcessor threw an error while reading github-discovery:https://github.com/wp-wcm/city/blob/main/catalog-info.yaml; caused by HttpError: Integration not found",
  "plugin": "catalog",
  "service": "backstage",
  "type": "plugin"
}
```

### How to check and potential fixes

* Make sure that the `github.com` or the repository in question is reachable
* Double-check the credentials that are pulled into Backstage via Vault Agent
  * You can do this by shelling into the main container, `developer-portal` and running `cat` on the files under Vaults mount

## Problem: Errconreset

### Cause

There is an issue with Backstage attemtping to reach out to a resource its scraping

### Symptom

The message in the logs looks something like this, note `message`and `stack`

```json
{
  "level": "error",
  "message": "request to https://api.github.com/app/installations failed, reason: read ECONNRESET",
  "name": "HttpError",
  "request": {
    "headers": {
      "accept": "application/vnd.github.v3+json",
      "authorization": "bearer [REDACTED]",
      "user-agent": "octokit-rest.js/19.0.5 octokit-core.js/4.1.0 Node.js/16.20.0 (linux; x64)"
    },
    "method": "GET",
    "request": {},
    "url": "https://api.github.com/app/installations"
  },
  "service": "backstage",
  "stack": "HttpError: request to https://api.github.com/app/installations failed, reason: read ECONNRESET\n    at /app/node_modules/@octokit/request/dist-node/index.js:110:11\n    at processTicksAndRejections (node:internal/process/task_queues:96:5)\n    at async hook (/app/node_modules/@octokit/auth-app/dist-node/index.js:319:18)\n    at async Object.next (/app/node_modules/@octokit/plugin-paginate-rest/dist-node/index.js:67:28)\n    at async GithubAppManager.getInstallationData (/app/node_modules/@backstage/integration/dist/index.cjs.js:1262:30)\n    at async /app/node_modules/@backstage/integration/dist/index.cjs.js:1229:45\n    at async Cache.getOrCreateToken (/app/node_modules/@backstage/integration/dist/index.cjs.js:1180:34)\n    at async Promise.all (index 0)\n    at async GithubAppCredentialsMux.getAppToken (/app/node_modules/@backstage/integration/dist/index.cjs.js:1300:21)\n    at async _SingleInstanceGithubCredentialsProvider.getCredentials (/app/node_modules/@backstage/integration/dist/index.cjs.js:1330:17)",
  "status": 500,
  "type": "errorHandler"
}
```

### How to check and potential fixes

* Ensure that the host in the `message` has a related Service Entry in the
  environment.
  * Check `/infrastructure/k8s/<ENV>/istio-system/service_entries.yaml`

## No page content is shown and a CSS error is shown in the console

### Cause

Depending on a user's development setup, port forwarding might be causing file fetch errors.

### Symptom

When you navigate to a document, even if the content is seemingly available (this can be checked by using the browser's dev tools), a CSS load error stops the content from being shown; techdocs seems to have a check for the CSS to be loaded before showing a document. The file stops loading with an `ERR_INCOMPLETE_CHUNKED_ENCODING` error.

### How to check and potential fixes

If you develop using Microsoft's VS Code and connecting via SSH into a remote machine (like a rack desktop), port forwarding might be the cause of this problem.

There's two known solutions:

* Changing `remote.SSH.useExecServer` in VS Code's settings to `false`
* Manually checking and managing the port forwarding in VS Code to make sure that the ports are mapped correctly

Related Github issues:

* VS Code: [Regression: Port forwarding fails while working with remote SSH](https://github.com/microsoft/vscode/issues/192521)
* techdocs: [Couldn't load the document because css 404 (Not Found)](https://github.com/backstage/backstage/issues/10529)
