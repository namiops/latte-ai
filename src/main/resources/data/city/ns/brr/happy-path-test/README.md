# Happy Path Test for BURR Services

## Overview

This is an automated component test that runs on Agora cluster to test only
normal cases. Currently, this test involves both Core BURR service (coreapi) and
Social Connection service. Typically, when you make changes to the services, you
need to test your updates on lab cluster before shipping it to dev cluster. This
test runs typical test cases on your behalf.

This test is built on top of testkube. You can see the test overview and results from
[testkube on lab](https://testkube.agora-lab.woven-planet.tech/tests/executions/burr-happy-path-test).

## How to run test manually

Go to
[here](https://testkube.agora-lab.woven-planet.tech/tests/executions/burr-happy-path-test).
Use the "Run now" button at the top right to run
the test. A new execution like "burr-happy-path-test-{num}" will be created in
the list. You can click the item to open the detail view and check the result.
