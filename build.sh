#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2025 kaumei.io
# SPDX-License-Identifier: Apache-2.0
set -eu
script_dir="$(cd "$(dirname "$0")" && pwd)"
root_dir="${script_dir}"

# return empty if not found, otherwise echo the flag value
hasFlag() {
    local flag="$1"
    shift

    for arg in "$@"; do
        if [[ "$arg" == "$flag" ]]; then
            echo "$flag"
        fi
    done
    echo ""   # nothing found
}

flagBuild=$(hasFlag       "--build"       "$@")
flagJacoco=$(hasFlag      "--jacoco"      "$@")
flagRelease=$(hasFlag     "--release"     "$@")
flagSnapshot=$(hasFlag    "--snapshot"    "$@")
flagTestCentral=$(hasFlag "--testCentral" "$@")
flagSize=$(hasFlag        "--size"        "$@")
flagSpec=$(hasFlag        "--spec"        "$@")
flagTest=$(hasFlag        "--test"        "$@")

first=("jdbc-parent" "jdbc-annotation" "jdbc-core" "jdbc-processor")
firstStage=$(IFS=,;  printf 'io.kaumei.jdbc:%s,' "${first[@]}")

spec=("jdbc-processor-spec" "jdbc-processor-spec2" "jdbc-processor-spec3")
specStage=$(IFS=,; printf 'io.kaumei.jdbc:%s,' "${spec[@]}")

integration=("jdbc-parent" "jdbc-spring" "jdbc-spring-service" "jdbc-spring-jpa" "jdbc-spring-kaumei")
integrationStage=$(IFS=,; printf 'io.kaumei.jdbc.integration:%s,' "${integration[@]}")

echo "firstStage......: ${firstStage}"
echo "specStage.......: ${specStage}"
echo "integrationStage: ${integrationStage}"


if [[ -n "$flagBuild" ]]; then
    echo "clean ~/.m2/io/kaumei"
    rm -rf ~/.m2/repository/io/kaumei
    mvn clean install --projects ${firstStage}
    mvn clean install --projects ${specStage}
    mvn clean install --projects ${integrationStage}

elif [[ -n "${flagTestCentral}" ]]; then
    rm -rf ~/.m2/repository/io/kaumei
    mvn test -X -Pcentral --update-snapshots --projects ${specStage},${integrationStage}

elif [[ -n "${flagSnapshot}" ]]; then
    echo "clean ~/.m2/repository/io/kaumei" 

    rm -rf ~/.m2/repository/io/kaumei
    mvn clean install
    echo "clean ~/.m2/repository/io/kaumei" 
    rm -rf ~/.m2/repository/io/kaumei
    mvn clean deploy -Ppublish --projects ${firstStage}

elif [[ -n "${flagRelease}" ]]; then
    echo "clean ~/.m2/repository/io/kaumei" 
    rm -rf ~/.m2/repository/io/kaumei

    mvn versions:set -DnewVersion=${newVersion} -DgenerateBackupPoms=false
    # build all with version
    mvn clean install
    echo "clean ~/.m2/repository/io/kaumei" 
    rm -rf ~/.m2/repository/io/kaumei
    mvn clean deploy -Ppublish --projects ${firstStage}

elif [[ -n "${flagSize}" ]]; then
    mvn clean install -DskipTests --projects ${firstStage},${specStage}
    annoLines=$(find ${root_dir}/jdbc-annotation/src/main -name '*.java' -exec cat {} + | wc -l) 
    annoSize=$(du -h ${root_dir}/jdbc-annotation/target/*SNAPSHOT.jar | awk '{print $1}')
    coreLines=$(find ${root_dir}/jdbc-core/src/main -name '*.java' -exec cat {} + | wc -l) 
    coreSize=$(du -h ${root_dir}/jdbc-core/target/*SNAPSHOT.jar | awk '{print $1}')
    procLines=$(find ${root_dir}/jdbc-processor/src/main  -name '*.java' -exec cat {} + | wc -l) 
    procSize=$(du -h ${root_dir}/jdbc-processor/target/*SNAPSHOT.jar | awk '{print $1}')
    specLines=$(find ${root_dir}/jdbc-processor-spec/src  -name '*.java' -exec cat {} + | wc -l) 
    genLines=$(find  ${root_dir}/jdbc-processor-spec/target/generated-sources  -name '*.java' -exec cat {} + | wc -l) 
    specSize=$(du -h ${root_dir}/jdbc-processor-spec/target/*SNAPSHOT.jar | awk '{print $1}')
    dateStr=$(date -I)
    echo "|            | annotation | core        | processor     | spec              |"
    echo "| ---------- | ---------- | ----------- | --------------| ----------------- |"
    echo "| $dateStr |  $(($annoLines)) ($annoSize) |  $(($coreLines)) ($coreSize) |   $(($procLines)) ($procSize) | $(($specLines))/$(($genLines)) ($specSize) |"

elif [[ -n "${flagSpec}" ]]; then
    mvn clean install -DskipTests  --projects ${firstStage}
    mvn clean install              --projects ${specStage}

elif [[ -n "${flagTest}" ]]; then
    mvn       install -DskipTests --projects jdbc-processor
    #mvn clean install -DskipTests --projects ${specStage}
    mvn clean install  --projects jdbc-processor-spec,jdbc-processor-spec2,jdbc-processor-spec3

elif [[ -n "${flagJacoco}" ]]; then
    # create coverage files
    rm -f  "${root_dir}/*/target/*.exec"
    mvn clean install -Pjacoco --projects ${firstStage}
    mvn clean test    -Pjacoco --projects ${specStage}
    # prepare report
    html="${root_dir}/my_jacoco_html"
    rm -rf "${html}"
    mkdir "${html}"
    # create coverage report
    mvn --projects jdbc-jacoco clean package exec:java -DskipTests \
        -Dexec.mainClass=io.kaumei.jdbc.jacoco.ReportMain \
        -Dexec.args="${root_dir}"

else
    echo "nothing to do, use one of"
    echo " --build ......... build the whole project"
    echo " --snapshot ...... publish snapshot"
    echo " --release ....... publish release"
    echo " --size .......... gather stats of project size"
    echo " --testCentral ... execute tests with central dependencies"
    echo " --jacoco ........ gather coverage"
    echo " --spec .......... execute only the spec tests"
fi
