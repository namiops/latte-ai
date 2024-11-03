## Usage
### Run on local (with vite(dev) server.)
1. `yarn install`
2. prepare `.env.dev` on root dir. Env vars are as follows.
    - `VITE_VUETIFY_THEME` (optional)
        - `lightTheme` (default)
        - `darkTheme`
    - `VITE_REMOTE_DWN_ENDPOINT` (required) 
        - e.g. https://foobar.com
3. `yarn dev`

### Run on container (with vite(dev) server)
```bash
cd docker/vite
```

put `.env` that contains the placeholders in `docker-compose.yml`

```bash
docker compose build
docker compose up -d
```

### Run on container(with nginx)
TBD

