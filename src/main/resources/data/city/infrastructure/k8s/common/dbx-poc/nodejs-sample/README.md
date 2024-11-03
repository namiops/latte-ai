# nodejs-sample

This deployment is to demonstrate NodeJS workloads can access DBX PoC MSK.
It is intentionally configured to be interactive.

app code: https://github.com/wp-wcm/city/blob/main/ns/dbx-poc/nodejs-sample

## How to run the process

The Pod is just sleeping by default. To kick the process:

```sh
kubectl -n dbx-poc exec -it deploy/nodejs-workload -- bash

# Inside the container
node main.js
```

## How to make tweaks to the program and run it

Vim is installed in the image. Feel free to edit main.js and try different things.

```sh
# Inside the container
vim main.js
node main.js
```

Don't forget to save your temporary change in the container to somewhere permanent.
