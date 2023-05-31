# Db Tests description

## Basic steps to perform db test

1. Create DB
2. DDL the DB
3. Populate Data
4. Query the DB

All the 4 parts can be used independently and combined in whatever manner.

### Create DB

Can be done in various ways, for example:

1. Manually create DB with a predefined name which is then used by tests.
Don't forget to clean it before running tests (dropping tables is a good idea)
2. Create DB with a JDBC query before running tests, for example:

        DriverManager.getConnection("jdbc:sqlserver://localhost;", "username", "password")
            .createStatement()
            .executeUpdate("CREATE DATABASE carrental");

3. Use [Testcontainers](https://www.testcontainers.org/), requires [Docker](https://www.docker.com/)
4. Use in-memory Database, but be aware that if it's a different engine, DB test results may be irrelevant.
5. Backup from some source.
This will contain schema and data, so use it only when you really need it to be this way.
For example for performance tests.

### DDL the DB

Required to setup data with correct tables.
Use one of the migration tools defined below.

### Populate Data

By far the best tool for the job is [DbUnit](http://dbunit.sourceforge.net/)

The non-obvious method of deleting all data from Database is:

    DatabaseOperation.DELETE_ALL.execute(conn, conn.createDataSet());

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

    FlywaySetupSchema

### Mybatis Migrations

Very lightweight. 
Separates installation and working directories by running `migrate init`
Is in no way tied to `MyBatis` framework.

Does not have clearly defined programming interface and is primarily a CLI tool.
But programmatic interaction is still possible.

Is absolutely free, no enterprise version.

Supports both .sql migrations and .java migrations, but they can't be used together out of the box.

    MyBatisSetupSchema

### Liquibase

Not too lightweight - 22 Megs.

Is a pretty large tool. 
It allows (among other things): 

 - Database comparison (`diff`) - tool for checking differences in DB schemas
 - Generate changeset to recreate given DB (`generateChangeLog`)
 - Generate changeset out of database differences (`diffChangeLog`)
 - Generate database documentation (`dbDoc`)
 - Revert
 - Xml migrations, which are transformed to SQL queries 
 - SQL migrations (specially formatted for Liquibase, I do not recommend using plain SQL)
 
Has clear programming interface.
 
Has enterprise version with some advanced features (XML based Stored procedures) and support.

It's easy to put liquibase in a strange state when there is no discipline in dev team.
For example changing the order of changesets in a changelog has no effect on validating.
Or accidentally changing the name:id tag in a changeset does not result in an error.  

Plain SQL migrations are supported, but then rollback will not work for them (and for all previous changesets as well).
After such migrations was applied it is quite difficult to make rollbacks to work again.

    LiquibaseSetupSchema

### Built-in Hibernate migrations

    hibernate.hbm2ddl.auto=update (validate | update | create | create-drop )
    
Not really a safe option, but can be used for testing.

However if production uses other DDL scripts, database for testing might not be the same as production.

Moreover as described in `hibernatemigrations` this does not always work for production. 

### Other options

Basic DB migration tool is a pretty simple thing to write, so some teams actually write the tool themselves.
Because it's so simple to write the tool yourself, there are many of them and each language has a preferred tool.
Sometimes there are special migration tools per database.
In a multilanguage project other tools can be used and it's not a problem.

Some non-java tools worth mentioning:
 
 - [DBMate](https://github.com/amacneil/dbmate) 
 - [Active record](https://guides.rubyonrails.org/v5.2/active_record_migrations.html)
 - [Django migrations](https://docs.djangoproject.com/en/2.2/topics/migrations/)

One can also use custom SQL scripts, though it's not advisable.

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

### jOOQ

Similar to QueryDSL, but generates classes from database (not from JPA entities as for QueryDSL).

    JooqDao

### SpringData

Provides basic CRUD queries out of the box and creates queries based on interface method names.

I could not find a simple setup of SpringData outside the spring context, so no examples available.
Therefore I'm not sure it is really good for testing.

## Test classes description

`services/CarServiceTest` shows why it's dangerous to rely on database mocking.
Test passes on mocks, but fails on a real DB.

`DataAccessTest` shows various ways of accessing data (step 4).

`ImportExportDataWithDbUnitTest` shows examples of how to use DBUnit to work with data.

`PrepareDatabaseTest` shows various ways how to prepare database for testing,
meaning it will show first 2 steps - DB creation and DDL.

`SqlServerDbTest` shows the usage of test containers (requires Docker)

## Other modules description

`dbmigrationtools` shows examples how to use various db migration tools from command line.
You might need to change the `setenv.cmd` to point to correct paths or to rewrite it for *nix system.

`dbunit` creates a DBUnit helper - a cmd utility to backup and restore DB data using DBUnit.

`flywayjavamigrations` shows and describes in a separate `Readme.md` how to make Java migrations in flyway.

`hibernatemigrations` shows problems which may occur when using the `auto-ddl` feature in Hibernate.

## Other notes

### Reasoning

Why did I write it in the first place?
First two results from google on "selenium db testing" gave this:

 - https://www.browserstack.com/guide/database-testing-using-selenium
 - https://www.guru99.com/database-testing-using-selenium-step-by-step-guide.html

### NoSQL

Test some older format data.

## Mistakes

 - Not testing DB
 - Not using same engine for test and prod
 - No data preparation

https://dzone.com/articles/basic-mistakes-database
