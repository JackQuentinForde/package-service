<h2>Overview:</h2>
<p>This application is a Java based RESTful web service that allows users to create a Package consisting of multiple Products, as well as query all exising Packages, delete Packages, and update Packages. It also includes functionality to convert
the total price displayed for Packages between several global currencies.</p>
<p>The solution includes an API, as well as a minimal React frontend. It makes use of a Postgres database, but there is also the option to run it locally using an in memory repository. The API includes a Swagger page for ease of testing endpoints.</p>
</br>

<h2>How to Run</h2>
<p>In order to run this solution you require the following prerequisites installed on your machine:</p>
<ul>
  <li>JDK 17</li>
  <li>React</li>
  <li>Postgres</li>
</ul>
</br>
<p>You will also need to add the following environment variables in order to access the ProductServiceGateway:</p>
<ul>
  <li>PRODUCT_GATEWAY_USER</li>
  <li>PRODUCT_GATEWAY_PASSWORD</li>
  <li>PRODUCT_DB_USER</li>
  <li>PRODUCT_DB_PASSWORD</li>
</ul>
<p>You should have access to the correct values for these variables</p>
</br>
<p>Ensure that Postgres is running prior to launching the web service, and that the packages database has been created.</p>
<p>Ensure that the web service is up and running prior to launching the React frontend, via npm start.</p>
<p>if you would like to use in memory data management, set package.repository.inmemory to true i application.properties</p>
</br>

<h2>High-Level Architecture</h2>
<p>Key architectural principles:</p>
<ul>
  <li><b>Separation of concerns:</b> Processing of Package requests, data storage and retrieval, retrieval of Products from the gateway client, currency conversion, is all handled by dedicated services.</li>
  <li><b>Dependency inversion:</b> External dependencies (ProductServiceGateway, Frankfurter CurrencyService) are accessed via interfaces.</li>
  <li><b>Extendability:</b> The use of interfaces for all services and the repository means that we can easily add new implementations in the future should we need to without having to modify the existing interfaces or overall structure of the solution.</li>
  <li><b>Performance:</b> The use of hash maps and caching for Products and Currency Rates means that lookups are fast and efficient, and we remove the need to constantly query the external APIs. The caches for both Products and Currency Rates are updated once per day,
  which is particularly relevant for Currency Rates as the Frankfurter service only updates it's conversion rates once per day.</li>
  <li><b>Concurrency:</b> The project uses ConcurrentHashMaps to manage shared data structures safely in a multi-threaded environment. ConcurrentHashMap provides thread-safe operations for lookups, inserts, 
  and updates, reducing the risk of race conditions when multiple threads access or modify the map concurrently. This ensures reliable and consistent behavior without requiring explicit synchronization for standard operations.</li>
</ul>
</br>

<h2>React Frontend</h2>
<p>A React frontend application is included in the solution. This was designed to be minimal, but functional as a proof-of-concept frontend interacting with the package-service API.</p>
<p>It serves it's purpose as a proof-of-concept, however with more time I would have liked to expand it in the following ways:</p>
<ul>
  <li>Add functionality to be able to retrieve specific Packages by id, and update existing packages</li>
  <li>Add validation for input fields</li>
  <li>A dropdown list of available currencies rather than a free text input. This could be populated by an request to the package-service API</li>
  <li>Error handling for failed API requests, and frontend friendly error popups</li>
  <li>A more thouroughly designed UI, with attracive styling and separate pages for different operations</li>
</ul>
</br>

<h2>Backend Java / Springboot Web Service</h2>
<p>The backend web service is the core of the soluton, and is therefore where I focused the majority of my development time. It was designed with performance, and extenability as the primary features.</p>
<p>Validation has been added to all API endpoints, to ensure that requests are made with the correct arguments supplied in the correct format, and within stipulations such as maximum length of string properties.</p>
<p>A Swagger page is also added to allow for easy testing of API endpoints.</p>
<p>I am mostly satisfied with this backend service, and I feel that it meets the requirements of an MVP, however with more time I would like to make the following improvements / additions:</p>
<ul>
  <li>Currently the Products are cached once per day on a schedule, which may be sufficient, however this depends on when and how Products are added to the external Product database. I am assuming that new Products are added only as
  often as once per day at midnight, however if that is not the case, a more frequent caching schedule may be required, or even an event that the CurrencyService could subscribe to so that every time a Product is added, the cache is updated</li>
  <li>Add an endpoint which retrieves all available Currency Rates, as mentioned previously</li>
  <li>Unit tests for the CurrencyService, ProductServiceGateway, and PacakgeRepository</li>
  <li>Implement authentication and authorisation for the API, this could be anything from OAuth to...</li>
  <li>The logger is sufficient for an MVP, but I would have liked to add some kind of auditing database to permanently record all interactions with the service.</li>
  <li>Implement a Service Bus which could be used to queue failed Create Package requests until a later time when the service is functioning, so as to guarantee eventual processing of these requests.</li>
  <li>Implement a Circuit Breaker to protect API calls from cascading failures by temporarily halting requests to failing external services (Frankfurter; ProductServiceGateway)</li>
  <li>Implement functionality to prevent a user from making a high volume of API requests withing a short time frame</li>
</ul>
</br>

<h2>Postgres Database / In Memory Data</h2>
<p>Two IPackageRepository implementations exist:</p>
<ul>
  <li><b>PackageRepository</b></li>
  <li><b>InMemoryPackageRepository</b></li>
</ul>
<p>Both implement <b>IPackageRepository</b>, allowing the data storage implementation to be swapped without changing application logic.</p>
</br>
<p>The appropriate implementation is registered at startup based on the <b>package.repository.inmemory</b> configuration value in application.properties. This approach:</p>
<ul>
  <li>Simplifies local development</li>
  <li>Improves testability</li>
  <li>Makes the system easily extensible to support additional data storage providers in the future</li>
</ul>
</br>

<h2>Backend Service Error handling</h2>
<p>Package operations include structured exception handling for:</p>
<ul>
  <li>Illegal argument errors</li>
  <li>Custom PackageNotFound errors</li>
  <li>Any other unexpected errors</li>
</ul>
</br>
<p>Errors are captured and logged in a readable, yet detailed manner. The application remains responsive and usable even after failures.</p>
</br>

<h2>Testing Strategy</h2>
<p>Unit tests are included for:</p>
<ul>
  <li>PackageService</li>
</ul>
</br>
<p>External dependencies IPackageRepository, IProductServiceGateway and ICurrencyService are mocked to ensure tests are fast, deterministic, and do not require external infrastructure.</p>
<p>Integration tests are included for:</p>
<ul>
  <li>PackageController</li>
</ul>
</br>
<p>These are desgined to be more thorough, as they actually spin up the service and test all the endpoints from end to end.</p>
