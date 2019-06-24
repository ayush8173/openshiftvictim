package com.eh.openshiftvictim.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.xml.sax.SAXException;

import com.eh.openshiftvictim.dao.UserDao;
import com.eh.openshiftvictim.exception.BookStoreException;
import com.eh.openshiftvictim.model.CreditRequest;
import com.eh.openshiftvictim.model.User;
import com.eh.openshiftvictim.utility.ApplicationUtility;
import com.eh.openshiftvictim.utility.SecureGenerator;
import com.eh.openshiftvictim.utility.Smtp;

public class UserService {
	final UserDao userDao = new UserDao();

	public User searchUser(boolean isSecure, String username) throws SQLException, BookStoreException {
		User user = null;
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { username })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!username.matches("^[a-zA-Z0-9]{1,20}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				user = userDao.searchUserSecure(username);
			}
		} else {
			user = userDao.searchUser(username);
		}
		return user;
	}

	public User doLogin(boolean isSecure, String username, String password) throws SQLException, BookStoreException {
		User user = null;
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { username, password })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!username.matches("^[a-zA-Z0-9]{1,20}$") || password.length() > 100) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				user = userDao.doLoginSecure(username, SecureGenerator.generateStringHash(password));
			}
		} else {
			if (userDao.checkUserExists(username)) {
				user = userDao.doLogin(username, SecureGenerator.generateStringHash(password));
			} else {
				throw new BookStoreException("User with username '" + username + "' doesn't exist!");
			}
		}
		return user;
	}

	public User doLoginLdap(String username, String password) throws LdapException, CursorException {
		User user = userDao.doLoginLdap(username, password);
		return user;
	}

	public User doLoginXml(String xmlPath, String username, String password)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		User user = userDao.doLoginXml(xmlPath, username, SecureGenerator.generateStringHash(password));
		return user;
	}

	public List<User> fetchAllUsers(boolean isSecure) throws SQLException {
		List<User> userList = null;
		if (isSecure) {
			userList = userDao.fetchAllUsersSecure();
		} else {
			userList = userDao.fetchAllUsers();
		}
		return userList;
	}

	public String addNewUser(boolean isSecure, User user, String password2) throws SQLException, BookStoreException {
		String successMessage = null;
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(
					new String[] { user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail() })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!user.getUsername().matches("^[a-zA-Z0-9]{1,20}$")
					|| !user.getFirstName().matches("^[a-zA-Z0-9]{1,20}$")
					|| !user.getLastName().matches("^[a-zA-Z0-9]{1,20}$")
					|| !user.getEmail().matches("^[a-zA-Z0-9.@]{1,100}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				user.setPassword("dummy");
				userDao.addNewUserSecure(user);

				String accountActivationToken = SecureGenerator.generateSecureString(50);
				String accountActivationLink = ApplicationUtility.getAccountActivationUrl(user.getUsername(),
						accountActivationToken);
				userDao.addUserTokenSecure(user.getUsername(), "ACCOUNT_ACTIVATION", accountActivationToken);
				Smtp.sendAccountActivationEmailSecure(user.getEmail(), user.getUsername(), accountActivationLink);
				successMessage = "An account activation link has been sent to user with username '" + user.getUsername()
						+ "' on registered email address!";
			}
		} else {
			user.setPassword(SecureGenerator.generateStringHash(user.getPassword()));
			userDao.addNewUser(user);

			String accountActivationToken = SecureGenerator.generateSecureString(50);
			String accountActivationLink = ApplicationUtility.getAccountActivationUrl(user.getUsername(),
					accountActivationToken) + "&FIS=Y";
			userDao.addUserTokenSecure(user.getUsername(), "ACCOUNT_ACTIVATION", accountActivationToken);
			Smtp.sendAccountActivationEmail(user.getEmail(), user.getUsername(), accountActivationLink);
			successMessage = "User with username '" + user.getUsername()
					+ "' created successfully and an activation link has been sent to registered email address!";
		}
		return successMessage;
	}

	public boolean validateAccountActivationToken(String username, String accountActivationToken)
			throws SQLException, BookStoreException {
		boolean tokenValid = false;
		if (ApplicationUtility.checkNullEmpty(new String[] { username, accountActivationToken })) {
			throw new BookStoreException("Input fields can't be null or empty!");
		} else if (!username.matches("^[a-zA-Z0-9]{1,20}$") || !accountActivationToken.matches("^[a-zA-Z0-9]{50}$")) {
			throw new BookStoreException("Invalid input, please check!");
		} else {
			User user = userDao.searchUserSecure(username);
			if (user.getUsername() != null
					&& userDao.validateUserTokenSecure(username, "ACCOUNT_ACTIVATION", accountActivationToken)) {
				userDao.updateUserAccountStatusSecure(username, true);
				tokenValid = true;
			}
		}
		return tokenValid;
	}

	public void deleteUser(boolean isSecure, String username) throws SQLException, BookStoreException {
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { username })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!username.matches("^[a-zA-Z0-9]{1,20}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				userDao.deleteUserSecure(username);
			}
		} else {
			userDao.deleteUser(username);
		}
	}

	public String forgotPassword(boolean isSecure, String username) throws SQLException, BookStoreException {
		if ("admin".equals(username)) {
			throw new BookStoreException("Password reset for 'admin' is not allowed!");
		}

		String successMessage = null;
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { username })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!username.matches("^[a-zA-Z0-9]{1,20}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				User user = userDao.searchUserSecure(username);
				if (user.getUsername() != null && user.isActive()) {
					String passwordResetToken = SecureGenerator.generateSecureString(50);
					String passwordResetLink = ApplicationUtility.getPasswordResetUrl(user.getUsername(),
							passwordResetToken);
					userDao.addUserTokenSecure(username, "PASSWORD_RESET", passwordResetToken);
					Smtp.sendForgotPasswordEmailSecure(true, user.getEmail(), passwordResetLink);
				} else {
					Smtp.sendForgotPasswordEmailSecure(false, user.getEmail(), null);
				}
			}
			successMessage = "An email has been sent to your regitered email address with further instructions. Please check your email!";
		} else {
			User user = userDao.searchUser(username);
			if (user.getUsername() != null && user.isActive()) {
				String newPassword = SecureGenerator.generateSecureString(10);
				userDao.updateUserPassword(username, SecureGenerator.generateStringHash(newPassword));
				Smtp.sendForgotPasswordEmail(user.getEmail(), newPassword);
			} else {
				throw new BookStoreException("User with username '" + username + "' doesn't exist!");
			}
			successMessage = "Your new password has been sent to your regitered email address. Please check your email!";
		}
		return successMessage;
	}

	public boolean validatePasswordResetToken(String username, String passwordResetToken)
			throws SQLException, BookStoreException {
		boolean tokenValid = false;
		if (ApplicationUtility.checkNullEmpty(new String[] { username, passwordResetToken })) {
			throw new BookStoreException("Input fields can't be null or empty!");
		} else if (!username.matches("^[a-zA-Z0-9]{1,20}$") || !passwordResetToken.matches("^[a-zA-Z0-9]{50}$")) {
			throw new BookStoreException("Invalid input, please check!");
		} else {
			User user = userDao.searchUserSecure(username);
			if (user.getUsername() != null && user.isActive()
					&& userDao.validateUserTokenSecure(username, "PASSWORD_RESET", passwordResetToken)) {
				tokenValid = true;
			}
		}
		return tokenValid;
	}

	public void updateUserPassword(String username, String password, String password2)
			throws SQLException, BookStoreException {
		if (ApplicationUtility.checkNullEmpty(new String[] { username, password, password2 })) {
			throw new BookStoreException("Input fields can't be null or empty!");
		} else if (!username.matches("^[a-zA-Z0-9]{1,20}$") || password.length() > 100 || password2.length() > 100) {
			throw new BookStoreException("Invalid input, please check!");
		} else if (!password2.equals(password)) {
			throw new BookStoreException("Password and re-entered password doesn't match!");
		} else {
			userDao.updateUserPasswordSecure(username, SecureGenerator.generateStringHash(password));
			userDao.deleteUserTokenSecure(username, "ACCOUNT_ACTIVATION");
			userDao.deleteUserTokenSecure(username, "PASSWORD_RESET");
		}
	}

	public void addUserToken(boolean isSecure, String username, String tokenType, String token)
			throws SQLException, BookStoreException {
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { username, tokenType, token })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!username.matches("^[a-zA-Z0-9]{1,20}$") || !tokenType.matches("^[A-Z_]{1,50}$")
					|| !token.matches("^[a-zA-Z0-9]{1,100}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				userDao.addUserTokenSecure(username, tokenType, token);
			}
		} else {
			userDao.addUserToken(username, tokenType, token);
		}
	}

	public boolean validateUserToken(boolean isSecure, String username, String tokenType, String token)
			throws SQLException, BookStoreException {
		boolean tokenValid = false;
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { username, tokenType, token })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!username.matches("^[a-zA-Z0-9]{1,20}$") || !tokenType.matches("^[A-Z_]{1,50}$")
					|| !token.matches("^[a-zA-Z0-9]{1,100}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				tokenValid = userDao.validateUserTokenSecure(username, tokenType, token);
			}
		} else {
			tokenValid = userDao.validateUserToken(username, tokenType, token);
		}
		return tokenValid;
	}

	public void deleteUserToken(boolean isSecure, String username, String tokenType)
			throws SQLException, BookStoreException {
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { username, tokenType })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!username.matches("^[a-zA-Z0-9]{1,20}$") || !tokenType.matches("^[A-Z_]{1,50}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				userDao.deleteUserTokenSecure(username, tokenType);
			}
		} else {
			userDao.deleteUserToken(username, tokenType);
		}
	}

	public void transferCredits(boolean isSecure, String amount, String toUsername, String username, String password)
			throws SQLException, BookStoreException {
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { amount, toUsername, username, password })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!amount.matches("^[0-9]{3,4}$") || !toUsername.matches("^[a-zA-Z0-9]{1,20}$")
					|| !username.matches("^[a-zA-Z0-9]{1,20}$") || password.length() > 100) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				if (userDao.doLoginSecure(username, SecureGenerator.generateStringHash(password))
						.getUsername() != null) {
					userDao.transferCreditsSecure(amount, toUsername, username);
				} else {
					throw new BookStoreException("Can't authenticate the logged in user. Please check your password!");
				}
			}
		} else {
			userDao.transferCredits(amount, toUsername, username);
		}
	}

	public void addCreditRequest(boolean isSecure, String amount, String username)
			throws SQLException, BookStoreException {
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { amount, username })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!amount.matches("^[0-9]{3,4}$") || !username.matches("^[a-zA-Z0-9]{1,20}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				userDao.addCreditRequestSecure(amount, username);
			}
		} else {
			userDao.addCreditRequest(amount, username);
		}
	}

	public List<CreditRequest> fetchCreditRequests(boolean isSecure) throws SQLException {
		List<CreditRequest> creditRequestList = null;
		if (isSecure) {
			creditRequestList = userDao.fetchCreditRequestsSecure();
		} else {
			creditRequestList = userDao.fetchCreditRequests();
		}
		return creditRequestList;
	}

	public void processCreditRequest(boolean isSecure, String approveReject, String username)
			throws SQLException, BookStoreException {
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { approveReject, username })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!approveReject.matches("^[A-Z]{8}$") || !username.matches("^[a-zA-Z0-9]{1,20}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				userDao.processCreditRequestSecure(approveReject, username);
			}
		} else {
			userDao.processCreditRequest(approveReject, username);
		}
	}
}
