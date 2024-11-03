# \AgreementSigningApi

All URIs are relative to *http://consent.consent.svc.cluster.local*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_user_agreements_for_group_v1**](AgreementSigningApi.md#get_user_agreements_for_group_v1) | **GET** /agreements/v1/agreement_groups/{agreement_group_id}/user/{user_id}/agreements | Get agreements of the given group, indicating if the given user has signed each one
[**post_agreements_sign_v1**](AgreementSigningApi.md#post_agreements_sign_v1) | **POST** /agreements/v1/agreements/sign | Record that the given user has signed the given agreements
[**post_agreements_unsign_v1**](AgreementSigningApi.md#post_agreements_unsign_v1) | **POST** /agreements/v1/agreements/unsign | Record that the given user has unsigned the given agreements
[**post_agreements_user_signings_v1**](AgreementSigningApi.md#post_agreements_user_signings_v1) | **POST** /agreements/v1/agreement/signings | Returns users who signed a specific agreement.



## get_user_agreements_for_group_v1

> crate::models::GetUserAgreementsForGroupV1200Response get_user_agreements_for_group_v1(agreement_group_id, user_id)
Get agreements of the given group, indicating if the given user has signed each one

The response contains the title and content for each agreement inline, so that they can be displayed to the user if needed/desired without further API calls.  The order of the agreements in the response is not guaranteed in any way and may change between calls.

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**agreement_group_id** | **String** |  | [required] |
**user_id** | **String** |  | [required] |

### Return type

[**crate::models::GetUserAgreementsForGroupV1200Response**](get_user_agreements_for_group_v1_200_response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json;charset=utf-8

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## post_agreements_sign_v1

> post_agreements_sign_v1(post_agreements_sign_v1_request)
Record that the given user has signed the given agreements

This endpoint is idempotent: if the given user has already signed the given agreement, nothing will change and this endpoint will return success anyway.

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**post_agreements_sign_v1_request** | [**PostAgreementsSignV1Request**](PostAgreementsSignV1Request.md) |  | [required] |

### Return type

 (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## post_agreements_unsign_v1

> post_agreements_unsign_v1(post_agreements_unsign_v1_request)
Record that the given user has unsigned the given agreements

NOTE. This endpoint is for development and testing purpous only! This endpoint won't be avilable in production!  This endpoint is idempotent: if the given user hasn't signed the given agreement, nothing will change and this endpoint will return success anyway.

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**post_agreements_unsign_v1_request** | [**PostAgreementsUnsignV1Request**](PostAgreementsUnsignV1Request.md) |  | [required] |

### Return type

 (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## post_agreements_user_signings_v1

> Vec<crate::models::PostAgreementsUserSigningsV1200ResponseInner> post_agreements_user_signings_v1(post_agreements_user_signings_v1_request)
Returns users who signed a specific agreement.

The result shows which specific versions and languages of the agreement the users have signed.

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**post_agreements_user_signings_v1_request** | [**PostAgreementsUserSigningsV1Request**](PostAgreementsUserSigningsV1Request.md) |  | [required] |

### Return type

[**Vec<crate::models::PostAgreementsUserSigningsV1200ResponseInner>**](post_agreements_user_signings_v1_200_response_inner.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json;charset=utf-8

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

