# \ConsentGrantManagementApi

All URIs are relative to *http://consent.consent.svc.cluster.local*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_consents_user**](ConsentGrantManagementApi.md#get_consents_user) | **GET** /v2alpha/consents/user/{data_subject_id} | Return all the consents granted by the given data subject
[**post_bulk_consents**](ConsentGrantManagementApi.md#post_bulk_consents) | **POST** /v2alpha/consents/bulk | Grant the given data subject's consent.
[**post_bulk_consents_user_revoke**](ConsentGrantManagementApi.md#post_bulk_consents_user_revoke) | **POST** /v2alpha/consents/bulk/user/{data_subject_id}/revoke | Revoke the given data subject's consent to the given data attributes for the given client group
[**post_consents**](ConsentGrantManagementApi.md#post_consents) | **POST** /v2alpha/consents | Grant the given data subject's consent to the given data attributes for the given client group.
[**post_consents_user_revoke**](ConsentGrantManagementApi.md#post_consents_user_revoke) | **POST** /v2alpha/consents/user/{data_subject_id}/revoke | Revoke the given data subject's consent to the given data attributes for the given client group



## get_consents_user

> crate::models::GetConsentsUser200Response get_consents_user(data_subject_id, data_attributes)
Return all the consents granted by the given data subject

For unknown users, the returned list is empty (no error is indicated).

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**data_subject_id** | **String** |  | [required] |
**data_attributes** | Option<[**Vec<String>**](String.md)> | Limits the result to only data_attributes provided. Search is case sensitive. If this is not included then it returns all consent grants for that user. |  |

### Return type

[**crate::models::GetConsentsUser200Response**](get_consents_user_200_response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json;charset=utf-8

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## post_bulk_consents

> post_bulk_consents(post_bulk_consents_request)
Grant the given data subject's consent.

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**post_bulk_consents_request** | [**PostBulkConsentsRequest**](PostBulkConsentsRequest.md) |  | [required] |

### Return type

 (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## post_bulk_consents_user_revoke

> post_bulk_consents_user_revoke(data_subject_id, post_bulk_consents_user_revoke_request)
Revoke the given data subject's consent to the given data attributes for the given client group

Unknown data attributes are ignored. Data attributes for which the given data subject has not granted consent to the given client group are ignored (i.e. this endpoint is idempotent).

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**data_subject_id** | **String** |  | [required] |
**post_bulk_consents_user_revoke_request** | [**PostBulkConsentsUserRevokeRequest**](PostBulkConsentsUserRevokeRequest.md) |  | [required] |

### Return type

 (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## post_consents

> post_consents(post_consents_request)
Grant the given data subject's consent to the given data attributes for the given client group.

The given data attributes are not validated and stored as-is.

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**post_consents_request** | [**PostConsentsRequest**](PostConsentsRequest.md) |  | [required] |

### Return type

 (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## post_consents_user_revoke

> post_consents_user_revoke(data_subject_id, post_bulk_consents_user_revoke_request_grants_inner)
Revoke the given data subject's consent to the given data attributes for the given client group

Unknown data attributes are ignored. Data attributes for which the given data subject has not granted consent to the given client group are ignored (i.e. this endpoint is idempotent).

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**data_subject_id** | **String** |  | [required] |
**post_bulk_consents_user_revoke_request_grants_inner** | [**PostBulkConsentsUserRevokeRequestGrantsInner**](PostBulkConsentsUserRevokeRequestGrantsInner.md) |  | [required] |

### Return type

 (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

