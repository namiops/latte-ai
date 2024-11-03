# HTTP Glue Documentation

## HTTP Request

#### Specify URL
- Purpose: 
   - The given URL points to a server endpoint. All further client HTTP steps send the requests to that endpoint.
   
- Glue pattern

   - `@Given("^(?:URL|url): {url}$")`

- Example

   - `Given URL: https://test.example.com`

#### Get access token from Keycloak
- Purpose: 
   - Authenticate Keycloak, and capture access token to variable that can be used in subsequent steps.
   
- Glue pattern

   - `@Then(login to Keycloak, and save token to {keycloak_token}`

- Example

   - `Then login to Keycloak, and save token to "TOKEN_FROM_KEYCLOAK"`

#### Send HTTP Request

- Purpose: 
   
   - Sending requests via HTTP, also choosing the HTTP method (GET, POST, PUT, DELETE, …​) to use.
   
- Glue pattern

   - `@When("^send (GET|HEAD|POST|PUT|PATCH|DELETE|OPTIONS|TRACE) {path}$")`

- Example

   - `When send GET /todo`

#### Set HTTP Headers

- Purpose: 
   
   - Add HTTP header to the request. The header is defined with a name and receives a value. You can set multiple headers in a single step.
   
- Glue pattern

   - `@Given("^HTTP request headers$")`

- Example

   ```
    Given HTTP request headers
  | Accept          | application/json |
  | Accept-Encoding | gzip |
  ```

#### Send HTTP Body

- Purpose: 
   
   - The HTTP request can have a body content which is sent as part of the request.
   
- Glue pattern

   - `@Given("^HTTP request body: {body}$")`

- Example

    ```
    Given HTTP request body
    """
    <<content>>
    """
  ```

#### Verify HTTP Response

- Purpose: 
   
   - Verify the HTTP response content in order to make sure that the server has processed the request as expected.
   
- Glue pattern

   - `@Then("^receive HTTP {status_code}(?: {reason_phrase})?$")`

- Example

   - `Then receive HTTP 200 OK`