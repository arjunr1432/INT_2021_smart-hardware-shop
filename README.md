# Smart hardware Shop Project
The hardware store Hardvare realises that online shopping has gotten popular and wish to offer their products through an online shop. We have
a UI ready and now need some backend APIs to support the proper functioning of this application.


## Important Documents to Consider
### Requirement specification
Initial requirement specification document with detailed description and diagrams can be found in the following location.
```
documents/ProjectRequirementSpecification.pdf
```
### Open API Specification
We have used OAS3 for defining our APIs, the document specification can be found in the following location. We have used this same document for generation of our APIs and its related model classes.
```
doccuments/OpenAPISpec.yaml
```
### Postman collection
All the API requests with sample data can be found in the following location.
```
documents/SmartHardWareShop.postman_collection.json
```
## How to install and run the application:
This is a simple spring-boot application with basic spring security, with two user roles.


Inorder to start with local, do the following steps.
1. Prerequisites : The MySql server should be up and running.
2. Clone the repository to your local
```
 git clone git@github.com:arjunr1432/smart-hardware-shop.git

```
3. Install the application using following command.
```
mvn clean install
```
4. Test the application using following command.
```
mvn clean test
```
5. Start the application using following command.
```
mvn spring-boot:run
```
## Running MySQL and our Application in Docker
The docker-compose file is already configured with our app-server to start along with the mysql container.
```shell script
docker-compose up -d
```

## Testing and Verifying the application
Once the application is started successfully, with the help of the shared postman collection, we can start testing our APIs.
All the APIs are secured with Basic Authentication mechanism. 

### User Groups
We have mainly two user groups (ADMIN and CUSTOMER)

Admin user credentials
```
username: admin
password: admin_password
```
Customer user credentials
```
username: customer
password: customer_password
```

The Admin user has access to CREATE/UPDATE/DELETE APIs, and the Customer user has access to GET APIs and Checkout/Order APIs. The Admin user has no access to the Checkout/Order APIs(With expectation that, the Admins itself will not make orders)

### API Summary and access roles
| API Name        | Type   | USER Roles    | Description |
| ----------------|------- | ------------- |----|
| 1.0 - GetProductList  | GET    | ADMIN/CUSTOMER| To list all the product list from the catalogue |
| 1.1 - AddProduct      | POST   | ADMIN         | To add a new Product to the catalogue |
| 1.2 - UpdateProduct   | PUT    | ADMIN         | Update an existing product in the catalogue |
| 1.3 - DeleteProduct   | DELETE | ADMIN         | Delete an existing product from the catalogue |
| 2.0 - GetNewsList     | GET    | ADMIN/CUSTOMER| To list all the news in our system |
| 2.1 - AddNews         | POST   | ADMIN         | Create a news to our system |
| 3.0 - CreateOrder     | POST   | CUSTOMER      | To create a new Order, to which we will add products for checkout |
| 3.1 - AddToOrder      | PUT    | CUSTOMER      | Add a product to an existing order |
| 3.2 - OrderSummary    | GET    | CUSTOMER      | To view the order summary with total price of the order|

### Application flow (Sample testing flow)
1. The Admin user will make use of the AddProduct/UpdateProduct/DeleteProduct APIs to do manipulation on the Products and create a catalogue for our smart hardware shop.
2. Admin or Customer can view the Product catalogue using GetProductList API. The API has both filtering option and pagination support.
3. The Admin user can create new News's to our system with the help of AddNews API.
4. Both Admin or Customer users can view the available newses in our system. This API supports pagination for data manipulation.
5. The Order related APIs can be accessed only by a Customer. For creating an order the customer needs to call CreateOrder API and create an order for them.
6. Once the order is created, the customer can add products to the order by calling AddToOrder API. 
7. Finally the Customer can view the order summary by making use of OrderSummary API.