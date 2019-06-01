package com.eh.openshiftvictim.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.eh.openshiftvictim.model.Book;

public class JaxbConvertor {

	public static String objectToXml(Book book) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		String xmlOutput = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Book.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.marshal(book, stream);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		xmlOutput = new String(stream.toByteArray());
		return xmlOutput;
	}

	public static Book xmlToObject(String xmlInput) {
		ByteArrayInputStream stream = new ByteArrayInputStream(xmlInput.getBytes());
		Book book = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Book.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			book = (Book) jaxbUnmarshaller.unmarshal(stream);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return book;
	}

	public static void main(String[] args) {
//		BookComment bookComment = new BookComment();
//		bookComment.setCommentor("anonymous");
//		bookComment.setComment("This comment is from WebService");
//
//		List<BookComment> bookComments = new ArrayList<BookComment>();
//		bookComments.add(bookComment);
//
//		Book book = new Book();
//		book.setBookId("BK001");
//		book.setBookComment(bookComments);
//
//		String xml = objectToXml(book);
//		System.out.println(xml);
	}

}
