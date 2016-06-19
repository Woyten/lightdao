package com.github.woyten.lightdao.example;

import com.github.woyten.lightdao.column.Column;
import com.github.woyten.lightdao.column.ColumnTransformations;
import com.github.woyten.lightdao.column.Columns;
import com.github.woyten.lightdao.core.SimpleTable;
import com.github.woyten.lightdao.util.CollectionUtil;

public class ExampleTable extends SimpleTable<ExampleTable> {
	public enum Status {
		ACTIVE, SUSPENDED, INACTIVE
	}

	public enum Level {
		LOW, MEDIUM, HIGH
	}

	public static final Column<ExampleTable, String> playerName = Columns
			.string()
			.nonNull()
			.withConstraint(playerName -> !playerName.trim().isEmpty())
			.build("player_name");

	public static final Column<ExampleTable, Status> status = Columns
			.string()
			.nonNull()
			.transform(ColumnTransformations.enumByName(Status.class))
			.withDefaultValue(Status.INACTIVE)
			.doNotUseForEquals()
			.doNotUseForToString()
			.build("status");

	public static final Column<ExampleTable, Integer> score = Columns
			.integer()
			.nonNull()
			.withDefaultValueFromDb()
			.withConstraint(score -> score >= 0)
			.withConstraint(score -> score < 1000000000)
			.doNotUseForEquals()
			.build("score");

	public static final Column<ExampleTable, Level> level = Columns
			.integer()
			.nonNull()
			.withDefaultValueFromDb()
			.transform(ColumnTransformations.enumByIndex(Level.class))
			.doNotUseForEquals()
			.build("level");

	public ExampleTable() {
		super("example_data", CollectionUtil.newSet(playerName, status, score, level));
	}
}
