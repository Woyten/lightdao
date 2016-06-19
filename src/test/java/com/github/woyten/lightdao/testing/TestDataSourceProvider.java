package com.github.woyten.lightdao.testing;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class TestDataSourceProvider {
	public static MysqlDataSource getDataSource() {
		MysqlDataSource mysqlDataSource = new MysqlDataSource();
		mysqlDataSource.setUser("user");
		mysqlDataSource.setDatabaseName("lightdao");
		return mysqlDataSource;
	}
}
