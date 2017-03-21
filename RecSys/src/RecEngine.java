import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import artistsPOJO.Artist;
import artistsPOJO.Topartists;
import static java.lang.Math.sqrt;

/**
 * 
 */

/**
 * @author thomasgreen
 *
 */
public class RecEngine {

	private List<Topartists> tal;

	private Topartists activeUser;
	
	private int topN;

	private int neighbours;
	public RecEngine(int input) {
		// constructor for Rec Engine
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
		tal = gson.fromJson(reader, collectionType);

		activeUser = tal.get(0);
		topN = 10;
		neighbours = input;
	}

	public static void main(String[] args) throws IOException {
		RecEngine engine = new RecEngine(15);

		/*
		 * for (int i = 0; i < nearest.size(); i++) {
		 * System.out.println(nearest.get(i).getKey() + " - " +
		 * nearest.get(i).getValue()); }
		 */

		// calcualte things for rating prediction algortihm

		int active = 0;

		int test = 400;
		double[] precisionarray = new double[test];
		while (active < test) {

			engine.setActiveUser(engine.getTal().get(active));

			Map<String, Integer> testmap = new HashMap<String, Integer>();
			for (Topartists topartists : engine.getTal()) {
				int sim = engine.similarity(engine.getActiveUser(), topartists);

				if (engine.totalPlays(topartists) > 10000)
				{
					if(!(topartists.getAttr().getUser().equals(engine.getActiveUser().getAttr().getUser())))
					{
						testmap.put(topartists.getAttr().getUser(), sim);
					}
					
				}
					
			}
			
			
			
			// SORT
			List<Entry<String, Integer>> sorted = engine.sortMapByValues(testmap);
			// get closest neighbours
			List<Entry<String, Integer>> nearest = new LinkedList<Entry<String, Integer>>(sorted.subList(0, engine.neighbours));
			Map<String, Double> pearsons = new HashMap<String, Double>();
			for (int i = 0; i < nearest.size(); i++) {
				if (nearest.get(i).getValue() > 0) {
					pearsons.put(nearest.get(i).getKey(), engine.pearson(nearest.get(i)));

				}

			}
			for (Entry<String, Double> entry : pearsons.entrySet()) {
				System.out.println(entry.getKey() + " " + entry.getValue());
			}
			List<Artist> nearestArtists = new ArrayList<Artist>();
			/**
			 * get a list of artists to generate ratings for
			 */
			for (Entry<String, Integer> nearestIter : nearest) // for each
																// nearest
																// neighbour map
			{
				/**
				 * get the users place in the array of topartists get its top
				 * artists and add them to the array if the artist with same
				 * name is not already there
				 */

				int userentry = engine.getuserentryfromusername(nearestIter.getKey());

				for (Artist artist : engine.getTal().get(userentry).getArtist()) {
					// for each artist in the users top 100 add it to the list
					// of
					// nearest artists if not already in
					if (engine.checkartistunique(artist, nearestArtists)) {
						// is not in the list already, add it
						nearestArtists.add(artist);
					}
				}
			}
			Map<Artist, Integer> reclonglist = new HashMap<Artist, Integer>(); // hashmap
																				// to
																				// hold
																				// artists
																				// and
																				// their
																				// predictive
																				// rating
			for (Artist artist : nearestArtists) {
				int rating = engine.predictedrating(pearsons, artist);

				reclonglist.put(artist, rating);
			}
			Map<Artist, Integer> sortedrec = new TreeMap<Artist, Integer>();
			sortedrec = engine.sortByValue(reclonglist);
			System.out.println();
			System.out.println("Recommendations for: " + engine.getActiveUser().getAttr().getUser());
			for (Entry<Artist, Integer> entry : sortedrec.entrySet()) {
				System.out.println(
						"Aritst: " + entry.getKey().getName() + "\t \t \t Predicted Rating: " + entry.getValue());

			}
			
			double correct = 0;
			
			
			for (Artist recartist : sortedrec.keySet())
			{
				for(Artist activeuser: engine.getTal().get(active).getArtist())
				{
					if(recartist.getName().equals(activeuser.getName()))
					{
						correct++;
					}
				}
			}
			
			
			double precision = correct / engine.topN;
			
			
			
			precisionarray[active] = precision;
			active++;
		}

		double precisiontotal = 0;
		for (int i = 0; i < test; i++) {
			precisiontotal += precisionarray[i];
		}
		
		

		double precisionavg = precisiontotal/test;
		
	

		System.out.println("avg precision: " + precisionavg);
		
	}
	
