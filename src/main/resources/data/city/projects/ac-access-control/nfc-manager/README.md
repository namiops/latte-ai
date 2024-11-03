# ac-nfc-manager

## Build and Run

```bash
# get dependencies if you need
bazel run @go_sdk//:bin/go -- get
# update BUILD files if you need
bazel run //:gazelle 
bazel run //:buildifier 


# build image
# Run the build command whenever you update the source code
bazel run //projects/ac-access-control/nfc-manager/internal/cmd:image.load
# run image with databases
cd $(git rev-parse --show-toplevel)/projects/ac-access-control/nfc-manager/.local_debug
docker compose up

# run tests
bazel test //projects/ac-access-control/nfc-manager/...

# run tests with log outputs --test_output=all
$ bazel test --test_output=all //projects/ac-access-control/nfc-manager/...

# re-run tests ignoring cache
$ bazel test --cache_test_results=no //projects/ac-access-control/nfc-manager/...
```

## Daily Operations

### Launch docker container for local debugging

- Set your current directory to `projects/ac-access-control/nfc-manager/.local_debug` and run the following command. The postgresDB for local debugging will launch.
  
```bash
docker compose up
```

- after debugging finishes, run

```bash
docker compose down
```

- When re-building the container, run

```bash
docker compose up --build
```

#### Set up CouchDB

- After you run docker compose, run following script to setup CouchDB databases. (Need to change current directory to `projects/ac-access-control/nfc-manager/.local_debug` and run the following command.)

```bash
bash setup_couchdb.sh
```

- You can access local CouchDB admin page via `http://localhost:55984/_utils`. see `docker-compose.yaml` about user name and password for login.

#### Each service coverage collection

Execute .projects/ac-access-control/nfc-manager/scripts/cover.sh with the root directory path of the service as the argument.

```bash
cd ./projects/ac-access-control/nfc-manager/scripts

./cover.sh ../
```

After execution, the following file is output

- cover.html : HTML file that displays the coverage range of the source
- cover.out : Raw data file of coverage collection results
- cover_all.txt : File that records the coverage rate for each function
- cover_directories.txt : File outputting coverage rate per directories
