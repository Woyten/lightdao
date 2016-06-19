package com.github.woyten.lightdao.core;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.woyten.lightdao.example.ExampleTable;
import com.github.woyten.lightdao.example.ExampleTable.Level;
import com.github.woyten.lightdao.example.ExampleTable.Status;
import com.github.woyten.lightdao.example.ExampleTableDao;
import com.github.woyten.lightdao.testing.TestDataSourceProvider;

public class DaoTest {
	private ExampleTableDao dao = new ExampleTableDao(TestDataSourceProvider.getDataSource());

	@Before
	public void setUp() {
		dao.deleteAll();
	}

	@Test
	public void testBasicSqlQueries() {
		Dto<ExampleTable> dto = createDto("Dieter Bohlen", Status.ACTIVE, 2000, Level.MEDIUM);
		Dto<ExampleTable> dtoAfterUpdate = createDto("Dieter Bohlen", Status.ACTIVE, 3000, Level.HIGH);

		assertThat(dao.insert(dto)).isEqualTo(1);

		List<Dto<ExampleTable>> queryResult = dao.selectAll();
		assertThat(queryResult).hasSize(1);
		assertThat(queryResult.get(0).get(ExampleTable.playerName)).isEqualTo("Dieter Bohlen");
		assertThat(queryResult.get(0).get(ExampleTable.status)).isEqualTo(Status.ACTIVE);
		assertThat(queryResult.get(0).get(ExampleTable.score)).isEqualTo(2000);
		assertThat(queryResult.get(0).get(ExampleTable.level)).isEqualTo(Level.MEDIUM);

		assertThat(dao.updateAll(dtoAfterUpdate.values())).isEqualTo(1);

		List<Dto<ExampleTable>> queryResultAfterUpdate = dao.selectAll();
		assertThat(queryResultAfterUpdate).hasSize(1);
		assertThat(queryResultAfterUpdate.get(0).get(ExampleTable.playerName)).isEqualTo("Dieter Bohlen");
		assertThat(queryResultAfterUpdate.get(0).get(ExampleTable.status)).isEqualTo(Status.ACTIVE);
		assertThat(queryResultAfterUpdate.get(0).get(ExampleTable.score)).isEqualTo(3000);
		assertThat(queryResultAfterUpdate.get(0).get(ExampleTable.level)).isEqualTo(Level.HIGH);

		assertThat(dao.deleteAll()).isEqualTo(1);

		assertThat(dao.selectAll()).isEmpty();
	}

	@Test
	public void testSelectWhere() {
		Dto<ExampleTable> dto1 = createDto("Dieter Bohlen", Status.ACTIVE, 1000, Level.MEDIUM);
		Dto<ExampleTable> dto2 = createDto("Helene Fischer", Status.ACTIVE, 1000, Level.MEDIUM);
		Dto<ExampleTable> dto3 = createDto("Helene Fischer", Status.INACTIVE, 1000, Level.MEDIUM);

		dao.insert(dto1);
		dao.insert(dto2);
		dao.insert(dto3);

		verifyNumberOfResults(createBuilder(), 3);
		verifyNumberOfResults(createBuilder().put(ExampleTable.playerName, "Dieter Bohlen"), 1);
		verifyNumberOfResults(createBuilder().put(ExampleTable.playerName, "Helene Fischer"), 2);
		verifyNumberOfResults(createBuilder().put(ExampleTable.playerName, "missing"), 0);
		verifyNumberOfResults(createBuilder().put(ExampleTable.status, Status.ACTIVE), 2);
		verifyNumberOfResults(createBuilder().put(ExampleTable.status, Status.INACTIVE), 1);
		verifyNumberOfResults(createBuilder().put(ExampleTable.status, Status.SUSPENDED), 0);
		verifyNumberOfResults(createBuilder().put(ExampleTable.playerName, "Dieter Bohlen").put(ExampleTable.status, Status.ACTIVE), 1);
		verifyNumberOfResults(createBuilder().put(ExampleTable.playerName, "Helene Fischer").put(ExampleTable.status, Status.ACTIVE), 1);
		verifyNumberOfResults(createBuilder().put(ExampleTable.playerName, "Helene Fischer").put(ExampleTable.status, Status.INACTIVE), 1);
		verifyNumberOfResults(createBuilder().put(ExampleTable.playerName, "Dieter Bohlen").put(ExampleTable.status, Status.INACTIVE), 0);
	}

