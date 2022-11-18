package nl.detoren.ijc.data.external.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nl.detoren.ijc.ui.util.Utils;

public class Plone52 {
	
	// method for retrieve homepage (Example, not actively used)
	public static String getrequest(String url) {
		String httpsbody = "";
		int code = 0;
		HttpClient httpClient = HttpClient.newHttpClient();
    
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https:/" + url))
				.build();

		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			code = response.statusCode();
			httpsbody += response.body();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Integer.toString(code);
		}	
		return httpsbody;
	}

	// method for logon and retrieving Token
	public static Token login (String url, String username, String password) throws Exception {
		Token token = null;
		Gson gson = new Gson();
		String httpsbody = "";
		JSONObject jsonOb1 = new JSONObject(); 
		jsonOb1.put("login", username);
		jsonOb1.put("password", password);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				  .uri(URI.create("https://" + url + "/@login"))
				  .header("Accept", "application/json")
				  .header("Content-Type", "application/json")
				  .POST(HttpRequest.BodyPublishers.ofString(jsonOb1.toString()))
				  .build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		httpsbody += response.body();
		token = gson.fromJson(httpsbody, Token.class);
		return token;
	}
	
	// 
    /**
     * method for creating a new page , with token for periode and ronde
     *
     * @param token - logon token for the external API 
     * @param periode - periode for page to be created
     * @param ronde - ronde for page to be created
     * @return String - OK or HTTP Status Code
     */
	public static String createpage(Token token, String url, String path, int periode, int ronde) throws URISyntaxException {

		Gson gson = new Gson();
		String httpsbody = "";
		int httpsresponse = 0;
		//String token = "";
		HttpClient httpClient = HttpClient.newHttpClient();
    
		// Charset charSet = Charset.forName("UTF-8");
		// String s = URLEncoder.encode(new SimpleDateFormat("dd MMMM yyyy").format(Calendar.getInstance().getTime()), charSet);
		String s = new SimpleDateFormat("dd MMMM yyyy").format(Calendar.getInstance().getTime());
		// temporary
		String documentTitle = "Clubavond " + s;
		String txtles = "Het was weer een leuke les met hoge opkomst.\nTrainer Lodewijk had weer een mooie les voorbereid.\n";
		String bestandsnaam = "R" + periode + "-" + ronde + "Uitslag.txt";
		String dirName = "R" + periode + "-" + ronde;

		String txtUitslag[] = Utils.leesBestand(dirName + File.separator + bestandsnaam);
		String txtUitslagparagraaf = "";
		for (String r : txtUitslag) {
			txtUitslagparagraaf += r + "\r\n";
		}

		bestandsnaam = "R" + periode + "-" + ronde + "Stand.txt";
		String txtStand[] = Utils.leesBestand(dirName + File.separator + bestandsnaam);
		String txtStandparagraaf = "";
		for (String r : txtStand) {
			txtStandparagraaf += r + "\n";
		}
		JSONObject jsonOb1 = new JSONObject(); 
		jsonOb1.put("@type", "Document");
		jsonOb1.put("title", documentTitle);
		jsonOb1.put("description", "Clubavond met uitslagen en stand");
		JSONObject jsonOb2 = new JSONObject(); 
		jsonOb2.put("content-type","text/html");
		jsonOb2.put("data","<h3>Les</h3>\n<p>" + txtles + "</p>\n<h3>IJC Uitslagen</h3>\n<pre>" + txtUitslagparagraaf + 
				"</pre>\n<h3>IJC Stand</h3>\n<pre>" + txtStandparagraaf + "</pre>\n");
		jsonOb2.put("encoding","utf-8");
		jsonOb1.put("text", jsonOb2);		
		//
		System.out.println("JSON : " + jsonOb1.toString(2));
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://" + url + path))
				.header("Accept", "application/json")
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token.getToken())
				.POST(HttpRequest.BodyPublishers.ofString(jsonOb1.toString()))
				.build();			  
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			httpsbody += response.body();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		/*
		 * if (httpsresponse / 100 != 2 ) {
		 * System.out.println("StatusCode :" + httpsresponse);
		 * System.out.println("URI :" + httpsbody); return
		 * Integer.toString(httpsresponse); }
		 */
		Map<String, Object> map = gson.fromJson(httpsbody, new TypeToken<Map<String, Object>>() {}.getType());
		Object id = map.get("id");
	        
		System.out.println("Document created with title : " + id.toString());
		httpsbody = "";
		request = HttpRequest.newBuilder()
				.uri(URI.create("https://" + url + path + "/" + id.toString() + "/@workflow/publish"))
				.header("Accept", "application/json")
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token.getToken())
				.POST(HttpRequest.BodyPublishers.ofString("{}"))
				.build();			  
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			httpsresponse = response.statusCode();
			httpsbody += response.body();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (httpsresponse / 100 != 2 ) {
			System.out.println("StatusCode :" + httpsresponse);
			System.out.println("URI :" + httpsbody);
			return Integer.toString(httpsresponse);
		} else {
			System.out.println("Document published!");
			return "OK";
		}
	}
	
	// Delete user from Plone52!!!
	public static int delete(Token token, String username) throws Exception {
		if (token == null) {	
			throw new NullPointerException();
		}
		int httpsresponse = 0;
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://www.svdestelling.nl/@users/" + username))
				.header("Accept", "application/json")
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token.getToken())
				.DELETE()
				.build();			  
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		httpsresponse = response.statusCode();
		return httpsresponse;
	}

	// Get userslist from Plone52!!!
	public static String userList(Token token) throws Exception {
		if (token == null) {	
			throw new NullPointerException();
		}
		String httpsbody = "";
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://www.svdestelling.nl/@users"))
				.header("Accept", "application/json")
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token.getToken())
				.GET()
				.build();			  
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		httpsbody = response.body();
		return httpsbody;
	}


}
