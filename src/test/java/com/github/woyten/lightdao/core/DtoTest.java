package com.github.woyten.lightdao.core;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.github.woyten.lightdao.example.ExampleTable;
import com.github.woyten.lightdao.example.ExampleTable.Level;
import com.github.woyten.lightdao.example.ExampleTable.Status;

public class DtoTest {
	@Test
	public void testEquals() {
		assertThat(createDto("Bernd Stromberg", Status.ACTIVE, 0, Level.MEDIUM))
				.isEqualTo(createDto("Bernd Stromberg", Status.ACTIVE, 0, Level.MEDIUM))
				.isEqualTo(createDto("Bernd Stromberg", Status.INACTIVE, 0, Level.MEDIUM))
				.isEqualTo(createDto("Bernd Stromberg", Status.INACTIVE, 1000, Level.MEDIUM))
				.isEqualTo(createDto("Bernd Stromberg", Status.INACTIVE, 1000, Level.HIGH))
				.isNotEqualTo(createDto("Helge Schneider", Status.ACTIVE, 0, Level.MEDIUM))
				.isNotEqualTo(new Object());
	}

	@Test
	public void testToString() {
		assertThat(createDto("Bernd Stromberg", Status.ACTIVE, 0, Level.MEDIUM).toString())
				.isEqualTo("Dto(example_data): [ level: MEDIUM, player_name: Bernd Stromberg, score: 0 ]");
	}

	private Dto<ExampleTable> createDto(String string, Status status, int score, Level level) {
		return new DtoBuilder<>(new ExampleTable(), ColumnMap.builder())
				.with(ExampleTable.playerName, string)
				.with(ExampleTable.status, status)
				.with(ExampleTable.score, score)
				.with(ExampleTable.level, level)
				.build();
	}
}
