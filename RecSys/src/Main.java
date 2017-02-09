import java.io.BufferedReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import FriendsPOJO.Friends;
import FriendsPOJO.GetFriends;
import UserPOJO.User;
import UserPOJO.UserInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.json.*;

public class Main {

	static String API_KEY = "78cc84967dc3a5fb577941e75bf7f8a9";
	static Gson gson = new Gson();
	private static String username;
	
	
	public static void getUserInfo(String username) throws IOException {
		
		
		final URL reqURL = new URL("http://ws.audioscrobbler.com/2.0/?method=user.getinfo&" +
				"user=" + username + 
				"&api_key=" + API_KEY + 
				"&format=json");
		
		final InputStream inputstream = APISend(reqURL);
			
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
		
			
		UserInfo userinfo = gson.fromJson(reader, UserInfo.class);
		
		if(userinfo != null){
			
			System.out.println("User Name: " + userinfo.getUser().getName() + 
					"\nURL: " + userinfo.getUser().getUrl() +
					"\nPlay Count: " + userinfo.getUser().getPlaycount()
					);
		}
		
	}
	
	
	public static void getUserFriends(String username) throws IOException{
		
		
		final URL reqURL = new URL("http://ws.audioscrobbler.com/2.0/?method=user.getfriends&" +
				"user=" + username + 
				"&api_key=" + API_KEY + 
				"&format=json");
		
		final InputStream inputstream = APISend(reqURL);
			
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
		
		
		GetFriends getfriends = gson.fromJson(reader, GetFriends.class);
		
		
		
		
		System.out.println("\nList of Friends: ");
		
		
		//System.out.println(getfriends.getFriends().getUser().get(0).getName());
		
		
		
		int friendsTotal = getfriends.getFriends().getUser().size();
		
		for(int i=0; i<friendsTotal; i++){
			System.out.println(getfriends.getFriends().getUser().get(i).getName());
       }
		
	
		
		
	}

	
	public static void main(String [ ] args) throws IOException{
		getUserID();
		
		getUserInfo(username);
		
		getUserFriends(username);
		
	}

	private static void getUserID() {
		Scanner in = new Scanner(System.in);

		
		System.out.println("Please Enter Your Last.FM Username: ");
		
		
		username = in.nextLine();
		
		in.close();
		
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

}
