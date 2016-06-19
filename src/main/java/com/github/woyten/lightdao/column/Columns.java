package com.github.woyten.lightdao.column;

import com.github.woyten.lightdao.column.ColumnBuilder.NullableColumnBuilder;

public class Columns {
	public static NullableColumnBuilder<Integer> integer() {
		return ColumnBuilder.start();
	}

	public static NullableColumnBuilder<String> string() {
		return ColumnBuilder.start();
	}

	public static ColumnBuilder<String> nullToEmptyString() {
		return ColumnBuilder.<String> start().transform(ColumnTransformations.nullToEmpty());
	}
}
