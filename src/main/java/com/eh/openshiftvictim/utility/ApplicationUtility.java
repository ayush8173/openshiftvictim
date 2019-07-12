package com.eh.openshiftvictim.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;;

public class ApplicationUtility {

	public static Map<String, String> bookImageMap = new HashMap<String, String>();

	public static void populateBookImageMap() {
		bookImageMap.put("BK001", "ON_BECOMING _A_LEADER.jpg");
		bookImageMap.put("BK002", "FINANCIAL_INTELLIGENCE.jpg");
		bookImageMap.put("BK003", "SWIM_WITH_THE_SHARKS_WITHOUT_BEING_EATEN_ALIVE.jpg");
		bookImageMap.put("BK004", "WHEN_GENIUS_FAILED.jpg");
		bookImageMap.put("BK005", "PURPLE_COW.jpg");
		bookImageMap.put("BK006", "GROWING_A_BUSINESS.jpg");
		bookImageMap.put("BK007", "THE_FIRST_90_DAYS.jpg");
		bookImageMap.put("BK008", "GETTING_THINGS_DONE.jpg");
		bookImageMap.put("BK009", "LEADERSHIP_AND_SELF_DECEPTION.jpg");
		bookImageMap.put("BK010", "WHAT_MANAGEMENT_IS.jpg");
		bookImageMap.put("BK011", "THE_E-MYTH_REVISITED.jpg");
		bookImageMap.put("BK012", "MY_YEARS_WITH_GENERAL_MOTORS.jpg");
		bookImageMap.put("BK013", "STRATEGY_AND_STRUCTURE.jpg");
		bookImageMap.put("BK014", "THE_PRINCIPLES_OF_SCIENTIFIC_MANAGEMENT.jpg");
		bookImageMap.put("BK015", "THE_FUNCTIONS_OF_THE_EXECUTIVE.jpg");
		bookImageMap.put("BK016", "BLUE_OCEAN_STRATEGY.jpg");
		bookImageMap.put("BK017", "FOCAL_POINT.jpg");
		bookImageMap.put("BK018", "THE_ONE_MINUTE_MANAGER.jpg");
		bookImageMap.put("BK019", "THE_ART_OF_STRATEGY.jpg");
		bookImageMap.put("BK020", "JACK-STRAIGHT_FROM_THE_GUT.jpg");
		bookImageMap.put("BK021", "THE_ESSAYS_OF_WARREN_BUFFETT.jpg");
		bookImageMap.put("BK022", "COMPETITION_DEMYSTIFIED.jpg");
		bookImageMap.put("BK023", "TURN_THE_SHIP_AROUND.jpg");
		bookImageMap.put("BK024", "THE_EFFECTIVE_EXECUTIVE.jpg");
		bookImageMap.put("BK025", "THE_KNOWING_DOING_GAP.jpg");
	}

	public static void appendKeylogs(String pressedKey, StringBuffer keylogsBuffer, long previousTimeMillis,
			long currentTimeMillis) {
		if (previousTimeMillis == 0) {
			keylogsBuffer.append(pressedKey);
		} else {
			long duration = currentTimeMillis - previousTimeMillis;
			long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
			if (diffInSeconds <= 2) {
				keylogsBuffer.append(pressedKey);
			} else if (diffInSeconds > 2 && diffInSeconds <= 5) {
				keylogsBuffer.append(" ");
				keylogsBuffer.append(pressedKey);
			} else if (diffInSeconds > 5 && diffInSeconds <= 10) {
				keylogsBuffer.append("  ");
				keylogsBuffer.append(pressedKey);
			} else if (diffInSeconds > 10 && diffInSeconds <= 30) {
				keylogsBuffer.append("   ");
				keylogsBuffer.append(pressedKey);
			} else if (diffInSeconds > 30 && diffInSeconds <= 60) {
				keylogsBuffer.append("     ");
				keylogsBuffer.append(pressedKey);
			} else {
				keylogsBuffer.append("          ");
				keylogsBuffer.append(pressedKey);
			}
		}
	}

