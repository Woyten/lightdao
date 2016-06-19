package com.github.woyten.lightdao.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.github.woyten.lightdao.column.Column;
import com.github.woyten.lightdao.sql.SqlLogger;

public class SqlQueryLauncher<TABLE extends Table<TABLE>> {
	private final TABLE table;
	private final DataSource dataSource;
	private final SqlLogger logger;

	public SqlQueryLauncher(TABLE table, DataSource dataSource, SqlLogger logger) {
		this.table = table;
		this.dataSource = dataSource;
		this.logger = logger;
	}

	public List<Dto<TABLE>> executeQuery(String query, Collection<Entry<Column<TABLE, ?>, Object>> entries) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			appendParameters(entries, statement);
			logger.log(() -> "executeQuery: " + query);
			if (!entries.isEmpty()) {
				logger.log(() -> "Parameters: " + printParameters(entries));
			}
			try (ResultSet resultSet = statement.executeQuery()) {
				List<Dto<TABLE>> result = new ArrayList<>();
				while (resultSet.next()) {
					ColumnMap.Builder<TABLE> builder = ColumnMap.builder();
					for (Column<TABLE, ?> column : table.columns()) {
						builder.putUnsafe(column, column.sqlToJava(resultSet));
					}
					result.add(new Dto<>(table, builder.build()));
				}
				logger.log(() -> "Queried rows: " + result.size());
				return Collections.unmodifiableList(result);
			}
		} catch (SQLException e) {
			throw new DaoException("SQL query failed: '" + query + "'", e);
		}
	}

	public int executeUpdate(String query, Collection<Entry<Column<TABLE, ?>, Object>> entries) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			appendParameters(entries, statement);
			logger.log(() -> "executeUpdate: " + query);
			if (!entries.isEmpty()) {
				logger.log(() -> "Parameters: " + printParameters(entries));
			}
			int result = statement.executeUpdate();
			logger.log(() -> "Updated rows: " + result);
			return result;
		} catch (SQLException e) {
			throw new DaoException("SQL update failed: '" + query + "'", e);
		}
	}

	private String printParameters(Collection<Entry<Column<TABLE, ?>, Object>> entries) {
		return entries.stream().map(entry -> entry.getKey().name() + "=" + entry.getValue()).collect(Collectors.joining(", "));
	}

	@SuppressWarnings("unchecked")
	private void appendParameters(Iterable<Entry<Column<TABLE, ?>, Object>> entries, PreparedStatement statement) throws SQLException {
		int index = 1;
		for (Entry<Column<TABLE, ?>, Object> entry : entries) {
			((Column<TABLE, Object>) entry.getKey()).javaToSsql(statement, index, entry.getValue());
			index++;
		}
	}
}
