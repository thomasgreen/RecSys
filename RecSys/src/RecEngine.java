
import java.util.*;
import java.util.Map.Entry;


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
public class RecEngine extends AdvancedModel{

	private int neighbours; //how many neighbours to use to generate

	public RecEngine(int topNValue, int neighboursValue) {
		super(topNValue);
		neighbours = neighboursValue;
	}

	public Map<Artist, Integer> recommend(List<Artist> trainingArtist, String username) {

			if(username.equals("bagelo"))
			{
				System.out.println("PASUE");
			}
			
			
			
			Map<String, Integer> testmap = new HashMap<String, Integer>();
			for (Topartists topartists : getTal()) {
				if(topartists.getAttr().getUser().equals(username))
				{
					continue; //skip this user from the neighbours if it is the same user
				}
				int sim = similarity(trainingArtist, topartists);

				if (totalPlays(topartists) > 100) {
					if (!(topartists.getAttr().getUser().equals(getActiveUser().getAttr().getUser()))) {
						testmap.put(topartists.getAttr().getUser(), sim);
					}

				}

			}

			// SORT
			List<Entry<String, Integer>> sorted = sortMapByValues(testmap);
			// get closest neighbours
			List<Entry<String, Integer>> nearest = new LinkedList<Entry<String, Integer>>(
					sorted.subList(0, neighbours));
			Map<String, Double> pearsons = new HashMap<String, Double>();
			for (int i = 0; i < nearest.size(); i++) {
				if (nearest.get(i).getValue() > 0) {
					pearsons.put(nearest.get(i).getKey(), pearson(nearest.get(i), trainingArtist));

				}

			}
			for (Entry<String, Double> entry : pearsons.entrySet()) {
				System.out.println(entry.getKey() + " " + entry.getValue());
			}
			List<Artist> nearestArtists = new ArrayList<Artist>();
			/**
			 * get a list of artists to generate ratings for
			 */
			for (Entry<String, Integer> nearestIter : nearest) // for each nearest neighbour map
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
					if (artistUnique(artist, nearestArtists) && artistDuplicate(artist, trainingArtist)) {
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
				int rating = predictedrating(pearsons, artist, trainingArtist);

				reclonglist.put(artist, rating);
			}
			Map<Artist, Integer> sortedrec = new TreeMap<Artist, Integer>();
			sortedrec = sortByValue(reclonglist);

			
			return sortedrec;

	}

	private int totalPlays(Topartists topartists) {
		int total = 0;

		for (Artist artist : topartists.getArtist()) {
			total += Integer.parseInt(artist.getPlaycount());
		}

		return total;
	}

	private boolean artistUnique(Artist artist, List<Artist> nearestArtists) {

		for (Artist artistinList : nearestArtists) {
			if (artistinList.getName().equals(artist.getName())) {
				return false;
			}
		}
		return true;
	}
	
	private boolean artistDuplicate(Artist artist, List<Artist> trainingArtist)
	{
		for (Artist artistinList : trainingArtist) {
			if (artistinList.getName().equals(artist.getName())) {
				return false;
			}
		}
		return true;
	}

	public int predictedrating(Map<String, Double> pearsons, Artist artist, List<Artist> trainingArtist) {

		double rbara = 0; // average rating given by active user.

		int tuTotal = 0; // target users total plays to calc rbara
		for (Artist targetUser : trainingArtist) {
			tuTotal += Integer.parseInt(targetUser.getPlaycount());
		}

		rbara = tuTotal / trainingArtist.size();
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

			rbaru = uTotal / user.getKey().getArtist().size(); //TODO NEEDS TO BE TOTAL
			double r = pearsons.get(user.getKey().getAttr().getUser());

			double rui = user.getValue();

			numerator += (rui - rbaru) * r; //TODO maybe wrong, think i need to do this at the end with arrays

			denominator += r;
		}

		Double pai = rbara + ((numerator) / (denominator));

		return pai.intValue();
	}

	public double pearson(Entry<String, Integer> entry, List<Artist> trainingArtist) {
		int n = 0; // number of data points = similarity;
		n = entry.getValue(); // similarity of the 2 users
		String userB = entry.getKey();

		int userBentry = getuserentryfromusername(userB);

		// get values of common

		List<Integer> x = new ArrayList<Integer>();// all x values, target users
													// rating in common
		List<Integer> y = new ArrayList<Integer>();// all x values, data users
													// rating in common

		for (int i = 0; i < trainingArtist.size(); i++)// for all
																	// the
		// artists the
		// first user
		// likes
		{
			String a = trainingArtist.get(i).getName();
			for (int j = 0; j < getTal().get(userBentry).getArtist().size(); j++) // and
																				// all
																				// the
																				// artists
																				// this
																				// user
																				// likes
			{

				String b = getTal().get(userBentry).getArtist().get(j).getName();
				if (a.equals(b))// if they match add the ranking to the x and y
								// array
				{

					x.add(Integer.parseInt(trainingArtist.get(i).getPlaycount()));
					y.add(Integer.parseInt(getTal().get(userBentry).getArtist().get(j).getPlaycount()));
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
		for (int i = 0; i < getTal().size(); i++) {
			boolean match = false;

			if (getTal().get(i).getAttr().getUser() == userB) {
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
			if (result.size() < getTopN()) {
				result.put(entry.getKey(), entry.getValue());
			}
		}

		return result;
	}

	public int similarity(List<Artist> userA, Topartists userB) {
		int matchesCount = 0;

		for (int i = 0; i < userA.size(); i++) { //use every other data point TEST DATA TODO
			boolean match = false;
			for (int j = 0; j < userB.getArtist().size(); j++) {
				String a = userA.get(i).getName();
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



}
