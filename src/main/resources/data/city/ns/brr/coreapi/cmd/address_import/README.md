# BRR Address Bulk Import Exploration / Experiment command

Related ticket: [CITYPF-2580](https://jira.tri-ad.tech/browse/CITYPF-2580)

What this command currently does:
* This command reads data about postal codes in Japan and imports them into a local DB.
* The input file must be a JSON file created by [Posuto][1]. 
* The command creates a new Postgres Docker container on launch, and uses this to store the imported data. The Docker container is _not_ removed when the command finishes, so that you can inspect the data afterwards.
* The DB schema follows the design in [this GSheet][2].

[1]: https://github.com/polm/posuto
[2]: https://docs.google.com/spreadsheets/d/1m-b4i_4jIIODskPvVvbBLbPArSVrKHkKco1YBpVvrc4/edit

## Prerequisites for running this command

* Golang toolchain to build and run Go programs
* Docker installed and running on your machine
  * If you're running Docker in a "non-standard" configuration (e.g. rootless), you may need to override how the command controls Docker. For example, if you're using Rancher Desktop and your Docker socket is in your home directory, setting the env var `DOCKER_HOST=unix://<USER_DIR>/.rd/docker.sock` points the command to the right place. See [here][3] for more details.

## Running this command

* If you don't have the JSON input file yet, download it from GDrive [here][4] and unzip it somewhere.
* Use your preferred method to build and run the command, e.g.:
  * your IDE
  * Go's toolchain: in `ns/brr/cmd/address_import`, run `go build` and then run the executable (on *nix, `./address_import`)
  * _Note: Using Bazel to build and run this isn't set up right now._
* Pass the path to the JSON input file to the command as a command-line argument.
* Optionally, you can also specify the `--keeprunning` CLI option to keep the Docker container running instead of stopping it at the end of the command. That's useful if you already know you want to inspect the data.
* For example, if you've put the JSON file into `~/Downloads`, you could invoke the command like:  
  `./address_import --keeprunning ~/Downloads/2022-10-01_postaldata.json`

[3]: https://pkg.go.dev/github.com/ory/dockertest/v3@v3.9.1#NewPool
[4]: https://drive.google.com/file/d/15GnZ3GGBqhozE7vrO1O20ppugJuOgTwK/view