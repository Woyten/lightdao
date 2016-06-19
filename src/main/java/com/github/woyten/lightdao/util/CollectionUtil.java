package com.github.woyten.lightdao.util;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface CollectionUtil {
	@SafeVarargs
	public static <T> Set<T> newSet(T... values) {
		return Stream.of(values).collect(Collectors.toSet());
	}
}
