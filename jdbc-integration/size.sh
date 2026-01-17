#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2025 kaumei.io
# SPDX-License-Identifier: Apache-2.0
set -eu
script_dir="$(cd "$(dirname "$0")" && pwd)"
root_dir="${script_dir}"


mvn -DskipTests install

#mvn dependency:tree -Dsort -Dscope=runtime > my_tree.txt
#mvn dependency:list -Dsort -DincludeScope=runtime > my_list.txt
 
stat -f '%z %N'  jdbc-spring-jpa/target/*.jar*
stat -f '%z %N'  jdbc-spring-kaumei/target/*.jar*

ls -alh jdbc-spring-jpa/target/*.jar*
ls -alh jdbc-spring-kaumei/target/*.jar*