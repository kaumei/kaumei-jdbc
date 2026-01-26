<!--
SPDX-FileCopyrightText: 2025 kaumei.io
SPDX-License-Identifier: Apache-2.0
-->
# **Kaumei&nbsp;JDBC**

**Kaumei&nbsp;JDBC** is a lightweight Java library that gives you the control of
hand-written SQL with better safety, ergonomics, and observability than heavier
frameworks.
It keeps SQL front and centre, while compiling repetitive JDBC boilerplate for
you.
This library is intended for Java developers who write SQL directly and want
improved safety and ergonomics without heavy abstractions.

**Core principles:**
* SQL-first workflows with compile-time validation
* Minimal runtime complexity and no reflection
* Clear ownership of connections, statements, and result mapping

---

## Why this project exists

Writing JDBC code often means repeating the same mechanical steps:
opening resources, binding parameters, mapping result sets, and handling errors.
Those parts are rarely the core logic of an application, but they dominate the
code.

Heavier frameworks reduce boilerplate, but often do so by hiding SQL, introducing
implicit behaviour, or adding runtime complexity that makes debugging and
observability harder.

**Kaumei JDBC** is an experiment in a different direction:
keeping SQL explicit, while structuring and generating the repetitive JDBC work
around it.

---

## A very small example

```SQL
CREATE TABLE db_customers (
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    budge   INTEGER,
    created TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);
```

```java
record CustomerAsRecord(long id,
                        @JdbcName("name") String firstName,
                        @org.jspecify.annotations.Nullable Integer budge,
                        LocalDateTime created) {
}

interface WithKaumeiJDBC {
    @JdbcSelect("SELECT * FROM db_customers ORDER BY name")
    List<CustomerAsRecord> listCustomers();
}
```

Here, the SQL remains explicit and reviewable, while the surrounding JDBC
plumbing is compiled rather than handwritten.
The goal is not to replace SQL, but to make SQL-centric code safer and more
consistent.

In contrast the same code handwritten:
```java
class WithoutKaumeiJDBC {
    private final JdbcConnectionProvider supplier;
    public WithoutKaumeiJDBC(JdbcConnectionProvider supplier) {
        this.supplier = requireNonNull(supplier,"supplier");
    }
    public List<CustomerAsRecord> listCustomers() {
        try {
            var con = db.getConnection();
            var sql = "SELECT * FROM db_customers ORDER BY name";
            try (var stmt = con.prepareStatement(sql)) {
                try (var rs = stmt.executeQuery()) {
                    var list = new ArrayList<CustomerAsRecord>();
                    while (rs.next()) {
                        var id = rs.getLong("id");
                        if (rs.wasNull()) {
                            throw new NullPointerException("JDBC column was null on column id");
                        }
                        var name = requireNonNull(rs.getString("name"));
                        var budgeInt = rs.getInt("budge");
                        var budge = rs.wasNull() ? null : budgeInt;
                        var created = rs.getTimestamp("created").toLocalDateTime();
                        var row = new CustomerAsRecord(id, name, budge, created);
                        list.add(row);
                    }
                    return Collections.unmodifiableList(list);
                }
            }
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }
}
```

---

## Where to go next
The documentation explains the concepts, design goals, limitations.

<!--
* Getting started with the [Installation guide](https://kaumei-jdbc.kaumei.io/installation/)
* Introduction the [Concept & Story](https://kaumei-jdbc.kaumei.io/intro/introduction/)
* [Motivation & Alternatives](https://kaumei-jdbc.kaumei.io/intro/why-kaumei/)
-->

* Getting started with the [Installation guide](https://kaumei.github.io/kaumei-jdbc/installation/)
* Introduction the [Concept & Story](https://kaumei.github.io/kaumei-jdbc/intro/introduction/)
* [Motivation & Alternatives](https://kaumei.github.io/kaumei-jdbc/intro/why-kaumei/)

Find the latest development documentation [here](https://kaumei.github.io/kaumei-jdbc/).

---

## Project status / Requirements

**Kaumei JDBC** is under active development.
APIs may change.
Feedback, design discussion, and critical review are welcome.
The project targets currently JDK 25.
Earlier or later Java releases are not supported at the moment.

---

## Contributing and communication

**Kaumei&nbsp;JDBC** is a technically focused, maintainer-driven project.

Development is kept clear and workable by using different channels 
for different purposes:
* [Issues and bug reports](https://github.com/kaumei/kaumei-jdbc/issues)
  For concrete, actionable work items such as bugs that have been confirmed,
  features that are clearly defined and technical tasks.
* [Repository discussions](https://github.com/kaumei/kaumei-jdbc/discussions)
  This is the place to go for ideas, design proposals, API questions
  and general discussion.

Please read [CONTRIBUTING.md](./CONTRIBUTING.md) before opening an
issue or pull request.

All participants are expected to follow the 
[Code of Conduct](./CODE_OF_CONDUCT.md).

---

## License and Generated Code
This project is licensed under the Apache License, Version 2.0.

Code generated by this annotation processor is **not** subject
to the Apache License 2.0 and may be used, modified, and
distributed without restriction.

<!--
This README is the public gateway to the project.
Its purpose is to explain what **Kaumei JDBC** is, why it exists, and where to go next.
Detailed concepts, motivation, and usage belong to the documentation website.
If content becomes too long, too conceptual, or too tutorial-like, it should be moved there.
-->