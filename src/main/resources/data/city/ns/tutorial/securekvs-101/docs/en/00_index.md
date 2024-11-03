# Introduction
This tutorial covers the basics of Secure KVS. It comes with a sample TodoAPI written in Go. At the end of this tutorial, you will have the API running that will connect to Secure KVS. You will be able to see the encrypted values by viewing the CouchDB instance.

This tutorial is for people who want to test their application that uses Secure KVS locally. Refer to the Secure KVS [onboarding process](https://developer.woven-city.toyota/docs/default/component/steelcouch/onbaording/) if you want to get your application on the Agora platform.

## What is Secure KVS
Secure KVS is an in-house solution for securely storing data on the Agora platform. Secure KVS uses Steelcouch to encrypt the data and then stores it on an instance of CouchDB.

!!!Warning
    You are intended to always access Secure KVS through the Steelcouch endpoint.

## What is Steelcouch?
Steelcouch is an encryption proxy that stands in front of an instance of a CouchDB. Secure KVS uses it to store data in an encrypted form. You make HTTP calls to Steelcouch as if you were making them to CouchDB. You can make the same HTTP calls to the Steelcouch API you would usually make to the CouchDB API.

For example, when you make a PUT request to Steelcouch with specific data, Steelcouch will take that data and encrypt it. Then the data will be passed along to the backing CouchDB instance and stored as an encrypted string. And then, when you make a GET request, Steelcouch will retrieve the data from CouchDB, decrypt it and return it to you.

Keep in mind that Steelcouch is still in Beta and doesn't fully support all the functionality of CouchDB. If Steelcouch does not support the API call, it will just pass it along as is. This might not work as expected because the data in the database instance is encrypted.

For information on the supported CouchDB API calls, refer [here](https://developer.woven-city.toyota/docs/default/component/steelcouch/onbaording/#supported-couchdb-apis-by-steelcouch).

## What is CouchDB?
CouchDB is an open-source NoSQL database. It uses JSON to store data that can be accessed through an HTTP API. Secure KVS uses CouchDB instances to store data. While you don't necessarily need to know much about CouchDB to use this tutorial, it would help you understand what is going on better if you played around with an instance of CouchDB. Please have a look at the official [Getting Started](https://docs.couchdb.org/en/3.2.2-docs/intro/tour.html) page.

### Supported CouchDB version
Because Steelcouch uses a backing CouchDB instance, Steelcouch might not support some version of CouchDB.

As of writing this (2022/09/21), Steelcouch works with:

```
CouchDB Version 3.2.2
CouchDB Version 3.2.1
CouchDB Version 3.2.0

CouchDB Version 3.1.x
```

Please have a look at the official [release notes](https://docs.couchdb.org/en/stable/whatsnew/index.html).

## Moving on to Agora clusters
After you finish this tutorial and want to get started on the Agora platform, you can read the Secure KVS [onboarding section](https://developer.woven-city.toyota/docs/default/Component/steelcouch-service/onbaording/) to learn how to request a database on the Agora platform.
