# Read Me First

* This project is a Spring Boot modular monolith that exposes a REST API for the Springs of MUD game platform.
* The Springs of MUD game platform is a re-imaginging of the Rivers of MUD Multi-User Dungeon.
* The primary consumer of this API is the [springs-of-mud-server](https://github.com/sneibarg/springs-of-mud-server/tree/master) repository.
* The secondary consumer of this API is the [springs-of-mud-designer](https://github.com/sneibarg/springs-of-mud-designer) repository.
* The README for the springs-of-mud-server repository gives more detail about the goals of this game platform.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.0-SNAPSHOT/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.0-SNAPSHOT/maven-plugin/build-image.html)
* [Spring Data MongoDB](https://docs.spring.io/spring-boot/3.4.0-SNAPSHOT/reference/data/nosql.html#data.nosql.mongodb)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.0-SNAPSHOT/reference/web/servlet.html)
* [Spring Modulith](https://docs.spring.io/spring-modulith/reference/)

### Guides
The following guides illustrate how to use some features concretely:

* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

