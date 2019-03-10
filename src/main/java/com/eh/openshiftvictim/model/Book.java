package com.eh.openshiftvictim.model;

import java.util.List;

public class Book {

	private String bookId;
	private String bookTitle;
	private String bookDescription;
	private String bookAuthor;
	private String bookPrice;
	private String bookImage;
	private List<BookComment> bookComment;
	private String hasBook;
	private String boughtDate;

	public String getBoughtDate() {
		return boughtDate;
	}

	public void setBoughtDate(String boughtDate) {
		this.boughtDate = boughtDate;
	}

	public List<BookComment> getBookComment() {
		return bookComment;
	}

	public void setBookComment(List<BookComment> bookComment) {
		this.bookComment = bookComment;
	}

	public String getHasBook() {
		return hasBook;
	}

	public void setHasBook(String hasBook) {
		this.hasBook = hasBook;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookName) {
		this.bookTitle = bookName;
	}

	public String getBookDescription() {
		return bookDescription;
	}

	public void setBookDescription(String bookDescription) {
		this.bookDescription = bookDescription;
	}

	public String getBookAuthor() {
		return bookAuthor;
	}

	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}

	public String getBookPrice() {
		return bookPrice;
	}

	public void setBookPrice(String bookPrice) {
		this.bookPrice = bookPrice;
	}

	public String getBookImage() {
		return bookImage;
	}

	public void setBookImage(String bookImage) {
		this.bookImage = bookImage;
	}

	public List<BookComment> getBookComments() {
		return bookComment;
	}

	public void setBookComments(List<BookComment> bookComment) {
		this.bookComment = bookComment;
	}

}
