# Vert.x 3.0 Example Application

This is an example of how you can create an example application using Vert.x 3.0 and Maven.

As you can see the project is a very standard Maven project with no special magic. In Vert.x 3.0 dependencies are
just simple jars so you can use the standard Maven dependency resolution to pull them in at build time.

This project has a very simple main Verticle called `MyVerticle`.

This verticle deploys an instance of the Vert.x MongoDB service, then interacts with it over the event bus using a proxy
and inserting and finding a value from a collection.

You can run the example in your IDE, or you can build an executable fat jar to run at the command line with:

    mvn package
    java -jar target/example-project-1.0-SNAPSHOT.jar

The fat jar contains all the dependencies required to run the application.

[Note: you need to have an instance of MongoDB running on your machine for this to work]
