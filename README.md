### Description
Weather Service uses OpenWeather API to find the current weather of the cities. It exposes a REST endpoint for the consumers to call the API.
Each consumer can call the API up to 5 times per hour.

### Tech stack and requirements
- Java 17
- Spring boot 3.2.5
- Bucket4J for rate limit implementation
- lombok for boiler plate codes
- H2 DB
- Spring JPA data
- JUnit for unit tests

Java 17 should be installed to be able to run the application.
You can check the java version by running:
~~~
java --versoin
~~~

### Run
The application can run by running the following command:
~~~
./gradlew bootRun
~~~

### Endpoint
Once the application is up and running, the endpoint is ready to get called:

~~~
curl -X GET \
  'http://localhost:8080/weather?city=melbourne&country=au&apiKey=api-key-1'
~~~

### API responses
- If everything is fine, will return:
  - HTTP status code `200`
  - Description returned from OpenWeather API in the body
  - return remaining call count in the header `X-Rate-Limit-Remaining`
- Returns "API Key is invalid" with status code `403` if api key is invalid
- Returns `404` not found if the city or country is invalid
- Returns `429 too many request` with a header of `X-Rate-Limit-Retry-After-Seconds` indicating waiting time


### Tables
Stores Consumer API Keys in the `Consumers` table
Stores Descriptions received from OpenWeather in the `WeatherItem` table


### Cache
It stores the responses from OpenWeather API in WeatherItem table and will use the same response if the next calls have the same city and country provided.
If the city and country provided combination is not there in db, it calls the OpenWeather API.

### todos
- Externalise rate limit configs
- Add code coverage validation mechanism and add more unit tests
- Add Integration test using Docker