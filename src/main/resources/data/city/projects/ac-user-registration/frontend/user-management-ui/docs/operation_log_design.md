# Operation log design

This document describes the expression of the operation log of the User management system Frontend.

## Registration Log

Operation logs for user information registration/edit/delete and household registration/edit/delete are displayed.

### Operation Pattern

Check the [BE doc](../../../backend/docs/database/design.md) for the latest information.

| Operation Name                  | Description(En)                           | Description(Ja)    |
| :------------------------------ | :---------------------------------------- | :----------------- |
| CreateUser                      | Create a new user                         | ユーザーの新規作成    |
| CreateChildUser                 | Create a new child user                   | 子供ユーザーの新規作成 |
| UpdateGuardians                 | Update guardian users                     | 保護者の更新         |
| DeleteGuardians                 | Delete guardian users                     | 保護者の削除         |
| UpdateBasicInformation          | Update basic information                  | 基本情報の更新        |
| UpdateEmergencyContact          | Update an emergency contact               | 緊急連絡先の更新      |
| UpdateFaceImage                 | Update a face image                       | 顔画像の更新         |
| UpdateIdVerification            | Update ID verification information        | 本人確認日の更新      |
| UpdateTrainingQualificationInfo | Update training qualification information | 交通教育完了日の更新   |
| CreateHousehold                 | Create a household                        | 世帯の新規作成        |
| AddHouseholdMembers             | Add household members                     | 世帯メンバーの追加     |
| RemoveHouseholdMembers          | Remove household members                  | 世帯メンバーの削除     |
| DeleteHousehold                 | Delete a household                        | 世帯の削除            |
| UpdateHouseholdRepresentative   | Update household representative           | 世帯主の更新          |

### Additional Information

#### CreateUser

The items in `detail` will be shown with `Description`.

Ex.

```text
Create a new user
Items: Alphabetical Name, Kanji, Kana, Date Of Birth, Email Address, Phone Number
```

#### CreateChildUser

仕様変更により使われなくなった

#### UpdateGuardians

Guardian's names (Both en and ja) are shown with `Description` using guardianIds.
If the corresponding user does not exist in BURR, display Id instead.

Ex.

```text
Update guardian users
Guardians: Taro Woven / タロー ウーブン
```

#### DeleteGuardians

Guardian's names (Both en and ja) are shown with `Description` using guardianIds.
If the corresponding user does not exist in BURR, display Id instead.

Ex.

```text
Delete guardian users
Guardians: Taro Woven / タロー ウーブン
```

#### UpdateBasicInformation

The items in `detail` will be shown with `Description`.

Ex.

```text
Update basic information
Items: Email Address, Phone Number
```

#### UpdateEmergencyContact

The items in `detail` will be shown with `Description`.

Ex.

```text
Update an emergency contact
Items: Name Of Emergency Contact Information, Phone Number
```

#### UpdateFaceImage

`Description` will be shown.

Ex.

```text
Update a face image
```

#### UpdateIdVerification

The items in `detail` will be shown with `Description`.

Ex.

```text
Update ID verification information
Verification Date: 2024/10/01
```

#### UpdateTrainingQualificationInfo

The items in `detail` will be shown with `Description`.

Ex.

```text
Update training qualification information
Completed Date: 2024/10/01
Revision: v1
```

#### CreateHousehold

The items in `detail` will be shown with `Description`.

Ex.

```text
Create a household
City Address: 1A-1F-1
Representative: Taro Woven / タロー ウーブン
Members: Jiro Woven / ジロー ウーブン, Saburo Woven, Hanako Woven
Period: 2024/10/01 - 2026/09/31
```

#### AddHouseholdMembers

The items in `detail` will be shown with `Description`.

Ex.

```text
Add household members
Household Id: XXXXXX-XXXXX-XXXXXX
```

#### RemoveHouseholdMembers

The items in `detail` will be shown with `Description`.

Ex.

```text
Remove household members
Household Id: XXXXXX-XXXXX-XXXXXX
```

#### DeleteHousehold

The items in `detail` will be shown with `Description`.

Ex.

```text
Delete a household 
Household Id: XXXXXX-XXXXX-XXXXXX
```

#### UpdateHouseholdRepresentative

The items in `detail` will be shown with `Description`.

Ex.

```text
Update household representative 
Household Id: XXXXXX-XXXXX-XXXXXX
```

#### The Other Case

`Description` will be shown.

## Corporation Log

Operation logs for corporation information registration/edit/delete, tenant information registration/edit/delete and worker add/edit/remove are displayed.

### Operation Pattern

Check the [BE doc](../../../backend/docs/database/design.md) for the latest information.

| Method | Path                                                | Description(En)                | Description(Ja) |
| :----- | :-------------------------------------------------- | :----------------------------- | :-------------- |
| POST   | /corporations                                       | Create a new corporation       | 法人の新規作成    |
| PUT    | /corporations/{corporationId}                       | Update corporation Information | 法人情報の更新    |
| POST   | /businessTenants                                    | Create a new tenant            | テナントの新規作成 |
| PUT    | /businessTenants/{businessTenantId}                 | Update tenant information      | テナント情報の更新 |
| PUT    | /corporations/{corporationId}/memberships/{wovenId} | Update worker information      | 従業員情報の更新   |
| DELETE | /corporations/{corporationId}                       | Delete a corporation           | 法人の削除        |
| DELETE | /businessTenants/{businessTenantId}                 | Delete a tenant                | テナントの削除     |
| DELETE | /corporations/{corporationId}/memberships/{wovenId} | Delete worker information    | 従業員情報の削除   |

### Additional Information

#### Create a new corporation

