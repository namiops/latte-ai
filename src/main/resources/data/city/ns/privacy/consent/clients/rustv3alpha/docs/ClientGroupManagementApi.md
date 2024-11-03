# \ClientGroupManagementApi

All URIs are relative to *http://consent.consent.svc.cluster.local*

Method | HTTP request | Description
------------- | ------------- | -------------
[**delete_clients_client**](ClientGroupManagementApi.md#delete_clients_client) | **DELETE** /v3alpha/admin/clients/{client_id} | Remove a client from all groups
[**delete_groups_group**](ClientGroupManagementApi.md#delete_groups_group) | **DELETE** /v3alpha/admin/groups/{group_id} | Delete a group
[**delete_groups_group_clients**](ClientGroupManagementApi.md#delete_groups_group_clients) | **DELETE** /v3alpha/admin/groups/{group_id}/clients | Remove clients from a group
[**get_groups**](ClientGroupManagementApi.md#get_groups) | **GET** /v3alpha/admin/groups | Get all groups and the entire consent client grouping
[**post_groups_group**](ClientGroupManagementApi.md#post_groups_group) | **POST** /v3alpha/admin/groups/{group_id} | Create a new group
[**post_groups_group_clients**](ClientGroupManagementApi.md#post_groups_group_clients) | **POST** /v3alpha/admin/groups/{group_id}/clients | Add clients to a group



## delete_clients_client

> delete_clients_client(client_id)
Remove a client from all groups

No error is raised if a given client is not currently part of any group.

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**client_id** | **String** |  | [required] |

### Return type

 (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## delete_groups_group

> delete_groups_group(group_id)
Delete a group

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**group_id** | **String** |  | [required] |

### Return type

 (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## delete_groups_group_clients

> delete_groups_group_clients(group_id, client_ids)
Remove clients from a group

No error is raised if a given client is not currently part of the given group.

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**group_id** | **String** |  | [required] |
**client_ids** | [**Vec<String>**](String.md) | Clients to remove from the given group | [required] |

### Return type

 (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## get_groups

> crate::models::GetGroups200Response get_groups()
Get all groups and the entire consent client grouping

### Parameters

This endpoint does not need any parameter.

### Return type

[**crate::models::GetGroups200Response**](get_groups_200_response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json;charset=utf-8

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## post_groups_group

> post_groups_group(group_id)
Create a new group

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**group_id** | **String** |  | [required] |

### Return type

 (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## post_groups_group_clients

> post_groups_group_clients(group_id, client_ids)
Add clients to a group

The group must already exist (this endpoint cannot create it).  This endpoint is idempotent, i.e. if any of the given clients are already part of the given group, no error is indicated.

### Parameters


Name | Type | Description  | Required | Notes
------------- | ------------- | ------------- | ------------- | -------------
**group_id** | **String** |  | [required] |
**client_ids** | [**Vec<String>**](String.md) | Clients to add to the given group | [required] |

### Return type

 (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

