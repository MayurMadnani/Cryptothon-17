package Cryptothon.unocoin;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class TokenFactory {
	
	final static Logger logger = Logger.getLogger(TokenFactory.class);
	
	private String getAccessToken() throws UnirestException   {
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		HashMap<String,Object> fields = new HashMap<String, Object>();
		fields.put("grant_type", "client_credentials");
		fields.put("access_lifetime", "7200");
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/oauth/token")
				.basicAuth("ZAJULYRLJ8", "be9c7869-7551-4c4d-8f93-56699bf0ef0a")
				.headers(headers)
				.fields(fields)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		String token= jsonobject.get("access_token").toString();
		logger.info("Access token -> "+ token);
		return token;
	}

	private void doSignUp(String accessToken) throws UnirestException {
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Authorization", "Bearer "+accessToken);
		HashMap<String,Object> fields = new HashMap<String, Object>();
		fields.put("email_id", "team95@gmail.com");
		fields.put("password", "i5nd2s2C");
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/api/v1/authentication/signup")
				.headers(headers)
				.fields(fields)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		logger.info("SignUp -> "+ jsonobject.get("message"));
	}

	private String doSignIn(String accessToken) throws UnirestException {
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Authorization", "Bearer "+accessToken);
		HashMap<String,Object> fields = new HashMap<String, Object>();
		fields.put("email_id","team95@gmail.com");
		fields.put("signinpassword","i5nd2s2C");
		fields.put("response_type","code");
		fields.put("client_id","ZAJULYRLJ8");
		fields.put("redirect_uri","https://sandbox.unocoin.co");
		fields.put("scope","all");
		fields.put("signinsecpwd","999999");
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/api/v1/authentication/signin")
				.headers(headers)
				.fields(fields)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		String code= jsonobject.get("code").toString();
		logger.info("SignIn -> "+ code);
		return code;
	}

	private String getAuthorizedAccessToken(String code) throws UnirestException {
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		HashMap<String,Object> fields = new HashMap<String, Object>();
		fields.put("grant_type", "authorization_code");
		fields.put("code",code);
		fields.put("redirect_uri","https://sandbox.unocoin.co");
		fields.put("access_lifetime","7200");
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/oauth/token")
				.basicAuth("ZAJULYRLJ8", "be9c7869-7551-4c4d-8f93-56699bf0ef0a")
				.headers(headers)
				.fields(fields)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		String token= jsonobject.get("access_token").toString();
		logger.info("Authorized Access token -> "+ token);
		return token;
	}

	public void doSignOut(String accessToken) throws UnirestException {
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Authorization", "Bearer "+ accessToken);
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/api/v1/general/token_expiry")
				.headers(headers)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		String message= jsonobject.get("message").toString();
		boolean expired= message.equals("sucessfully")?true:false;
		logger.info("Signed out -> "+ message);
	}
	
	public String authorize() throws UnirestException {
		String AccessToken =  getAccessToken();
		doSignUp(AccessToken);
		String CODE = doSignIn(AccessToken);
		String AuthorizedAccessToken =  getAuthorizedAccessToken(CODE);
		return AuthorizedAccessToken;
	}

}
