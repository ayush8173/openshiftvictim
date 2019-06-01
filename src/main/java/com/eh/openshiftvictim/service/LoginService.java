package com.eh.openshiftvictim.service;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.xml.sax.SAXException;

import com.eh.openshiftvictim.dao.LoginDao;
import com.eh.openshiftvictim.model.User;

public class LoginService {
	final LoginDao loginDao = new LoginDao();

	public User doLogin(String username, String password) throws SQLException {
		User user = loginDao.doLogin(username, password);
		return user;
	}

	public User doLoginSql(String username, String password) throws SQLException {
		User user = loginDao.doLoginSql(username, password);
		return user;
	}

	public User doLoginLdap(String username, String password) throws LdapException, CursorException {
		User user = loginDao.doLoginLdap(username, password);
		return user;
	}

	public User doLoginXml(String xmlPath, String username, String password)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		User user = loginDao.doLoginXml(xmlPath, username, password);
		return user;
	}
}
