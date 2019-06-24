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

import com.eh.openshiftvictim.exception.BookStoreException;
import com.eh.openshiftvictim.model.Book;
import com.eh.openshiftvictim.model.BookComment;
import com.eh.openshiftvictim.utility.ApplicationUtility;

public class ApplicationDao extends SqlQueries {
	final UserDao userDao = new UserDao();
	final JdbcConnection jdbcConnection = new JdbcConnection();
	Connection connection = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	// ========================================================================================

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

	public List<Book> searchBookSecure(boolean isBookId, String searchParam) throws SQLException {
		final String SQL_QUERY = "select * from BOOK_INFO where BOOK_ID = ?";
		final String SQL_QUERY1 = "select * from BOOK_INFO where BOOK_TITLE like ?";

		connection = jdbcConnection.openConnection();
		if (isBookId) {
			preparedStatement = connection.prepareStatement(SQL_QUERY);
			preparedStatement.setString(1, searchParam);
		} else {
			preparedStatement = connection.prepareStatement(SQL_QUERY1);
			preparedStatement.setString(1, "%" + searchParam + "%");
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

	// ========================================================================================

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

	public boolean checkBookExistSecure(String bookId) throws SQLException {
		boolean bookExist = false;

		final String SQL_QUERY = "select * from BOOK_INFO where BOOK_ID = ?";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, bookId);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			bookExist = true;
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		return bookExist;
	}

	// ========================================================================================

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

	public List<Book> searchUserBooksSecure(String username, String sortBy) throws SQLException {
		final String orderBy = (null == sortBy || "".equals(sortBy)) ? "bbm.BOUGHT_DATE desc" : sortBy.toUpperCase();
		final String SQL_QUERY = "select * from BOOK_INFO bi inner join BOOK_BOUGHT_MAP bbm on bi.BOOK_ID = bbm.BOOK_ID where bbm.USERNAME = ? order by "
				+ orderBy;

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, username);
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

	// ========================================================================================

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

	public Book searchBookForDisplaySecure(String bookId, String username) throws SQLException {
		final String SQL_QUERY = "select * from BOOK_INFO where BOOK_ID = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, bookId);
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

		final String SQL_QUERY2 = "select * from BOOK_COMMENTS where BOOK_ID = ? order by COMMENT_DATE desc";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		preparedStatement.setString(1, bookId);
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

		final String SQL_QUERY3 = "select * from BOOK_BOUGHT_MAP where BOOK_ID = ? and USERNAME = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY3);
		preparedStatement.setString(1, bookId);
		preparedStatement.setString(2, username);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			book.setHasBook("Y");
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		return book;
	}

	// ========================================================================================

	public void logXmlInput(String xmlInput) throws SQLException {
		final String SQL_QUERY = "insert into INPUT_XML (XML_DATA, CREATED_DATE) values ('" + xmlInput + "', now())";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void logXmlInputSecure(String xmlInput) throws SQLException {
		final String SQL_QUERY = "insert into INPUT_XML (XML_DATA, CREATED_DATE) values (?,now())";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, xmlInput);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	// ========================================================================================

	public void postComment(Book book) throws SQLException {
		final String SQL_QUERY = "insert into BOOK_COMMENTS (BOOK_ID, USERNAME, COMMENT, COMMENT_DATE) values ('"
				+ book.getBookId() + "', '" + book.getBookComment().get(0).getCommentor() + "', '"
				+ book.getBookComment().get(0).getComment() + "', now())";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void postCommentSecure(Book book) throws SQLException {
		final String SQL_QUERY = "insert into BOOK_COMMENTS (BOOK_ID, USERNAME, COMMENT, COMMENT_DATE) values (?,?,?,now())";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, book.getBookId());
		preparedStatement.setString(2, book.getBookComment().get(0).getCommentor());
		preparedStatement.setString(3, book.getBookComment().get(0).getComment());
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	// ========================================================================================

	public void buyBook(String bookId, String bookPrice, String username) throws SQLException, BookStoreException {
		int price = 0, credits = 0;

		price = Integer.parseInt(bookPrice);

		credits = Integer.parseInt(userDao.searchUser(username).getCredits());

		if (price <= credits) {
			userDao.updateUserCredit(username, (credits - price));

			final String SQL_QUERY2 = "insert into BOOK_BOUGHT_MAP (BOOK_ID, USERNAME, BOUGHT_DATE) values ('" + bookId
					+ "', '" + username + "', now())";
			connection = jdbcConnection.openConnection();
			preparedStatement = connection.prepareStatement(SQL_QUERY2);
			preparedStatement.executeUpdate();
			jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		} else {
			throw new BookStoreException(
					"You don't have sufficient credits for this transaction! Current available credits are : " + credits
							+ "/-");
		}
	}

	public void buyBookSecure(String bookId, String username) throws SQLException, BookStoreException {
		int price = 0, credits = 0;

		final String SQL_QUERY = "select BOOK_PRICE from BOOK_INFO where BOOK_ID = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, bookId);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			price = Integer.parseInt(resultSet.getString("BOOK_PRICE"));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		credits = Integer.parseInt(userDao.searchUserSecure(username).getCredits());

		if (price <= credits) {
			userDao.updateUserCreditSecure(username, (credits - price));

			final String SQL_QUERY2 = "insert into BOOK_BOUGHT_MAP (BOOK_ID, USERNAME, BOUGHT_DATE) values (?,?,now())";
			connection = jdbcConnection.openConnection();
			preparedStatement.setString(1, bookId);
			preparedStatement.setString(2, username);
			preparedStatement = connection.prepareStatement(SQL_QUERY2);
			preparedStatement.executeUpdate();
			jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
		} else {
			throw new BookStoreException(
					"You don't have sufficient credits for this transaction! Current available credits are : " + credits
							+ "/-");
		}
	}

	// ========================================================================================

	public void returnBook(String bookId, String bookPrice, String username) throws SQLException {
		int price = 0, credits = 0;

		price = Integer.parseInt(bookPrice);

		credits = Integer.parseInt(userDao.searchUser(username).getCredits());

		userDao.updateUserCredit(username, (credits + price));

		final String SQL_QUERY2 = "delete from BOOK_BOUGHT_MAP where BOOK_ID = '" + bookId + "' and USERNAME = '"
				+ username + "'";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void returnBookSecure(String bookId, String username) throws SQLException {
		int price = 0, credits = 0;

		final String SQL_QUERY = "select BOOK_PRICE from BOOK_INFO where BOOK_ID = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, bookId);
		resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			price = Integer.parseInt(resultSet.getString("BOOK_PRICE"));
		}
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);

		credits = Integer.parseInt(userDao.searchUserSecure(username).getCredits());

		userDao.updateUserCreditSecure(username, (credits + price));

		final String SQL_QUERY2 = "delete from BOOK_BOUGHT_MAP where BOOK_ID = ? and USERNAME = ?";
		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY2);
		preparedStatement.setString(1, bookId);
		preparedStatement.setString(2, username);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	// ========================================================================================

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

	public void uploadBookImageSecure(String bookId, String filename) throws SQLException, FileNotFoundException {
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

	// ========================================================================================

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

	public Map<String, String> fetchAllFilesSecure() throws SQLException {
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

	// ========================================================================================

	public void createFile(String fileName) throws SQLException {
		final String SQL_QUERY = "insert into FILES (FILE_NAME, CREATED_DATE) values ('" + fileName + "', now())";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	public void createFileSecure(String fileName) throws SQLException {
		final String SQL_QUERY = "insert into FILES (FILE_NAME, CREATED_DATE) values (?,now())";

		connection = jdbcConnection.openConnection();
		preparedStatement = connection.prepareStatement(SQL_QUERY);
		preparedStatement.setString(1, fileName);
		preparedStatement.executeUpdate();
		jdbcConnection.closeConnection(connection, statement, preparedStatement, resultSet);
	}

	// ========================================================================================
}
