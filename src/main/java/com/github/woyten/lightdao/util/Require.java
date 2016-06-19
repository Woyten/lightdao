package com.github.woyten.lightdao.util;

import java.util.Optional;

public class Require {
	public static <T> T nonNull(T value) {
		if (value == null) {
			throw new IllegalArgumentException("Value was null");
		}
		return value;
	}

	public static <T> T nonNull(T value, String name) {
		if (value == null) {
			throw new IllegalArgumentException("Value '" + name + "' was null");
		}
		return value;
	}

	public static <T> T present(Optional<T> value) {
		if (!value.isPresent()) {
			throw new IllegalArgumentException("Value was absent");
		}
		return value.get();
	}

	@SuppressWarnings("unchecked")
	public static <T> T type(Class<T> clazz, Object object) {
		if (!clazz.isInstance(object)) {
			throw new IllegalArgumentException("Wrong type. Expected " + clazz.getSimpleName() + " but was " + object.getClass().getSimpleName());
		}
		return (T) object;
	}
}
