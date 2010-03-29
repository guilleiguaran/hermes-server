# Hermes Server

Hermes is a minimal path-finding developed in <a href="http://scala-lang.org">Scala</a> and using <a href="http://cassandra.apache.org">Cassandra</a> for storage.

Hermes is the great messenger of the gods in Greek mythology and additionally as a guide to the Underworld. An Olympian god, he is also the patron of boundaries and of the travelers who travel across them.

## Build targets

The primary useful build targets are the usual suspects:

  - `ant clean` -- to clear out previous build results
  - `ant compile` -- only compile, then stop
  - `ant test` -- only compile and run tests, then stop
  - `ant package` -- build and create an executable jar file and distribution tarball

The default target for ant is `package`. You can see a more complete list with `ant -p`.


## Running locally

You can run the server locally by using the executable jar:

    $ java -jar ./dist/hermesd/hermesd-1.0.jar

**Note:** The server need permissions to write logs in /var/log, you can create /var/log/hermesd and add read/write permissions to the user thar runs hermes or run server as root user.

## Authors
 * Juan Carlos Berrio <jcberrio@uninorte.edu.co>
 * Lacides Charris <lacidesc@uninorte.edu.co>
 * Guillermo Iguaran <guillermoi@uninorte.edu.co>
 * Luis Porras <lporras@uninorte.edu.co>

