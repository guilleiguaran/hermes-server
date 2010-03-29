
Sorry for the sparse/nonexistent documentation.

This is a set of ant tasks to build a scala server or reusable library. To
get started, run "mkproject.sh" and answer a few questions. Afterwards, you
can run "ant" to build your project, and run it with:

    java -jar ./dist/myproject/myproject-1.0.jar

This toolchain is provided under the Apache 2 license:

/*
 * Copyright 2009 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


----------

Folder layout:
  ant/  -- boilerplate ant files
  src/  -- source code
    main/
      scala/
      java/
      jni/
      thrift/
      resources/  -- files to copy into dist
    test/
      scala/
      resources/  -- files needed only for tests
    scripts/
  ivy/
    ivy.xml  -- package description
    ivysettings.xml  -- repositories
  config/  -- items to package in the distribution but not in jars

To configure, search through build.xml and ivy/ivy.xml for the 
string CONFIG and modify accordingly. The Ivy settings are also
documented below.

Created during the build:
  target/  -- compiled files
    classes/
    test-classes/
      resources/  -- copied from src/test/resources/
    gen-java/  -- generated from thrift
    scripts/
  dist/
    <package>-<version>/
      <package>-<version>.jar
      *.so  -- if jni stuff was compiled
      *.jnilib  -- if jni stuff was compiled
      libs/  -- dependent jars (and any extra)
      config/  -- copied over from config/
      resources/  -- from src/main/resources/


Primary targets
---------------
  - clean
    - erase all generated/built files
  - distclean
    - clean *everything*
  - prepare
    - resolve dependencies and download them
  - compile
    - build any java/scala source
  - test
    - build and run test suite (requires e:testclass)
  - stress
    - (optional) run stress test suite (requires e:stresstestclass)
  - docs
    - generate documentation
  - package
    - copy all generated/built files into distribution folder
      (requires "local" repo)
  - push
    - copy all generated/built files into a final repository
      (requires "push" repo)


Properties that can change behavior
-----------------------------------

- skip.download
    if defined, don't download ivy; assume it's present
- skip.test
    if defined, don't run test suite
- skip.docs
    if defined, don't build docs
- libs.extra
    any extra files to copy into dist/<p>/libs/ during compile
- dist.extra
    any extra files to copy into dist/<p> during compile
- config.extra
    any extra files to copy into config/ during compile
- pack.deps
    if defined, pack dependent jars into the final dist jar, to remove dependencies
- thrift.bin
    location of the "thrift" executable
- push.build_name
    if defined, add the build name (and git revision) to jars when pushed to the repo
- dist.build_integration
    if defined, add the git revision to the name of the final distribution zip file, and
    don't use the version number in paths


Extra ivy thingies
------------------

- e:buildpackage
    causes a build.properties file to be created in the named package
    (stores version #, etc -- used by RuntimeEnvironment in configgy)
- e:thriftpackage
    output package for generated thrift classes; causes thrift DDLs to be
    compiled
- e:testclass
    class to execute for unit tests -- required, in order to run tests
- e:stresstestclass
    class to execute for stress tests (optional)
- e:checktestclass
    class to execute for scalacheck tests (optional)
- e:jarclassname
    creates an executable jar with this as the main class name


JNI
---

JNI will be built if there appears to be a `build.xml` file in src/main/jni/.
That ant file should contain a "clean" target and a "compile" target.

Post-compile, the jni/ folder is expected to look like this:
  src/
    main/
      jni/
        build.xml  -- used to build the jni packages
        <package>/
          target/
            *.so  -- copied into dist/<p>/
            *.jnilib  -- copied into dist/<p>/
            *.jar  -- copied into dist/<p>/

There may be as many <package> folders in jni/ as you desire.

