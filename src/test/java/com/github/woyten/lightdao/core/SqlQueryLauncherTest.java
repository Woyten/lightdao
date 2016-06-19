package com.github.woyten.lightdao.core;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.junit.Test;

import com.github.woyten.lightdao.example.ExampleTable;
import com.github.woyten.lightdao.example.ExampleTable.Level;
import com.github.woyten.lightdao.example.ExampleTable.Status;
import com.github.woyten.lightdao.sql.SqlLogger;
import com.github.woyten.lightdao.testing.TestDataSourceProvider;

public class SqlQueryLauncherTest {
	private static class TestSqlLogger implements SqlLogger {
		private List<String> loggedStatements = new ArrayList<>();

		@Override
		public void log(Supplier<String> messageSupplier) {
			loggedStatements.add(messageSupplier.get());
		}

		public void verifyLine(int lineNumber, String expectedText) {
			assertThat(loggedStatements.get(lineNumber - 1)).isEqualTo(expectedText);
		}
	}

	@Test
	public void testLoggingForExecuteQuery() {
		TestSqlLogger logger = new TestSqlLogger();
		SqlQueryLauncher<ExampleTable> sqlQueryLauncher = new SqlQueryLauncher<>(new ExampleTable(), TestDataSourceProvider.getDataSource(), logger);
		sqlQueryLauncher.executeQuery("select * from example_data", Collections.emptySet());

		logger.verifyLine(1, "executeQuery: select * from example_data");
		logger.verifyLine(2, "Queried rows: 0");

		ColumnMap<ExampleTable> parameters = ColumnMap.<ExampleTable> builder()
				.put(ExampleTable.status, Status.INACTIVE)
				.put(ExampleTable.level, Level.LOW)
				.build();
		sqlQueryLauncher.executeQuery("select * from example_data where player_name=? and level=?", parameters.sortedEntries());

		logger.verifyLine(3, "executeQuery: select * from example_data where player_name=? and level=?");
		logger.verifyLine(4, "Parameters: level=LOW, status=INACTIVE");
		logger.verifyLine(5, "Queried rows: 0");
	}

	@Test
	public void testLoggingForExecuteUpdate() {
		TestSqlLogger logger = new TestSqlLogger();
		SqlQueryLauncher<ExampleTable> sqlQueryLauncher = new SqlQueryLauncher<>(new ExampleTable(), TestDataSourceProvider.getDataSource(), logger);
		sqlQueryLauncher.executeUpdate("delete from example_data", Collections.emptySet());

		logger.verifyLine(1, "executeUpdate: delete from example_data");
		logger.verifyLine(2, "Updated rows: 0");

		ColumnMap<ExampleTable> parameters = ColumnMap.<ExampleTable> builder()
				.put(ExampleTable.status, Status.INACTIVE)
				.put(ExampleTable.level, Level.LOW)
				.build();
		sqlQueryLauncher.executeUpdate("delete from example_data where player_name=? and level=?", parameters.sortedEntries());

		logger.verifyLine(3, "executeUpdate: delete from example_data where player_name=? and level=?");
		logger.verifyLine(4, "Parameters: level=LOW, status=INACTIVE");
		logger.verifyLine(5, "Updated rows: 0");
	}
}
