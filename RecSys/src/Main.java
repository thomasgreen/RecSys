import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import FriendsPOJO.GetFriends;
import UserPOJO.UserInfo;
import artistsPOJO.GetTopArtists;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	static String API_KEY = "78cc84967dc3a5fb577941e75bf7f8a9";
	static Gson gson = new Gson();

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

		try { // try if user has friends

			int friendsTotal = getfriends.getFriends().getUser().size();

			// saving usernames to files

			for (int i = 0; i < friendsTotal; i++) {
				BufferedWriter writer = new BufferedWriter(new FileWriter("rsc/usernames.txt", true));
				BufferedReader freader = new BufferedReader(new FileReader("rsc/usernames.txt"));
				String userToAdd = getfriends.getFriends().getUser().get(i).getName();

				boolean hasDuplicate = false;
				String line;

				while ((line = freader.readLine()) != null && !hasDuplicate) {

					if (line.equals(userToAdd)) {
						hasDuplicate = true;
					}

				}

				if (!hasDuplicate) {
					writer.newLine();
					System.out.println(userToAdd);
					writer.write(userToAdd);
					writer.flush();
					writer.close();

				}
				freader.close();

			}

		} catch (NullPointerException e) { // if no friends are found
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
		

		File file = new File("rsc/topartists.json");

		if (file.exists()) {// if there is already 1 element in the file
			Scanner sc = new Scanner(new File("rsc/topartists.json"));

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

	public static void jsontest() {

		Gson gson = new Gson();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File("rsc/jsontest7.json")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("That File Does Not Exist");
			e.printStackTrace();
		}

		// TopArtistsList getartists = gson.fromJson(reader,
		// TopArtistsList.class);

		List<GetTopArtists> logs = null;

		logs = gson.fromJson(reader, new TypeToken<List<GetTopArtists>>() {
		}.getType());

		String result = logs.get(0).getTopartists().getArtist().get(0).getName();

		// System.out.println(getartists.GetTopArtistsSchema().get(0).getTopartists().getArtist().get(0).getName());

		System.out.println(result);

	}

	public static List<String> getUsersFriendsFromFile() throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader("rsc/usernames.txt"));

		List<String> usernamesOnFile = new ArrayList<String>();
		String currentline;

		while ((currentline = reader.readLine()) != null) {
			usernamesOnFile.add(currentline);

		}

		reader.close();

		return usernamesOnFile;

	}

	public static void main(String[] args) throws IOException {

		while (true) {

			// getUserInfo(username);

			int usersonfileno = getUsersFriendsFromFile().size();
			for (int i = 0; i < usersonfileno; i++) {
				getUserTopArtists(getUsersFriendsFromFile().get(i));
			}

		}

	}

	private static void getUserID() {
		Scanner in = new Scanner(System.in);

		System.out.println("Please Enter Your Last.FM Username: ");

		String username = in.nextLine();

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
