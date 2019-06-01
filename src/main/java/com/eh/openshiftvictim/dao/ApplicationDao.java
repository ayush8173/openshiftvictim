package com.eh.openshiftvictim.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eh.openshiftvictim.model.Book;
import com.eh.openshiftvictim.model.BookComment;
import com.eh.openshiftvictim.model.CreditRequest;
import com.eh.openshiftvictim.model.User;
import com.eh.openshiftvictim.utility.ApplicationUtility;

public class ApplicationDao extends SqlQueries {
	final JdbcConnection jdbcConnection = new JdbcConnection();
	Connection connection = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	public List<Book> searchBook(boolean isBookId, String searchParam) throws SQLException {
		final String SQL_QUERY = "select * from BOOK_INFO where BOOK_ID = '" + searchParam + "'";
		final String SQL_QUERY1 = "select * from BOOK_INFO where BOOK_TITLE like '%" + searchParam.toUpperCase() + "%'";

		connection = jdbcConnection.openConnection();
		if (isBookId) {
			preparedStatement = connection.prepareStatement(SQL_QUERY);
		} else {
			preparedStatement = connection.prepareStatement(SQL_QUERY1);
		}
		resultSet = preparedStatement.executeQuery();
		List<Book> bookList = new ArrayList<Book>();

		while (resultSet.next()) {
			Book book = new Book();
			book.setBookId(resultSet.getString("BOOK_ID"));
			book.setBookTitle(resultSet.getString("BOOK_TITLE"));
			book.setBookAuthor(resultSet.getString("BOOK_AUTHOR"));
			book.setBookPrice(resultSet.getString("BOOK_PRICE"));
			book.setBookImage(ApplicationUtility.getBlobToBase64String(resultSet.getBlob("BOOK_IMAGE")));
			bookList.add(book);
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return bookList;
	}

	public boolean checkBookExist(String bookId) throws SQLException {
		boolean bookExist = false;

		final String SQL_QUERY = "select * from BOOK_INFO where BOOK_ID = '" + bookId + "'";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			bookExist = true;
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return bookExist;
	}

	public List<Book> searchUserBooks(String username, String sortBy) throws SQLException {
		final String orderBy = (null == sortBy || "".equals(sortBy)) ? "bbm.BOUGHT_DATE desc" : sortBy;
		final String SQL_QUERY = "select * from BOOK_INFO bi inner join BOOK_BOUGHT_MAP bbm on bi.BOOK_ID = bbm.BOOK_ID where bbm.USERNAME = '"
				+ username + "' order by " + orderBy;

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();
		List<Book> bookList = new ArrayList<Book>();

		while (resultSet.next()) {
			Book book = new Book();
			book.setBookId(resultSet.getString("BOOK_ID"));
			book.setBookTitle(resultSet.getString("BOOK_TITLE"));
			book.setBookAuthor(resultSet.getString("BOOK_AUTHOR"));
			book.setBookPrice(resultSet.getString("BOOK_PRICE"));
			book.setBoughtDate(resultSet.getString("BOUGHT_DATE"));
			book.setBookImage(ApplicationUtility.getBlobToBase64String(resultSet.getBlob("BOOK_IMAGE")));
			bookList.add(book);
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return bookList;
	}

	public Book searchBookForDisplay(String bookId, String username) throws SQLException {
		final String SQL_QUERY = "select * from BOOK_INFO where BOOK_ID = '" + bookId + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();
		Book book = new Book();
		while (resultSet.next()) {
			book.setBookId(resultSet.getString("BOOK_ID"));
			book.setBookTitle(resultSet.getString("BOOK_TITLE"));
			book.setBookAuthor(resultSet.getString("BOOK_AUTHOR"));
			book.setBookPrice(resultSet.getString("BOOK_PRICE"));
			book.setBookImage(ApplicationUtility.getBlobToBase64String(resultSet.getBlob("BOOK_IMAGE")));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		final String SQL_QUERY2 = "select * from BOOK_COMMENTS where BOOK_ID = '" + bookId
				+ "' order by COMMENT_DATE desc";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		resultSet = preparedStatement.executeQuery();
		List<BookComment> bookComments = new ArrayList<BookComment>();
		while (resultSet.next()) {
			BookComment bookComment = new BookComment();
			bookComment.setComment(resultSet.getString("COMMENT"));
			bookComment.setCommentor(resultSet.getString("USERNAME"));
			bookComment.setCommentDate(resultSet.getString("COMMENT_DATE"));
			bookComments.add(bookComment);
		}
		book.setBookComment(bookComments);
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		final String SQL_QUERY3 = "select * from BOOK_BOUGHT_MAP where BOOK_ID = '" + bookId + "' and USERNAME = '"
				+ username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY3);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			book.setHasBook("Y");
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		return book;
	}

	public void logXmlInput(String xmlInput) throws SQLException {
		final String SQL_QUERY = "insert into INPUT_XML (XML_DATA, CREATED_DATE) values ('" + xmlInput + "', now())";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void postComment(Book book) throws SQLException {
		final String SQL_QUERY = "insert into BOOK_COMMENTS (BOOK_ID, USERNAME, COMMENT, COMMENT_DATE) values ('"
				+ book.getBookId() + "', '" + book.getBookComment().get(0).getCommentor() + "', '"
				+ book.getBookComment().get(0).getComment() + "', now())";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public boolean buyBook(String bookId, String bookPrice, String username) throws SQLException {
		boolean boughtStatus = false;
		int price = 0, credits = 0;

		price = Integer.parseInt(bookPrice);
		// final String SQL_QUERY = "select BOOK_PRICE from BOOK_INFO where
		// BOOK_ID = '" + bookId + "'";
		// connection = jdbcConnection.openConnection();
		// preparedStatement = connection.prepareStatement(SQL_QUERY);
		// resultSet = preparedStatement.executeQuery();
		// while (resultSet.next()) {
		// price = Integer.parseInt(resultSet.getString("BOOK_PRICE"));
		// }
		// jdbcConnection.closeConnection(connection, statement,
		// preparedStatement, resultSet);

		final String SQL_QUERY2 = "select CREDITS from USERS where USERNAME = '" + username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			credits = Integer.parseInt(resultSet.getString("CREDITS"));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		if (price <= credits) {
			final String SQL_QUERY3 = "update USERS set CREDITS = " + (credits - price) + " where USERNAME = '"
					+ username + "'";
			connection = jdbcConnection.openConnection();
			preparedStatement = connection.prepareStatement(SQL_QUERY3);
			preparedStatement.executeUpdate();
			jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

			final String SQL_QUERY4 = "insert into BOOK_BOUGHT_MAP (BOOK_ID, USERNAME, BOUGHT_DATE) values ('" + bookId
					+ "', '" + username + "', now())";
			connection = jdbcConnection.openConnection();
			preparedStatement = connection.prepareStatement(SQL_QUERY4);
			preparedStatement.executeUpdate();
			jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

			boughtStatus = true;
		}

		return boughtStatus;
	}

	public void returnBook(String bookId, String bookPrice, String username) throws SQLException {
		int price = 0, credits = 0;

		price = Integer.parseInt(bookPrice);
		// final String SQL_QUERY = "select BOOK_PRICE from BOOK_INFO where
		// BOOK_ID = '" + bookId + "'";
		// connection = jdbcConnection.openConnection();
		// preparedStatement = connection.prepareStatement(SQL_QUERY);
		// resultSet = preparedStatement.executeQuery();
		// while (resultSet.next()) {
		// price = Integer.parseInt(resultSet.getString("BOOK_PRICE"));
		// }
		// jdbcConnection.closeConnection(connection, statement,
		// preparedStatement, resultSet);

		final String SQL_QUERY2 = "select CREDITS from USERS where USERNAME = '" + username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			credits = Integer.parseInt(resultSet.getString("CREDITS"));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		final String SQL_QUERY3 = "update USERS set CREDITS = " + (credits + price) + " where USERNAME = '" + username
				+ "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY3);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		final String SQL_QUERY4 = "delete from BOOK_BOUGHT_MAP where BOOK_ID = '" + bookId + "' and USERNAME = '"
				+ username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY4);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public boolean addCreditRequest(String amount, String username) throws SQLException {
		boolean creditRequestExists = false;
		int creditAmount = 0;

		final String SQL_QUERY = "select 1 from CREDIT_REQUESTS where USERNAME = '" + username
				+ "' and REQUEST_STATUS = 'PENDING'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			creditRequestExists = true;
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		if (!creditRequestExists) {
			creditAmount = Integer.parseInt(amount);
			final String SQL_QUERY2 = "insert into CREDIT_REQUESTS (USERNAME, CREDIT_AMOUNT, REQUEST_STATUS, REQUEST_DATE) values ('"
					+ username + "', " + creditAmount + ", 'PENDING', now())";
			connection = jdbcConnection.openConnection();
			preparedStatement = connection.prepareStatement(SQL_QUERY2);
			preparedStatement.executeUpdate();
			jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		}

		return creditRequestExists;
	}

	public boolean transferCredits(String amount, String toUsername, String username) throws SQLException {
		boolean transferStatus = false;
		int creditAmount, toCredits, fromCredits;

		User toUser = searchUser(toUsername);
		User fromUser = searchUser(username);

		if (toUser.getUsername() != null) {
			creditAmount = Integer.parseInt(amount);
			toCredits = Integer.parseInt(toUser.getCredits());
			fromCredits = Integer.parseInt(fromUser.getCredits());

			if (creditAmount <= fromCredits) {
				final String SQL_QUERY = "update USERS set CREDITS = " + (fromCredits - creditAmount)
						+ " where USERNAME = '" + fromUser.getUsername() + "'";
				connection = jdbcConnection.openConnection();
				preparedStatement = connection.prepareStatement(SQL_QUERY);
				preparedStatement.executeUpdate();
				jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

				final String SQL_QUERY2 = "update USERS set CREDITS = " + (toCredits + creditAmount)
						+ " where USERNAME = '" + toUser.getUsername() + "'";
				connection = jdbcConnection.openConnection();
				preparedStatement = connection.prepareStatement(SQL_QUERY2);
				preparedStatement.executeUpdate();
				jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

				transferStatus = true;
			}
		}

		return transferStatus;
	}

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

			final String SQL_QUERY2 = "update USERS set CREDITS = " + (credits + creditAmount) + " where USERNAME = '"
					+ user.getUsername() + "'";
			connection = jdbcConnection.openConnection();
			preparedStatement = connection.prepareStatement(SQL_QUERY2);
			preparedStatement.executeUpdate();
			jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		}

		final String SQL_QUERY3 = "update CREDIT_REQUESTS set REQUEST_STATUS = '" + approveReject
				+ "' where USERNAME = '" + username + "' and REQUEST_STATUS = 'PENDING'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY3);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

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
			user.setCredits(resultSet.getString("CREDITS"));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return user;
	}

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
			user.setCredits(resultSet.getString("CREDITS"));
			userList.add(user);
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return userList;
	}

	public boolean addNewUser(User user) throws SQLException {
		boolean userExists = false;

		if (searchUser(user.getUsername()).getUsername() == null) {
			final String SQL_QUERY = "insert into USERS (USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, CREDITS) values ('"
					+ user.getUsername() + "', '" + user.getPassword() + "', '" + user.getFirstName() + "', '"
					+ user.getLastName() + "', 2000)";
			connection = jdbcConnection.openConnection();
			preparedStatement = connection.prepareStatement(SQL_QUERY);
			preparedStatement.executeUpdate();
			jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

			final String SQL_QUERY2 = "insert into USER_ROLES (USERNAME, ROLE_NAME) values ('" + user.getUsername()
					+ "', 'APP_USER')";
			connection = jdbcConnection.openConnection();
			preparedStatement = connection.prepareStatement(SQL_QUERY2);
			preparedStatement.executeUpdate();
			jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		} else {
			userExists = true;
		}

		return userExists;
	}

	public void uploadBookImage(String bookId, String filename) throws SQLException, FileNotFoundException {
		final String SQL_QUERY = "update BOOK_INFO set BOOK_IMAGE = ? where BOOK_ID = ?";

		File file = new File(filename);
		FileInputStream fileInputStream = new FileInputStream(file);

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setBinaryStream(1, fileInputStream);
		preparedStatement.setString(2, bookId);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public Map<String, String> fetchAllFiles() throws SQLException {
		final String SQL_QUERY = "select * from FILES where (timediff(now(), CREATED_DATE) < '24:00:00') order by CREATED_DATE desc";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		resultSet = preparedStatement.executeQuery();
		Map<String, String> fileMap = new HashMap<String, String>();

		while (resultSet.next()) {
			fileMap.put(resultSet.getString("FILE_NAME"), resultSet.getString("CREATED_DATE"));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return fileMap;
	}

	public void createFile(String fileName) throws SQLException {
		final String SQL_QUERY = "insert into FILES (FILE_NAME, CREATED_DATE) values ('" + fileName + "', now())";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

}