`name` and `representative`'s name will be shown with `Description`.
If the corresponding representative does not exist in BURR, display Id instead.

Ex.

- If `resultCode` === 201

```text
[Success]Create a new corporation
name: AAA Corporation
representative: Taro Woven / タロー ウーブン
```

- If `resultCode` !== 201

```text
[Failed]Create a new corporation
name: AAA Corporation
representative: Taro Woven / タロー ウーブン
```

#### Update corporation Information

`name` and `representative`'s name will be shown with `Description` and `result`.
If the corresponding representative does not exist in BURR, display Id instead.

Ex.

- If `resultCode` === 204

  ```text
  [Success]Update a new corporation
  Name: AAA Corporation
  Representative: Taro Woven / タロー ウーブン
  ```

- If `resultCode` !== 204

  ```text
  [Failed]Update a new corporation
  Name: AAA Corporation
  Representative: Taro Woven / タロー ウーブン
  ```

#### Create a new tenant

`name`, `representative`'s name, `corporation name`, `city address`, and `period` will be shown with `Description`.
If the corresponding representative or corporation does not exist in BURR, display Id instead.

Ex.

- If `resultCode` === 201

```text
[Success]Create a new tenant
Tenant Id: XXXXX-XXXXX-XXXXX
Name: AAA tenant
Representative: Taro Woven / タロー ウーブン
City Address: 1A-B1F-1
Period: 2024/10/01 - 2026/09/31
```

- If `resultCode` !== 201

```text
[Failed]Create a new tenant
Tenant Id: XXXXX-XXXXX-XXXXX
Name: AAA tenant
Representative: Taro Woven / タロー ウーブン
City Address: 1A-B1F-1
Period: 2024/10/01 - 2026/09/31
```

#### Update tenant Information

`name`, `representative`'s name, and `corporation name`, will be shown with `Description`.
If the corresponding representative or corporation does not exist in BURR, display Id instead.

Ex.

- If `resultCode` === 204

```text
[Success]Update tenant information
Tenant Id: XXXXX-XXXXX-XXXXX
Name: AAA tenant
Representative: Taro Woven / タロー ウーブン
```

- If `resultCode` !== 204

```text
[Failed]Update tenant information
Tenant Id: XXXXX-XXXXX-XXXXX
Name: AAA tenant
Representative: Taro Woven / タロー ウーブン
```

#### Update worker Information

`worker`'s name, `corporation name`, `tenants` name, and `period` will be shown with `Description`.
If the corresponding user or corporation does not exist in BURR, display Id instead.

Ex.

- If `resultCode` === 204

```text
[Success]Update worker information
Name: Hanako Woven / ハナコ ウーブン
Tenants: tenant A, tenant B
Period: 2024/10/01 - 2024/12/31
```

- If `resultCode` !== 204

```text
[Failed]Update worker information
Name: Hanako Woven / ハナコ ウーブン
Tenants: tenant A, tenant B
Period: 2024/10/01 - 2024/12/31
```

#### Delete a corporation

`corporation Id` will be shown with `Description`.

Ex.

- If `resultCode` === 204

```text
[Success]Delete a corporation
```

- If `resultCode` !== 204

```text
[Failed]Delete a corporation
```

#### Delete a tenant

`tenant Id` will be shown with `Description`.

Ex.

- If `resultCode` === 204

```text
[Success]Delete a tenant
Tenant Id: XXXXX-XXXXX-XXXXX
```

- If `resultCode` !== 204

```text
[Failed]Delete a tenant
Tenant Id: XXXXX-XXXXX-XXXXX
```

#### Delete worker Information

`worker`'s name and `corporation name` will be shown with `Description`.
If the corresponding user or corporation does not exist in BURR, display Id instead.

Ex.

- If `resultCode` === 204

```text
[Success]Delete worker information
Name: Hanako Woven / ハナコ ウーブン
```

- If `resultCode` !== 204

```text
[Failed]Delete worker information
Name: Hanako Woven / ハナコ ウーブン
```

#### The Other Case

`Description` will be shown.

## NFC Log

Operation logs for nfc information registration/edit/delete are displayed.

### Operation Pattern

Check the [BE doc](../../../../ac-access-control/nfc-manager/docs/data_design/README.md) for the latest information.

| Operation Type  | Description(En)         | Description(Ja)    |
| :-------------- | :---------------------- | :----------------- |
| CreateNFC       | Create a new card       | カードの新規作成      |
| DeleteNFC       | Delete a card           | カードの削除         |
| UpdateNFC       | Update card information | カード情報の更新      |
| UpdateNFCStatus | Update card status      | カードステータスの更新 |

### Additional Information

#### CreateNFC

The information in `body` will be shown with `Description`.

Ex.

- If `shared` === false

  ```text
  Create a new card
  Type: Personal
  User: Taro Woven / タロー ウーブン
  Period: 2024/10/01 - 2026/09/31
  ```

- If `shared` === true

  ```text
  Create a new card
  Type: Shared
  Business Id: For Uber eats driver
  User Group: ug:XXXXXX
  Period: 2024/10/01 - 2026/09/31
  ```

#### DeleteNFC

`Description` will be shown.

Ex.

```text
Delete a card
```

#### UpdateNFC

The information in `body` will be shown with `Description`.

Ex.

- If `shared` === false

  ```text
  Update card information
  Status: lending
  Period: 2024/10/01 - 2026/09/31
  ```

- If `shared` === true

  ```text
  Update card information
  Business Id: For Uber eats driver
  Status: lending
  Period: 2024/10/01 - 2026/09/31
  ```

#### UpdateNFCStatus

The `status` will be shown with `Description`.

Ex.

```text
Update card status
Status: lending
```

#### The Other Case

`Description` will be shown.
