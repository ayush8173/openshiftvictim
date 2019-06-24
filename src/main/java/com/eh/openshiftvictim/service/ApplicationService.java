package com.eh.openshiftvictim.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.eh.openshiftvictim.dao.ApplicationDao;
import com.eh.openshiftvictim.exception.BookStoreException;
import com.eh.openshiftvictim.model.Book;
import com.eh.openshiftvictim.utility.ApplicationUtility;
import com.eh.openshiftvictim.utility.OsValidatior;

public class ApplicationService {
	final ApplicationDao applicationDao = new ApplicationDao();

	public List<Book> searchBook(boolean isSecure, boolean isBookId, String searchParam)
			throws SQLException, BookStoreException {
		List<Book> bookList = null;
		if (isSecure) {
			if (ApplicationUtility.checkNull(new String[] { searchParam })) {
				throw new BookStoreException("Input fields can't be null!");
			} else if (!searchParam.matches("^[a-zA-Z0-9]{0,100}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				bookList = applicationDao.searchBookSecure(isBookId, searchParam);
			}
		} else {
			bookList = applicationDao.searchBook(isBookId, searchParam);
		}
		return bookList;
	}

	public boolean checkBookExist(boolean isSecure, String bookId) throws SQLException, BookStoreException {
		boolean bookExist = false;
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { bookId })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!bookId.matches("^[a-zA-Z0-9]{5}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				bookExist = applicationDao.checkBookExistSecure(bookId);
			}
		} else {
			bookExist = applicationDao.checkBookExist(bookId);
		}
		return bookExist;
	}

	public List<Book> searchUserBooks(boolean isSecure, String username, String sortBy)
			throws SQLException, BookStoreException {
		List<Book> bookList = null;
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { username })
					|| ApplicationUtility.checkNull(new String[] { sortBy })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!username.matches("^[a-zA-Z0-9]{1,20}$") || (!"".equalsIgnoreCase(sortBy)
					&& !"book_title".equalsIgnoreCase(sortBy) && !"book_author".equalsIgnoreCase(sortBy)
							&& !"book_price".equalsIgnoreCase(sortBy) && !"bought_date".equalsIgnoreCase(sortBy))) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				bookList = applicationDao.searchUserBooksSecure(username, sortBy);
			}
		} else {
			bookList = applicationDao.searchUserBooks(username, sortBy);
		}
		return bookList;
	}

	public Book searchBookForDisplay(boolean isSecure, String bookId, String username)
			throws SQLException, BookStoreException {
		Book book = null;
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { bookId, username })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!bookId.matches("^[a-zA-Z0-9]{5}$") || !username.matches("^[a-zA-Z0-9]{1,20}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				book = applicationDao.searchBookForDisplaySecure(bookId, username);
			}
		} else {
			book = applicationDao.searchBookForDisplay(bookId, username);
		}
		return book;
	}

	public void logXmlInput(boolean isSecure, String xmlInput) throws SQLException, BookStoreException {
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { xmlInput })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else {
				applicationDao.logXmlInputSecure(xmlInput);
			}
		} else {
			applicationDao.logXmlInput(xmlInput);
		}
	}

	public void postComment(boolean isSecure, Book book) throws SQLException, BookStoreException {
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { book.getBookId(),
					book.getBookComment().get(0).getCommentor(), book.getBookComment().get(0).getComment() })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!book.getBookId().matches("^[a-zA-Z0-9]{5}$")
					|| !book.getBookComment().get(0).getCommentor().matches("^[a-zA-Z0-9]{1,20}$")
					|| !book.getBookComment().get(0).getComment().matches("^[a-zA-Z0-9]{1,1000}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				applicationDao.postCommentSecure(book);
			}
		} else {
			applicationDao.postComment(book);
		}
	}

	public void buyBook(boolean isSecure, String bookId, String bookPrice, String username)
			throws SQLException, BookStoreException {
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { bookId, username })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!bookId.matches("^[a-zA-Z0-9]{5}$") || !username.matches("^[a-zA-Z0-9]{1,20}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				applicationDao.buyBookSecure(bookId, username);
			}
		} else {
			applicationDao.buyBook(bookId, bookPrice, username);
		}
	}

	public void returnBook(boolean isSecure, String bookId, String bookPrice, String username)
			throws SQLException, BookStoreException {
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { bookId, username })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!bookId.matches("^[a-zA-Z0-9]{5}$") || !username.matches("^[a-zA-Z0-9]{1,20}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				applicationDao.returnBookSecure(bookId, username);
			}
		} else {
			applicationDao.returnBook(bookId, bookPrice, username);
		}
	}

	public void uploadBookImage(boolean isSecure, String bookId, String filename)
			throws SQLException, FileNotFoundException, BookStoreException {
		if (isSecure) {
			if (ApplicationUtility.checkNullEmpty(new String[] { bookId, filename })) {
				throw new BookStoreException("Input fields can't be null or empty!");
			} else if (!bookId.matches("^[a-zA-Z0-9]{5}$") || !filename.matches("^[a-zA-Z0-9./]{1,100}$")) {
				throw new BookStoreException("Invalid input, please check!");
			} else {
				applicationDao.uploadBookImageSecure(bookId, filename);
			}
		} else {
			applicationDao.uploadBookImage(bookId, filename);
		}
	}

	public Map<String, String> fetchAllFiles(boolean isSecure) throws SQLException {
		Map<String, String> fileMap = null;
		if (isSecure) {
			fileMap = applicationDao.fetchAllFilesSecure();
		} else {
			fileMap = applicationDao.fetchAllFiles();
		}
		return fileMap;
	}

	public void createFile(boolean isSecure, String jarPath, String filePath, String fileName, String username)
			throws IOException, SQLException, BookStoreException {
		if (isSecure) {
			if (isSecure) {
				if (ApplicationUtility.checkNullEmpty(new String[] { username })) {
					throw new BookStoreException("Input fields can't be null or empty!");
				} else if (!username.matches("^[a-zA-Z0-9]{1,20}$")) {
					throw new BookStoreException("Invalid input, please check!");
				} else {
					if (OsValidatior.isWindows()) {
						Runtime.getRuntime().exec(new String[] { "cmd", "/C", "java -jar " + jarPath + "CreateFile.jar "
								+ filePath + " " + fileName + " " + username });
						applicationDao.createFile(fileName);
					} else {
						Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "java -jar " + jarPath
								+ "CreateFile.jar " + filePath + " " + fileName + " " + username });
						applicationDao.createFile(fileName);
					}
				}
			}
		} else {
			if (OsValidatior.isWindows()) {
				Runtime.getRuntime().exec(new String[] { "cmd", "/C",
						"java -jar " + jarPath + "CreateFile.jar " + filePath + " " + fileName + " " + username });
				applicationDao.createFile(fileName);
			} else {
				if (ApplicationUtility.checkNullEmpty(new String[] { username })) {
					throw new BookStoreException("Input fields can't be null or empty!");
				} else if (!username.matches("^[a-zA-Z0-9]{1,20}$")) {
					throw new BookStoreException("Invalid input, please check!");
				} else {
					Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c",
							"java -jar " + jarPath + "CreateFile.jar " + filePath + " " + fileName + " " + username });
					applicationDao.createFile(fileName);
				}
			}
		}
	}
}
