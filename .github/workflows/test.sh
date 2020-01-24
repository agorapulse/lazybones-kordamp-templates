#!/usr/bin/env bash

function test_mfg() {
    lazybones create micronaut-function-groovy 1.1.0-SNAPSHOT mfg-test \
      -Pname=MfgTest \
      -Pstandalone=yes \
      -Pslug=agorapulse/mfg-test \
      -Pgroup=com.agorapulse \
      -Ppkg=com.agorapulse.mfg.test \
      -Pregion=eu-west-1 \
      -Pprofile=beta \
      -Pport=8080 \
      -Pinput=$1 \
      -Poutput=$2 \
      -Plibs=all

  cd mfg-test
  ./gradlew test

  cd ..
}

set -e

curl -s "https://get.sdkman.io" | bash
source "/home/runner/.sdkman/bin/sdkman-init.sh"

sdk install lazybones

./gradlew installAllTemplates

cd /tmp

# test kordamp-groovy-test template
lazybones create kordamp-groovy 1.1.0-SNAPSHOT kordamp-groovy-test \
 -Pid=kordamp-groovy-test \
 -Porg=agorapulse \
 -Pgroup=com.agorapulse\
 -Ppkg=com.agorapulse.kordamp.groovy.test\
 -Pname="Kordamp Groovy"\
 -Pdesc="Testing"\
 -Pvendor="Agorapulse"\
 -Pdev.name="Vladimir Orany"\
 -Pdev.id=musketyr\
 -Pbintray.org=agorapulse\
 -Pbintray.repo=libs\

cd kordamp-groovy-test
./gradlew test

# test kordamp-groovy-test template
test_mfg req, resp
test_mfg map, string
test_mfg ap, ap