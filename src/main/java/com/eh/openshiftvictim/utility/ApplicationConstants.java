package com.eh.openshiftvictim.utility;

public class ApplicationConstants {

	public static final String SERVER = "OPENSHIFT"; // LOCAL, NTEG, OPENSHIFT
	public static final String APP_CONTEXT = "BookStoreVictim";

	public static final String LOCAL_PROTOCOL = "http";
	public static final String NTEG_PROTOCOL = "http";
	public static final String OPENSHIFT_PROTOCOL = "http";

	public static final String LOCAL_DOMAIN = "localhost";
	public static final String NTEG_DOMAIN = "10.127.127.83";
	public static final String OPENSHIFT_DOMAIN = "victim-bookstore.1d35.starter-us-east-1.openshiftapps.com";

	public static final String LOCAL_PORT = "8080";
	public static final String NTEG_PORT = "8080";
	public static final String OPENSHIFT_PORT = "80";

	public static final String LOCAL_URL = LOCAL_PROTOCOL + "://" + LOCAL_DOMAIN + ":" + LOCAL_PORT + "/" + APP_CONTEXT;
	public static final String NTEG_URL = NTEG_PROTOCOL + "://" + NTEG_DOMAIN + ":" + NTEG_PORT + "/" + APP_CONTEXT;
	public static final String OPENSHIFT_URL = OPENSHIFT_PROTOCOL + "://" + OPENSHIFT_DOMAIN + ":" + OPENSHIFT_PORT
			+ "/" + APP_CONTEXT;
}
