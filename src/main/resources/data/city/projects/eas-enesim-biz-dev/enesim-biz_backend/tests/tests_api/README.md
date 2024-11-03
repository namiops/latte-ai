# BusinessPlanning_Simlator Backend API Test

ディレクトリ構成（記載を一部省略しています。）

```tree
project_root    （プロジェクトルートフォルダ）
│
├─backend   (バックエンドルートフォルダ)
│  │  .env.development          (バックエンドコンテナ動作環境設定)
│  │  .env.local_development    (バックエンドローカル動作環境設定)
│  │  .env.local_test           (バックエンドローカルテスト環境設定)
│  │  .env.test                 (バックエンドコンテナテスト環境設定)
│  │  .gitignore                (git登録対象外設定)
│  │  Dockerfile.dev            (バックエンド開発用Docker環境設定)
│  │  main.py                   (バックエンドエントリポイントスクリプト)
│  │  pytest.ini                (バックエンドテスト環境設定)
│  │  requirements.txt          (バックエンドライブラリ環境設定)
│  │  requirements_dev.txt      (バックエンド開発用ライブラリ環境設定)
│  │  
│  ├─app    (バックエンド機能フォルダ)
│  │  ├─api (バックエンドAPI機能フォルダ)
│  │  │  │  error_handle.py     (エラー検出スクリプト)
│  │  │  │  format_download.py  (フォーマットダウンロードスクリプト)
│  │  │  │  
│  │  │  ├─financial    (API機能経済性評価フォルダ)
│  │  │  │      financial.py    (経済性評価処理スクリプト)
│  │  │  │      
│  │  │  ├─plantmodel   (API機能設備設定フォルダ)
│  │  │  │      bat.py  (蓄電池設備設定処理スクリプト)
│  │  │  │      fc.py   (FC設備設定処理スクリプト)
│  │  │  │      grid.py (系統設備設定処理スクリプト)
│  │  │  │      load.py (需要設備設定処理スクリプト)
│  │  │  │      pv.py   (PV設備設定処理スクリプト)
│  │  │  │      sale.py (売電設備設定処理スクリプト)
│  │  │  │      
│  │  │  ├─project  (API機能プロジェクト管理フォルダ)
│  │  │  │      project.py  (プロジェクト管理処理スクリプト)
│  │  │  │      scenario.py (シナリオ管理処理スクリプト)
│  │  │  │      
│  │  │  ├─simulate (バックエンドAPI機能シミュレーション実行フォルダ)
│  │  │  │      simresult.py    (シミュレーション結果閲覧処理スクリプト)
│  │  │  │      simulation.py   (シミュレーション実行処理スクリプト)
│  │  │  │      
│  │  │  └─user (バックエンドAPI機能ユーザー管理フォルダ)
│  │  │          auth.py    (認証処理スクリプト)
│  │  │          user.py    (ユーザー管理処理スクリプト)
│  │  │          
│  │  ├─config  (バックエンド環境設定フォルダ)
│  │  │      setting.py (バックエンド環境設定スクリプト)
│  │  │      
│  │  ├─db  (バックエンドDB処理フォルダ)
│  │  │      connect.py         (DB接続処理スクリプト)
│  │  │      create_seeds.py    (初期データdb保存処理スクリプト)
│  │  │      seed_models.py     (初期データスクリプト)
│  │  │      seed_result.json   (シミュレーション結果初期データスクリプト)
│  │  │      setting_url.py     (DB接続環境設定処理スクリプト)
│  │  │      
│  │  ├─models  (モデル定義フォルダ)
│  │  │      model_class.py (モデル定義スクリプト)
│  │  │      
│  │  └─optimlogic  (optimize演算処理フォルダ)
│  │          calc_optim.py         (最適化計算処理スクリプト)
│  │          create_constrain.py   (制約式の作成スクリプト)
│  │          create_objective.py   (目的関数の作成スクリプト)
│  │          create_val.py         (設計変数の作成スクリプト)
│  │          formatting_output.py  (最適化計算結果加工スクリプト)
│  │          main_optimization.py  (最適化計算処理メインスクリプト)
│  │          
│  └─tests  (バックエンドテストフォルダ)
│      │  __init__.py   (テストモジュールマーカー)
│      │  
│      └─tests_api  (バックエンドAPIテストフォルダ)
│          │  README.md         (APIテストReadme、本ファイル)
│          │  conftest.py       (APIテスト用hook処理スクリプト)
│          │  test_common.py    (APIテスト用共通処理スクリプト)
│          │  test_config.py    (APIテスト用環境設定スクリプト)
│          │  test_const.py     (APIテスト用定数定義スクリプト)
│          │  __init__.py       (APIテストモジュールマーカー)
│          │  
│          ├─financial  (APIテスト経済性評価フォルダ)
│          │  │  test_financial.py  (APIテスト経済性評価用テスト処理スクリプト)
│          │  │  
│          │  └─test_data   (APIテスト経済性評価用データフォルダ)
│          │          test_data_financial.py    (APIテスト経済性評価用テストデータスクリプト)
│          │          
│          ├─plantdata  (APIテスト設備設定フォルダ)
│          │  │  test_bat.py              (APIテスト蓄電池設備設定テスト処理スクリプト)
│          │  │  test_fc.py               (APIテストFC設備設定テスト処理スクリプト)
│          │  │  test_format_download.py  (APIテストフォーマットダウンロード処理スクリプト)
│          │  │  test_grid.py             (APIテスト系統設備設定テスト処理スクリプト)
│          │  │  test_load.py             (APIテスト需要設備設定テスト処理スクリプト)
│          │  │  test_pv.py               (APIテストPV設備設定テスト処理スクリプト)
│          │  │  test_sale.py             (APIテスト売電設備設定テスト処理スクリプト)
│          │  │  
│          │  └─test_data   (APIテスト設備設定用データフォルダ)
│          │          test_data_bat.py              (APIテスト蓄電池設備設定用テストデータスクリプト)
│          │          test_data_fc.py               (APIテストFC設備設定用テストデータスクリプト)
│          │          test_data_format_download.py  (APIテストフォーマットダウンロード用テストデータスクリプト)
│          │          test_data_grid.py             (APIテスト系統設備設定用テストデータスクリプト)
│          │          test_data_load.py             (APIテスト需要設備設定用テストデータスクリプト)
│          │          test_data_pv.py               (APIテストPV設備設定用テストデータスクリプト)
│          │          test_data_sale.py             (APIテスト売電設備設定用テストデータスクリプト)
│          │          
│          ├─project    (APIテストプロジェクト管理フォルダ)
│          │  │  test_project.py    (APIテストプロジェクト管理テスト処理スクリプト)
│          │  │  test_scenario.py   (APIテストシナリオ管理テスト処理スクリプト)
│          │  │  
│          │  └─test_data   (APIテストプロジェクト管理用データフォルダ)
│          │          test_data_project.py  (APIテストプロジェクト管理用テストデータスクリプト)
│          │          test_data_scenario.py (APIテストシナリオ管理用テストデータスクリプト)
│          │          
│          └─simulate   (APIテストシミュレーション実行フォルダ)
│              │  test_resimulation.py  (APIテストシミュレーション再実行テスト処理スクリプト)
│              │  test_simresult.py     (APIテストシミュレーション結果閲覧テスト処理スクリプト)
│              │  test_simulation.py    (APIテストシミュレーション実行テスト処理スクリプト)
│              │  
│              └─test_data  (APIテストシミュレーション実行用データフォルダ)
│                      test_data_resimulation.py    (APIテストシミュレーション再実行用テストデータスクリプト)
│                      test_data_simresult.py       (APIテストシミュレーション結果閲覧用テストデータスクリプト)
│                      test_data_simulation.py      (APIテストシミュレーション実行用テストデータスクリプト)
│                      
├─database  (バックエンドDBフォルダ)
└─test_database (バックエンドテストDBフォルダ)
```

