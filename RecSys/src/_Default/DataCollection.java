package _Default;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;

import FriendsPOJO.GetFriends;
import UserPOJO.UserInfo;
import artistsPOJO.GetTopArtists;
import tracksPOJO.GetTopTracks;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author thomasgreen Used for collecting the usernames and favourite artists
 *         of the users found. Archieve Code, Recommendations made in the more
 *         appropriate class
 */
public class DataCollection {

	static String API_KEY = "78cc84967dc3a5fb577941e75bf7f8a9";
	static Gson gson = new Gson();

	public static void main(String[] args) throws IOException {

		List<String> users = getUsersFromFile();

//		for (int i = 0; i < 1000; i++) {
//			getUserTopArtists(users.get(i));
//			System.out.println(i);
//		}
		
		for (int i = 0; i < 1000; i++) {
			getUserTopTracks(users.get(i));
			System.out.println(i);
		}

		
		System.out.println("DONE");
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

	public static void getUserTopArtists(String username) throws IOException {
		final URL reqURL = new URL("http://ws.audioscrobbler.com/2.0/?method=user.gettopartists&" + "user=" + username
				+ "&api_key=" + API_KEY + "&limit= 100 &format=json");

		final InputStream inputstream = APISend(reqURL);

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));

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

			currentdata = currentdata + "" + gson.toJson(getartists.getTopartists());

			currentdata += "]"; // close bracket

			fw.write("");

			fw.write(currentdata);
			fw.flush();
			fw.close();
			sc.close();

		} else { // if this is the first json added
			FileWriter fw = new FileWriter(file);

			String data = "[" + gson.toJson(getartists.getTopartists()) + "]";

			fw.write(data);
			fw.flush();
			fw.close();

		}

	}
	
	public static void getUserTopTracks(String username) throws IOException {
		final URL reqURL = new URL("http://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&" + "user=" + username
				+ "&api_key=" + API_KEY + "&limit= 100 &format=json");

		final InputStream inputstream = APISend(reqURL);

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));

		GetTopTracks gettracks = gson.fromJson(reader, GetTopTracks.class);

		reader.close();

		File file = new File("rsc/toptracks.json");

		if (file.exists()) {// if there is already 1 element in the file
			Scanner sc = new Scanner(new File("rsc/toptracks.json"));

			String currentdata = "";

			while (sc.hasNextLine()) {
				currentdata = sc.nextLine();
				System.out.println();

			}

			FileWriter fw = new FileWriter(file, false);

			currentdata = currentdata.substring(0, (currentdata.length() - 1));

			currentdata += ","; // append comma

			currentdata = currentdata + "" + gson.toJson(gettracks.getToptracks());

			currentdata += "]"; // close bracket

			fw.write("");

			fw.write(currentdata);
			fw.flush();
			fw.close();
			sc.close();

		} else { // if this is the first json added
			FileWriter fw = new FileWriter(file);

			String data = "[" + gson.toJson(gettracks.getToptracks()) + "]";

			fw.write(data);
			fw.flush();
			fw.close();

		}

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
