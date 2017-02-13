import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;
import FriendsPOJO.GetFriends;
import UserPOJO.UserInfo;
import artistsPOJO.GetTopArtists;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Main {

	static String API_KEY = "78cc84967dc3a5fb577941e75bf7f8a9";
	static Gson gson = new Gson();
	private static String username;

	public static void getUserInfo(String username) throws IOException {

		final URL reqURL = new URL("http://ws.audioscrobbler.com/2.0/?method=user.getinfo&" + "user=" + username
				+ "&api_key=" + API_KEY + "&format=json");

		final InputStream inputstream = APISend(reqURL);

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));

		UserInfo userinfo = gson.fromJson(reader, UserInfo.class);

		if (userinfo != null) {

			System.out.println("User Name: " + userinfo.getUser().getName() + "\nURL: " + userinfo.getUser().getUrl()
					+ "\nPlay Count: " + userinfo.getUser().getPlaycount());
		}

	}

	public static void getUserFriends(String username) throws IOException {

		final URL reqURL = new URL("http://ws.audioscrobbler.com/2.0/?method=user.getfriends&" + "user=" + username
				+ "&api_key=" + API_KEY + "&format=json");

		final InputStream inputstream = APISend(reqURL);

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));

		GetFriends getfriends = gson.fromJson(reader, GetFriends.class);

		// if(getfriends.getFriends() == null) throw new
		// IllegalArgumentException();

		System.out.println("\nList of Friends: ");

		try {
			int friendsTotal = getfriends.getFriends().getUser().size();

			// saving usernames to files

			BufferedWriter writer = new BufferedWriter(new FileWriter("rsc/test1.txt", true));

			for (int i = 0; i < friendsTotal; i++) {
				String userToAdd = getfriends.getFriends().getUser().get(i).getName();

				Scanner filescanner = new Scanner("rsc.test1.txt");

				while (filescanner.hasNextLine()) {
					if (userToAdd.equals(filescanner.nextLine().trim())) {
						// found
						break;
					} else {
						// not found
						writer.write(getfriends.getFriends().getUser().get(i).getName());
						writer.newLine();

					}

				}

				// writer.write(getfriends.getFriends().getUser().get(i).getName());
				// writer.newLine();

				filescanner.close();

			}

			writer.flush();
			writer.close();

		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			System.out.println("This user does not follow any accounts.");
			// e.printStackTrace();
		}

	}

	public static void getUserTopArtists(String username) throws IOException {
		final URL reqURL = new URL("http://ws.audioscrobbler.com/2.0/?method=user.gettopartists&" + "user=" + username
				+ "&api_key=" + API_KEY + "&limit= 100 &format=json");

		final InputStream inputstream = APISend(reqURL);

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));

		// String test = reader.readLine();

		GetTopArtists getartists = gson.fromJson(reader, GetTopArtists.class);

		reader.close();
		for (int i = 0; i < 50; i++) {

			System.out.println(getartists.getTopartists().getArtist().get(i).getName());
		}

		File file = new File("rsc/jsontest6.json");

		if (file.exists()) {// if there is already 1 element in the file
			Scanner sc = new Scanner(new File("rsc/jsontest6.json"));

			String currentdata = "";

			while (sc.hasNextLine()) {
				currentdata = sc.nextLine();
				System.out.println();

			}

			FileWriter fw = new FileWriter(file, false);

			currentdata = currentdata.substring(0, (currentdata.length() - 1));

			currentdata += ","; // append comma

			currentdata += gson.toJson(getartists.getTopartists());

			currentdata += "]"; // close bracket

			fw.write("");

			fw.write(currentdata);
			fw.flush();
			fw.close();
			sc.close();

		} else { // if this is the first json added
			FileWriter fw = new FileWriter(file);
			// start bracket
			// add json
			// close bracket
			String data = "[" + gson.toJson(getartists.getTopartists()) + "]";

			fw.write(data);
			fw.flush();
			fw.close();

		}

	}
	
	public static void jsontest(){
		
		Gson gson = new Gson();

		
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File("rsc/jsontest6.json")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("That File Does Not Exist");
			e.printStackTrace();
		}
		
		
		//TopArtistsList getartists = gson.fromJson(reader, TopArtistsList.class);
		
		
		
		//System.out.println(getartists.GetTopArtistsSchema().get(0).getTopartists().getArtist().get(0).getName());
		
		
	}

	public static void main(String[] args) throws IOException {
		while (true) {

			getUserID();

			getUserInfo(username);

			 getUserFriends(username);
			getUserTopArtists(username);
			
			//jsontest();
		}

	}

	private static void getUserID() {
		Scanner in = new Scanner(System.in);

		System.out.println("Please Enter Your Last.FM Username: ");

		username = in.nextLine();

		// in.close();

	}

	private static InputStream APISend(URL url) throws IOException {

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