---

## Backend APIテスト概要

- pytestを使用し、Backend APIのテストを実行します。
- APIテストでは、各Backend API毎のテストパターンを実行します。
- APIテストには、テストDBコンテナを使用します。

※シミュレーション実行が複数回実行されるため、すべてのテスト完了には時間がかかりますので、ご注意ください。※

### APIテスト実行方法

#### Backendコンテナ上でAPIテスト実行

- powershell、ターミナルを起動します。
- project_rootパスに移動します。

```shell
cd <project_root>
```

- バックエンド、テスト用DBコンテナを起動します。

```shell
docker compose  -f "docker-compose.yml" up -d test_db backend
```

- Backendコンテナに接続します。

```shell
docker exec -it businessplanning_simulator-backend-1 bash
```

- テストを実行します。

```bash
pytest tests/tests_api/
```

#### ローカルPC上でAPIテスト実行

- powershell、ターミナルを起動します。
- project_rootパスに移動します。

```shell
cd <project_root>
```

- テスト用DBコンテナを起動します。

```shell
docker compose  -f "docker-compose.yml" up -d test_db
```

- Backendパスに移動します。

```shell
cd backend
```

- test_config.pyの環境設定を、以下のようにlocal実行に切り替えます。

```editor
# コンテナ実行用
# TEST_API_ENV = "test"
# TEST_API_ENV_ERR = "local_test"

# local実行用
TEST_API_ENV = "local_test"
TEST_API_ENV_ERR = "test"
```

