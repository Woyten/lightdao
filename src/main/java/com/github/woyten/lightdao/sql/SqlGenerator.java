package com.github.woyten.lightdao.sql;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SqlGenerator<SELF extends SqlGenerator<SELF>> {
	private String query = "";

	protected SqlGenerator(String initalQuery) {
		this.query = initalQuery;
	}

	public static <SELF extends SqlGenerator<SELF>> SqlGenerator<SELF> beginWith(String initialQuery) {
		return new SqlGenerator<>(initialQuery);
	}

	@SuppressWarnings("unchecked")
	public SELF self() {
		return (SELF) this;
	}

	public SELF wrapSelect(String selector) {
		query = "select " + selector + " from (" + query + ")";
		return self();
	}

	public SELF wrapInsert(List<String> columns) {
		String columNames = columns
				.stream()
				.collect(Collectors.joining(", "));
		String placeholders = Collections
				.nCopies(columns.size(), "?")
				.stream()
				.collect(Collectors.joining(", "));
		query = "insert into " + query + " (" + columNames + ") values (" + placeholders + ")";
		return self();
	}

	public SELF wrapUpdate(List<String> columns) {
		String assignments = columns
				.stream()
				.map(column -> column + "=?")
				.collect(Collectors.joining(", "));
		query = "update " + query + " set " + assignments;
		return self();
	}

	public SELF wrapDelete() {
		query = "delete from " + query;
		return self();
	}

	public SELF appendWhere(String whereClause) {
		query = query + " where " + whereClause;
		return self();
	}

	public SELF appendWhere(List<String> columns) {
		if (columns.isEmpty()) {
			return appendWhere("1=1");
		}
		String whereClause = columns
				.stream()
				.map(column -> column + "=?")
				.collect(Collectors.joining(" and "));
		return appendWhere(whereClause);
	}

	public String getQuery() {
		return query;
	}
}
