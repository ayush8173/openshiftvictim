package com.eh.openshiftvictim.utility;

import com.eh.openshiftvictim.model.User;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XstreamConvertor {

	private static XStream xstream = new XStream(new DomDriver()) {
		{
			processAnnotations(User.class);
		}
	};

	public static String objectToXml(Object object) {
		return null == object ? "" : xstream.toXML(object);
	}

	public static Object xmlToObject(String xmlInput) {
		return xstream.fromXML(xmlInput);
	}

	public static void main(String[] args) {
//		User user = new User();
//		user.setUsername("user01");
//		user.setPassword("user01");
//		user.setFirstName("User");
//		user.setLastName("01");
//
//		String xml = objectToXml(user);
//		System.out.println(xml);
	}

}