	private void verifyNumberOfResults(ColumnMap.Builder<ExampleTable> builder, int expectedNumberOfRows) {
		assertThat(dao.selectWhere(builder.build())).hasSize(expectedNumberOfRows);
	}

	@Test
	public void testPartialInsert() {
		Dto<ExampleTable> partialInsert = dao.dtoBuilder()
				.with(ExampleTable.playerName, "Helge Schneider")
				.build();
		assertThat(dao.insert(partialInsert)).isEqualTo(1);

		List<Dto<ExampleTable>> queryResult = dao.selectAll();
		assertThat(queryResult).hasSize(1);
		assertThat(queryResult.get(0).get(ExampleTable.playerName)).isEqualTo("Helge Schneider");
		assertThat(queryResult.get(0).get(ExampleTable.status)).isEqualTo(Status.INACTIVE);
		assertThat(queryResult.get(0).get(ExampleTable.score)).isEqualTo(0);
		assertThat(queryResult.get(0).get(ExampleTable.level)).isEqualTo(Level.LOW);
	}

	@Test
	public void testPartialUpdate() {
		Dto<ExampleTable> dto = createDto("Dieter Bohlen", Status.ACTIVE, 1000, Level.LOW);
		assertThat(dao.insert(dto)).isEqualTo(1);

		ColumnMap<ExampleTable> partialUpdate = createBuilder()
				.put(ExampleTable.status, Status.INACTIVE)
				.build();
		assertThat(dao.updateAll(partialUpdate)).isEqualTo(1);

		List<Dto<ExampleTable>> queryResult = dao.selectAll();
		assertThat(queryResult).hasSize(1);
		assertThat(queryResult.get(0).get(ExampleTable.playerName)).isEqualTo("Dieter Bohlen");
		assertThat(queryResult.get(0).get(ExampleTable.status)).isSameAs(Status.INACTIVE);
	}

	@Test
	public void testUpdateWhere() {
		Dto<ExampleTable> dto = createDto("Dieter Bohlen", Status.ACTIVE, 1000, Level.LOW);
		assertThat(dao.insert(dto)).isEqualTo(1);

		ColumnMap<ExampleTable> partialUpdate = createBuilder()
				.put(ExampleTable.status, Status.INACTIVE)
				.build();

		ColumnMap<ExampleTable> whereWithNoMath = createBuilder()
				.put(ExampleTable.playerName, "missing")
				.build();
		ColumnMap<ExampleTable> whereWithOneMath = createBuilder()
				.put(ExampleTable.playerName, "Dieter Bohlen")
				.build();

		assertThat(dao.updateWhere(partialUpdate, whereWithNoMath)).isEqualTo(0);
		assertThat(dao.updateWhere(partialUpdate, whereWithOneMath)).isEqualTo(1);
	}

	@Test
	public void testDeleteWhere() {
		Dto<ExampleTable> dto = createDto("Dieter Bohlen", Status.ACTIVE, 1000, Level.LOW);
		assertThat(dao.insert(dto)).isEqualTo(1);

		ColumnMap<ExampleTable> whereWithNoMath = createBuilder()
				.put(ExampleTable.playerName, "missing")
				.build();
		ColumnMap<ExampleTable> whereWithOneMath = createBuilder()
				.put(ExampleTable.playerName, "Dieter Bohlen")
				.build();

		assertThat(dao.deleteWhere(whereWithNoMath)).isEqualTo(0);
		assertThat(dao.deleteWhere(whereWithOneMath)).isEqualTo(1);
	}

	private Dto<ExampleTable> createDto(String string, Status status, int score, Level level) {
		return dao.dtoBuilder()
				.with(ExampleTable.playerName, string)
				.with(ExampleTable.status, status)
				.with(ExampleTable.score, score)
				.with(ExampleTable.level, level)
				.build();
	}

	private ColumnMap.Builder<ExampleTable> createBuilder() {
		return ColumnMap.<ExampleTable> builder();
	}

	@After
	public void tearDown() {
		dao.deleteAll();
	}
}
