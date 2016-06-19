package com.github.woyten.lightdao.core;

import java.util.Collection;
import java.util.Optional;

import com.github.woyten.lightdao.column.Column;
import com.github.woyten.lightdao.core.ColumnMap.Builder;

public interface Table<SELF extends Table<SELF>> {
	abstract String name();

	abstract Collection<Column<SELF, ?>> columns();

	@SuppressWarnings("unchecked")
	default DtoBuilder<SELF> dtoBuilder() {
		Builder<SELF> builder = ColumnMap.builder();
		for (Column<SELF, ?> column : columns()) {
			Optional<?> defaultValue = column.defaultValue();
			if (defaultValue.isPresent()) {
				builder.putUnsafe(column, column.defaultValue().get());
			}
		}
		return new DtoBuilder<>((SELF) this, builder);
	}
}