- テストを実行します。

```shell
pytest tests/tests_api/
```

#### APIテスト実行結果例

```shell
=========================================================== test session starts ============================================================
platform win32 -- Python 3.11.5, pytest-7.3.1, pluggy-1.0.0
rootdir: C:\Users\57488\Documents\git\BusinessPlanning_Simulator\backend
configfile: pytest.ini
plugins: anyio-3.7.0, asyncio-0.21.1, cov-4.1.0, html-3.2.0, metadata-3.0.0
asyncio: mode=Mode.STRICT
collected 762 items

tests\tests_api\financial\test_financial.py ......ssssssssssss..sssssss.sss.s.ss....s.....s.ssssssssssss                              [  7%]
tests\tests_api\plantdata\test_bat.py .....sssssssssss..s...sss..sss..........ssssssssss                                              [ 14%]
tests\tests_api\plantdata\test_fc.py .....sssssssssss..s...sss..sss..........ssssssssss                                               [ 20%]
tests\tests_api\plantdata\test_format_download.py ..........s.s.......ss                                                              [ 23%]
tests\tests_api\plantdata\test_grid.py .........ssssssssssssssssss..s..sss.ss..ss.ssss..ss.ss..................ssssssssssssssssss     [ 35%]
tests\tests_api\plantdata\test_load.py ........ssssssssssssssss.s..sss.s..ss.sss..ss.ss................ssssssssssssssss               [ 46%]
tests\tests_api\plantdata\test_pv.py s.....ssssssssssssss..s.s..ssss..ssss.....s.....ssssssssssss                                     [ 54%]
tests\tests_api\plantdata\test_sale.py .........ssssssssssssssssss..s..sss.ss..ss.ssss..ss.ss..................ssssssssssssssssss     [ 65%]
tests\tests_api\project\test_project.py .....ss...sssssssssss.s.ss.s.s..........ssssssssss                                            [ 72%]
tests\tests_api\project\test_scenario.py ......sss...sss...ssssssss.s.sss.s.s............ssssssssssss                                 [ 80%]
tests\tests_api\simulate\test_resimulation.py ssssssssssssssssssssssssssssss                                                          [ 84%]
tests\tests_api\simulate\test_simresult.py ........ssssssss........ssssssssssssssssssssssss................ssssssssssssssss           [ 94%]
tests\tests_api\simulate\test_simulation.py ......................ssssss.s.sssssssss                                                  [100%]

=============================================== 321 passed, 441 skipped in 487.54s (0:08:07) =============================================== 
```

#### APIテスト実行結果HTML出力

- py、pytest-htmlを事前にインストールしてください。  
PCの環境に合わせて、インストールをお願いします。

```sheell
pip install py pytest-html
```

- WindowsのHTML出力字文字化け回避として、以下の設定をお願いします。  
管理者権限でPowerShellを起動します。  
以下の内容をPowerShellにコピー、ペーストして、Enterを押し、設定を更新し、PowerShellを終了します。  
設定更新後は、新たに起動したPowerShellを使用することで設定が適用されます。

```sheell
[System.Environment]::SetEnvironmentVariable("PYTHONUTF8", "1", "Machine")
```

- 以下のテスト実行コマンドを使用し、テスト結果をHTMLファイルに出力できます。

```sheell
pytest tests/tests_api/ --html=tests/tests_api/api_test_report.html --self-contained-html
```

- hook処理を実装することで、項目に情報を追加できます。  
hook処理使用する際は、テスト全体に適用されるため、注意してご使用ください。　　
以下は、dockstringに記載したテスト番号と、mark.skipのreasonを項目に追加する例になります。

