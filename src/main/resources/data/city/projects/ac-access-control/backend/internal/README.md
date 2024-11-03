# ac-access-control-backend common modules

## Run unit tests

Move directory under each module.
Execute the following command.

```bash
go test -v
```

Some tests are skipped by default due to their heavy time consumption.
Set the following environment variables to test them.

```bash
RUN_AMQP_TEST=1 # infra/amqp/amqp_test.go
```

Coverage measurements are execute following command.

```bash
go test -coverage
```
