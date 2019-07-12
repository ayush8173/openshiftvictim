package com.eh.openshiftvictim.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.eh.openshiftvictim.exception.BookStoreException;
import com.eh.openshiftvictim.model.CreditRequest;
import com.eh.openshiftvictim.model.User;

public class UserDao extends SqlQueries {
	final JdbcConnection jdbcConnection = new JdbcConnection();
	Connection connection = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	final AdConnection adConnection = new AdConnection();
	LdapConnection ldapConnection = null;
	EntryCursor cursor = null;

	// ========================================================================================

	public boolean checkUserExists(String username) throws SQLException {
		boolean userExists = false;

		final String SQL_QUERY = "select 1 from USERS where USERNAME = '" + username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			userExists = true;
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return userExists;
	}

	public boolean checkUserExistsSecure(String username) throws SQLException {
		boolean userExists = false;

		final String SQL_QUERY = "select 1 from USERS where USERNAME = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, username);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			userExists = true;
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return userExists;
	}

	// ========================================================================================

	public User searchUser(String username) throws SQLException {
		final String SQL_QUERY = "select * from USERS where USERNAME = '" + username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();
		User user = new User();
		while (resultSet.next()) {
			user.setUsername(resultSet.getString("USERNAME"));
			user.setFirstName(resultSet.getString("FIRST_NAME"));
			user.setLastName(resultSet.getString("LAST_NAME"));
			user.setEmail(resultSet.getString("EMAIL"));
			user.setCredits(resultSet.getString("CREDITS"));
			user.setActive(resultSet.getBoolean("ACCOUNT_STATUS"));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		user.setRoles(getUserRoles(user.getUsername()));
		return user;
	}

	public User searchUserSecure(String username) throws SQLException {
		final String SQL_QUERY = "select * from USERS where USERNAME = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, username);
		resultSet = preparedStatement.executeQuery();
		User user = new User();
		while (resultSet.next()) {
			user.setUsername(resultSet.getString("USERNAME"));
			user.setFirstName(resultSet.getString("FIRST_NAME"));
			user.setLastName(resultSet.getString("LAST_NAME"));
			user.setEmail(resultSet.getString("EMAIL"));
			user.setCredits(resultSet.getString("CREDITS"));
			user.setActive(resultSet.getBoolean("ACCOUNT_STATUS"));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		user.setRoles(getUserRolesSecure(user.getUsername()));
		return user;
	}

	// ========================================================================================

	public User doLogin(String username, String password) throws SQLException, BookStoreException {
		final String SQL_QUERY = "select * from USERS where USERNAME = '" + username + "' and PASSWORD = '" + password
				+ "'";
		connection = jdbcConnection.openConnection();
		statement = connection.createStatement();
		resultSet = statement.executeQuery(SQL_QUERY);
		User user = new User();
		while (resultSet.next()) {
			user.setUsername(resultSet.getString("USERNAME"));
			user.setFirstName(resultSet.getString("FIRST_NAME"));
			user.setLastName(resultSet.getString("LAST_NAME"));
			user.setEmail(resultSet.getString("EMAIL"));
			user.setCredits(resultSet.getString("CREDITS"));
			user.setActive(resultSet.getBoolean("ACCOUNT_STATUS"));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		if (user.getUsername() != null && !user.isActive()) {
			throw new BookStoreException("User with username '" + username
					+ "' is not active!\nPlease follow the activation link sent in email to activate this user.");
		}
		user.setRoles(getUserRoles(user.getUsername()));
		return user;
	}

	public User doLoginSecure(String username, String password) throws SQLException, BookStoreException {
		final String SQL_QUERY = "select * from USERS where USERNAME = ? and PASSWORD = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, username);
		preparedStatement.setString(2, password);
		resultSet = preparedStatement.executeQuery();
		User user = new User();
		while (resultSet.next()) {
			user.setUsername(resultSet.getString("USERNAME"));
			user.setFirstName(resultSet.getString("FIRST_NAME"));
			user.setLastName(resultSet.getString("LAST_NAME"));
			user.setEmail(resultSet.getString("EMAIL"));
			user.setCredits(resultSet.getString("CREDITS"));
			user.setActive(resultSet.getBoolean("ACCOUNT_STATUS"));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		if (user.getUsername() != null && !user.isActive()) {
			throw new BookStoreException("User with username '" + username
					+ "' is not active!\nPlease follow the activation link sent in email to activate this user.");
		}
		user.setRoles(getUserRolesSecure(user.getUsername()));
		return user;
	}

	// ========================================================================================

	public List<String> getUserRoles(String username) throws SQLException {
		final String SQL_QUERY = "select * from USER_ROLES where USERNAME = '" + username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();
		List<String> roles = new ArrayList<String>();
		while (resultSet.next()) {
			roles.add(resultSet.getString("ROLE_NAME"));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return roles;
	}

	public List<String> getUserRolesSecure(String username) throws SQLException {
		final String SQL_QUERY = "select * from USER_ROLES where USERNAME = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, username);
		resultSet = preparedStatement.executeQuery();
		List<String> roles = new ArrayList<String>();
		while (resultSet.next()) {
			roles.add(resultSet.getString("ROLE_NAME"));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return roles;
	}

	// ========================================================================================

	public User doLoginLdap(String username, String password) throws LdapException, CursorException {
		final String BASE_DN = "dc=example,dc=com";
		final String FILTER = "(&(uid=" + username + ")(mail=" + password + "))";

		ldapConnection = adConnection.openConnection();
		cursor = ldapConnection.search(BASE_DN, FILTER, SearchScope.SUBTREE);

		User user = new User();
		if (cursor.next()) {
			user.setUsername(cursor.get().get("uid").getString());
			user.setFirstName(cursor.get().get("cn").getString());
			user.setLastName(cursor.get().get("sn").getString());
			user.setCredits("0");
		}
		user.setRoles(new ArrayList<String>());
		adConnection.closeConnection(ldapConnection, cursor);
		return user;
	}

	// ========================================================================================

	public User doLoginXml(String xmlPath, String username, String password)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		// final String LOGIN_XPATH = "/Users/User[@id='1']";
		final String LOGIN_XPATH = "//User[username/text()='" + username + "' and  password/text()='" + password + "']";

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse(xmlPath + "users.xml");

		// Create XPathFactory object
		XPathFactory xpathFactory = XPathFactory.newInstance();

		// Create XPath object
		XPath xpath = xpathFactory.newXPath();

		XPathExpression expr = xpath.compile(LOGIN_XPATH);
		Node node = (Node) expr.evaluate(document, XPathConstants.NODE);

		User user = new User();

		if (node != null) {
			NodeList nodeList = node.getChildNodes();
			for (int i = 0; null != nodeList && i < nodeList.getLength(); i++) {
				Node nod = nodeList.item(i);
				if (nod.getNodeType() == Node.ELEMENT_NODE) {
					System.out.println(nodeList.item(i).getNodeName() + " : " + nod.getFirstChild().getNodeValue());
					if ("username".equals(nodeList.item(i).getNodeName())) {
						user.setUsername(nod.getFirstChild().getNodeValue());
					} else if ("firstName".equals(nodeList.item(i).getNodeName())) {
						user.setFirstName(nod.getFirstChild().getNodeValue());
					} else if ("lastName".equals(nodeList.item(i).getNodeName())) {
						user.setLastName(nod.getFirstChild().getNodeValue());
					} else if ("email".equals(nodeList.item(i).getNodeName())) {
						user.setEmail(nod.getFirstChild().getNodeValue());
					} else if ("credits".equals(nodeList.item(i).getNodeName())) {
						user.setCredits(nod.getFirstChild().getNodeValue());
					}
				}
			}
			user.setRoles(new ArrayList<String>());
		}

		return user;
	}

	// ========================================================================================

	public List<User> fetchAllUsers() throws SQLException {
		final String SQL_QUERY = "select * from USERS order by FIRST_NAME, LAST_NAME";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();
		List<User> userList = new ArrayList<User>();
		while (resultSet.next()) {
			User user = new User();
			user.setUsername(resultSet.getString("USERNAME"));
			user.setFirstName(resultSet.getString("FIRST_NAME"));
			user.setLastName(resultSet.getString("LAST_NAME"));
			user.setEmail(resultSet.getString("EMAIL"));
			user.setCredits(resultSet.getString("CREDITS"));
			user.setActive(resultSet.getBoolean("ACCOUNT_STATUS"));
			userList.add(user);
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return userList;
	}

	public List<User> fetchAllUsersSecure() throws SQLException {
		final String SQL_QUERY = "select * from USERS order by FIRST_NAME, LAST_NAME";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();
		List<User> userList = new ArrayList<User>();
		while (resultSet.next()) {
			User user = new User();
			user.setUsername(resultSet.getString("USERNAME"));
			user.setFirstName(resultSet.getString("FIRST_NAME"));
			user.setLastName(resultSet.getString("LAST_NAME"));
			user.setEmail(resultSet.getString("EMAIL"));
			user.setCredits(resultSet.getString("CREDITS"));
			user.setActive(resultSet.getBoolean("ACCOUNT_STATUS"));
			userList.add(user);
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return userList;
	}

	// ========================================================================================

	public void addNewUser(User user) throws SQLException, BookStoreException {
		boolean waitForActivation = false;

		final String SQL_QUERY = "select 1 from USER_TOKENS where USERNAME = '" + user.getUsername()
				+ "' and TOKEN_TYPE = 'ACCOUNT_ACTIVATION' and (timediff(now(), TOKEN_DATE) < '24:00:00')";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			waitForActivation = true;
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		User userTemp = searchUser(user.getUsername());

		if (userTemp.getUsername() != null && (userTemp.isActive() || waitForActivation)) {
			throw new BookStoreException("User with username '" + user.getUsername() + "' already exists!");
		} else if (userTemp.getUsername() != null && !waitForActivation) {
			deleteUser(userTemp.getUsername());
		}

		final String SQL_QUERY2 = "insert into USERS (USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, EMAIL, CREDITS, ACCOUNT_STATUS) values ('"
				+ user.getUsername() + "', '" + user.getPassword() + "', '" + user.getFirstName() + "', '"
				+ user.getLastName() + "', '" + user.getEmail() + "', 2000, " + false + ")";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		final String SQL_QUERY3 = "insert into USER_ROLES (USERNAME, ROLE_NAME) values ('" + user.getUsername()
				+ "', 'APP_USER')";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY3);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void addNewUserSecure(User user) throws SQLException, BookStoreException {
		boolean waitForActivation = false;

		final String SQL_QUERY = "select 1 from USER_TOKENS where USERNAME = ? and TOKEN_TYPE = ? and (timediff(now(), TOKEN_DATE) < '24:00:00')";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, user.getUsername());
		preparedStatement.setString(2, "ACCOUNT_ACTIVATION");
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			waitForActivation = true;
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		User userTemp = searchUserSecure(user.getUsername());

		if (userTemp.getUsername() != null && (userTemp.isActive() || waitForActivation)) {
			throw new BookStoreException("User with username '" + user.getUsername() + "' already exists!");
		} else if (userTemp.getUsername() != null && !waitForActivation) {
			deleteUserSecure(userTemp.getUsername());
		}

		final String SQL_QUERY2 = "insert into USERS (USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, EMAIL, CREDITS, ACCOUNT_STATUS) values (?,?,?,?,?,?,?)";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		preparedStatement.setString(1, user.getUsername());
		preparedStatement.setString(2, user.getPassword());
		preparedStatement.setString(3, user.getFirstName());
		preparedStatement.setString(4, user.getLastName());
		preparedStatement.setString(5, user.getEmail());
		preparedStatement.setInt(6, 2000);
		preparedStatement.setBoolean(7, false);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		final String SQL_QUERY3 = "insert into USER_ROLES (USERNAME, ROLE_NAME) values (?,?)";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY3);
		preparedStatement.setString(1, user.getUsername());
		preparedStatement.setString(2, "APP_USER");
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	// ========================================================================================

	public void deleteUser(String username) throws SQLException {
		final String SQL_QUERY = "delete from USER_TOKENS where USERNAME = '" + username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		final String SQL_QUERY2 = "delete from CREDIT_REQUESTS where USERNAME = '" + username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		final String SQL_QUERY3 = "delete from USER_ROLES where USERNAME = '" + username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY3);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		final String SQL_QUERY4 = "delete from USERS where USERNAME = '" + username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY4);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void deleteUserSecure(String username) throws SQLException {
		final String SQL_QUERY = "delete from USER_TOKENS where USERNAME = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, username);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		final String SQL_QUERY2 = "delete from CREDIT_REQUESTS where USERNAME = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		preparedStatement.setString(1, username);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		final String SQL_QUERY3 = "delete from USER_ROLES where USERNAME = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY3);
		preparedStatement.setString(1, username);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		final String SQL_QUERY4 = "delete from USERS where USERNAME = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY4);
		preparedStatement.setString(1, username);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	// ========================================================================================

	public void updateUserPassword(String username, String password) throws SQLException {
		final String SQL_QUERY = "update USERS set PASSWORD = '" + password + "' where USERNAME = '" + username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void updateUserPasswordSecure(String username, String password) throws SQLException {
		final String SQL_QUERY = "update USERS set PASSWORD = ? where USERNAME = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, password);
		preparedStatement.setString(2, username);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	// ========================================================================================

	public void updateUserAccountStatus(String username, boolean status) throws SQLException {
		final String SQL_QUERY = "update USERS set ACCOUNT_STATUS = '" + status + "' where USERNAME = '" + username
				+ "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void updateUserAccountStatusSecure(String username, boolean status) throws SQLException {
		final String SQL_QUERY = "update USERS set ACCOUNT_STATUS = ? where USERNAME = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setBoolean(1, status);
		preparedStatement.setString(2, username);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	// ========================================================================================

	public void addUserToken(String username, String tokenType, String token) throws SQLException {
		final String SQL_QUERY = "insert into USER_TOKENS (USERNAME, TOKEN_TYPE, TOKEN_VALUE, TOKEN_STATUS, TOKEN_DATE) values ('"
				+ username + "', '" + tokenType + "', '" + token + "', '" + true + ", now())";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void addUserTokenSecure(String username, String tokenType, String token) throws SQLException {
		final String SQL_QUERY = "insert into USER_TOKENS (USERNAME, TOKEN_TYPE, TOKEN_VALUE, TOKEN_STATUS, TOKEN_DATE) values (?,?,?,?,now())";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, username);
		preparedStatement.setString(2, tokenType);
		preparedStatement.setString(3, token);
		preparedStatement.setBoolean(4, true);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	// ========================================================================================

	public boolean validateUserToken(String username, String tokenType, String token) throws SQLException {
		boolean tokenValid = false;

		final String SQL_QUERY = "select 1 from USER_TOKENS where USERNAME = '" + username + "' and TOKEN_TYPE = '"
				+ tokenType + "' and TOKEN_VALUE = '" + token + "' and (timediff(now(), TOKEN_DATE) < '168:00:00')";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			tokenValid = true;
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return tokenValid;
	}

	public boolean validateUserTokenSecure(String username, String tokenType, String token) throws SQLException {
		boolean tokenValid = false;

		final String SQL_QUERY = "select 1 from USER_TOKENS where USERNAME = ? and TOKEN_TYPE = ? and TOKEN_VALUE = ? and (timediff(now(), TOKEN_DATE) < '168:00:00')";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, username);
		preparedStatement.setString(2, tokenType);
		preparedStatement.setString(3, token);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			tokenValid = true;
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return tokenValid;
	}

	// ========================================================================================

	public void deleteUserToken(String username, String tokenType) throws SQLException {
		final String SQL_QUERY = "delete from USER_TOKENS where USERNAME = '" + username + "' and TOKEN_TYPE = '"
				+ tokenType + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void deleteUserTokenSecure(String username, String tokenType) throws SQLException {
		final String SQL_QUERY = "delete from USER_TOKENS where USERNAME = ? and TOKEN_TYPE = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, username);
		preparedStatement.setString(2, tokenType);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	// ========================================================================================

	public void updateUserCredit(String username, int credits) throws SQLException {
		final String SQL_QUERY = "update USERS set CREDITS = " + credits + " where USERNAME = '" + username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void updateUserCreditSecure(String username, int credits) throws SQLException {
		final String SQL_QUERY = "update USERS set CREDITS = ? where USERNAME = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setInt(1, credits);
		preparedStatement.setString(2, username);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	// ========================================================================================

	public void transferCredits(String amount, String toUsername, String username)
			throws SQLException, BookStoreException {
		int creditAmount, toCredits, fromCredits;

		User toUser = searchUser(toUsername);
		User fromUser = searchUser(username);

		if (toUser.getUsername() != null) {
			creditAmount = Integer.parseInt(amount);
			toCredits = Integer.parseInt(toUser.getCredits());
			fromCredits = Integer.parseInt(fromUser.getCredits());

			if (creditAmount <= fromCredits) {
				updateUserCredit(fromUser.getUsername(), (fromCredits - creditAmount));
				updateUserCredit(toUser.getUsername(), (toCredits + creditAmount));
			} else {
				throw new BookStoreException(
						"You don't have sufficient credits for this transaction! Current available credits are : "
								+ fromCredits + "/-");
			}
		} else {
			throw new BookStoreException("User with username '" + toUsername + "' doesn't exist!");
		}
	}

	public void transferCreditsSecure(String amount, String toUsername, String username)
			throws SQLException, BookStoreException {
		int creditAmount, toCredits, fromCredits;

		User toUser = searchUserSecure(toUsername);
		User fromUser = searchUserSecure(username);

		if (toUser.getUsername() != null) {
			creditAmount = Integer.parseInt(amount);
			toCredits = Integer.parseInt(toUser.getCredits());
			fromCredits = Integer.parseInt(fromUser.getCredits());

			if (creditAmount <= fromCredits) {
				updateUserCreditSecure(fromUser.getUsername(), (fromCredits - creditAmount));
				updateUserCreditSecure(toUser.getUsername(), (toCredits + creditAmount));
			} else {
				throw new BookStoreException(
						"You don't have sufficient credits for this transaction! Current available credits are : "
								+ fromCredits + "/-");
			}
		} else {
			throw new BookStoreException("User with username '" + toUsername + "' doesn't exist!");
		}
	}

	// ========================================================================================

	public void addCreditRequest(String amount, String username) throws SQLException, BookStoreException {
		int creditAmount = 0;

		final String SQL_QUERY = "select 1 from CREDIT_REQUESTS where USERNAME = '" + username
				+ "' and REQUEST_STATUS = 'PENDING'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
			throw new BookStoreException("A pending credit request already exists!");
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		creditAmount = Integer.parseInt(amount);
		final String SQL_QUERY2 = "insert into CREDIT_REQUESTS (USERNAME, CREDIT_AMOUNT, REQUEST_STATUS, REQUEST_DATE) values ('"
				+ username + "', " + creditAmount + ", 'PENDING', now())";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void addCreditRequestSecure(String amount, String username) throws SQLException, BookStoreException {
		int creditAmount = 0;

		final String SQL_QUERY = "select 1 from CREDIT_REQUESTS where USERNAME = ? and REQUEST_STATUS = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, username);
		preparedStatement.setString(2, "PENDING");
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
			throw new BookStoreException("A pending credit request already exists!");
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		creditAmount = Integer.parseInt(amount);
		final String SQL_QUERY2 = "insert into CREDIT_REQUESTS (USERNAME, CREDIT_AMOUNT, REQUEST_STATUS, REQUEST_DATE) values (?,?,?,now())";
		connection = jdbcConnection.openConnection();
		preparedStatement.setString(1, username);
		preparedStatement.setInt(2, creditAmount);
		preparedStatement.setString(3, "PENDING");
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	// ========================================================================================

	public List<CreditRequest> fetchCreditRequests() throws SQLException {
		final String SQL_QUERY = "select * from CREDIT_REQUESTS where REQUEST_STATUS = 'PENDING' order by REQUEST_DATE desc";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();
		List<CreditRequest> creditRequestList = new ArrayList<CreditRequest>();
		while (resultSet.next()) {
			CreditRequest creditRequest = new CreditRequest();
			creditRequest.setUsername(resultSet.getString("USERNAME"));
			creditRequest.setCreditAmount(resultSet.getString("CREDIT_AMOUNT"));
			creditRequest.setRequestStatus(resultSet.getString("REQUEST_STATUS"));
			creditRequest.setRequestDate(resultSet.getString("REQUEST_DATE"));
			creditRequestList.add(creditRequest);
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return creditRequestList;
	}

	public List<CreditRequest> fetchCreditRequestsSecure() throws SQLException {
		final String SQL_QUERY = "select * from CREDIT_REQUESTS where REQUEST_STATUS = ? order by REQUEST_DATE desc";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, "PENDING");
		resultSet = preparedStatement.executeQuery();
		List<CreditRequest> creditRequestList = new ArrayList<CreditRequest>();
		while (resultSet.next()) {
			CreditRequest creditRequest = new CreditRequest();
			creditRequest.setUsername(resultSet.getString("USERNAME"));
			creditRequest.setCreditAmount(resultSet.getString("CREDIT_AMOUNT"));
			creditRequest.setRequestStatus(resultSet.getString("REQUEST_STATUS"));
			creditRequest.setRequestDate(resultSet.getString("REQUEST_DATE"));
			creditRequestList.add(creditRequest);
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return creditRequestList;
	}

	// ========================================================================================

	public void processCreditRequest(String approveReject, String username) throws SQLException {
		if ("APPROVED".equals(approveReject)) {
			int creditAmount = 0, credits = 0;
			User user = searchUser(username);
			credits = Integer.parseInt(user.getCredits());

			final String SQL_QUERY = "select CREDIT_AMOUNT from CREDIT_REQUESTS where USERNAME = '" + username
					+ "' and REQUEST_STATUS = 'PENDING'";
			connection = jdbcConnection.openConnection();
			preparedStatement = connection.prepareStatement(SQL_QUERY);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				creditAmount = Integer.parseInt(resultSet.getString("CREDIT_AMOUNT"));
			}
			jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

			updateUserCredit(user.getUsername(), (credits + creditAmount));
		}

		final String SQL_QUERY2 = "update CREDIT_REQUESTS set REQUEST_STATUS = '" + approveReject
				+ "' where USERNAME = '" + username + "' and REQUEST_STATUS = 'PENDING'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void processCreditRequestSecure(String approveReject, String username) throws SQLException {
		if ("APPROVED".equals(approveReject)) {
			int creditAmount = 0, credits = 0;
			User user = searchUser(username);
			credits = Integer.parseInt(user.getCredits());

			final String SQL_QUERY = "select CREDIT_AMOUNT from CREDIT_REQUESTS where USERNAME = ? and REQUEST_STATUS = ?";
			connection = jdbcConnection.openConnection();
			preparedStatement = connection.prepareStatement(SQL_QUERY);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, "PENDING");
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				creditAmount = Integer.parseInt(resultSet.getString("CREDIT_AMOUNT"));
			}
			jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

			updateUserCredit(user.getUsername(), (credits + creditAmount));
		}

		final String SQL_QUERY2 = "update CREDIT_REQUESTS set REQUEST_STATUS = ? where USERNAME = ? and REQUEST_STATUS = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		preparedStatement.setString(1, approveReject);
		preparedStatement.setString(2, username);
		preparedStatement.setString(3, "PENDING");
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	// ========================================================================================
}
