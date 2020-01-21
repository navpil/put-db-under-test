# Db Tests description

## Basic steps to perform db test

1. Create DB
2. DDL the DB
3. Populate Data
4. Query the DB

All the 4 parts can be used independently and combined in whatever manner.

### Create DB

Can be done in various ways, for example:

1. Manually create DB with a predefined name which is then used by tests
2. Create DB with a JDBC query before running tests, for example:

        DriverManager.getConnection("jdbc:sqlserver://localhost;", "username", "password")
            .createStatement()
            .executeUpdate("CREATE DATABASE carrental");

3. Use [Testcontainers](https://www.testcontainers.org/), requires [Docker](https://www.docker.com/)
4. Use in-memory Database, but be aware that if it's a different engine, DB test results may be irrelevant.

### DDL the DB

Required to setup data with correct tables.
Use one of the migration tools defined below.

### Populate Data

By far the best tool for the job is [DbUnit](http://dbunit.sourceforge.net/)

Data can be populated by using of the tools for accessing the DB, described below.

### Query the data

Data can be queried by using of the tools for accessing the DB, described below.

## Migration tools

Usually migration tools fit into one of the categories:

 - Changeset based tools - flyway, mybatis, liquibase all fit into this category.
 - Diff based tools - these are not mentioned below, but they exist. 
 They take the current DB state, compare it with desired DB state and generate the diff-script.
 - Code first tools - Hibernate built in migrations fall into this category. 

### Flyway

Comes with JRE and lots of drivers. 
But if those are removed, then the installation is very lightweight.
By default requires you to use the installation directory as the working directory - the workdir concept is not cleanly defined.

Can pass `-configFiles=conf\flyway.conf` as a parameter.
Configure flyway.conf to contain flyway.url and flyway.locations

Has clear programming interface.

Has Enterprise version which supports old databases and more features (for instance `undo`)

### Mybatis Migrations

Very lightweight. 
Separates installation and working directories by running `migrate init`
Is in no way tied to `MyBatis` framework.

Does not have clearly defined programming interface and is primarily a CLI tool.
But programmatic interaction is still possible.

Is absolutely free, no enterprise version.

Supports both .sql migrations and .java migrations, but they can't be used together out of the box.

### Liquibase

Not too lightweight - 22 Megs.

Is a pretty large tool. 
It allows (among other things): 

 - Revert
 - Database comparison, 
 - Xml migrations, which are transformed to SQL queries, 
 - SQL migrations
 
Has clear programming interface.
 
Has enterprise version with some advanced features (XML based Stored procedures) and support.

### Built-in Hibernate migrations

    hibernate.hbm2ddl.auto=create-drop
    
Not really a safe option, but can be used for testing.

However if production uses other DDL scripts, database for testing might not be the same as production.

### Other options

Basic DB migration tool is a pretty simple thing to write, so some teams actually write the tool themselves.
Because it's so simple to write the tool yourself, there are many of them and each language has a preferred tool.
Sometimes there are special migration tools per database.
In a multilanguage project other tools can be used and it's not a problem.

Some non-java tools worth mentioning:
 
 - [DBMate](https://github.com/amacneil/dbmate) 
 - [Active record](https://guides.rubyonrails.org/v5.2/active_record_migrations.html)
 - [Django migrations](https://docs.djangoproject.com/en/2.2/topics/migrations/)

## Access DB

Nice link about technologies mentioned below:

https://www.marcobehler.com/guides/a-guide-to-accessing-databases-in-java

All the methods have corresponding Dao class and are called from `TestDataAccess`.

Most of the methods require some resource handling, which is not always done in the example code.

In case developers have already created the DAO layer, you may wish to reuse it instead of rewriting the DAO logic yourself.

### Plain JDBC

Is a great, but very low level tool.
Allows to access all Databases through the same interface, if it has the JDBC driver (and most SQL Databases have).
All other tools build on top of JDBC.

    JdbcDao
    
### Spring JdbcTemplate

Removes JDBC boilerplate code and simplifies working with JDBC.
It also picks up Connection Pools if those are available.
You don't have to be inside Spring Container to use JdbcTemplate

    JdbcTemplateDao
    
### Hibernate

Extremely popular ORM (object-relational mapping) framework, which maps Java classes to SQL tables.
Can be used in two ways - as pure Hibernate or as a JPA implementation.

User can access Database through: 
 
 - Build in functions, such as find()   
 - HQL (or JPQL)
 - Criteria API
 - Native queries 

  
    HibernateNativeDao
    HibernateJpaDao

Requires annotated `CarEntity.java`
If entities contain collections it is advised to transform them into other classes during opened session.
Good library for doing that is `MapStruct`.

### JPA

JPA stands for Java Persistence API.
This is a standard Java ORM specification, which is implemented in JavaEE application servers and standalone libraries,
such as Hibernate or EclipseLink.

Example class is the same as the second one from Hibernate section.
 
    HibernateJpaDao

Requires `persistence.xml` file in META-INF.
Requires annotated `CarEntity.java`. 

### MyBatis

Tool which stands something in between the ORM and plain JDBC.
Useful for people who tend to use a lot of native queries, but want JPA-like functionality, like Mapping.

    MyBatisDao
    
Requires `CarMapper.java` and `CarMapper.xml` in `resources` 
    
### QueryDSL

Project which is currently dead, but maybe will be revived in the future.
Similar to Criteria API but its interface is more fluent.

    QueryDSLDao
    
Builds `QCarEntity` using maven tasks.
Can build them from `JPA`, `JDO` or `SQL` by connecting to the Database.

### SpringData

Provides basic CRUD queries out of the box and creates queries based on interface method names.

I could not find a simple setup of SpringData outside the spring context, so no examples available.
Therefore I'm not sure it is really good for testing.
