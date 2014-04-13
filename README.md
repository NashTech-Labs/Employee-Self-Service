[Employee-Self-Service](http://employee-self-service.herokuapp.com/)
==================================================================
Building Reactive Play application with Anorm SQL data access

This is a classic CRUD application, backed by a JDBC database. It demonstrates:
- Handling asynchronous results, Handling time-outs
- Achieving, Futures to use more idiomatic error handling.
- Accessing a JDBC database, using Anorm.
- Achieving, table pagination and sorting functionality.
- Achieving, embedded JS & CSS libraries with [WebJars](http://www.webjars.org/).
- Play and Scala-based template engine implementation
- Integrating with a CSS framework (Twitter Bootstrap 3.1.1).  Twitter Bootstrap requires a different form layout to the default one that the Play form helper generates, so this application also provides an example of integrating a custom form input constructor.
- Bootswatch-Yeti with Twitter Bootstrap 3.1.1 to improve the look and feel of the application

-----------------------------------------------------------------------
###Instructions :-
-----------------------------------------------------------------------
* The live application is currently hosted at : http://employee-self-service.herokuapp.com/
* The Github code for the project is at : https://github.com/knoldus/Employee-Self-Service
* Clone the project into local system
* Install play  if you do not have it already. You can get it from here: http://www.playframework.com/download
* Execute `play compile` to build the product
* Execute `play run` to execute the product
* Employee-Self-Service should now be accessible at localhost:9000

-----------------------------------------------------------------------
###References :-
-----------------------------------------------------------------------
* Play Framework :- http://www.playframework.com/documentation/2.2.2/ScalaAnorm
* Bootstrap 3.1.1 :- http://getbootstrap.com/css/
* Bootswatch :- http://bootswatch.com/yeti/
* WebJars :- http://www.webjars.org/
