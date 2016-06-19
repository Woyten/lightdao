package com.github.woyten.lightdao.sql;

import static com.github.woyten.lightdao.sql.SqlGenerator.beginWith;
import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import org.fest.assertions.StringAssert;
import org.junit.Test;

public class SqlGeneratorTest {
	@Test
	public void testBasicQueries() {
		verifyQuery(beginWith("FOO").wrapSelect("BAR"), "select BAR from (FOO)");
		verifyQuery(beginWith("FOO").wrapInsert(Arrays.asList("BAR", "BAZ")), "insert into FOO (BAR, BAZ) values (?, ?)");
		verifyQuery(beginWith("FOO").wrapUpdate(Arrays.asList("BAR", "BAZ")), "update FOO set BAR=?, BAZ=?");
		verifyQuery(beginWith("FOO").wrapDelete(), "delete from FOO");
	}

	@Test
	public void testAppendWhere() {
		verifyQuery(beginWith("FOO").wrapSelect("BAR").appendWhere("1=0"), "select BAR from (FOO) where 1=0");
		verifyQuery(beginWith("FOO").wrapSelect("BAR").appendWhere(Arrays.asList()), "select BAR from (FOO) where 1=1");
		verifyQuery(beginWith("FOO").wrapSelect("BAR").appendWhere(Arrays.asList("COL1")), "select BAR from (FOO) where COL1=?");
		verifyQuery(beginWith("FOO").wrapSelect("BAR").appendWhere(Arrays.asList("COL1", "COL2")), "select BAR from (FOO) where COL1=? and COL2=?");
		verifyQuery(beginWith("FOO").wrapDelete().appendWhere("1=0"), "delete from FOO where 1=0");
	}

	private StringAssert verifyQuery(SqlGenerator<?> sqlGenerator, String expectedQuery) {
		return assertThat(sqlGenerator.getQuery()).isEqualTo(expectedQuery);
	}
}
