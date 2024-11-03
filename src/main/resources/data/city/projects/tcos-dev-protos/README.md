# WTC-TCOS-SDOM: Fourkeys Service

## Description
**Fourkeys** is a set of indicators proposed by **[Google's DORA Team](https://cloud.google.com/devops)** to measure team development productivity. Fourkeys consists of four key indicators:

1. **Deployment Frequency**: How often an organization successfully releases to production.
2. **Lead Time**: The time it takes to go from code committed to code successfully running in production.
3. **Change Failure Rate**: The percentage of changes that fail in production.
4. **Restore Time**: How long it takes an organization to recover from a failure in production.

We believe that Fourkeys can help service teams understand their development **Speed** and **Stability**, ultimately leading to improved **efficiency** and **team motivation**.

For more information about Fourkeys, please visit the [Specification link (in progress)](https://docs.google.com/presentation/d/1i_nhT3GUAFo-1oaoOmqc2Rus35VYlJA1/edit#slide=id.g2bf2d35f9a3_0_244).


## Architecture
![Architecture Diagram](path_to_your_architecture_diagram.png)

## Setup
### Bazel Installation
To build and test the project, you need to install Bazel. Follow these steps:

1. **Install Bazel**:
    - **macOS**:
        ```bash
        brew install bazel
        ```
    - **Ubuntu**:
        ```bash
        sudo apt update && sudo apt install apt-transport-https curl gnupg -y
        curl -fsSL https://bazel.build/bazel-release.pub.gpg | gpg --dearmor >bazel-archive-keyring.gpg
        sudo mv bazel-archive-keyring.gpg /usr/share/keyrings
        sudo apt update && sudo apt install bazel
        sudo apt update && sudo apt full-upgrade
        ```
    - **Windows**:
        Follow the instructions on the [Bazel website](https://bazel.build/).

2. **Verify Installation**:
    ```bash
    bazel version
    ```

### Python Development Environment

If you are developing with Python, you can set up your environment using Rye.
1. **Install Rye**:
    ```bash
    curl -sSf https://rye-up.com/get | bash
    ```

2. **Set Up the Python Environment**:
    ```bash
    # Navigate to your project directory
    cd /path/to/your/project

    # Use Rye to install dependencies
    rye sync

    # To activate the Rye environment
    source .venv/bin/activate
    ```

## Test
- tcos-dev-protos/fourkeys/utils/confにconfig.jsonを追加(Vault情報を記載)
- conf内のBUILDを書き換える
```bash
exports_files([
    "conf.yaml",
    "config.json",
])
```
- bazelでのテストと接続部分のテスト。
```bash
# bazelのコンパイル
bazel run //:buildifier
bazel run //:gazelle

# 実行test(後々バッチ化)
bazel run //projects/tcos-dev-protos/fourkeys/tests:test

# postgreの確認
psql -U postgres -d mydatabase
\dt
SELECT * FROM table_name;
```
- preview機能を使ったk8s環境での動作確認
```bash
/preview tcos-dev-protos tcos-protos-fourkeys-leadtime //projects/tcos-dev-protos/fourkeys/qc/pr:push_image
```

## Public Database Tables

### deployment table
This table defines the `release date` from the `ServiceNow Change Request table`.
| Column               | Description                                                   | Example                         |
|----------------------|---------------------------------------------------------------|---------------------------------|
| id                   | Unique identifier for each deployment record                  | DF_WTC-TCOS-CSM_2024-06-02      |
| product_id           | Identifier for the product                                    | WTC-TCOS-CSM                    |
| release_date         | Date of the deployment                                        | 2024-06-02                      |
| num_deployments      | Number of deployments executed on the given date              | 1                               |

### leadtime table
This table defines the lead time from the Change table, calculated from the median commit time of pull requests to the release date.

| Column               | Description                                                   | Example                         |
|----------------------|---------------------------------------------------------------|---------------------------------|
| id                   | Unique identifier for each lead time record                   | LT_WTC-TCOS-CSM_2024-05-31      |
| date                 | Date of the ServiceNow ChangeRequest Table Closed              | 2024-05-31                      |
| product_id           | Identifier for the product associated with the lead time      | WTC-TCOS-CSM                    |
| leadtime             | Lead time in milliseconds                                     | 14854047                        |

### change for failure table
This table defines the change failure rate from the Problem table for the past year, specifically focusing on failures caused by changes. The total count aggregates data for one year.

| Column               | Description                                                   | Example                         |
|----------------------|---------------------------------------------------------------|---------------------------------|
| id                   | Unique identifier for each change failure record              | C4F_WTC-TCOS-SDOM_2024-06-11    |
| date                 | Date of the ServiceNow Problem Table Closed                   | 2024-06-11                      |
| product_id           | Identifier for the product                                    | WTC-TCOS-SDOM                   |
| change_fail_count    | Number of failed changes recorded on the given date           | 1                               |
| total_count          | Total number of changes attempted on the given date           | 2                               |
| change_failure_rate  | Ratio of failed changes to total changes                      | 0.5                             |

### time to restore table
This table defines the time to restore from the Incident table, calculated from the time an incident is created to when it is resolved.

| Column               | Description                                                   | Example                         |
|----------------------|---------------------------------------------------------------|---------------------------------|
| id                   | Unique identifier for each time to restore record             | T2R_WTC-TCOS-SDOM_2024-05-17    |
| date                 | Date of the ServiceNow ChangeRequest Table Closed             | 2024-05-17                      |
| product_id           | Identifier for the product associated with the time to restore| WTC-TCOS-SDOM                   |
| time2restore         | Time to restore in seconds                                    | 2164                            |