```python:conftest.py
from py._xmlgen import html
import pytest


"""APIテスト結果HTML出力hook処理"""


def pytest_html_report_title(report):
    report.title = "BusinessPlanning_Simulator バックエンドAPIテストレポート"


def pytest_html_results_table_header(cells):
    # SKIP理由のタイトル名を設定
    cells.insert(1, html.th("SKIP理由"))
    cells[1].attr.class_ = "sortable reason"
    # api-test-noのタイトル名を設定
    cells.insert(1, html.th("API-Test-No[SpecNo-ApiNo-TestNo]"))
    # 最初のソートをapi-test-noに設定
    cells[1].attr.class_ = "sortable api-test-no initial-sort"
    cells[0].attr.class_ = "sortable result"
    cells.pop()


def pytest_html_results_table_row(report, cells):
    # SKIP理由の情報を設定
    cells.insert(1, html.td(report.skip_reason))
    # api-test-noの情報を設定
    cells.insert(1, html.td(report.api_test_no))
    cells.pop()


def pytest_html_results_table_html(report, data):
    # ログを削除
    if report.passed or report.skipped:
        del data[:]


@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item, call):
    outcome = yield
    report = outcome.get_result()
    # docstringのAPIテストNoの情報取得
    report.api_test_no = str(item.function.__doc__)
    # skip理由を抽出、設定
    skip_reason = ""
    # 個別設定のskipマーカー情報を取得、マーカーは1つだけ設定（0指定）
    if len(item.own_markers) > 0 and item.own_markers[0].name == "skip":
        skip_reason = item.own_markers[0].kwargs.get("reason", "")
    # Class設定のマーカー情報を取得、マーカーは、1つだけ設定（0指定）
    if len(item.parent.own_markers) > 0 and item.parent.own_markers[0].name == "skip":
        skip_reason = item.parent.own_markers[0].kwargs.get("reason", "")
    report.skip_reason = skip_reason
```

- 結果HTMLの詳細情報を初期非表示にする場合、pytest.iniへ以下を記載します。  
pytest.iniがない場合、pytestを実行するフォルダ直下「backend」に作成してください。

```txt:pytest.ini
[pytest]
;pytest-html出力結果のdetails初期非表示設定
render_collapsed = True
```

## APIテスト説明

- APIテストは、基本テストパターンと、テスト固有のパターンがあります。  
APIテストは、テストパターンに従い、テスト処理があります。

### APIテスト基本パターン

|テストNo.|ステータスコード|SendData仕様|Return評価項目|テストClass名|
|:-------:|:------------:|:-----------|:------------|:------------|
|1|200|正常|正常ステータスコード、メッセージ|TestOK|
|2|404|project_idが存在なし|異常ステータスコード、メッセージ|TestNotFoundProjectId|
|3|404|scenario_idが存在なし|異常ステータスコード、メッセージ|TestNotFoundScenarioId|
|4|404|対象idが存在なし|異常ステータスコード、メッセージ|TestNotFoundTargetId|
|5|422|SendDataのkey存在なし|異常ステータスコード、メッセージ|TestUnprocessableEntityNotEnough|
|6|422|SendDataのvalue不正値|異常ステータスコード、メッセージ|TestUnprocessableEntityInvalid|
|7|500|DB接続不可エラー|異常ステータスコード、メッセージ|TestInternalServerErrorDbConnectionRefused|
|8|500|DBなしエラー|異常ステータスコード、メッセージ|TestInternalServerErrorDbErr|
|9|500|Optimization演算エラー|異常ステータスコード、メッセージ|TestInternalServerErrorOptCalc|
|10|500|その他のエラー|異常ステータスコード、メッセージ|TestInternalServerErrorEtc|

### 固有テストパターン

- シミュレーション実行テストパターン追加分

|テストNo.|ステータスコード|SendData仕様|Return評価項目|テストClass名|
|:-------:|:------------:|:-----------|:------------|:------------|
|11|200|受付拒否（実行中に、再実行）|正常ステータスコード、メッセージ|TestOK|

- シミュレーション実行状態確認テストパターン追加分