	public double run() {
		//RecEngine engine = new RecEngine();

		/*
		 * for (int i = 0; i < nearest.size(); i++) {
		 * System.out.println(nearest.get(i).getKey() + " - " +
		 * nearest.get(i).getValue()); }
		 */

		// calcualte things for rating prediction algortihm

		int active = 0;

		int test = 200;
		double[] precisionarray = new double[test];
		while (active < test) {

			setActiveUser(getTal().get(active));

			Map<String, Integer> testmap = new HashMap<String, Integer>();
			for (Topartists topartists : getTal()) {
				int sim = similarity(getActiveUser(), topartists);

				if (totalPlays(topartists) > 10000)
				{
					if(!(topartists.getAttr().getUser().equals(getActiveUser().getAttr().getUser())))
					{
						testmap.put(topartists.getAttr().getUser(), sim);
					}
				}
					
			}
			
			
			
			// SORT
			List<Entry<String, Integer>> sorted = sortMapByValues(testmap);
			// get closest neighbours
			List<Entry<String, Integer>> nearest = new LinkedList<Entry<String, Integer>>(sorted.subList(0, neighbours));
			Map<String, Double> pearsons = new HashMap<String, Double>();
			for (int i = 0; i < nearest.size(); i++) {
				if (nearest.get(i).getValue() > 0) {
					pearsons.put(nearest.get(i).getKey(), pearson(nearest.get(i)));

				}

			}
			for (Entry<String, Double> entry : pearsons.entrySet()) {
				System.out.println(entry.getKey() + " " + entry.getValue());
			}
			List<Artist> nearestArtists = new ArrayList<Artist>();
			/**
			 * get a list of artists to generate ratings for
			 */
			for (Entry<String, Integer> nearestIter : nearest) // for each
																// nearest
																// neighbour map
			{
				/**
				 * get the users place in the array of topartists get its top
				 * artists and add them to the array if the artist with same
				 * name is not already there
				 */

				int userentry = getuserentryfromusername(nearestIter.getKey());

				for (Artist artist : getTal().get(userentry).getArtist()) {
					// for each artist in the users top 100 add it to the list
					// of
					// nearest artists if not already in
					if (checkartistunique(artist, nearestArtists)) {
						// is not in the list already, add it
						nearestArtists.add(artist);
					}
				}
			}
			Map<Artist, Integer> reclonglist = new HashMap<Artist, Integer>(); // hashmap
																				// to
																				// hold
																				// artists
																				// and
																				// their
																				// predictive
																				// rating
			for (Artist artist : nearestArtists) {
				int rating = predictedrating(pearsons, artist);

				reclonglist.put(artist, rating);
			}
			Map<Artist, Integer> sortedrec = new TreeMap<Artist, Integer>();
			sortedrec = sortByValue(reclonglist);
			System.out.println();
			System.out.println("Recommendations for: " + getActiveUser().getAttr().getUser());
			for (Entry<Artist, Integer> entry : sortedrec.entrySet()) {
				System.out.println(
						"Aritst: " + entry.getKey().getName() + "\t \t \t Predicted Rating: " + entry.getValue());

			}
			
			double correct = 0;
			
			
			for (Artist recartist : sortedrec.keySet())
			{
				for(Artist activeuser: getTal().get(active).getArtist())
				{
					if(recartist.getName().equals(activeuser.getName()))
					{
						correct++;
					}
				}
			}
			
			
			double precision = correct / topN;
			
			
			
			precisionarray[active] = precision;
			active++;
		}

		double precisiontotal = 0;
		for (int i = 0; i < test; i++) {
			precisiontotal += precisionarray[i];
		}
		
		

		double precisionavg = precisiontotal/test;
		
	

		System.out.println("avg precision: " + precisionavg);
		return precisionavg;
		
	}

	private int totalPlays(Topartists topartists) {
		int total = 0;

		for (Artist artist : topartists.getArtist()) {
			total += Integer.parseInt(artist.getPlaycount());
		}

		return total;
	}

	private boolean checkartistunique(Artist artist, List<Artist> nearestArtists) {

		for (Artist artistinList : nearestArtists) {
			if (artistinList.getName().equals(artist.getName())) {
				return false;
			}
		}
		return true;
	}

	public int predictedrating(Map<String, Double> pearsons, Artist artist) {

		double rbara = 0; // average rating given by active user.

		int tuTotal = 0; // target users total plays to calc rbara
		for (Artist targetUser : getActiveUser().getArtist()) {
			tuTotal += Integer.parseInt(targetUser.getPlaycount());
		}

		rbara = tuTotal / 100;
		// for each user who has rated the artist

		double numerator = 0;

		double denominator = 0; // sum of r ratings used

		Map<Topartists, Integer> users = new HashMap<Topartists, Integer>(); // users
																				// who
																				// have
																				// reviewed
																				// this
																				// artist/item
		for (String userString : pearsons.keySet()) {
			// find the user in the
			int userentry = getuserentryfromusername(userString);

			for (Artist userartistlist : getTal().get(userentry).getArtist()) {

				if (userartistlist.getName().equals(artist.getName())) {

					users.put(getTal().get(userentry), Integer.parseInt(userartistlist.getPlaycount()));
				}

			}

		}

		// Calculate Sum of Wai (sum of pearsons), denominator

		for (Entry<Topartists, Integer> user : users.entrySet()) {
			double rbaru = 0; // average playcount of user;
			int uTotal = 0; // target users total plays to calc rbara
			for (Artist currentuser : user.getKey().getArtist()) {
				uTotal += Integer.parseInt(currentuser.getPlaycount());
			}

			rbaru = uTotal / 100;
			double r = pearsons.get(user.getKey().getAttr().getUser());

			double rui = user.getValue();

			numerator += (rui - rbaru) * r;

			denominator += r;
		}

		Double pai = rbara + ((numerator) / (denominator));

		return pai.intValue();
	}

