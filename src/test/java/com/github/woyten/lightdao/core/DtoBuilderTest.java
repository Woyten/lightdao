package com.github.woyten.lightdao.core;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.woyten.lightdao.example.ExampleTable;
import com.github.woyten.lightdao.example.ExampleTable.Level;
import com.github.woyten.lightdao.example.ExampleTable.Status;

public class DtoBuilderTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testBuild() {
		Dto<ExampleTable> dto = new DtoBuilder<>(new ExampleTable(), ColumnMap.builder())
				.with(ExampleTable.playerName, "Bernd Stromberg")
				.with(ExampleTable.status, Status.ACTIVE)
				.with(ExampleTable.score, 1000)
				.with(ExampleTable.level, Level.MEDIUM)
				.build();

		assertThat(dto.get(ExampleTable.playerName)).isEqualTo("Bernd Stromberg");
		assertThat(dto.get(ExampleTable.status)).isSameAs(Status.ACTIVE);
		assertThat(dto.get(ExampleTable.score)).isEqualTo(1000);
		assertThat(dto.get(ExampleTable.level)).isSameAs(Level.MEDIUM);
	}

	@Test
	public void testBuildWithMissingEntries() {
		Dto<ExampleTable> dto = new DtoBuilder<>(new ExampleTable(), ColumnMap.builder())
				.with(ExampleTable.playerName, "Bernd Stromberg")
				.with(ExampleTable.status, Status.ACTIVE)
				.build();

		assertThat(dto.get(ExampleTable.playerName)).isEqualTo("Bernd Stromberg");
		assertThat(dto.get(ExampleTable.status)).isEqualTo(Status.ACTIVE);
	}

	@Test
	public void testQueryForMissingEntries() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Value 'status' was null");

		new DtoBuilder<>(new ExampleTable(), ColumnMap.builder())
				.with(ExampleTable.playerName, "Bernd Stromberg")
				.build()
				.get(ExampleTable.status);
	}

	@Test
	public void testBuildWithMissingMandatoryEntries() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Value 'player_name' was null");

		new DtoBuilder<>(new ExampleTable(), ColumnMap.builder())
				.with(ExampleTable.status, Status.ACTIVE)
				.with(ExampleTable.score, 1000)
				.with(ExampleTable.level, Level.LOW)
				.build();
	}

	@Test
	public void testBuilderFromCopy() {
		Dto<ExampleTable> dto = new DtoBuilder<>(new ExampleTable(), ColumnMap.builder())
				.with(ExampleTable.playerName, "Bernd Stromberg")
				.with(ExampleTable.status, Status.ACTIVE)
				.with(ExampleTable.score, 1000)
				.with(ExampleTable.level, Level.MEDIUM)
				.build();

		Dto<ExampleTable> copiedDto = dto.builderFromCopy()
				.with(ExampleTable.status, Status.INACTIVE)
				.with(ExampleTable.score, 1500)
				.build();

		assertThat(dto.get(ExampleTable.playerName)).isEqualTo("Bernd Stromberg");
		assertThat(dto.get(ExampleTable.status)).isSameAs(Status.ACTIVE);
		assertThat(dto.get(ExampleTable.score)).isEqualTo(1000);
		assertThat(dto.get(ExampleTable.level)).isSameAs(Level.MEDIUM);
		assertThat(copiedDto.get(ExampleTable.playerName)).isEqualTo("Bernd Stromberg");
		assertThat(copiedDto.get(ExampleTable.status)).isSameAs(Status.INACTIVE);
		assertThat(copiedDto.get(ExampleTable.score)).isEqualTo(1500);
		assertThat(copiedDto.get(ExampleTable.level)).isSameAs(Level.MEDIUM);
	}
}
