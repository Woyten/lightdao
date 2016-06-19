package com.github.woyten.lightdao.column;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Predicate;

public class ColumnBuilder<TYPE> {
	private final ColumnTransformation<Object, TYPE> transformation;
	private final Predicate<TYPE> constraints;
	private final boolean hasDefaultValueFromDb;
	private final Optional<TYPE> defaultValue;
	private final boolean useForEquals;
	private final boolean useForToString;

	public ColumnBuilder(ColumnTransformation<Object, TYPE> transformation, Predicate<TYPE> constraint,
			boolean hasDefaultVauleFromDb, Optional<TYPE> defaultValue, boolean useForEquals, boolean useForToString) {
		this.transformation = transformation;
		this.constraints = constraint;
		this.hasDefaultValueFromDb = hasDefaultVauleFromDb;
		this.defaultValue = defaultValue;
		this.useForEquals = useForEquals;
		this.useForToString = useForToString;

	}

	public static <TYPE> NullableColumnBuilder<TYPE> start() {
		return new NullableColumnBuilder<>();
	}

	public <NEW_TYPE> ColumnBuilder<NEW_TYPE> transform(ColumnTransformation<TYPE, NEW_TYPE> transformation) {
		return new ColumnBuilder<>(this.transformation.chain(transformation), value -> constraints.test(transformation.applyWriteTransform(value)),
				hasDefaultValueFromDb, defaultValue.map(transformation::applyReadTransform), useForEquals, useForToString);
	}

	public ColumnBuilder<TYPE> withDefaultValueFromDb() {
		return new ColumnBuilder<>(transformation, constraints, true, defaultValue, useForEquals, useForToString);
	}

	public ColumnBuilder<TYPE> withDefaultValue(TYPE defaultValue) {
		return new ColumnBuilder<>(transformation, constraints, hasDefaultValueFromDb, Optional.of(defaultValue), useForEquals, useForToString);
	}

	public ColumnBuilder<TYPE> withConstraint(Predicate<TYPE> constraint) {
		return new ColumnBuilder<>(transformation, this.constraints.and(constraint), hasDefaultValueFromDb, defaultValue, useForEquals, useForToString);
	}

	public ColumnBuilder<TYPE> doNotUseForEquals() {
		return new ColumnBuilder<>(transformation, constraints, hasDefaultValueFromDb, defaultValue, false, useForToString);
	}

	public ColumnBuilder<TYPE> doNotUseForToString() {
		return new ColumnBuilder<>(transformation, constraints, hasDefaultValueFromDb, defaultValue, useForEquals, false);
	}

	public <TABLE> Column<TABLE, TYPE> build(String name) {
		return new Column<TABLE, TYPE>() {
			@Override
			public String name() {
				return name;
			}

			@Override
			public boolean mandatory() {
				return !hasDefaultValueFromDb;
			}

			@Override
			public Optional<TYPE> defaultValue() {
				return defaultValue;
			}

			@Override
			public boolean checkConstraints(TYPE value) {
				return constraints.test(value);
			}

			@Override
			public TYPE sqlToJava(ResultSet resultSet) throws SQLException {
				return transformation.applyReadTransform(resultSet.getObject(name));
			}

			@Override
			public void javaToSsql(PreparedStatement statement, int parameterIndex, TYPE parameter) throws SQLException {
				statement.setObject(parameterIndex, transformation.applyWriteTransform(parameter));
			}

			@Override
			public boolean useForEquals() {
				return useForEquals;
			}

			@Override
			public boolean useForToString() {
				return useForToString;
			}
		};
	}

	public static class NullableColumnBuilder<TYPE> extends ColumnBuilder<Optional<TYPE>> {
		private NullableColumnBuilder() {
			super(new ColumnTransformation<Object, Optional<TYPE>>() {
				@SuppressWarnings("unchecked")
				@Override
				public Optional<TYPE> applyReadTransform(Object originalValue) {
					return Optional.ofNullable((TYPE) originalValue);
				}

				@Override
				public Object applyWriteTransform(Optional<TYPE> originalValue) {
					return originalValue.orElse(null);
				}
			}, x -> true, false, Optional.empty(), true, true);
		}

		public ColumnBuilder<TYPE> nonNull() {
			return transform(ColumnTransformations.nonNull());
		}
	}
}
