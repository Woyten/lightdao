package com.github.woyten.lightdao.core;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.github.woyten.lightdao.column.Column;
import com.github.woyten.lightdao.util.Require;

public class ColumnMap<TABLE> {
	private final SortedMap<Column<TABLE, ?>, Object> values;

	/** For internal use only. Provided map must be type-consistent */
	private ColumnMap(SortedMap<Column<TABLE, ?>, Object> values) {
		this.values = values;
	}

	public static <TABLE> ColumnMap<TABLE> empty() {
		return ColumnMap.<TABLE> builder().build();
	}

	public Object get(Column<TABLE, ?> key) {
		return Require.nonNull(values.get(key), key.name());
	}

	public Set<Entry<Column<TABLE, ?>, Object>> sortedEntries() {
		return values.entrySet();
	}

	@Override
	public int hashCode() {
		return sortedEntries()
				.stream()
				.filter(entry -> entry.getKey().useForEquals())
				.collect(Collectors.toList())
				.hashCode();
	}

	@Override
	public boolean equals(Object otherObject) {
		if (!(otherObject instanceof ColumnMap<?>)) {
			return false;
		}
		ColumnMap<?> otherDto = (ColumnMap<?>) otherObject;

		List<?> relevantThisValues = sortedEntries()
				.stream()
				.filter(entry -> entry.getKey().useForEquals())
				.collect(Collectors.toList());

		List<?> relevantOtherValues = otherDto.sortedEntries()
				.stream()
				.filter(entry -> entry.getKey().useForEquals())
				.collect(Collectors.toList());

		return relevantThisValues.equals(relevantOtherValues);
	}

	@Override
	public String toString() {
		String valuesAsString = sortedEntries()
				.stream()
				.filter(entry -> entry.getKey().useForToString())
				.map(entry -> entry.getKey().name() + ": " + entry.getValue())
				.collect(Collectors.joining(", "));
		return "[ " + valuesAsString + " ]";
	}

	public static <TABLE> Builder<TABLE> builder() {
		return new Builder<>();
	}

	public static class Builder<TABLE> {
		private Builder() {
		}

		protected final SortedMap<Column<TABLE, ?>, Object> values = new TreeMap<>(Comparator.comparing(Column::name));

		public <TYPE> Builder<TABLE> put(Column<TABLE, TYPE> column, TYPE value) {
			if (!column.checkConstraints(value)) {
				throw new IllegalArgumentException("Constraint violated. Value '" + column.name() + "' was '" + value + "'");
			}
			values.put(column, Require.nonNull(value, column.name()));
			return this;
		}

		/** For internal use only. Provided value must be of correct type */
		@SuppressWarnings("unchecked")
		Builder<TABLE> putUnsafe(Column<TABLE, ?> column, Object value) {
			return put((Column<TABLE, Object>) column, Require.nonNull(value, column.name()));
		}

		public ColumnMap<TABLE> build() {
			return new ColumnMap<>(values);
		}
	}
}
