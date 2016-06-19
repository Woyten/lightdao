package com.github.woyten.lightdao.column;

public interface ColumnTransformation<TYPE, NEW_TYPE> {
	NEW_TYPE applyReadTransform(TYPE originalValue);

	TYPE applyWriteTransform(NEW_TYPE originalValue);

	default <NEWER_TYPE> ColumnTransformation<TYPE, NEWER_TYPE> chain(ColumnTransformation<NEW_TYPE, NEWER_TYPE> chainedTransformation) {
		return new ColumnTransformation<TYPE, NEWER_TYPE>() {
			@Override
			public NEWER_TYPE applyReadTransform(TYPE originalValue) {
				return chainedTransformation.applyReadTransform(ColumnTransformation.this.applyReadTransform(originalValue));
			}

			@Override
			public TYPE applyWriteTransform(NEWER_TYPE originalValue) {
				return ColumnTransformation.this.applyWriteTransform(chainedTransformation.applyWriteTransform(originalValue));
			}
		};
	}
}
