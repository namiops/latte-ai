# Superset

## Setup

- Add Hudi Database for Trino
    - Click `Settings` in the upper right corner => Click `Database Connections` => Click `+ DATABASE`  
      => Add Trino with the config `trino://trino@trino.trino.svc:8080/hudi`
- Add Hudi Database for Presto
    - Click `Settings` in the upper right corner => Click `Database Connections` => Click `+ DATABASE`
      => Add Presto with the config `presto://presto@presto.data-platform-demo.svc:8080/hudi` (
      replace `data-platform-demo` with your namespace)
