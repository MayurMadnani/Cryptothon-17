package Cryptothon.unocoin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URLEncoder;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class App  extends Frame implements ActionListener {
	final static Logger logger = Logger.getLogger(App.class);

	//TODO: use API token and sessionid from API.AI
	final static String API_AI_TOKEN = "";
	final static String sessionID = "";
	final static String baseUrl = "https://api.api.ai/v1/";
	static String query;
	static String token;
	static JFrame f;
	static JTextField text;
	static JButton btn;
	static JLabel label;

	TokenFactory auth = new TokenFactory();
	PaymentGateway bitBank = new PaymentGateway();
	UserImpl user = new UserImpl();

	public App() {
		f=new JFrame("CryptoAssistant");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		f.setBounds(dim.width/2, dim.height/2,400,400);
		text = new JTextField();
		text.setBounds(50,50, 300,50);
		f.add(text);
		btn=new JButton("click");  
		btn.setBounds(150,150,100,50); 
		btn.addActionListener(this); 
		f.add(btn);  
		label = new JLabel();
		label.setForeground(Color.RED);
		label.setText("<html>"
				+ "Hello, I am CryptoAssistant. How may I help you"
				+ "</html>");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(50,200, 300,50);
		f.add(label);
		f.setSize(400,400);  
		f.setLayout(null);  
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main( String[] args ) throws UnirestException, IOException {
		new App();
	}

	public void actionPerformed(ActionEvent e) {  
		query=text.getText().toString();
		try {
			label.setText("<html>"
					+ beginPrediction()
					+ "</html>");
			text.setText("");
			text.grabFocus();
		} catch (UnirestException | IOException e1) {
			e1.printStackTrace();
		}
	}  

	public String beginPrediction() throws UnirestException, IOException {	
		//user.addBankAccount(token);
		//user.updateUserProfile(token);
		String labeltext = null;
		query = URLEncoder.encode(query, "UTF-8");
		String url = baseUrl+"query?v=20150910&query="+query+"&lang=en&sessionId="+sessionID;
		System.out.println(url);
		HttpResponse<JsonNode> jsonResponse = Unirest.get(url)
				.header("Authorization","Bearer "+API_AI_TOKEN)
				.asJson();
		JSONObject jsonobject= jsonResponse.getBody().getObject();
		String action = jsonobject.getJSONObject("result").getString("action");
		String value = null;
		String amount = null;
		switch(action) {
		case "query":
			try {
				value = jsonobject.getJSONObject("result").getJSONObject("parameters").getString("address").toString();
			}
			catch (Exception e) {}
			if(value.equals("address")) {
				labeltext = bitBank.getAddress(token);
				break;
			}
			try {
				value = jsonobject.getJSONObject("result").getJSONObject("parameters").getString("balance").toString();
			}
			catch (Exception e) {}
			if(value.equals("balance")) {
				labeltext = bitBank.checkBalance(token);
				break;
			}
			else {
				//bitBank.getLimits(token);
				JSONObject output= bitBank.getPrices(token);
				labeltext = output.getString("buybtc");
			}		
			break;
		case "auth":
			value = jsonobject.getJSONObject("result").getJSONObject("parameters").getString("auth");
			if(value.equals("login")) {
				token = auth.authorize();
				labeltext = "Logged In";
			}
			else if(value.equals("logout")) {
				auth.doSignOut(token);
				labeltext = "Logged out";
			}
			break;
		case "transact_btc":
			value = jsonobject.getJSONObject("result").getJSONObject("parameters").getString("transcations");
			if(value.equals("buy")) {
				try {
					amount = jsonobject.getJSONObject("result").getJSONObject("parameters").getJSONObject("unit-currency").get("amount").toString();
				}
				catch(Exception e) {
				}
				if(amount==null)
					amount = jsonobject.getJSONObject("result").getJSONObject("parameters").get("number").toString();
				labeltext = bitBank.buyBTC(token, amount);
			}
			else if(value.equals("sell")) {
				amount = jsonobject.getJSONObject("result").getJSONObject("parameters").get("number").toString();
				labeltext = bitBank.sellBTC(token, amount);
			}
			break;
		case "transact_inr":
			value = jsonobject.getJSONObject("result").getJSONObject("parameters").getString("transcations");
			if(value.equals("withdraw")) {
				amount = jsonobject.getJSONObject("result").getJSONObject("parameters").getJSONObject("unit-currency").get("amount").toString();
				labeltext = bitBank.withdrawINR(token, amount);
			}
			else if(value.equals("deposit")) {
				amount = jsonobject.getJSONObject("result").getJSONObject("parameters").getJSONObject("unit-currency").get("amount").toString();
				labeltext = bitBank.depositINR(token, amount);
			}
			break;
		default:
			labeltext="I did not understand what you said";
			break;
		}
		return labeltext;
	}
}
