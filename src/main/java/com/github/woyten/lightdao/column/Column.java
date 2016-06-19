package com.github.woyten.lightdao.column;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * @param <TABLE>
 *            For type-safety only
 */
public interface Column<TABLE, TYPE> {
	String name();

	default boolean mandatory() {
		return true;
	}

	default Optional<TYPE> defaultValue() {
		return Optional.empty();
	}

	default boolean checkConstraints(@SuppressWarnings("unused") TYPE value) {
		return true;
	}

	TYPE sqlToJava(ResultSet resultSet) throws SQLException;

	void javaToSsql(PreparedStatement statement, int parameterIndex, TYPE parameter) throws SQLException;

	default boolean useForEquals() {
		return true;
	}

	default boolean useForToString() {
		return true;
	}
}
