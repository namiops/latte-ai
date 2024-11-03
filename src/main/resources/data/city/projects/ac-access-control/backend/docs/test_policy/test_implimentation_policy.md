# Test implementation policy

## About testing policy for all Woven products

Testing policy for all Woven products can be found [here](https://docs.google.com/document/d/16BEGkGOcEo16ZWt3v7YA6Sc4LXpijZSMhfY1MxXr34A/edit#heading=h.i1hbrur815ma)
This document describe for testing policy under this project.

## About test implementation

Test code is implement from a CI/CD perspective.
Emphasizes integration testing over function unit testing.
Do not require that unit test exists for all functions you create.

## Coverage ratio

The coverage ratio to be maintained for unit and integration testing shall be the following values for each directory within each project

| directory   | coverage ratio [%] | note |
| :---------- | :----------------: | :--- |
| application | 85                 |      |
| handler     | 75                 | Low value due to many cases where testing can be made unnecessary |
| message     | 85                 |      |
| model       | 90                 |      |
| service     | 85                 |      |
| store       | 70                 | Low value due to many conditions that cannot be tested, such as failure of DB operation itself |
| (otherwise) | 80                 | Calculated from the lowest value in directories other than the above     |

The coverage ratio is applied to the code that actually works, not to the code that does not work, such as data definition-only files.

The coverage rate should always be measured using a script (See the Coverage collection in the README) when generating Pull Requests for code changes, and marked on the checklist in the Pull Request template.

If you create a Pull Request with the coverage rate lower than the above, include the target directory, coverage rate, and reason in the Pull Request.
This is not required for directories for which the coverage ratio is below the test policy value at the time before the code change.
It is also unnecessary when updating documents other than the code.

## Test Code Implementation Criteria

- Unit tests should not be written for the main logic that is combined with many modules of the project. (Because of the cost of maintaining unit tests when some changes occur. Verification of such code should be done by integration testing as much as possible.)

- Unit tests may be written for utilities, request processing for other services, DB processing, and other functions that should be verified to work as stand-alone units.

- Integration test is basically implemented without using Mock except for I/F between external systems.(To verify the integrated behavior within this project)
  - Basically, check a single operation consistently from its starting point to its end (if it is REST, check the operation from the API call to the return of the response).
  - If the process is crossed between threads, may be difficult to validate in integration testing. In such a case, it is acceptable to implement the integration test in such a way that subsequent processes can be verified starting from the thread that takes over the process.
