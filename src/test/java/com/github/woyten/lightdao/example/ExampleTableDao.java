package com.github.woyten.lightdao.example;

import javax.sql.DataSource;

import com.github.woyten.lightdao.core.Dao;
import com.github.woyten.lightdao.sql.SqlLogger;

public class ExampleTableDao extends Dao<ExampleTable> {
	public ExampleTableDao(DataSource dataSource) {
		super(new ExampleTable(), dataSource, SqlLogger.NO_LOGGING);
	}
}
