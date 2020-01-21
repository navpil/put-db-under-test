# Flyway Java migration sample

Java migrations can be used to run more complicated logic.

These migrations are not checked for a checksum, so those can be changed any time.
This makes sense, because migration jar file can call other jars, situated in lib folder.
And even if migration jar does not change itself, logic can be changed in another lib.

There are several ways of running Java based migrations:

 - Programmatically from code
 - Using maven plugin
 - From CLI
 
## Running from CLI

The simplest way is to create a project with a single dependency to flyway.
It is possible to reference any other jars, for example custom DAO files,
but then all of the needed jars have to be put into the `lib` folder of flyway.

Create a Java based migration (example is `V3__ChangeCarNames.java`),
compile it and package to jar.
Copy the jar file into the `jar` folder of flyway.

Change the `flyway.conf` file to contain the package where the java based migration is.
In our example that would be:
    
    flyway.locations=sql,classpath:io.github.navpil.dbtests.flywayjavamigrations