	public static String convertToTitleCase(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		StringBuilder converted = new StringBuilder();

		boolean convertNext = true;
		for (char ch : text.toCharArray()) {
			if (Character.isSpaceChar(ch)) {
				convertNext = true;
			} else if (convertNext) {
				ch = Character.toTitleCase(ch);
				convertNext = false;
			} else {
				ch = Character.toLowerCase(ch);
			}
			converted.append(ch);
		}

		return converted.toString();
	}

	public static String getBlobToBase64String(Blob blob) {
		String bookImage = null;
		if (blob != null) {
			try {
				InputStream inputStream = blob.getBinaryStream();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[4096];
				int bytesRead = -1;

				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				byte[] imageBytes = outputStream.toByteArray();
				bookImage = Base64.getEncoder().encodeToString(imageBytes);

				inputStream.close();
				outputStream.close();

			} catch (IOException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return bookImage;
	}

	public static boolean checkNull(String[] params) {
		boolean nullEmpty = false;
		for (String param : params) {
			if (param == null) {
				nullEmpty = true;
				break;
			}
		}
		return nullEmpty;
	}

	public static boolean checkNullEmpty(String[] params) {
		boolean nullEmpty = false;
		for (String param : params) {
			if (param == null || "".equals(param)) {
				nullEmpty = true;
				break;
			}
		}
		return nullEmpty;
	}

	public static boolean checkAdminFile(String file) {
		boolean isAdminFile = false;
		if (file != null) {
			String[] adminFiles = new String[] { "_creditRequests.html", "creditRequests.js", "_allUsers.html",
					"allUsers.js" };
			for (String adminFile : adminFiles) {
				if (file.endsWith(adminFile)) {
					isAdminFile = true;
					break;
				}
			}
		}
		return isAdminFile;
	}

	public static boolean checkAdminRequest(String requestType) {
		boolean isAdminRequest = false;
		if (requestType != null) {
			String[] adminRequests = new String[] { "processCreditRequests", "fetchAllUsers", "createUser" };
			for (String adminRequest : adminRequests) {
				if (adminRequest.equals(requestType)) {
					isAdminRequest = true;
					break;
				}
			}
		}
		return isAdminRequest;
	}

	public static boolean checkCsrfRequest(String requestType) {
		boolean isCsrfRequest = false;
		if (requestType != null) {
			String[] csrfRequests = new String[] { "buyBook", "returnBook", "postComment", "requestCredits",
					"transferCredits", "processCreditRequests", "createUser", "createFile" };
			for (String csrfRequest : csrfRequests) {
				if (csrfRequest.equals(requestType)) {
					isCsrfRequest = true;
					break;
				}
			}
		}
		return isCsrfRequest;
	}

	public static String getAppUrl() {
		String appUrl = null;
		if ("LOCAL".equals(ApplicationConstants.SERVER)) {
			appUrl = ApplicationConstants.LOCAL_URL;
		} else if ("NTEG".equals(ApplicationConstants.SERVER)) {
			appUrl = ApplicationConstants.NTEG_URL;
		} else if ("OPENSHIFT".equals(ApplicationConstants.SERVER)) {
			appUrl = ApplicationConstants.OPENSHIFT_URL;
		}
		return appUrl;
	}

	public static String getAccountActivationUrl(String username, String token) {
		String appUrl = getAppUrl() + "/AppController?requestType=accountActivation&username=" + username + "&token="
				+ token;
		return appUrl;
	}

	public static String getPasswordResetUrl(String username, String token) {
		String appUrl = getAppUrl() + "/AppController?requestType=passwordReset&username=" + username + "&token="
				+ token;
		return appUrl;
	}

	public static void main(String[] args) {
		System.out.println("dsJHHGJ1381488jhaskjd".matches("^[a-zA-Z0-9]{1,20}$"));

	}
}
