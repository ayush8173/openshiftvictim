package com.eh.openshiftvictim.dao;

import java.io.IOException;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

public class AdConnection {

	static final String LDAP_HOST = "ldap.forumsys.com";
	static final String BIND_USERNAME = "cn=read-only-admin,dc=example,dc=com";
	static final String BIND_PASSWORD = "password";

	public LdapConnection openConnection() {
		LdapConnection connection = null;

		LdapConnectionConfig config = new LdapConnectionConfig();
		config.setLdapHost(LDAP_HOST);
		config.setLdapPort(389);
		// config.setSecureRandom(new java.security.SecureRandom());
		// config.setTrustManagers(new AlwaysTrustManager());
		config.setUseSsl(false);
		config.setUseTls(false);

		try {
			connection = new LdapNetworkConnection(config);
			connection.connect();
			connection.bind(BIND_USERNAME, BIND_PASSWORD);
		} catch (LdapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connection;
	}

	public void closeConnection(LdapConnection connection, EntryCursor cursor) {
		try {
			if (cursor != null) {
				cursor.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