|テストNo.|ステータスコード|SendData仕様|Return評価項目|テストClass名|
|:-------:|:------------:|:-----------|:------------|:------------|
|12|200|受付|正常ステータスコード、メッセージ|TestOK|
|13|200|処理中|正常ステータスコード、メッセージ|TestOK|
|14|200|正常、シナリオデータ欠損：pv|正常ステータスコード、メッセージ|TestOK|
|15|200|正常、シナリオデータ欠損：fc|正常ステータスコード、メッセージ|TestOK|
|16|200|正常、シナリオデータ欠損：battery|正常ステータスコード、メッセージ|TestOK|
|17|200|失敗、シナリオデータ欠損：grid|正常ステータスコード、メッセージ|TestOK|
|18|200|失敗、シナリオデータ欠損：sale|正常ステータスコード、メッセージ|TestOK|
|19|200|正常、シナリオデータ欠損：load|正常ステータスコード、メッセージ|TestOK|
|20|200|失敗、DB接続不可|正常ステータスコード、メッセージ|TestOK|
|21|200|失敗、DBなし：scenario|正常ステータスコード、メッセージ|TestOK|
|22|200|失敗、DBなし：pv|正常ステータスコード、メッセージ|TestOK|
|23|200|失敗、DBなし：fc|正常ステータスコード、メッセージ|TestOK|
|24|200|失敗、DBなし：battery|正常ステータスコード、メッセージ|TestOK|
|25|200|失敗、DBなし：grid|正常ステータスコード、メッセージ|TestOK|
|26|200|失敗、DBなし：sale|正常ステータスコード、メッセージ|TestOK|
|27|200|失敗、DBなし：load|正常ステータスコード、メッセージ|TestOK|
|28|200|失敗、DBなし：sim_result|正常ステータスコード、メッセージ|TestOK|
|29|200|失敗、DBなし：financial|正常ステータスコード、メッセージ|TestOK|
|30|200|失敗、DBなし：risk_impact|正常ステータスコード、メッセージ|TestOK|

### APIテスト処理、テストデータ説明

- APIテスト処理の共通部分は、共通処理にまとめてあります。  
APIテスト処理とAPIテスト用テストデータがあります。  
APIの仕様により、実施不可のテストパターンは、SKIPとし、理由を対象外に設定し、テスト処理は、空（pass）となっています。  
処理内容保留、未対応、テスト実施方法不明も、SKIPとし、理由を設定しています。

#### APIテスト処理テスト環境、データ準備処理

- pytest.fixtureを使用し、テスト処理に合わせた環境設定、データ準備が可能となります。  
テスト処理実行の前後にて、テストデータの準備処理が実行されます。

#### APIテスト処理テストClass名

- APIテスト基本パターンに合わせ、テストClass名を設定しております。  
テストClass名は、APIテスト基本パターン表のテストClass名の項目に記載しています。

#### APIテスト処理docstring記載

- HTML結果出力にて、APIテスト番号を設定できるようにするため、各APIテスト処理のdocstringに、APIテスト番号を設定しています。  
docstringへのAPIテスト番号の記載フォーマットは、[SpecNo-ApiNo-TestNo]になります。  
なお、SpecNo、ApiNoは、データ設計書の記載番号を設定しています。  

  記載対象例）  
  3.2 エネルギー設備の設定  
  3.2.1 PV設備の設定  
  PV発電出力計算API(ApiNo:1)  
  テストパターン1

  docstring記載例）  
  """3.2.1-001-01"""  

以下、tests_apiフォルダ配下にあるテスト処理について、説明を記載しています。

#### APIテスト共通処理

- conftest.py       (APIテスト用hook処理スクリプト)  
  本スクリプトは、HTML結果出力内容の拡張するためのhook処理スクリプトです。
  現状は、HTML結果に、APIテスト番号、SKIP理由を追加する拡張を実装しています。

- test_common.py    (APIテスト用共通処理スクリプト)  
  本スクリプトは、APIテストの共通処理のスクリプトです。
  - fixture用テストデータ準備前後処理の共通化。
  - APIテスト実行、評価処理を共通化。  

- test_config.py    (APIテスト用環境設定スクリプト)  
  本スクリプトは、APIテストの環境設定のスクリプトです。
  テスト実行時の環境設定切替処理を実装しています。
  このスクリプトの環境設定変数で、コンテナ上、ローカルPC上でのテスト実行の切替ができます。

- test_const.py     (APIテスト用定数定義スクリプト)  
  本スクリプトは、APIテストで使用する共通の定数を定義しているスクリプトです。

#### APIテストプロジェクト管理

- project/test_project.py    (APIテストプロジェクト管理テスト処理スクリプト)  
project/test_data/test_data_project.py  (APIテストプロジェクト管理用テストデータスクリプト)  

  プロジェクト管理のAPIテスト処理、テストデータのスクリプトです。  
  プロジェクト管理の5APIのテストを実施できます。

