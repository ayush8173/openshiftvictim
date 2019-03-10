package com.eh.openshiftvictim.service;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import com.eh.openshiftvictim.dao.ApplicationDao;
import com.eh.openshiftvictim.model.Book;
import com.eh.openshiftvictim.model.BookComment;
import com.eh.openshiftvictim.model.CreditRequest;
import com.eh.openshiftvictim.model.User;

public class ApplicationService {
	final ApplicationDao applicationDao = new ApplicationDao();

	public List<Book> searchBook(String bookNameParam) throws SQLException {
		List<Book> bookList = applicationDao.searchBook(bookNameParam);
		return bookList;
	}

	public List<Book> searchUserBooks(String username, String sortBy) throws SQLException {
		List<Book> bookList = applicationDao.searchUserBooks(username, sortBy);
		return bookList;
	}

	public Book searchBookById(String bookId, String username) throws SQLException {
		Book book = applicationDao.searchBookById(bookId, username);
		return book;
	}

	public void postComment(String bookId, BookComment bookComment) throws SQLException {
		applicationDao.postComment(bookId, bookComment);
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
}
