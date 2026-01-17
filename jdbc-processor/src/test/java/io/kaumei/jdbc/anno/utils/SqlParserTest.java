/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SqlParserTest {

    // ------------------------------------------------------------------------
    // SqlParser2.LoopString

    @Test
    void test_LoopString_null() {
        // when ... then
        assertThatThrownBy(() -> new SqlParser.LoopString(null));
    }

    @Test
    void test_LoopString_empty() {
        // when ... then
        var parser = new SqlParser.LoopString("");
        assertThatThrownBy(parser::lastChar);
        assertThatThrownBy(parser::currentChar);
        assertThat(parser.lookAhead()).isNull();
        // when ... then
        assertThat(parser.next()).isEqualTo(false);
        assertThatThrownBy(parser::lastChar);
        assertThatThrownBy(parser::currentChar);
        assertThat(parser.lookAhead()).isNull();
    }

    @Test
    void test_LoopString_one() {
        // when ... then
        var parser = new SqlParser.LoopString("1");
        assertThatThrownBy(parser::lastChar);
        assertThatThrownBy(parser::currentChar);
        assertThat(parser.lookAhead()).isEqualTo('1');
        // when ... then
        assertThat(parser.next()).isEqualTo(true);
        assertThatThrownBy(parser::lastChar);
        assertThat(parser.currentChar()).isEqualTo('1');
        assertThat(parser.lookAhead()).isNull();
        // when ... then
        assertThat(parser.next()).isEqualTo(false);
        assertThat(parser.lastChar()).isEqualTo('1');
        assertThatThrownBy(parser::currentChar);
        assertThat(parser.lookAhead()).isNull();
        // when ... then
        assertThat(parser.next()).isEqualTo(false);
        assertThat(parser.lastChar()).isEqualTo('1');
        assertThatThrownBy(parser::currentChar);
        assertThat(parser.lookAhead()).isNull();
    }

    @Test
    void test_LoopString_two() {
        // when ... then
        var parser = new SqlParser.LoopString("12");
        assertThatThrownBy(parser::lastChar);
        assertThatThrownBy(parser::currentChar);
        assertThat(parser.lookAhead()).isEqualTo('1');
        // when ... then
        assertThat(parser.next()).isEqualTo(true);
        assertThatThrownBy(parser::lastChar);
        assertThat(parser.currentChar()).isEqualTo('1');
        assertThat(parser.lookAhead()).isEqualTo('2');
        // when ... then
        assertThat(parser.next()).isEqualTo(true);
        assertThat(parser.lastChar()).isEqualTo('1');
        assertThat(parser.currentChar()).isEqualTo('2');
        assertThat(parser.lookAhead()).isNull();
        // when ... then
        assertThat(parser.next()).isEqualTo(false);
        assertThat(parser.lastChar()).isEqualTo('2');
        assertThatThrownBy(parser::currentChar);
        assertThat(parser.lookAhead()).isNull();
        // when ... then
        assertThat(parser.next()).isEqualTo(false);
        assertThat(parser.lastChar()).isEqualTo('2');
        assertThatThrownBy(parser::currentChar);
        assertThat(parser.lookAhead()).isNull();
    }

    // ------------------------------------------------------------------------

    @Test
    void test_parse_null() {
        assertThatThrownBy(() -> SqlParser.parse(null));
    }

    @Test
    void test_parse_empty() {
        // when
        var result = SqlParser.parse("");
        // then
        assertThat(result.nativeSql()).isEqualTo("");
        assertThat(result.index2name()).isEmpty();
        assertThat(result.originalSql()).isEqualTo("");
    }

    @Test
    void test_parse_escape() {
        // given
        SqlParser.Result result;

        // when
        result = SqlParser.parse("''");
        // then
        assertThat(result.nativeSql()).isEqualTo("''");
        assertThat(result.index2name()).isEmpty();
        assertThat(result.originalSql()).isEqualTo("''");

        // when
        result = SqlParser.parse("'\\\"'");
        // then
        assertThat(result.nativeSql()).isEqualTo("'\\\"'");
        assertThat(result.index2name()).isEmpty();
        assertThat(result.originalSql()).isEqualTo("'\\\"'");

        // when
        result = SqlParser.parse("'\\':' ");
        // then
        assertThat(result.nativeSql()).isEqualTo("'\\':' ");
        assertThat(result.index2name()).isEmpty();
        assertThat(result.originalSql()).isEqualTo("'\\':' ");
    }

    @Test
    void test_parse_one_parameter() {
        // when
        var result = SqlParser.parse(":one");
        // then
        assertThat(result.nativeSql()).isEqualTo("?");
        assertThat(result.index2name()).containsExactly(
                new SqlParser.IntString(1, "one", 0)
        );
    }

    @Test
    void test_parse_two_parameter_1() {
        // when
        var result = SqlParser.parse(":one :one");
        // then
        assertThat(result.nativeSql()).isEqualTo("? ?");
        assertThat(result.index2name()).containsExactly(
                new SqlParser.IntString(1, "one", 0),
                new SqlParser.IntString(2, "one", 2)
        );
    }

    @Test
    void test_parse_two_parameter_2() {
        // when
        var result = SqlParser.parse(":one :two");
        // then
        assertThat(result.nativeSql()).isEqualTo("? ?");
        assertThat(result.index2name()).containsExactly(
                new SqlParser.IntString(1, "one", 0),
                new SqlParser.IntString(2, "two", 2)
        );
    }

    @Test
    void test_parse_two_parameter_3() {
        // when
        var result = SqlParser.parse(":one:one");
        // then
        assertThat(result.nativeSql()).isEqualTo("??");
        assertThat(result.index2name()).containsExactly(
                new SqlParser.IntString(1, "one", 0),
                new SqlParser.IntString(2, "one", 1)
        );
    }

    @Test
    void test_parse_in_string_1() {
        // when
        var result = SqlParser.parse(":one':two':three");
        // then
        assertThat(result.nativeSql()).isEqualTo("?':two'?");
        assertThat(result.index2name()).containsExactly(
                new SqlParser.IntString(1, "one", 0),
                new SqlParser.IntString(2, "three", 7)
        );
    }

    @Test
    void test_parse_in_string_2() {
        // when
        var result = SqlParser.parse(":one\":two\":three");
        // then
        assertThat(result.nativeSql()).isEqualTo("?\":two\"?");
        assertThat(result.index2name()).containsExactly(
                new SqlParser.IntString(1, "one", 0),
                new SqlParser.IntString(2, "three", 7)
        );
    }

    @Test
    void test_parse_double_colon_1() {
        // when
        var result = SqlParser.parse("::");
        // then
        assertThat(result.nativeSql()).isEqualTo("::");
        assertThat(result.index2name()).isEmpty();
    }

    @Test
    void test_parse_double_colon_2() {
        // when
        var result = SqlParser.parse(":one:");
        // then
        assertThat(result.nativeSql()).isEqualTo("?:");
        assertThat(result.index2name()).containsExactly(
                new SqlParser.IntString(1, "one", 0)
        );
    }

    @Test
    void test_parse_double_colon_3() {
        // when
        var result = SqlParser.parse("::one");
        // then
        assertThat(result.nativeSql()).isEqualTo(":?");
        assertThat(result.index2name()).containsExactly(
                new SqlParser.IntString(1, "one", 1)
        );
    }

}
