package com.github.woyten.lightdao.core;

import java.util.Collection;

import com.github.woyten.lightdao.column.Column;

public class SimpleTable<SELF extends SimpleTable<SELF>> implements Table<SELF> {
	private final String name;
	private Collection<Column<SELF, ?>> columns;

	public SimpleTable(String name, Collection<Column<SELF, ?>> columns) {
		this.name = name;
		this.columns = columns;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Collection<Column<SELF, ?>> columns() {
		return columns;
	}
}
