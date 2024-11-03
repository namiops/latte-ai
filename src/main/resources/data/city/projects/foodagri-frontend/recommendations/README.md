## How to run the recommendation service in local environment

### Start recommendation service by docker-compose

```bash
$ docker-compose up --force-recreate
```

### Use virtual Python env instad of docker-compose
#### Create virtual Python env (Only execute once)
```bash
$ python3 -m venv <path/to/venv>
```
#### Activate virtual Python env
```bash
$ source <path/to/venv>/bin/activate
```
#### Install required modules (The configuration is stored in the virtual env)
```bash
$ python3 -m pip install <module name>
```
#### Deactivate virtual Python env
```bash
$ deactivate
```

### How to run Migration and Seeder

#### Install necessary packages by the following commands

```bash
$ pip3 install sqlalchemy psycopg2-binary python-dotenv
```

#### Migration

```bash
$ python3 migration.py
```

#### Seed

```bash
$ python3 seeder.py
```

#### Delete DB

```bash
$ python3 dropDB.py
```

### Set up Cronjobs

```bash
$ python3 recom-cron-local.py
```

### Start recommendation API service

```bash
$ python3 __main__.py
```

### Test cURL command for recommendation API

Need to insert access_token.
You can get access_token from 1Password.

```bash
$ curl -X GET "localhost:8083/recommend/{key_cloak_ID}" -H 'Content-Type: application/json' -H 'access_token:1234567abcdefg'
```

### Execute unit tests in local env
```bash
$ cd <project root>
$ python3 -m unittest <python test module>
eg.)
$ python3 -m unittest cronjobs.tests.test_recommend_bai
```