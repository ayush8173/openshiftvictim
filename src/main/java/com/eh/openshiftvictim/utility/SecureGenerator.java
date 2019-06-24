package com.eh.openshiftvictim.utility;

import java.security.SecureRandom;

import org.apache.commons.codec.digest.DigestUtils;

public class SecureGenerator {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public static String generateSecureString(int length) {
		StringBuilder stringBuilder = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			stringBuilder.append(CHARACTERS.charAt(SECURE_RANDOM.nextInt(CHARACTERS.length())));
		}
		String secureString = stringBuilder.toString();
		stringBuilder.setLength(0);
		return secureString;
	}
	
	public static String generateStringHash(String input) {
		String result = DigestUtils.sha256Hex(input);
		return result;
	}

	public static void main(String[] args) {

//		int passwordLength = 50;
//		String password = generateSecureString(passwordLength);
//		System.out.println("Secure password: " + password);
		
//		System.out.println("Password hash is : " + generateStringHash("siddhant"));
	}

}
