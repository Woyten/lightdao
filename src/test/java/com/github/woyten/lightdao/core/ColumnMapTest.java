package com.github.woyten.lightdao.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.woyten.lightdao.example.ExampleTable;
import com.github.woyten.lightdao.example.ExampleTable.Level;
import com.github.woyten.lightdao.example.ExampleTable.Status;

public class ColumnMapTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testSingleConstraint() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Constraint violated. Value 'player_name' was '   '");

		new DtoBuilder<>(new ExampleTable(), ColumnMap.builder())
				.with(ExampleTable.playerName, "   ")
				.with(ExampleTable.status, Status.ACTIVE)
				.with(ExampleTable.score, 1000)
				.with(ExampleTable.level, Level.LOW)
				.build();
	}

	@Test
	public void testFirstConditionOfChainedConstraint() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Constraint violated. Value 'score' was '-1'");

		new DtoBuilder<>(new ExampleTable(), ColumnMap.builder())
				.with(ExampleTable.playerName, "Donald Trump")
				.with(ExampleTable.status, Status.ACTIVE)
				.with(ExampleTable.score, -1)
				.with(ExampleTable.level, Level.LOW)
				.build();
	}

	@Test
	public void testSecondConditionOfChainedConstraint() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Constraint violated. Value 'score' was '1000000000'");

		new DtoBuilder<>(new ExampleTable(), ColumnMap.builder())
				.with(ExampleTable.playerName, "Donald Trump")
				.with(ExampleTable.status, Status.ACTIVE)
				.with(ExampleTable.score, 1000000000)
				.with(ExampleTable.level, Level.LOW)
				.build();
	}
}
