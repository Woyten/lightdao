package com.github.woyten.lightdao.example;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.github.woyten.lightdao.core.Dto;
import com.github.woyten.lightdao.core.SqlQueryLauncher;
import com.github.woyten.lightdao.sql.SqlLogger;
import com.github.woyten.lightdao.testing.MetadataResult;
import com.github.woyten.lightdao.testing.TestDataSourceProvider;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class ExampleTableMetadataTest {
	@Test
	public void verifyCorrectTableStructure() {
		MysqlDataSource dataSource = TestDataSourceProvider.getDataSource();
		MetadataResult metadataResult = new MetadataResult();
		SqlQueryLauncher<MetadataResult> queryLauncher = new SqlQueryLauncher<>(metadataResult, dataSource, SqlLogger.NO_LOGGING);
		List<Dto<MetadataResult>> tableMetadata = queryLauncher.executeQuery("describe example_data", Collections.emptySet());

		Dto<MetadataResult> expectedPlayerNameMetadata = metadataResult.dtoBuilder()
				.with(MetadataResult.field, "player_name")
				.with(MetadataResult.type, "varchar(20)")
				.with(MetadataResult.null_, "NO")
				.with(MetadataResult.key, "")
				.with(MetadataResult.default_, Optional.empty())
				.with(MetadataResult.extra, "")
				.build();

		Dto<MetadataResult> expectedStatusMetadata = expectedPlayerNameMetadata.builderFromCopy()
				.with(MetadataResult.field, "status")
				.build();

		Dto<MetadataResult> expectedScoreMetadata = metadataResult.dtoBuilder()
				.with(MetadataResult.field, "score")
				.with(MetadataResult.type, "int(11)")
				.with(MetadataResult.null_, "NO")
				.with(MetadataResult.key, "")
				.with(MetadataResult.default_, Optional.of("0"))
				.with(MetadataResult.extra, "")
				.build();

		Dto<MetadataResult> expectedLevelMetadata = expectedScoreMetadata.builderFromCopy()
				.with(MetadataResult.field, "level")
				.build();

		assertThat(tableMetadata).containsOnly(expectedPlayerNameMetadata, expectedStatusMetadata, expectedScoreMetadata, expectedLevelMetadata);
	}
}
