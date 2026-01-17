<!--
  SPDX-FileCopyrightText: 2025 kaumei.io
  SPDX-License-Identifier: Apache-2.0
-->

# Annotation process

JdbcGetter-Pool:

* search a row/column getter by Return-Type and @JdbcGetter.value

## Stage one

* process `@JdbcGetter`
  * at Java class/interface/record
    * collect all `public` `static` methods which implement `@JdbcGetter.Row` or `@JdbcGetter.Column`
      * skip all other methods (possible some infos in the log)
    * if type is `record` or type is `class` or type contains at least one `@JdbcSelect`
      * add getter to local (type) JdbcGetterPool
      * every type has its one local JdbcGetterPool
    * else
      * add getter to global JdbcGetterPool
  * at `ElementType.METHOD`
    * check if method implement  `@JdbcGetter.Row` or `@JdbcGetter.Column`
    * if not, print warning else add to JdbcGetterPool
      * the type of the method is the Java type to which the method belongs
    * if type is `record` or type is `class` or type contains at least one `@JdbcSelect`
      * add getter to local (type) JdbcGetterPool
    * else
      * add getter to global JdbcGetterPool

  * process `@JdbcGetter` at `ElementType.PARAMETER` and `ElementType.RECORD_COMPONENT`
    * skip

* process `@JdbcType`
  * at `record`
    * create `@JdbcGetter.Row` implementation with `@JdbcType.value` and `records` as return type
    * if `record` belongs to a type
      * add local
    * else
      * add global
  * at `class`
  * at `enum`
  * else skip (is not supported)

* domain values


* after all `@JdbcGetter` have been process:
  * check if we have duplicates in global JdbcGetterPools
  * duplicate: same return type and same @JdbcGetter.value

Setup:    3615084 3678708 3325459
Time:    35372626 34299084 33489750
Setup:  4991250 4767000 5043750
Time:   34265376 34394916 34009709

Setup:  19164375 24477750 16237750
Time: 562.216.417 589.819.249 569.669.292
Setup:  22265250 26852833 19591167
Time: 564.324.209 567.135.583 573.105.999

3.445
[INFO] TimeNs:    34299084

example: PT0.026119208S

example-big: PT0.569459708S

META-INF/services/javax.annotation.processing.Processor
META-INF/services/javax.annotation.processing.Processor