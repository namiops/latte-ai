# \ConsentCheckingApi

All URIs are relative to *http://consent.consent.svc.cluster.local*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_check_consent**](ConsentCheckingApi.md#get_check_consent) | **GET** /v2alpha/be/check_consent | Check if consent is granted



## get_check_consent

> crate::models::CheckConsentResponse get_check_consent(dataattrs, user, client, action, consent_for_client_id)
Check if consent is granted

Check if the given user has granted consent for all the given data attributes to the given client.  For unknown users and unknown data attributes, \"consent not granted\" is returned (no error is indicated).

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**dataattrs** | [**Vec<String>**](String.md) | Data attributes for which to check the consent | [required] |
**user** | **String** | Woven ID of data subject | [required] |
**client** | Option<[**String**](.md)> | ID of the client for which to check whether the data subject has granted consent to share their data  If `action` is `SHARE` this field MUST be present (and non-empty). If `action` is not `SHARE` this field MUST be absent. |  |
**action** | Option<**String**> | Action for which to check consent. If absent, `SHARE` is assumed.  This field is TEMPORARILY OPTIONAL, and will become REQUIRED soon. Clients should be updated to specify this field. |  |
**consent_for_client_id** | Option<[**String**](.md)> | Service for which to check whether the data subject has granted consent  If `action` is not `SHARE`, this field MUST be present (and non-empty).  If `action` is `SHARE`, this field is TEMPORARILY OPTIONAL, and will become REQUIRED soon. Clients should be updated to specify this field.  If `action` is `SHARE`, this field has the following semantics: - if `consent_for_client_id` is present, check whether the data subject has granted consent for _that_ client to share the data subject's data with `client` - if `consent_for_client_id` is absent, check whether the data subject has granted consent for _every_ service to share the data subject's data with `client` - **Important note**: Support for this case is only temporary and will be replaced by other functionality in the future. |  |

### Return type

[**crate::models::CheckConsentResponse**](CheckConsentResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json;charset=utf-8

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

