# Security Token Service

Called from External Authorizer in Istio to provide authentication, authorization with Keycloak.

## Setting Keycloak
The following clients must exist in Keycloak.
1. admin-cli(in MasterRealm)
   - Exists by default when MasterRealm is created.
   - Required to use AdminAPI.
2. oauth-client(in WovenRealm)
   - Required to authentication.
   - Set the URL of sts's callback in "Valid Redirect URIs".
3. security-token-service(in WovenRealm)
   - Required to authorization.
   
## Setting Environments
Description of the environment variables and an example of their settings are as follows.

```shell script
export AUTH_SERVER_URL="http://id.woven-city.global/auth" #BACKEND-URL of Keycloak.
export AUTH_SERVER_URL_REDIRECT="http://id.woven-city.global/auth" #FRONT-URL of Keycloak.
export AUTH_REALM_NAME="woven"
export ADMIN_CLIENT_ID="admin-cli"
export ADMIN_USER_ID="USER" #Keycloak's admin username.
export ADMIN_USER_PW="PASSWORD" #Keycloak's admin password.
export OAUTH_COOKIE_NAME="_oauth2_istio_ingressgateway" #Name of cookie used by sts.
export OAUTH_COOKIE_SECRET="XXXXX" #Secret of cookie. Use for create cookie signatures.
export OAUTH_CALLBACK_PATH="_id/callback" #Callback Path for sts.
export OAUTH_SIGNOUT_PATH="_id/signout" #Signout Path for sts.
export OAUTH_COOKIE_MAXAGE="86400" #Cookie Max-Age Setting. Numbers Only.
export OAUTH_COOKIE_SECURE="false" #true/false Cookie Secure Setting.
export CONFIG_COLLECTION_PATH="../config/sts-config-collection.yml" #Path where the ConfigCollectionFile created by CityServiceOperator is located.
export DEFAULT_CONFIG_PATH="../config/default-config.yml" #Path where DefaultConfigFile is located.
export SESSION_MANAGE="inmemory" #Using inmemory session management, add like this.
```
If using redis for session management, add environment variables as follows.
```shell script
export SESSION_MANAGE="redis"
export REDIS_URL="redis" #URL of redis(without prefix 'http://').
export REDIS_PORT="6379" #redis's portNo.
```
If using couchDB for session management, add environment variables as follows.
```shell script
export SESSION_MANAGE="couchdb"
export COUCHDB_URL="couchdb" #URL of couchdb(without prefix 'http://').
export COUCHDB_PORT="5984" #couchdb's portNo.
export COUCHDB_DBNAME="sts-session" #Database name for session management.
export COUCHDB_USER="USER" #couchdb's admin username.
export COUCHDB_PW="PASSWORD" #couchdb's admin password.
```
