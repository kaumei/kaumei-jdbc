# Kaumei JDBC

Kaumei JDBC is a proof‑of‑concept that explores how far we can push annotation processing to generate JDBC boilerplate. 
Development currently lives on the `develop` branch, and we publish the docs regularly at https://kaumei-jdbc.kaumei.io.

The project is under active development — APIs may change, and feedback is welcome. Initial targets:
* SQL `SELECT` and `UPDATE` support
* Mapping Java objects and records
* Returning Java `List`, `Stream`, and scalar values
* Mapping between SQL and Java types in both directions
* Support for batch processing
* Support to retrieve generated values back from SQL into Java

Out of scope for now (native SQL is the workaround):
* Multi-module support
* Calling SQL procedures
* Loading SQL statements from the classpath
* JPA annotations
* Executing SQL scripts
* Lightweight Kaumei transaction helper
* Targeting multiple databases from the same codebase