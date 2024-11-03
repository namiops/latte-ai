This is a [Next.js](https://nextjs.org/) project bootstrapped with [`create-next-app`](https://github.com/vercel/next.js/tree/canary/packages/create-next-app).

## Prerequisites

- Node 18+
- pnpm 8.6+

## Getting Started

First, this repo uses a modified (by the Woven Design Team) version of Material UI. To get access to this library, follow the instructions in their [README](https://github.com/wp-wcm/clib/blob/main/react/README.md) **up to and including step 3 of the "Setting up NPM & Artifactory access" section.**

Next, install dependencies via:

```bash
pnpm install
```

Then, run the development server:

```bash
pnpm dev
```

Finally, navigate to [http://localhost:3000/map-service/portal](http://localhost:3000/map-service/portal).

## Storybook (optional)

Run Storybook:

```bash
pnpm storybook
```

Then, open [http://localhost:6006/](http://localhost:6006/)

## Other Topics

### Open API Generator

Our api client under `src/app/openapi` is generated with [Open API Generator](https://openapi-generator.tech/).

1. Install Open API Generator on your laptop globally with the following command.

```bash
npm install @openapitools/openapi-generator-cli -g
```

2. Acquire the updated swagger.yaml file(s) from the one of the sub services under the map-service in the [city repo](https://github.com/wp-wcm/city/tree/main/projects/map-service). Example [swagger.yaml](https://github.com/wp-wcm/city/blob/main/projects/map-service/places-service/docs/swagger.yaml)

3. Generate TypeScript code from swagger.yaml file

Once you've received the new swagger.yaml files, overwrite the corresponding local yaml file in this repo and generate typescript code with the command below:

```bash
cd path/to/src/app/openapi/places-service
openapi-generator generate -g typescript-fetch -i places-service.yaml
```
