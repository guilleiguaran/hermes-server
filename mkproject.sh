
inject_dep() {
  org=$1
  name=$2
  rev=$3
  conf=$4

  cat ivy/ivy.xml | \
    awk "/  <\/dependencies>/ { print \"    <dependency org=\\\"${org}\\\" name=\\\"${name}\\\" rev=\\\"${rev}\\\" conf=\\\"${conf}\\\" /> <!--auto-->\" } { print }" \
        > ivy/ivy2.xml && \
    mv ivy/ivy2.xml ivy/ivy.xml
}

reset_dep() {
  cat ivy/ivy.xml | awk "!/<!--auto-->/ { print }" > ivy/ivy2.xml && mv ivy/ivy2.xml ivy/ivy.xml
}

echo
echo "Let's create a new scala project."
read -p "  package root (like 'com.example'): " -e package_root
read -p "  project name (like 'echod'): " -e project_name
read -p "  description for humans: " -e description
echo "  ----------"
read -p "  using thrift? [n]: " -e use_thrift
read -p "  using jmock? [y]: " -e use_jmock
read -p "  building a server (start/stop script & admin port)? [n]: " -e use_initd

test "x$package_root" = "x" && package_root="com.example"
test "x$project_name" = "x" && project_name="echod"
test "x$description" = "x" && description="sample project"
test "x$use_thrift" = "x" && use_thrift="n"
test "x$use_jmock" = "x" && use_jmock="y"
test "x$use_initd" = "x" && use_initd="n"
package_path=$(echo ${package_root} | sed -e 's/\./\//g')

echo
echo "Creating project ${package_root}.${project_name}"
reset_dep

cat ivy/ivy.xml | \
  sed -e "s/organisation=\".*\"/organisation=\"${package_root}\"/" \
      -e "s/module=\".*\"/module=\"${project_name}\"/" \
      -e "s/e:buildpackage=\".*\"/e:buildpackage=\"${package_root}.${project_name}\"/" \
      -e "s/e:testclass=\".*\"/e:testclass=\"${package_root}.${project_name}.TestRunner\"/" \
      -e "s/e:jarclassname=\".*\"/e:jarclassname=\"${package_root}.${project_name}.Main\"/" \
      -e "s/e:thriftpackage=\".*\"/e:thriftpackage=\"${package_root}.${project_name}.gen\"/" \
      > ivy/ivy2.xml && \
  mv ivy/ivy2.xml ivy/ivy.xml

cat build.xml | \
  sed -e "s/<project name=\".*\" default/<project name=\"${project_name}\" default/" \
      -e "s/<description>.*<\/description>/<description>${description}<\/description>/" \
      -e "s/usr\/local\/example/usr\/local\/${project_name}/" \
      > build2.xml && \
  mv build2.xml build.xml

if test $use_initd = "n"; then
  rm src/scripts/startup.sh
else
  cat src/scripts/startup.sh | \
    sed -e "s/example/${project_name}/" \
    > src/scripts/${project_name}.sh && \
    rm src/scripts/startup.sh
  inject_dep com.twitter ostrich 1.0 "*"
  mkdir -p config
  cat >config/development.conf <<__EOF__
admin_text_port = 9989
admin_http_port = 9990

log {
  level = "info"
  console = false
  filename = "${project_name}.log"
  roll = "never"
}
__EOF__
  cat >config/test.conf <<__EOF__
admin_text_port = 9989
admin_http_port = 9990

log {
  level = "fatal"
  console = true
}
__EOF__
  cat >config/production.conf <<__EOF__
admin_text_port = 9989
admin_http_port = 9990

log {
  filename = "/var/log/${project_name}/production.log"
  level = "info"
  roll = "hourly"
}
__EOF__
fi

mkdir -p src/main/scala/${package_path}/${project_name}
mkdir -p src/test/scala/${package_path}/${project_name}
test $use_thrift = "n" || {
  mkdir -p src/test/thrift
  inject_dep thrift libthrift 751142 "*"
}

test $use_jmock = "n" || {
  inject_dep org.jmock jmock 2.4.0 "test->*"
  inject_dep org.hamcrest hamcrest-all 1.1 "test->*"
  inject_dep cglib cglib 2.1_3 "test->*"
  inject_dep asm asm 1.5.3 "test->*"
  inject_dep org.objenesis objenesis 1.1 "test->*"
}

inject_dep com.twitter xrayspecs 1.0.7 "*"

if test $use_initd = "n"; then
  cat >src/main/scala/${package_path}/${project_name}/Main.scala <<__EOF__
package ${package_root}.${project_name}

object Main {
  def main(args: Array[String]) {
    println("Hello, world!")
  }
}
__EOF__
else
  cat >src/main/scala/${package_path}/${project_name}/Main.scala <<__EOF__
package ${package_root}.${project_name}

import com.twitter.ostrich.{BackgroundProcess, Server, ServerInterface, Stats}
import net.lag.configgy.{Configgy, RuntimeEnvironment}
import net.lag.logging.Logger

object Main extends ServerInterface {
  val log = Logger.get(getClass.getName)

  def main(args: Array[String]) {
    val runtime = new RuntimeEnvironment(getClass)
    runtime.load(args)
    val config = Configgy.config
    Server.startAdmin(this, config, runtime)

    log.info("Starting ${project_name}!")
    BackgroundProcess.spawnDaemon("main") {
      while (true) {
        Thread.sleep(2000)
        Stats.incr("sheep")
      }
    }
  }

  def shutdown() {
    log.info("Shutting down!")
    System.exit(0)
  }

  def quiesce() {
    shutdown()
  }
}
__EOF__
fi

cat >src/test/scala/${package_path}/${project_name}/TestRunner.scala <<__EOF__
package ${package_root}.${project_name}

import com.twitter.xrayspecs.XraySpecsRunner

object TestRunner extends XraySpecsRunner
__EOF__

cat >src/test/scala/${package_path}/${project_name}/SampleSpec.scala << __EOF__
package ${package_root}.${project_name}

import org.specs.Specification
import org.specs.mock.{ClassMocker, JMocker}

object SampleSpec extends Specification with JMocker with ClassMocker {
  "Sample" should {
    "run a test" in {
      23 mustEqual 23
    }
  }
}
__EOF__

mv .git .git-scala-build 2>/dev/null || echo
mkdir -p libs

cat >.gitignore << __EOF__
.git*
target
dist
__EOF__

echo "Done."
echo
