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

flagBuild=$(hasFlag    "--build"    "$@")
flagSpec=$(hasFlag     "--spec"     "$@")
flagSnapshot=$(hasFlag "--snapshot" "$@")
flagRelease=$(hasFlag  "--release"  "$@")
flagSize=$(hasFlag     "--size"     "$@")

first=("jdbc-parent" "jdbc-annotation" "jdbc-core" "jdbc-processor")
firstStage=$(IFS=,;  printf 'io.kaumei.jdbc:%s,' "${first[@]}")

spec=("jdbc-processor-spec" "jdbc-processor-spec2" "jdbc-processor-spec3")
specStage=$(IFS=,; printf 'io.kaumei.jdbc:%s,' "${spec[@]}")

integration=("jdbc-integration-spring")
integrationStage=$(IFS=,; printf 'io.kaumei.jdbc:%s,' "${integration[@]}")

echo "firstStage......: ${firstStage}"
echo "specStage.......: ${specStage}"
echo "integrationStage: ${integrationStage}"

function mvn_clean_install() {
    mvn clean install --projects ${firstStage}
    mvn clean install --projects ${specStage}
    mvn clean install --projects ${integrationStage}
}

function clean_repository() {
    echo "clean ~/.m2/io/kaumei"
    rm -rf ~/.m2/repository/io/kaumei
}

function check_credentials() {
    if [[ -z ${CENTRAL_USERNAME:-} || -z ${CENTRAL_TOKEN:-} ]]; then
       echo "CENTRAL_USERNAME and CENTRAL_TOKEN must be set"
       exit -1;
    fi
}

if [[ -n "$flagBuild" ]]; then
    mvn_clean_install

elif [[ -n "${flagSpec}" ]]; then
    mvn clean install -DskipTests  --projects ${firstStage}
    mvn clean install              --projects ${specStage}

elif [[ -n "${flagSnapshot}" ]]; then
    check_credentials
    clean_repository
    #mvn_clean_install

    # build first stage with version
    clean_repository
    mvn clean deploy -Ppublish --projects ${firstStage}

elif [[ -n "${flagRelease}" ]]; then
    check_credentials
    clean_repository
    #mvn_clean_install

    # build first stage with version
    clean_repository
    mvn versions:set -DnewVersion=${newVersion} -DgenerateBackupPoms=false --projects ${firstStage}
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

else
    echo "nothing to do, use one of"
    echo " --build ......... build the whole project"
    echo " --spec .......... execute only the spec tests"
    echo " --snapshot ...... publish snapshot"
    echo " --release ....... publish release"
    echo " --size .......... gather stats of project size"
fi
