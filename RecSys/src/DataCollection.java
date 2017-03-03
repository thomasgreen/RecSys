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
import artistsPOJO.Topartists;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.lang.reflect.Type;

/**
 * @author thomasgreen Used for collecting the usernames and favourite artists
 *         of the users found. Archieve Code, Recommendations made in the more
 *         appropriate class
 */
public class DataCollection {

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
			System.out.println("This user does not follow any accounts.");
			// e.printStackTrace();
		}

	}

	public static void getUserTopArtists(String username, int no) throws IOException {
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

			currentdata = currentdata.substring(0, (currentdata.length() - 2));

			currentdata += ","; // append comma

			currentdata = currentdata + "" + gson.toJson(getartists.getTopartists());

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
			String data = "[" + gson.toJson(getartists.getTopartists()) + " ]";

			fw.write(data);
			fw.flush();
			fw.close();

		}

	}

	public static void ratingtest() {

		Gson gson = new Gson();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File("rsc/topartists.json")));
		} catch (FileNotFoundException e) {
			System.out.println("That File Does Not Exist");
			e.printStackTrace();
		}

		Type collectionType = new TypeToken<List<Topartists>>() {
		}.getType();
		List<Topartists> tal = gson.fromJson(reader, collectionType);

		String topsong = tal.get(1).getArtist().get(0).getName();
		String user = tal.get(1).getAttr().getUser();
		System.out.println(user + " " + topsong);

		long rank = Integer.parseInt(tal.get(0).getArtist().get(56).getAttr().getRank());

		long total = tal.get(0).getArtist().size();

		float rating = rank / total;

		long finalrating = (long) (5 * (1 - rating)); // rating of the user

		System.out.println(finalrating);

	}

	public static List<String> getUsersFromFile() throws IOException {

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

	}

	// public static void generateRating

	@SuppressWarnings("unused")
	private static String getUserID() {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);

		System.out.println("Please Enter Your Last.FM Username: ");

		return in.nextLine();

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
