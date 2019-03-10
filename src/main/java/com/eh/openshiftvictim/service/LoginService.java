package com.eh.openshiftvictim.service;

import java.sql.SQLException;

import com.eh.openshiftvictim.dao.LoginDao;
import com.eh.openshiftvictim.model.User;

public class LoginService {
	final LoginDao loginDao = new LoginDao();

	public User doLogin(String username, String password) throws SQLException {
		User user = loginDao.doLogin(username, password);
		return user;
	}
}
