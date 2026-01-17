/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.jdbc2java;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NameMapperTest {

    @Test
    void mapName_no_changes() {
        assertThat(NameMapper.mapName("a")).isEqualTo("a");
        assertThat(NameMapper.mapName("a1a")).isEqualTo("a1a");
        assertThat(NameMapper.mapName("a_b")).isEqualTo("a_b");
        assertThat(NameMapper.mapName("_a")).isEqualTo("_a");
        assertThat(NameMapper.mapName("b_")).isEqualTo("b_");
        assertThat(NameMapper.mapName("a__b")).isEqualTo("a__b");
    }

    @Test
    void mapName() {
        assertThat(NameMapper.mapName("fooBar")).isEqualTo("foo_Bar");
        assertThat(NameMapper.mapName("fooBAR")).isEqualTo("foo_BAR");
        assertThat(NameMapper.mapName("fooBaR")).isEqualTo("foo_Ba_R");
        assertThat(NameMapper.mapName("fooBAr")).isEqualTo("foo_BAr");
        assertThat(NameMapper.mapName("a__Bb")).isEqualTo("a__Bb");
    }

    @Test
    void mapName_abcd() {
        assertThat(NameMapper.mapName("abcd")).isEqualTo("abcd");
        assertThat(NameMapper.mapName("abcD")).isEqualTo("abc_D");
        assertThat(NameMapper.mapName("abCd")).isEqualTo("ab_Cd");
        assertThat(NameMapper.mapName("abCD")).isEqualTo("ab_CD");
        assertThat(NameMapper.mapName("aBcd")).isEqualTo("a_Bcd");  //
        assertThat(NameMapper.mapName("aBcD")).isEqualTo("a_Bc_D"); //
        assertThat(NameMapper.mapName("aBCd")).isEqualTo("a_BCd");  //
        assertThat(NameMapper.mapName("aBCD")).isEqualTo("a_BCD");  //

        assertThat(NameMapper.mapName("Abcd")).isEqualTo("Abcd");
        assertThat(NameMapper.mapName("AbcD")).isEqualTo("Abc_D");
        assertThat(NameMapper.mapName("AbCd")).isEqualTo("Ab_Cd");
        assertThat(NameMapper.mapName("AbCD")).isEqualTo("Ab_CD");
        assertThat(NameMapper.mapName("ABcd")).isEqualTo("ABcd");  //
        assertThat(NameMapper.mapName("ABcD")).isEqualTo("ABc_D"); //
        assertThat(NameMapper.mapName("ABCd")).isEqualTo("ABCd");  //
        assertThat(NameMapper.mapName("ABCD")).isEqualTo("ABCD");
    }

}