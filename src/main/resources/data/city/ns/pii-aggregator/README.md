# pii-aggregator (namespace: pii-aggregator)

This repository is REST API used for PII Aggregation PoC of Differential Privacy (DP).

## Setup on your local

### Launch PostgreSQL on your local

`.env` is used for parameters via which API server is connected to PostgreSQL.

```sh
USER="pii-aggregator"
PASSWORD="postgres"
DBNAME="pii-aggregator"
HOST="127.0.0.1"
PORT="5432"
```

Start Postgres Docker container.

```sh
export $(cat .env | xargs)
```

```sh
docker run --name postgres -e POSTGRES_USER="${USER}" -e POSTGRES_PASSWORD="${PASSWORD}" -e POSTGRES_DB="${DBNAME}" -p 5432:"${PORT}" -d postgres:16
```

Access to the Postogres container

```sh
psql -h 127.0.0.1 -p 5432 -U pii-aggregator
```

#### Lauch the API server

Create your environment

```sh
python -m venv [your environment]
```

Activate your environment

```sh
source [your environment]/bin/activate
```

Install required packages

```sh
pip install -r requirements.txt
```

Start API server

```sh
python __main__.py
```

With the function of FastAPI, Open API spec in json is automatically created in `http://127.0.0.1:8000/openapi.json`. 

Access `http://127.0.0.1/docs` for GUI interface for more API details.

The following command can be used to convert OpenAPI spec json to yaml.

```sh
python cmd/json2yaml.py
```

### Mock Data creation for our Differential Privacy evaluation

You can upload mock data into this API with a command below by passing database variables via `.env`.

```sh
$ export $(cat .env| xargs) && python cmd/upload_mock_inputs.py -h
```

```
usage: upload_mock_inputs.py [-h] [-n N] --group_type {location,birth_year} --attr_type
                             {yearly_income,current_marriage_status,gender,divorce_history} [--dry_run] [--delete_and_recreate]

optional arguments:
  -h, --help            show this help message and exit
  -n N
  --group_type {location,birth_year}
  --attr_type {yearly_income,current_marriage_status,gender,divorce_history}
  --dry_run
  --delete_and_recreate
```


### Optional

#### Launch postgres-operator in your namespace

Infrastructure settings are [here](https://github.com/wp-wcm/city/tree/yujiro/aggregated-pii-test). 
Deploy these settings into your local minikube with the help of FluxCD.

#### Bind your local port to postgres-operator-instance

```sh
kubectl port-forward aggregated-pii-db-instance1-glg9-0 5432:5432
```

#### Connect your API server with postgres-operator

Get secrets of your postgres-operator.

```sh
kubectl get secret <cluster-name>-pguser-<user-name> -n <namespace> -o yaml
```

