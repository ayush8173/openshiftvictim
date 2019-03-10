package com.eh.openshiftvictim.utility;

import com.google.gson.Gson;

public class JsonResponse {

	private String status;
	private Object data;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getJsonResponseString(JsonResponse jsonResponse) {
		String responseString = new Gson().toJson(jsonResponse);
		return responseString;
	}
}
