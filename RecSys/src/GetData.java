import java.io.BufferedReader;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class GetData {

	static String API_KEY = "78cc84967dc3a5fb577941e75bf7f8a9";
	static Gson gson = new Gson();
	
	public static void getUserInfo() throws IOException {
		
		
		final URL reqURL = new URL("http://ws.audioscrobbler.com/2.0/?method=user.getinfo&user=Bigham96&api_key=" + API_KEY + "&format=json");
		
		final InputStream inputstream = APISend(reqURL);
			
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
		
		//String firstline = reader.readLine();
		
		//System.out.println(firstline);
		
		UserInfo userinfo = gson.fromJson(reader, UserInfo.class);
		
		if(userinfo != null){
			
			System.out.println("User Name: " + userinfo.getUser().getName() + "\nURL: " + userinfo.getUser().getUrl());
		}
		
	}
	
	private static InputStream APISend(URL url) throws IOException{

		final URLConnection urlConnection = url.openConnection();
		urlConnection.setDoOutput(true);
		urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		urlConnection.connect();
		final OutputStream outputStream = urlConnection.getOutputStream();
		outputStream.flush();
		final InputStream inputstream = urlConnection.getInputStream();
		
		return inputstream;
		
	}
	
	public static void getUserTopTracks() throws IOException {
		
		URL reqURL = new URL("http://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&user=Bigham96&api_key=" + API_KEY + "&format=json");
		
		final InputStream inputstream = APISend(reqURL);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
		
		String firstline = reader.readLine();
		
		System.out.println(firstline);
		
	}

	
	
	
	

	public static void main(String [ ] args) throws IOException{
		getUserInfo();
		
		
	}

}
