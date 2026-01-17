#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2025 kaumei.io
# SPDX-License-Identifier: Apache-2.0
set -eu
script_dir="$(cd "$(dirname "$0")" && pwd)"
root_dir="${script_dir}/.."

podman container rm postgres || true
podman run --name postgres \
    -e POSTGRES_PASSWORD=postgres \
    -v ${root_dir}/jdbc-processor-spec/src/test/resources/postgresql_create_db.sql:/docker-entrypoint-initdb.d/postgresql_create_db.sql \
    -p 5432:5432 \
    -d postgres:alpine