	public double pearson(Entry<String, Integer> entry) {
		int n = 0; // number of data points = similarity;
		n = entry.getValue(); // similarity of the 2 users
		String userB = entry.getKey();

		int userBentry = getuserentryfromusername(userB);

		// get values of common

		List<Integer> x = new ArrayList<Integer>();// all x values, target users
													// rating in common
		List<Integer> y = new ArrayList<Integer>();// all x values, data users
													// rating in common

		for (int i = 0; i < getActiveUser().getArtist().size(); i++)// for all
																	// the
		// artists the
		// first user
		// likes
		{
			String a = getActiveUser().getArtist().get(i).getName();
			for (int j = 0; j < tal.get(userBentry).getArtist().size(); j++) // and
																				// all
																				// the
																				// artists
																				// this
																				// user
																				// likes
			{

				String b = tal.get(userBentry).getArtist().get(j).getName();
				if (a.equals(b))// if they match add the ranking to the x and y
								// array
				{

					x.add(Integer.parseInt(getActiveUser().getArtist().get(i).getPlaycount()));
					y.add(Integer.parseInt(tal.get(userBentry).getArtist().get(j).getPlaycount()));
				}
			}

		}

		List<Integer> xy = new ArrayList<Integer>();// sum of x * y for same
													// song

		// calc xy values
		for (int i = 0; i < n; i++) {
			int xyval = x.get(i) * y.get(i);

			xy.add(xyval);

		}

		int sumx = 0; // sum of x values
		int sumy = 0; // sum of y values
		int sumxy = 0; // sum of xy values

		for (int i = 0; i < n; i++) {
			sumx = sumx + x.get(i);
			sumy = sumy + y.get(i);
			sumxy = sumxy + xy.get(i);
		}

		int sumXallSq = sumx * sumx; // (sum(x))^2
		int sumYallSq = sumy * sumy; // (sum(y))^2

		int sumXSq = 0; // sum (x^2)

		int sumYSq = 0; // sum (y^2)

		for (int i = 0; i < n; i++) {
			sumXSq += x.get(i) * x.get(i);
			sumYSq += y.get(i) * y.get(i);
		}

		double top = (sumxy - ((sumx * sumy) / n));

		double left = sqrt((sumXSq) - (sumXallSq / n));
		double right = sqrt((sumYSq) - (sumYallSq / n));

		double r = top / (left * right);

		return r;
	}

	private int getuserentryfromusername(String userB) {
		for (int i = 0; i < tal.size(); i++) {
			boolean match = false;

			if (tal.get(i).getAttr().getUser() == userB) {
				match = true;

			}
			if (match) {
				return i;

			}

		}
		return 0;
	}

	private List<Entry<String, Integer>> sortMapByValues(Map<String, Integer> aMap) {

		Set<Entry<String, Integer>> mapEntries = aMap.entrySet();

		// used linked list to sort, because insertion of elements in linked
		// list is faster than an array list.
		List<Entry<String, Integer>> aList = new LinkedList<Entry<String, Integer>>(mapEntries);

		// sorting the List
		aList.sort(new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {

				return o1.getValue().compareTo(o2.getValue());
			}

		});

		Collections.reverse(aList);

		// Storing the list into Linked HashMap to preserve the order of
		// insertion.
		Map<String, Integer> aMap2 = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> entry : aList) {
			
				if (!(entry.getKey().equals(getActiveUser().getAttr().getUser()))) {
					aMap2.put(entry.getKey(), entry.getValue());
				}
			
		}

		return aList;

	}

	public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Collections.reverse(list);
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			if (result.size() <= topN) {
				result.put(entry.getKey(), entry.getValue());
			}
		}

		return result;
	}

	public int similarity(Topartists userA, Topartists userB) {
		int matchesCount = 0;

		for (int i = 0; i < userA.getArtist().size(); i++) {
			boolean match = false;
			for (int j = 0; j < userB.getArtist().size(); j++) {
				String a = userA.getArtist().get(i).getName();
				String b = userB.getArtist().get(j).getName();
				if (a.equals(b)) {
					match = true;
				}
			}
			if (match) {
				matchesCount++;
			}
		}

		return matchesCount;
	}

	
	public List<Topartists> getTal() {
		return tal;
	}

	public void setTal(List<Topartists> tal) {
		this.tal = tal;
	}

	public Topartists getActiveUser() {
		return activeUser;
	}

	public void setActiveUser(Topartists activeUser) {
		this.activeUser = activeUser;
	}

}
