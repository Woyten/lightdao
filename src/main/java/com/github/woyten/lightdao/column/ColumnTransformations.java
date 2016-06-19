package com.github.woyten.lightdao.column;

import java.util.Optional;

import com.github.woyten.lightdao.util.Require;

public interface ColumnTransformations {
	public static <TYPE> ColumnTransformation<Optional<TYPE>, TYPE> nonNull() {
		return new ColumnTransformation<Optional<TYPE>, TYPE>() {
			@Override
			public TYPE applyReadTransform(Optional<TYPE> originalValue) {
				return Require.nonNull(originalValue.orElse(null));
			}

			@Override
			public Optional<TYPE> applyWriteTransform(TYPE originalValue) {
				return Optional.of(Require.nonNull(originalValue));
			}
		};
	}

	public static ColumnTransformation<Optional<String>, String> nullToEmpty() {
		return new ColumnTransformation<Optional<String>, String>() {
			@Override
			public String applyReadTransform(Optional<String> originalValue) {
				return originalValue.orElse("");
			}

			@Override
			public Optional<String> applyWriteTransform(String originalValue) {
				return Optional.of(Require.nonNull(originalValue));
			}
		};
	}

	public static <T extends Enum<T>> ColumnTransformation<String, T> enumByName(Class<T> clazz) {
		return new ColumnTransformation<String, T>() {
			@Override
			public T applyReadTransform(String originalValue) {
				return Enum.valueOf(clazz, originalValue);
			}

			@Override
			public String applyWriteTransform(T originalValue) {
				return originalValue.name();
			}
		};
	}

	public static <T extends Enum<T>> ColumnTransformation<Integer, T> enumByIndex(Class<T> clazz) {
		return new ColumnTransformation<Integer, T>() {
			@Override
			public T applyReadTransform(Integer originalValue) {
				return clazz.getEnumConstants()[originalValue];
			}

			@Override
			public Integer applyWriteTransform(T originalValue) {
				return originalValue.ordinal();
			}
		};
	}
}
