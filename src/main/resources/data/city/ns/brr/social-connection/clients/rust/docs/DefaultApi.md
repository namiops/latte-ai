# \DefaultApi

All URIs are relative to *http://social-connection.brr.svc.cluster.local/social-connection/v1alpha*

Method | HTTP request | Description
------------- | ------------- | -------------
[**connection_woven_id_get**](DefaultApi.md#connection_woven_id_get) | **GET** /connection/{wovenId} | Returns a profile list of connected persons.
[**connection_woven_id_target_woven_id_delete**](DefaultApi.md#connection_woven_id_target_woven_id_delete) | **DELETE** /connection/{wovenId}/{targetWovenId} | Remove a mutual connection between persons.
[**connection_woven_id_target_woven_id_post**](DefaultApi.md#connection_woven_id_target_woven_id_post) | **POST** /connection/{wovenId}/{targetWovenId} | Create a mutual connection between persons.



## connection_woven_id_get

> Vec<crate::models::Profile> connection_woven_id_get(woven_id)
Returns a profile list of connected persons.

**Consent:** This endpoint is currently not subject to consent check. 

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**woven_id** | **String** |  | [required] |

### Return type

[**Vec<crate::models::Profile>**](Profile.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## connection_woven_id_target_woven_id_delete

> connection_woven_id_target_woven_id_delete(woven_id, target_woven_id)
Remove a mutual connection between persons.

**Consent:** This endpoint is currently not subject to consent check. 

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**woven_id** | **String** |  | [required] |
**target_woven_id** | **String** |  | [required] |

### Return type

 (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## connection_woven_id_target_woven_id_post

> connection_woven_id_target_woven_id_post(woven_id, target_woven_id)
Create a mutual connection between persons.

**Consent:** This endpoint is currently not subject to consent check. 

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**woven_id** | **String** |  | [required] |
**target_woven_id** | **String** |  | [required] |

### Return type

 (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

