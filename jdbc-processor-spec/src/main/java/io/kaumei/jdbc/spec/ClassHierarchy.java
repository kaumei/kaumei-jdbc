/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec;

/**
 * interface: Level01 -- Level02 -- Level03
 * records..: Level01Record, Level02Record, Level03Record
 * class....: Level01Cls -- Level02Cls -- Level03Cls
 */
public class ClassHierarchy {

    // ------------------------------------------------------------------------

    public interface Level01 {
        String value();
    }

    public interface Level02 extends Level01 {
    }

    public interface Level03 extends Level02 {
    }

    // ------------------------------------------------------------------------

    public record Level01Record(String value) implements Level01 {
    }

    public record Level02Record(String value) implements Level02 {
    }

    public record Level03Record(String value) implements Level03 {
    }

    // ------------------------------------------------------------------------

    public static class Level01Cls implements Level01 {
        private final String value;

        public Level01Cls(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

    }

    public static class Level02Cls extends Level01Cls implements Level02 {
        public Level02Cls(String value) {
            super(value);
        }
    }

    public static class Level03Cls extends Level02Cls implements Level03 {
        public Level03Cls(String value) {
            super(value);
        }
    }

    // ------------------------------------------------------------------------

}
