package com.eh.openshiftvictim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.eh.openshiftvictim.model.User;

public class LoginDao extends SqlQueries {
	final JdbcConnection jdbcConnection = new JdbcConnection();
	Connection connection = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	public User doLogin(String username, String password) throws SQLException {
		final String SQL_QUERY = "select * from USERS where USERNAME = '" + username + "' and PASSWORD = '" + password + "'";
		connection = jdbcConnection.openConnection();
		statement = connection.createStatement();
		resultSet = statement.executeQuery(SQL_QUERY);
		User user = new User();
		while (resultSet.next()) {
			user.setUsername(resultSet.getString("USERNAME"));
			user.setFirstName(resultSet.getString("FIRST_NAME"));
			user.setLastName(resultSet.getString("LAST_NAME"));
			user.setCredits(resultSet.getString("CREDITS"));
		}
		user.setRoles(getUserRoles(user.getUsername()));
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return user;
	}

	public List<String> getUserRoles(String username) throws SQLException {
		final String SQL_QUERY = "select * from USER_ROLES where USERNAME = '" + username + "'";
		connection = jdbcConnection.openConnection();
		statement = connection.createStatement();
		resultSet = statement.executeQuery(SQL_QUERY);
		List<String> roles = new ArrayList<String>();
		while (resultSet.next()) {
			roles.add(resultSet.getString("ROLE_NAME"));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return roles;
	}
}
