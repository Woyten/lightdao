package com.github.woyten.lightdao.core;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import com.github.woyten.lightdao.column.Column;
import com.github.woyten.lightdao.sql.SqlGenerator;
import com.github.woyten.lightdao.sql.SqlLogger;

public class Dao<TABLE extends Table<TABLE>> {
	private final TABLE table;
	private final SqlQueryLauncher<TABLE> queryLauncher;

	public Dao(TABLE table, DataSource dataSource, SqlLogger logger) {
		this.table = table;
		this.queryLauncher = new SqlQueryLauncher<>(table, dataSource, logger);
	}

	protected SqlGenerator<?> newSqlGenerator() {
		return SqlGenerator.beginWith(table.name());
	}

	public List<Dto<TABLE>> selectAll() {
		return selectWhere(ColumnMap.empty());
	}

	public List<Dto<TABLE>> selectWhere(ColumnMap<TABLE> whereMap) {
		Set<Entry<Column<TABLE, ?>, Object>> entries = whereMap.sortedEntries();
		String query = newSqlGenerator()
				.wrapSelect("*")
				.appendWhere(entries.stream().map(entry -> entry.getKey().name()).collect(Collectors.toList()))
				.getQuery();
		return queryLauncher.executeQuery(query, entries);
	}

	public int insert(Dto<TABLE> dto) {
		Set<Entry<Column<TABLE, ?>, Object>> entries = dto.values().sortedEntries();
		String query = newSqlGenerator()
				.wrapInsert(entries.stream().map(entry -> entry.getKey().name()).collect(Collectors.toList()))
				.getQuery();
		return queryLauncher.executeUpdate(query, entries);
	}

	public int updateAll(ColumnMap<TABLE> columnsToUpdate) {
		return updateWhere(columnsToUpdate, ColumnMap.empty());
	}

	public int updateWhere(ColumnMap<TABLE> columnsToUpdate, ColumnMap<TABLE> whereMap) {
		Set<Entry<Column<TABLE, ?>, Object>> updateEntries = columnsToUpdate.sortedEntries();
		Set<Entry<Column<TABLE, ?>, Object>> whereEntries = whereMap.sortedEntries();

		String query = newSqlGenerator()
				.wrapUpdate(updateEntries.stream().map(entry -> entry.getKey().name()).collect(Collectors.toList()))
				.appendWhere(whereEntries.stream().map(entry -> entry.getKey().name()).collect(Collectors.toList()))
				.getQuery();

		List<Entry<Column<TABLE, ?>, Object>> concat = Stream.concat(updateEntries.stream(), whereEntries.stream()).collect(Collectors.toList());
		return queryLauncher.executeUpdate(query, concat);
	}

	public int deleteAll() {
		return deleteWhere(ColumnMap.empty());
	}

	public int deleteWhere(ColumnMap<TABLE> whereMap) {
		Set<Entry<Column<TABLE, ?>, Object>> whereEntries = whereMap.sortedEntries();
		String query = newSqlGenerator()
				.wrapDelete()
				.appendWhere(whereMap.sortedEntries().stream().map(Entry -> Entry.getKey().name()).collect(Collectors.toList()))
				.getQuery();
		return queryLauncher.executeUpdate(query, whereEntries);
	}

	public DtoBuilder<TABLE> dtoBuilder() {
		return table.dtoBuilder();
	}
}
