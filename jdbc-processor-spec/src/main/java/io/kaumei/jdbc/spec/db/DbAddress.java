/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.db;

import org.jspecify.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public record DbAddress(long id, @Nullable String city, @Nullable String street,
                        @Nullable Integer length) {

    public static void init(DataSource ds) {
        try (Connection con = ds.getConnection()) {
            try (Statement stmt = con.createStatement()) {
                stmt.execute("DELETE FROM db_address");
                stmt.execute("INSERT INTO db_address VALUES ( 0, null     ,  null                       , null)");
                stmt.execute("INSERT INTO db_address VALUES ( 1, 'Hamburg', 'Reeperbahn'                ,  930)");
                stmt.execute("INSERT INTO db_address VALUES ( 2, 'Hamburg', 'Mönckebergstraße'          ,  800)");
                stmt.execute("INSERT INTO db_address VALUES ( 3, 'Hamburg', 'Jungfernstieg'             ,  600)");
                stmt.execute("INSERT INTO db_address VALUES (10, 'Berlin' , 'Kurfürstendamm'            , 3500)");
                stmt.execute("INSERT INTO db_address VALUES (11, 'Berlin' , null                        , null)");
                stmt.execute("INSERT INTO db_address VALUES (20, 'London' , null                        , null)");
                stmt.execute("INSERT INTO db_address VALUES (21, 'London' , 'Oxford Street'             , 2000)");
                stmt.execute("INSERT INTO db_address VALUES (30, 'Paris'  , 'Avenue des Champs-Élysées' , 1900)");
                stmt.execute("INSERT INTO db_address VALUES (40, 'Madrid' , null                        , null)");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public final static DbAddress NULL = new DbAddress(0, null, null, null);
    public final static DbAddress HAMBURG_1 = new DbAddress(1, "Hamburg", "Reeperbahn", 930);
    public final static DbAddress HAMBURG_2 = new DbAddress(2, "Hamburg", "Mönckebergstraße", 800);
    public final static DbAddress HAMBURG_3 = new DbAddress(3, "Hamburg", "Jungfernstieg", 600);
    public final static DbAddress BERLIN_1 = new DbAddress(10, "Berlin", "Kurfürstendamm", 3500);
    public final static DbAddress BERLIN_2 = new DbAddress(11, "Berlin", null, null);
    public final static DbAddress LONDON_1 = new DbAddress(20, "London", null, null);
    public final static DbAddress LONDON_2 = new DbAddress(21, "London", "unknown", 0);
    public final static DbAddress PARIS = new DbAddress(30, "Paris", "Avenue des Champs-Élysées", 1900);
    public final static DbAddress MADRID = new DbAddress(40, "Madrid", null, null);
    public final static DbAddress UNKNOWN = new DbAddress(-1, "unknown", "unknown", 0);

    public final static List<Long> ALL_ID_LIST = List.of(0L, 1L, 2L, 3L, 10L, 11L, 20L, 21L, 30L, 40L);
    public final static long[] ALL_ID_ARRAY = new long[]{0L, 1L, 2L, 3L, 10L, 11L, 20L, 21L, 30L, 40L};
    public final static List<DbAddress> ALL_ADDRESS_LIST = List.of(NULL, HAMBURG_1, HAMBURG_2, HAMBURG_3, BERLIN_1, BERLIN_2, LONDON_1, LONDON_2, PARIS, MADRID);

    public final static List<Long> HAMBURG_ID_LIST = List.of(1L, 2L, 3L);
    public final static long[] BERLIN_ID_ARRAY = new long[]{10L, 11L};
}
