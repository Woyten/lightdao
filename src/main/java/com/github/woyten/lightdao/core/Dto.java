package com.github.woyten.lightdao.core;

import java.io.Serializable;
import java.util.Map.Entry;

import com.github.woyten.lightdao.column.Column;

public class Dto<TABLE extends Table<TABLE>> implements Serializable {
	private static final long serialVersionUID = 1L;

	private final ColumnMap<TABLE> values;
	private final TABLE table;

	/** For internal use only. Provided map must be type-safe */
	Dto(TABLE table, ColumnMap<TABLE> values) {
		for (Column<TABLE, ?> column : table.columns()) {
			if (column.mandatory()) {
				values.get(column);
			}
		}
		this.values = values;
		this.table = table;
	}

	@SuppressWarnings("unchecked")
	public <TYPE> TYPE get(Column<TABLE, TYPE> key) {
		return (TYPE) values.get(key);
	}

	public ColumnMap<TABLE> values() {
		return values;
	}

	public DtoBuilder<TABLE> builderFromCopy() {
		ColumnMap.Builder<TABLE> builder = ColumnMap.builder();
		for (Entry<Column<TABLE, ?>, Object> entry : values.sortedEntries()) {
			builder.putUnsafe((entry.getKey()), entry.getValue());
		}
		return new DtoBuilder<>(table, builder);
	}

	@Override
	public int hashCode() {
		return values.hashCode();
	}

	@Override
	public boolean equals(Object otherObject) {
		return otherObject instanceof Dto<?> && values.equals(((Dto<?>) otherObject).values);
	}

	@Override
	public String toString() {
		return "Dto(" + table.name() + "): " + values.toString();
	}
}