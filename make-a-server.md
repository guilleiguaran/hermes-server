
# Make Your Own Server

Let's make a new server!

    $ git clone git://github.com/robey/scala-build.git


## Folder layout

Copy the scala-build folder into a new folder, and run `mkproject.sh`.

    $ cp -r scala-build mynewproject
    $ cd mynewproject
    $ ./mkproject.sh

It will ask a few questions and create a folder structure that will build a "hello world" app for
you. The folder structure is worth poking into and familiarizing yourself with. It can seem a little
over-engineered at first, but it's the structure used by maven, which has become a pseudo-standard
in the java world.

Source files are kept in `src/<stage>/<language>`, so initially three folders are created:

    src/main/scala -- for the main code
    src/test/scala -- for tests
    src/scripts -- for startup & maintenance scripts

Most builds use ant (a java build tool) and ivy (an adapter for ant to allow it to access maven
repositories). Some new projects are experimenting with sbt ("scala build tool") but it isn't
fully fleshed out yet.


## Unit tests

We use [scala-specs](http://code.google.com/p/specs/) for unit tests. It's pretty similar to rspec.

A sample test is created in `src/test/scala` and you can run it with `ant test`.


## Build targets

The primary useful build targets are the usual suspects:

  - `ant clean` -- to clear out previous build results
  - `ant compile` -- only compile, then stop
  - `ant test` -- only compile and run tests, then stop
  - `ant package` -- build and create an executable jar file and distribution tarball

The default target for ant is `package`. You can see a more complete list with `ant -p`.


## Running locally

You can run the server locally by using the executable jar:

    $ java -jar ./dist/mynewproject/mynewproject-1.0.jar

The name and version number of the jar come from `ivy/ivy.xml` (the ivy definitions file).


## Deploy

The deployed tarball can be named after the version number (1.0) or the current git revision (8 hex
chars). Since we don't use version numbers often, the git revision is the default. Modify
`dist.build_integration` in `build.xml` if you would rather use the version number.
