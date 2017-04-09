package Cryptothon.unocoin;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class PaymentGateway {

	final static Logger logger = Logger.getLogger(PaymentGateway.class);

	public JSONObject getPrices(String accessToken) throws UnirestException {
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Authorization", "Bearer "+accessToken);
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/api/v1/general/prices")
				.headers(headers)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		logger.info("Prices: Buy BTC -> "+ jsonobject.get("buybtc"));
		return jsonobject;

	}

	public void getLimits(String accessToken) throws UnirestException {
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Authorization", "Bearer "+accessToken);
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/api/v1/general/buy_sell_limits")
				.headers(headers)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		logger.info("Min Buy INR -> "+ jsonobject.get("min_buy_inr_amt"));
	}

	public String getAddress(String accessToken) throws UnirestException {
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Authorization", "Bearer "+accessToken);
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/api/v1/wallet/bitcoinaddress")
				.headers(headers)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		String address = jsonobject.get("bitcoinaddress").toString();
		logger.info("Address -> "+ address);
		return address;
	}

	public String checkBalance(String token) throws UnirestException {
		String address = getAddress(token);
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Authorization", "Bearer "+token);
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/api/v1/wallet/bitcoinaddress")
				.headers(headers)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		logger.info("BTC Balance -> "+ jsonobject.get("btc_balance"));
		logger.info("INR Balance -> "+ jsonobject.get("inr_balance"));
		return jsonobject.get("btc_balance").toString()+" :: "+jsonobject.get("inr_balance");
	}

	public String depositINR(String accessToken, String amount) throws UnirestException {
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Authorization", "Bearer "+accessToken);
		HashMap<String,Object> fields = new HashMap<String, Object>();
		fields.put("inr_amount", amount);
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/api/v1/wallet/inr_deposit")
				.headers(headers)
				.fields(fields)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		logger.info("Deposit INR -> "+ jsonobject.get("message"));
		return jsonobject.get("message").toString();
	}
	
	public String withdrawINR(String accessToken, String amount) throws UnirestException {
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Authorization", "Bearer "+accessToken);
		HashMap<String,Object> fields = new HashMap<String, Object>();
		fields.put("inr_amount", amount);
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/api/v1/wallet/inr_withdraw")
				.headers(headers)
				.fields(fields)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		logger.info("Withdraw INR -> "+ jsonobject.get("message"));
		return jsonobject.get("message").toString();
	}
	
	public String buyBTC(String accessToken,String amount) throws UnirestException {
		JSONObject priceObject = getPrices(accessToken);
		Float inr = Float.parseFloat(amount);
		Float fee = Float.parseFloat(priceObject.getString("buyfees"));
		fee = fee/100*inr;
		Float tax = Float.parseFloat(priceObject.getString("buytax"));
		tax =  (float) Math.ceil(tax/100*fee);
		Float rate = Float.parseFloat(priceObject.getString("buybtc"));
		Float total = inr+fee+tax;
		Float btc = inr/rate;
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Authorization", "Bearer "+accessToken);
		HashMap<String,Object> fields = new HashMap<String, Object>();
		fields.put("destination","My wallet");
		fields.put("inr",inr);
		fields.put("total",total);
		fields.put("btc",btc);
		fields.put("fee",fee);
		fields.put("tax",tax);
		fields.put("rate",rate);
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/api/v1/trading/buyingbtc")
				.headers(headers)
				.fields(fields)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		logger.info("Buy BTC -> "+ jsonobject.get("message"));
		return jsonobject.get("message").toString();
	}

	public String sellBTC(String accessToken,String amount) throws UnirestException {
		JSONObject priceObject = getPrices(accessToken);
		Float btc = Float.parseFloat(amount);
		Float fee = Float.parseFloat(priceObject.getString("sellfee"));
		Float tax = Float.parseFloat(priceObject.getString("selltax"));
		Float rate = Float.parseFloat(priceObject.getString("sellbtc"));
		Float inr = rate*btc;
		fee =  (float) Math.floor(fee/100*inr);
		tax =  (float) Math.floor(tax/100*fee);
		Float total = inr-fee-tax;
		HashMap<String, String> headers= new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Authorization", "Bearer "+accessToken);
		HashMap<String,Object> fields = new HashMap<String, Object>();
		fields.put("payment","inr_wallet");
		fields.put("inr",inr);
		fields.put("total",total);
		fields.put("btc",btc);
		fields.put("fee",fee);
		fields.put("tax",tax);
		fields.put("rate",rate);
		HttpResponse<JsonNode> jsonResponse = Unirest.post("https://sandbox.unocoin.co/api/v1/trading/sellingbtc")
				.headers(headers)
				.fields(fields)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		logger.info("Sell BTC -> "+ jsonobject.get("message"));
		return jsonobject.get("message").toString();
	}

}
