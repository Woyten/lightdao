package com.github.woyten.lightdao.core;

import com.github.woyten.lightdao.column.Column;

public class DtoBuilder<TABLE extends Table<TABLE>> {
	private final TABLE table;
	private final ColumnMap.Builder<TABLE> builder;

	DtoBuilder(TABLE table, ColumnMap.Builder<TABLE> builder) {
		this.table = table;
		this.builder = builder;
	}

	public <TYPE> DtoBuilder<TABLE> with(Column<TABLE, TYPE> column, TYPE value) {
		builder.put(column, value);
		return this;
	}

	public Dto<TABLE> build() {
		return new Dto<>(table, builder.build());
	}
}
