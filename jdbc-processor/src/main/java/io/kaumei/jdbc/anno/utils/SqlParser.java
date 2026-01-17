/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.utils;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

import static java.util.Objects.requireNonNull;

public class SqlParser {
    public record IntString(int index, String name, int pos) {
    }

    public record Result(String nativeSql,
                         IntString[] index2name,
                         String originalSql) {


    }

    public static Result parse(String sql) {
        var sb = new StringBuilder();
        var loop = new LoopString(sql);
        var array = new ArrayList<IntString>();

        int sqlIndex = 1;
        int lastIndex = 0;
        while (loop.next()) {
            if (loop.currentChar == '?') {

                sb.append(sql, lastIndex, loop.index - 1);
                array.add(new IntString(sqlIndex++, "?", sb.length()));
                lastIndex = loop.index;

            } else if (loop.currentChar == '\'' && loop.lastChar != '\\') {
                while (loop.next()) {
                    if (loop.currentChar == '\'' && loop.lastChar != '\\') {
                        break;
                    }
                }

            } else if (loop.currentChar == '\"' && loop.lastChar != '\\') {
                while (loop.next()) {
                    if (loop.currentChar == '\"' && loop.lastChar != '\\') {
                        break;
                    }
                }

            } else if (loop.currentChar == ':') {
                Character lookAhead;
                lookAhead = loop.lookAhead();
                if (lookAhead != null && Character.isJavaIdentifierStart(lookAhead)) {
                    var colonIndex = loop.index;
                    while (loop.next()) {
                        lookAhead = loop.lookAhead();
                        if (lookAhead == null || !Character.isJavaIdentifierPart(lookAhead)) {
                            break;
                        }
                    }
                    sb.append(sql, lastIndex, colonIndex - 1);
                    var name = sql.substring(colonIndex, loop.index);
                    array.add(new IntString(sqlIndex++, name, sb.length()));
                    lastIndex = loop.index;
                    sb.append('?');
                }
            }
        }

        if (lastIndex < sql.length()) {
            sb.append(sql, lastIndex, sql.length());
        }

        return new Result(sb.toString(), array.toArray(new IntString[0]), sql);
    }

    // ------------------------------------------------------------------------

    /**
     * Loop over a string and provide the current and the last char
     */
    static class LoopString {
        private final String str;
        private final int strLength;
        private int index;
        private char lastChar;
        private char currentChar;

        LoopString(String str) {
            this.str = requireNonNull(str);
            this.strLength = str.length();
        }

        char lastChar() {
            if (index > 1) {
                return lastChar;
            }
            throw new IllegalStateException("Index out of bounds: " + index);
        }

        char currentChar() {
            if (index > 0 && index <= this.strLength) {
                return currentChar;
            }
            throw new IllegalStateException("Index out of bounds: " + index);
        }

        @Nullable
        Character lookAhead() {
            if (this.index < this.strLength) {
                return this.str.charAt(this.index);
            }
            return null;
        }

        boolean next() {
            if (this.index < this.strLength) {
                lastChar = currentChar;
                currentChar = str.charAt(index);
                index++;
                return true;
            } else if (this.index == this.strLength) {
                lastChar = currentChar;
                currentChar = '\u0000';
                index++;
            }
            return false;
        }
    }

}
