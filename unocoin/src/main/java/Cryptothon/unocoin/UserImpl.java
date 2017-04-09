package Cryptothon.unocoin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class UserImpl {

	final static Logger logger = Logger.getLogger(UserImpl.class);
	
	private String encodeFile(String filepath) throws IOException {
		File file = new File(filepath);
		FileInputStream imageInFile = new FileInputStream(file);
        byte imageData[] = new byte[(int) file.length()];
        imageInFile.read(imageData);
        String imageDataString = Base64.encodeBase64URLSafeString(imageData);
        return imageDataString;
	}

	public boolean addBankAccount(String token) throws UnirestException {
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Authorization", "Bearer "+token);
		HashMap<String,Object> fields = new HashMap<String, Object>();
		fields.put("accountnum","1234567");
		fields.put("ifsc","vysa0002290");
		fields.put("nickname","JohnDoe");
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/api/v1/settings/addaccount")
				.headers(headers)
				.fields(fields)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		logger.info("UserImpl -> "+ jsonobject.get("message"));
		return true;
	}
	public boolean updateUserProfile(String token) throws UnirestException, IOException {
		//String imageEncoded = encodeFile("C:\\Users\\I334282\\Desktop\\unocoin\\workspace\\unocoin\\src\\main\\java\\Cryptothon\\unocoin\\images.png");
		String imageEncoded = encodeFile("src/main/resources/images.png");
		imageEncoded="iVBORw0KGgoAAAANSUhEUgAAALUAAAByCAMAAAAiYGCXAAAAMFBMVEXEzN/////Q1+a/yN3g5O7r7vTJ0eLY3ur4+frDyt/09fn///3m6fH8/P3N1OPU2uih63SYAAADH0lEQVR4nO2bC5KsIAxF0SCC+Nn/bge0u0erPyQjLdf3PAuYOpWJIYS0UhcXF/8RrrSAEEcBU4+lPfgE3VE3toroU4Tbkav7m3GULu3DIBjrh3BgaKm0UoqQCrrzVeV/rTt0aUd1s/JdsAZbm8ZmE+UF79vSYh9wqn8yvieJQg23m+yLQC/RrmyNqU36XaCXQjKVFnwF9Z+c4/9A40Wb9Efp+Zvs0bSpTUlH0KJtONLeg2k3HOuAKS26pmZKh7qNQ6J+rAE6JYktjdRJmTdH4jO+Ku36wLHTOljDfI//vDVQ7RNZ1yj33nNaK1YTgmbNa50u6xyc09qd0jp9j7msc3FZH8dlfRzntHantJbFGuTiaDR3GhIBGVKKIl1VA8ZIRGhdDRh3MEnvFK1L+864SSTtLcTnSKe0duMg0gaZmRmZdYNhTZY75ZsBeVMnm1ZdgXLM8KfXEZRdkVGSIRhHY6QTWMM8KDnFz2yMaj3Dz2xfl3ZdwT4eO4zWacFwUwTkiLnB/R6hNqDolG+73M8RqIIEHOv1v6r60qJbmL0ISA9yhzcSwaogkY7RjEwoE5w74R6W1Aa5xaxxqTLiQUYKW5LVD6bb25CqfijXgS3jGa1T73ce0jo57xvR6l6E3i4EI2dIsoYgFj5FqR4b0zrVQEFaJy/qBvFrTI5Wka7nN6Yu2WHbDi7YKeUZsLVx5jYOzoxvgXfbhZrhsCcLA9bxyJ09QbXY/Od/JGv+y0yLU/wEL44wswVSmj90HxpD5cVJTZI1i4jVY1nvEGbJk8wj4N1U7sfeNKZuL+/wIeBFfl8aUuMvYV4FvKmP/qUmUUiNPwb6l6490puM7DX3A31Nx2Q4jc0Qfz+XyTuUwq97k2r3pfML7LcThdrO78/nDfGvWf097xBn2U6FxL3/ztETnfNGeUs46/MfPSE3MqfGE77JffLU2b/BV8R4Z4NUk63QJfB9rjRxrWyJbB9DnqVh6r0/KtQRm2XOLe2fd+JzDASFC2RZ2D/sEa4p52HvZI29ZJMRv3vUXSTUoRHcldnCZc5c7Hy8pgMr3traJxZJfgCXQiJ/NmSFzQAAAABJRU5ErkJggg==";
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Authorization", "Bearer "+token);
		HashMap<String,Object> fields = new HashMap<String, Object>();
		fields.put("name","JohnDoe");
		fields.put("mobile","9999999999");
		fields.put("pannumber","BAD876G567");
		fields.put("address","Abc #1024 6th cross Bangalore");
		fields.put("state","Karnataka");
		fields.put("city","Bangalore");
		fields.put("pincode","560010");
		fields.put("bank_accnum","1234567");
		fields.put("ifsc","vysa0002290");
		fields.put("pancard_photo",imageEncoded);
		fields.put("photo",imageEncoded);
		fields.put("address_proof",imageEncoded);
		fields.put("id_proof",imageEncoded);
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/api/v1/settings/uploaduserprofile")
				.headers(headers)
				.fields(fields)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		logger.info("UserImpl -> "+ jsonobject.get("message"));
		return true;
	}
}
