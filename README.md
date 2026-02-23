<h2>Overview:</h2>
<p>
This application is a Java-based RESTful web service that allows users to create a Package consisting of multiple Products, as well as retrieve, update, and delete existing Packages. It also supports converting the total price of a Package into several global currencies.
</p>
<p>
The solution includes a Spring Boot API and a minimal React frontend. It makes use of a Postgres database, with the option to run locally using an in-memory repository implementation. The API also exposes a Swagger (OpenAPI) page for convenient endpoint testing.
</p>

<h2>How to Run</h2>
<p>In order to run this solution you require the following prerequisites installed on your machine:</p>
<ul>
  <li>JDK 17</li>
  <li>Node.js / npm</li>
  <li>Postgres</li>
</ul>
<br/>

<p>You will also need to configure the following environment variables in order to access the ProductServiceGateway:</p>
<ul>
  <li>PRODUCT_GATEWAY_USER</li>
  <li>PRODUCT_GATEWAY_PASSWORD</li>
  <li>PRODUCT_DB_USER</li>
  <li>PRODUCT_DB_PASSWORD</li>
</ul>
<p>You should have access to the correct values for these variables.</p>
<br/>

<p>
Ensure that Postgres is running prior to launching the backend service, and that the <b>packages</b> database has been created.
</p>
<p>
Ensure that the backend service is running prior to launching the React frontend via <b>npm start</b>.
</p>
<p>
If you would like to use in-memory data management instead of Postgres, set <b>package.repository.inmemory=true</b> in <b>application.properties</b>.
</p>

<br/>

<h2>High-Level Architecture</h2>
<p>Key architectural principles:</p>
<ul>
  <li>
    <b>Separation of concerns:</b> Processing of Package requests, data storage and retrieval, product retrieval via the gateway client, and currency conversion are all handled by dedicated services.
  </li>
  <li>
    <b>Dependency inversion:</b> External dependencies (ProductServiceGateway and Frankfurter CurrencyService) are accessed via interfaces.
  </li>
  <li>
    <b>Extensibility:</b> The use of interfaces for services and repositories allows new implementations to be added in the future without modifying existing business logic.
  </li>
  <li>
    <b>Performance:</b> Hash-based lookups and caching for Products and Currency Rates ensure efficient data access. Currency rates are refreshed once per day, which aligns with the Frankfurter service update schedule.
  </li>
  <li>
    <b>Concurrency:</b> ConcurrentHashMap is used for shared in-memory data structures to ensure thread-safe operations in a multi-threaded environment without requiring explicit synchronization.
  </li>
</ul>

<br/>

<h2>React Frontend</h2>
<p>
A minimal React frontend application is included as a proof-of-concept interface for interacting with the package-service API.
</p>
<p>
It currently supports creating and listing Packages. With more time, I would expand it in the following ways:
</p>
<ul>
  <li>Add functionality to retrieve specific Packages by ID and update existing Packages</li>
  <li>Add validation for input fields</li>
  <li>Replace free-text currency input with a dropdown populated from the API</li>
  <li>Add improved error handling and user-friendly error notifications</li>
  <li>Enhance overall UI design with clearer separation of pages and improved styling</li>
</ul>

<br/>

<h2>Backend Java / Spring Boot Web Service</h2>
<p>
The backend service is the core of the solution, and therefore where the majority of development effort was focused. It was designed with performance and extensibility as primary goals.
</p>
<p>
Validation is applied to all API endpoints to ensure requests contain correctly formatted data and comply with constraints such as maximum string lengths.
</p>
<p>
A Swagger (OpenAPI) page is included to allow for easy testing and exploration of the API.
</p>

<p>
While the backend meets the requirements of an MVP, the following improvements could be made with more time:
</p>
<ul>
  <li>
    Review and potentially adjust the product caching schedule depending on how frequently Products are updated externally.
  </li>
  <li>Add an endpoint to retrieve all supported currency rates.</li>
  <li>Increase unit test coverage (CurrencyService, ProductServiceGateway, and repository layer).</li>
  <li>Implement authentication and authorization (e.g. OAuth2 or JWT).</li>
  <li>Add structured auditing to persist a record of all interactions with the service.</li>
  <li>Introduce a service bus to queue failed Create Package requests for later processing.</li>
  <li>Implement a circuit breaker to protect against cascading failures from external services.</li>
  <li>Add request rate limiting to prevent high-volume abuse of the API.</li>
</ul>
<br/>

<h2>Potential Additions</h2>

<p>
With more time, I would have implemented CI/CD pipelines and infrastructure provisioning scripts to enable automated deployment to a cloud provider.
</p>

<p>
This would include infrastructure-as-code (e.g. Terraform or similar) to provision the required resources (compute, database, networking), as well as separate deployment pipelines for the Spring Boot API and the React frontend.
</p>

<p>
I would also containerise both the backend and frontend using Docker, ensuring consistent environments across local development, testing, and production. This would simplify deployment and improve portability across cloud platforms.
</p>

<p>
Sensitive configuration values (such as gateway credentials and database passwords) would be migrated to a managed cloud secrets service rather than relying on local environment variables. This would improve security, centralise secret management, and better align the solution with production-grade deployment practices.
</p>

<h2>Postgres Database / In-Memory Data</h2>
<p>Two <b>IPackageRepository</b> implementations exist:</p>
<ul>
  <li><b>PackageRepository</b> (Postgres)</li>
  <li><b>InMemoryPackageRepository</b></li>
</ul>

<p>
The appropriate implementation is registered at startup based on the <b>package.repository.inmemory</b> configuration value in <b>application.properties</b>.
</p>

<p>This approach:</p>
<ul>
  <li>Simplifies local development</li>
  <li>Improves testability</li>
  <li>Makes the system easily extensible to support additional data storage providers in the future</li>
</ul>

<br/>

<h2>Backend Service Error Handling</h2>
<p>Package operations include structured exception handling for:</p>
<ul>
  <li>IllegalArgumentException</li>
  <li>Custom PackageNotFoundException</li>
  <li>Unexpected runtime exceptions</li>
</ul>

<p>
Errors are logged in a readable and structured manner, and appropriate HTTP responses are returned. The application remains responsive even in the event of failures.
</p>

<br/>

<h2>Testing Strategy</h2>

<p><b>Unit tests are included for:</b></p>
<ul>
  <li>PackageService</li>
</ul>

<p>
External dependencies (IPackageRepository, IProductServiceGateway, and ICurrencyService) are mocked to ensure tests are fast, deterministic, and independent of external infrastructure.
</p>

<p><b>Integration tests are included for:</b></p>
<ul>
  <li>PackageController</li>
</ul>

<p>
These tests spin up the application context and verify endpoints end-to-end.
</p>
