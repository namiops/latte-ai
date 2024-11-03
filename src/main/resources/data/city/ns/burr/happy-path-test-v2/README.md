# Happy Path Test for BURR v2 Services

## Overview

This is an automated component test that runs on Agora cluster to test only
normal cases. Currently, this test involves only core v2 service. Typically,
when you make changes to the services, you need to test your updates on lab
cluster before shipping it to dev cluster. This test runs typical test cases on
your behalf.

This test is built on top of testkube. You can see the test overview and results
from [testkube tests](https://github.com/wp-wcm/city/tree/main/ns/service-page/docs/burr#testing)

## How to run happy-path-test-v2 on laptop

Use city address importer

```sh
CITY_ADDRESS_ENTRIES_DATA=$(cat ../../../city/infrastructure/k8s/common/brr/city-address-entries-0.2.0/entries.csv) CITY_ADDRESS_ENTRIES_REVISION=0.1.0 CITY_ADDRESS_ENTRIES_SCHEMA_VERSION=v1alpha2 bazel run //ns/burr/jobs:city_address_importer
```

BURR must be running locally when running the happy path test. See [core-v2 doc](../core-v2/README.md) for how to run
the BURR service and the database it needs.

Run test executor:

```sh
bazel run //ns/burr/happy-path-test-v2/test-executor:test_executor
```

## How to run happy-path-test-v2 on lab/lab2/dev/dev2 manually

Use the "Run now" button at the top right to run
the test. A new execution like "burr-happy-path-test-v2-{num}" will be created in
the list. You can click the item to open the detail view and check the result.