- project/test_scenario.py   (APIテストシナリオ管理テスト処理スクリプト)  
project/test_data/test_data_scenario.py (APIテストシナリオ管理用テストデータスクリプト)  

  シナリオ管理のAPIテスト処理、テストデータのスクリプトです。  
  シナリオ管理の6APIのテストを実施できます。

#### APIテスト設備設定

- plantdata/test_bat.py    (APIテスト蓄電池設備設定テスト処理スクリプト)  
plantdata/test_data/test_data_bat.py  (APIテスト蓄電池設備設定用テストデータスクリプト)  

  蓄電池設備設定のAPIテスト処理、テストデータのスクリプトです。  
  蓄電池設備設定の5APIのテストを実施できます。

- plantdata/test_fc.py     (APIテストFC設備設定テスト処理スクリプト)  
plantdata/test_data/test_data_fc.py   (APIテストFC設備設定用テストデータスクリプト)  

  FC設備設定のAPIテスト処理、テストデータのスクリプトです。  
  FC設備設定の5APIのテストを実施できます。

- plantdata/test_grid.py   (APIテスト系統設備設定テスト処理スクリプト)  
plantdata/test_data/test_data_grid.py (APIテスト系統設備設定用テストデータスクリプト)  

  系統設備設定のAPIテスト処理、テストデータのスクリプトです。  
  系統設備設定の9APIのテストを実施できます。

- plantdata/test_load.py   (APIテスト需要設備設定テスト処理スクリプト)  
plantdata/test_data/test_data_load.py (APIテスト需要設備設定用テストデータスクリプト)  

  需要設備設定のAPIテスト処理、テストデータのスクリプトです。  
  需要設備設定の8APIのテストを実施できます。

- plantdata/test_pv.py     (APIテストPV設備設定テスト処理スクリプト)  
plantdata/test_data/test_data_pv.py   (APIテストPV設備設定用テストデータスクリプト)  

  PV設備設定のAPIテスト処理、テストデータのスクリプトです。  
  PV設備設定の6APIのテストを実施できます。

- plantdata/test_sale.py   (APIテスト売電設備設定テスト処理スクリプト)  
plantdata/test_data/test_data_sale.py (APIテスト売電設備設定用テストデータスクリプト)  

  売電設備設定のAPIテスト処理、テストデータのスクリプトです。  
  売電設備設定の9APIのテストを実施できます。

- plantdata/test_format_download.py  (APIテストフォーマットダウンロード処理スクリプト)  
plantdata/test_data/test_data_format_download.py  (APIテストフォーマットダウンロード用テストデータスクリプト)

  フォーマットダウンロードのAPIテスト処理、テストデータのスクリプトです。  
  フォーマットダウンロードの1APIのテストを実施できます。

#### APIテストシミュレーション実行

- simulate/test_resimulation.py  (APIテストシミュレーション再実行テスト処理スクリプト)  
simulate/test_data/test_data_resimulation.py    (APIテストシミュレーション再実行用テストデータスクリプト)  

  シミュレーション再実行のAPIテスト処理、テストデータのスクリプトです。  
  現在、API未実装のため、テスト処理も未実装です。

- simulate/test_simresult.py     (APIテストシミュレーション結果閲覧テスト処理スクリプト)  
simulate/test_data/test_data_simresult.py       (APIテストシミュレーション結果閲覧用テストデータスクリプト)  

  シミュレーション結果閲覧のAPIテスト処理、テストデータのスクリプトです。  
  シミュレーション結果閲覧の8APIのテストを実施できます。

- simulate/test_simulation.py    (APIテストシミュレーション実行テスト処理スクリプト)  
simulate/test_data/test_data_simulation.py      (APIテストシミュレーション実行用テストデータスクリプト)  

  シミュレーション実行のAPIテスト処理、テストデータのスクリプトです。  
  シミュレーション実行の2APIのテストを実施できます。  
  本テストは、非同期並列処理が必要なため、AsyncClientを導入し、TestClientで実行できない非同期並列実行を実現しています。

#### APIテスト経済性評価

- financial/test_financial.py  (APIテスト経済性評価用テスト処理スクリプト)  
financial/test_data/test_data_financial.py    (APIテスト経済性評価用テストデータスクリプト)  

  経済性評価のAPIテスト処理、テストデータのスクリプトです。  
  経済性評価の6APIのテストを実施できます。
