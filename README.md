# hateoflux-demos

## Table of Contents

- [Description](#description)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
  - [Clone the Repository](#clone-the-repository)
  - [Run in the IDE](#run-in-the-ide)
  - [Run on Command Line](#run-on-command-line)
- [Usage](#usage)
- [Examples from the Documentation Cookbook](#examples-from-the-documentation-cookbook)
- [Useful Links](#useful-links)
- [License](#license)
- [Contact](#contact)

## Description

`hateoflux-demos` is an example implementation of a Spring WebFlux service using [hateoflux](https://github.com/kamillionlabs/hateoflux). It demonstrates the integration of hateoflux into a WebFlux service, allowing developers to experiment with various options such as creating custom wrappers and assemblers. This project serves as a practical guide for implementing HATEOAS principles in reactive applications.

 
## Prerequisites
* Java 21+
* Gradle 8.5+

## Installation
### Clone the Repository
Clone the repository:
```bash
# Using HTTP
git clone https://github.com/kamillionlabs/hateoflux-demos.git

# Or using SSH 
git clone git@github.com:kamillionlabs/hateoflux-demos.git
```

### Run in the IDE

If you want to run the server in your IDE, simply execute the `main` method in [HateofluxDemosApplication](./src/main/java/de/kamillionlabs/hateofluxdemos/HateofluxDemosApplication.java). No further configuration is required.

### Run on Command Line
To run the application via the command line:
```bash
# Navigate to the project directory
cd hateoflux-demos

# Build the project using Gradle
./gradlew build

# Run the application
./gradlew bootRun

```
## Usage

Once the application is running, you can interact with the API using tools like Postman or cURL.

- **Base URL:** `http://localhost:8080`
- **Example Endpoint:** `/manual/order-no-embedded/1234`

### Example Request

```bash
curl -X GET "http://localhost:8080/manual/order-no-embedded/1234"
```

### Example Response

```JSON
{
  "id": 1234,
  "userId": 37,
  "total": 99.99,
  "status": "Processing",
  "_links": {
    "shipment": {
      "href": "orders/1234/shipment"
    },
    "self": {
      "href": "orders/1234"
    }
  }
}
```
_Feel free to explore other endpoints as defined in the controllers. Unit tests are also a great way to get to know how things work._

## Examples from the Documentation Cookbook

The documentation at https://hateoflux.kamillionlabs.de, particularly the cookbook, provides extensive code examples demonstrating how to create wrappers and assemblers. The code examples are all part of this repository. The following table highlights the key entry points for running, debugging, or reviewing implementations of these examples:

| Class                                                                                                                      | Content                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
|----------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [HateofluxDemosApplication](./src/main/java/de/kamillionlabs/hateofluxdemos/HateofluxDemosApplication.java)                | Class with `main()` method. Can be used to run the server for debugging purposes.                                                                                                                                                                                                                                                                                                                                                                                                                      |
| [ManualOrderController](./src/main/java/de/kamillionlabs/hateofluxdemos/controller/ManualOrderController.java)             | Contains all examples from the [Manually Creating Wrappers](https://hateoflux.kamillionlabs.de/docs/cookbook.html#manually-creating-wrappers) section in the cookbook. It is a controller class that shows how wrapping could be done **manually** at that level. If you run this application, the endpoints can also be used to test hateoflux with Postman, cURL, etc.                                                                                                                               |
| [ManualOrderControllerTest](./src/test/java/de/kamillionlabs/hateofluxdemos/cookbook/ManualOrderControllerTest.java)       | Calls endpoints of [ManualOrderController](./src/main/java/de/kamillionlabs/hateofluxdemos/controller/ManualOrderController.java) and tests against an expected JSON. The unit tests can be used to debug given endpoints.                                                                                                                                                                                                                                                                             |
| [AssembledOrderController](./src/main/java/de/kamillionlabs/hateofluxdemos/controller/AssembledOrderController.java)       | Contains the example from the [Using an Assembler to Create a HalListWrapper For Resources With an Embedded Resource](https://hateoflux.kamillionlabs.de/docs/cookbook.html#using-an-assembler-to-create-a-hallistwrapper-for-resources-with-an-embedded-resource) section in the cookbook. It is a controller class that shows how wrapping could be done **with an assembler** at that level. If you run this application, the endpoints can also be used to test hateoflux with Postman, cURL, etc. |
| [AssembledOrderControllerTest](./src/test/java/de/kamillionlabs/hateofluxdemos/cookbook/AssembledOrderControllerTest.java) | Calls endpoints of [AssembledOrderController](./src/main/java/de/kamillionlabs/hateofluxdemos/controller/AssembledOrderController.java) and tests against an expected JSON. The unit tests can be used to debug given endpoints.                                                                                                                                                                                                                                                                       |
| [FurtherAssemblerTest](./src/test/java/de/kamillionlabs/hateofluxdemos/cookbook/FurtherAssemblerTest.java)                 | Contains all the examples from the [Further Examples Using the Same Assembler](https://hateoflux.kamillionlabs.de/docs/cookbook.html#further-examples-using-the-same-assembler) section in the cookbook. The tests also assert against an expected JSON.                                                                                                                                                                                                                                               |
## Useful Links
* The [hateoflux repository](https://github.com/kamillionlabs/hateoflux)
* The [hateoflux documentation](https://hateoflux.kamillionlabs.de)

## License

This project is licensed under the [GPL-3.0 License](https://github.com/kamillionlabs/hateoflux-demos/blob/master/LICENSE). You are free to use, modify, and distribute this software under the terms of the GNU General Public License v3.0.

## Contact

For any inquiries or support, feel free to start a [discussion](https://github.com/kamillionlabs/hateoflux-demos/discussions) or email us at [contact@kamillionlabs.de](mailto:contact@kamillionlabs.de).
