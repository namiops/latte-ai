# Bazel SonarQube integration

Utilities to help analyse Bazel projects with SonarQube.

## Table of Contents
- [Bazel SonarQube integration](#bazel-sonarqube-integration)
  - [Table of Contents](#table-of-contents)
  - [SonarQube Coverage Report Printer](#sonarqube-coverage-report-printer)

## SonarQube Coverage Report Printer

SonarQube supports a specific format for test coverage and test execution data. LCOV coverage report formats are not supported by SonarQube.

[SonarQubeCoverageReportPrinter](./src/main/java/com/google/devtools/coverageoutputgenerator/SonarQubeCoverageReportPrinter.java) utility is based on of the equivalent lcov
generator from Bazel upstream:
<https://github.com/bazelbuild/bazel/blob/master/tools/test/CoverageOutputGenerator/java/com/google/devtools/coverageoutputgenerator/LcovPrinter.java> with the following modifications:

- Format Bazel's LCOV coverage report in a compatible SonarQube [generic test data](https://docs.sonarqube.org/latest/analysis/generic-test/)
- Print the absolute paths of source files in the generated XML coverage report, according to our GHA runners.

it runs with Bazel's embedded `"@remote_coverage_tools//:all_lcov_merger_lib"` as a dependency.

To inspect the generated report:

```sh
bazel coverage --combined_report=lcov --coverage_report_generator=//ns/bazel_sonarqube:SonarQubeCoverageGenerator //PATH/TO/PROJECT/...
```

```sh
cat bazel-out/_coverage/_coverage_report.dat
```
