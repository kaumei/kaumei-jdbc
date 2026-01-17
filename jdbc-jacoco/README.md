<!--
  SPDX-FileCopyrightText: 2025 kaumei.io
  SPDX-License-Identifier: Apache-2.0
-->

# JDBC kaumei jacoco tool

A tool to produce Jacoco coverage reports with the following enhancements:

* add filter to not reports uncovered toString methods
* add filter line with special comments, like
  * the string must be always at the end of a line
  * filter lines between `jacoco:off` and `jacoco:on`
  * filter lines between `sanity-check:on` and `sanity-check:off`
  * line which end with `jacoco:no`, or `sanity-check`