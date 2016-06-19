package com.github.woyten.lightdao.core;

import java.sql.SQLException;

public class DaoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DaoException(String message, SQLException e) {
		super(message, e);
	}
}
