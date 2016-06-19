package com.github.woyten.lightdao.testing;

import java.util.Optional;

import com.github.woyten.lightdao.column.Column;
import com.github.woyten.lightdao.column.Columns;
import com.github.woyten.lightdao.core.SimpleTable;
import com.github.woyten.lightdao.util.CollectionUtil;

public class MetadataResult extends SimpleTable<MetadataResult> {
	public static final Column<MetadataResult, String> field = Columns.string().nonNull().build("field");
	public static final Column<MetadataResult, String> type = Columns.string().nonNull().build("type");
	public static final Column<MetadataResult, String> null_ = Columns.string().nonNull().build("null");
	public static final Column<MetadataResult, String> key = Columns.string().nonNull().build("key");
	public static final Column<MetadataResult, Optional<String>> default_ = Columns.string().build("default");
	public static final Column<MetadataResult, String> extra = Columns.string().nonNull().build("extra");

	public MetadataResult() {
		super("MySQL 'describe' result", CollectionUtil.newSet(field, type, null_, key, default_, extra));
	}
}
