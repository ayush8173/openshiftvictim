package com.eh.openshiftvictim.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.eh.openshiftvictim.dao.ApplicationDao;
import com.eh.openshiftvictim.model.Book;
import com.eh.openshiftvictim.model.CreditRequest;
import com.eh.openshiftvictim.model.User;
import com.eh.openshiftvictim.utility.OsValidatior;

public class ApplicationService {
	final ApplicationDao applicationDao = new ApplicationDao();

	public List<Book> searchBook(boolean isBookId, String searchParam) throws SQLException {
		List<Book> bookList = applicationDao.searchBook(isBookId, searchParam);
		return bookList;
	}

	public boolean checkBookExist(String bookId) throws SQLException {
		boolean bookExist = applicationDao.checkBookExist(bookId);
		return bookExist;
	}

	public List<Book> searchUserBooks(String username, String sortBy) throws SQLException {
		List<Book> bookList = applicationDao.searchUserBooks(username, sortBy);
		return bookList;
	}

	public Book searchBookForDisplay(String bookId, String username) throws SQLException {
		Book book = applicationDao.searchBookForDisplay(bookId, username);
		return book;
	}

	public void logXmlInput(String xmlInput) throws SQLException {
		applicationDao.logXmlInput(xmlInput);
	}

	public void postComment(Book book) throws SQLException {
		applicationDao.postComment(book);
	}

	public boolean buyBook(String bookId, String bookPrice, String username) throws SQLException {
		boolean boughtStatus = applicationDao.buyBook(bookId, bookPrice, username);
		return boughtStatus;
	}

	public void returnBook(String bookId, String bookPrice, String username) throws SQLException {
		applicationDao.returnBook(bookId, bookPrice, username);
	}

	public boolean addCreditRequest(String amount, String username) throws SQLException {
		boolean creditRequestExists = applicationDao.addCreditRequest(amount, username);
		return creditRequestExists;
	}

	public boolean transferCredits(String amount, String toUsername, String username) throws SQLException {
		boolean transferStatus = applicationDao.transferCredits(amount, toUsername, username);
		return transferStatus;
	}

	public List<CreditRequest> fetchCreditRequests() throws SQLException {
		List<CreditRequest> creditRequestList = applicationDao.fetchCreditRequests();
		return creditRequestList;
	}

	public void processCreditRequest(String approveReject, String username) throws SQLException {
		applicationDao.processCreditRequest(approveReject, username);
	}

	public User searchUser(String username) throws SQLException {
		User user = applicationDao.searchUser(username);
		return user;
	}

	public List<User> fetchAllUsers() throws SQLException {
		List<User> userList = applicationDao.fetchAllUsers();
		return userList;
	}

	public boolean addNewUser(User user) throws SQLException {
		boolean userExists = applicationDao.addNewUser(user);
		return userExists;
	}

	public void uploadBookImage(String bookId, String filename) throws SQLException, FileNotFoundException {
		applicationDao.uploadBookImage(bookId, filename);
	}

	public Map<String, String> fetchAllFiles() throws SQLException {
		Map<String, String> fileMap = applicationDao.fetchAllFiles();
		return fileMap;
	}

	public void createFile(String jarPath, String filePath, String fileName, String username)
			throws IOException, SQLException {
		if (OsValidatior.isWindows()) {
			Runtime.getRuntime().exec(new String[] { "cmd", "/C",
					"java -jar " + jarPath + "CreateFile.jar " + filePath + " " + fileName + " " + username });
			applicationDao.createFile(fileName);
		} else {
			if(username != null && username.matches("^[a-zA-Z0-9]*$")) {
				Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c",
						"java -jar " + jarPath + "CreateFile.jar " + filePath + " " + fileName + " " + username });
				applicationDao.createFile(fileName);
			}
		}
	}
}
