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

import com.eh.openshiftvictim.model.User;

public class LoginDao extends SqlQueries {
	final JdbcConnection jdbcConnection = new JdbcConnection();
	Connection connection = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	final AdConnection adConnection = new AdConnection();
	LdapConnection ldapConnection = null;
	EntryCursor cursor = null;

	public User doLogin(String username, String password) throws SQLException {
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
			user.setCredits(resultSet.getString("CREDITS"));
		}
		user.setRoles(getUserRoles(user.getUsername()));
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return user;
	}

	public User doLoginSql(String username, String password) throws SQLException {
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
			user.setCredits(resultSet.getString("CREDITS"));
		}
		user.setRoles(getUserRoles(user.getUsername()));
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return user;
	}

	public List<String> getUserRoles(String username) throws SQLException {
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

	public User doLoginXml(String xmlPath, String username, String password)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		//final String LOGIN_XPATH = "/Users/User[@id='1']";
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
					if("username".equals(nodeList.item(i).getNodeName())) {
						user.setUsername(nod.getFirstChild().getNodeValue());
					} else if("firstName".equals(nodeList.item(i).getNodeName())) {
						user.setFirstName(nod.getFirstChild().getNodeValue());
					} else if("lastName".equals(nodeList.item(i).getNodeName())) {
						user.setLastName(nod.getFirstChild().getNodeValue());
					} else if("credits".equals(nodeList.item(i).getNodeName())) {
						user.setCredits(nod.getFirstChild().getNodeValue());
					}
				}
			}
			user.setRoles(new ArrayList<String>());
		}

		return user;
	}
}
