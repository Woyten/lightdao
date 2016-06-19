package com.github.woyten.lightdao.sql;

import java.util.function.Supplier;

public interface SqlLogger {
	void log(Supplier<String> messageSupplier);

	default void log(String message) {
		log(() -> message);
	}

	SqlLogger NO_LOGGING = new SqlLogger() {
		@Override
		public void log(Supplier<String> messageSupplier) {
		}
	};
}
