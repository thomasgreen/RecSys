package _Default;

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
public class ArtistModel extends AdvancedModel<Artist, Topartists>{

	private int neighbours; //how many neighbours to use to generate

	public ArtistModel(int topNValue, int neighboursValue) {
		super(topNValue);
		neighbours = neighboursValue;
	}

	@Override
	public Map<Artist, Integer> recommend(List<Artist> training, String username) {

			
		Map<String, Integer> testmap = new HashMap<String, Integer>();
		for (Topartists topartists : getTal()) {
			if(topartists.getAttr().getUser().equals(username))
			{
				continue; //skip this user from the neighbours if it is the same user
			}
			int sim = similarity(training, topartists);
	
			if (totalPlays(topartists) > 500) {
				if (!(topartists.getAttr().getUser().equals(username))) {
					testmap.put(topartists.getAttr().getUser(), sim);
				}

			}

		}

		// SORT
		List<Entry<String, Integer>> sorted = sortMapByValues(testmap, username);
		// get closest neighbours
		List<Entry<String, Integer>> nearest = new LinkedList<Entry<String, Integer>>(
				sorted.subList(0, neighbours));
		Map<String, Double> pearsons = new HashMap<String, Double>();
		for (int i = 0; i < nearest.size(); i++) {
			if (nearest.get(i).getValue() > 1) {
				pearsons.put(nearest.get(i).getKey(), pearson(nearest.get(i), training));
			}
		}
		for (Entry<String, Double> entry : pearsons.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
		List<Artist> nearestArtists = new ArrayList<Artist>();
		/**
		 * get a list of artists to generate ratings for
		 **/
		for (Entry<String, Integer> nearestIter : nearest) // for each nearest neighbour map
		{
			int userentry = entryOfUser(nearestIter.getKey());
			for (Artist artist : getTal().get(userentry).getItem()) {
				// for each artist in the users top 100 add it to the list
				// of
				// nearest artists if not already in
				if (isUnique(artist, nearestArtists) && isDuplicate(artist, training)) {
					// is not in the list already, add it	
					nearestArtists.add(artist);
				}
			}
		}
		Map<Artist, Integer> reclonglist = new HashMap<Artist, Integer>(); 
		// hashmap to hold  artists and their predictive rating
		for (Artist artist : nearestArtists) {
			int rating = predictedrating(pearsons, artist, training);
			reclonglist.put(artist, rating);
		}
		Map<Artist, Integer> sortedrec = new TreeMap<Artist, Integer>();
		sortedrec = sortByValue(reclonglist);
		return sortedrec;
	}

	public int totalPlays(Topartists topartists) {
		int total = 0;
		for (Artist artist : topartists.getItem()) {
			total += Integer.parseInt(artist.getPlaycount());
		}
		return total;
	}

	public boolean isUnique(Artist artist, List<Artist> nearestArtists) {
		for (Artist artistinList : nearestArtists) {
			if (artistinList.getName().equals(artist.getName())) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isDuplicate(Artist artist, List<Artist> trainingArtist)
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
		double numerator = 0;
		double denominator = 0; // sum of r ratings used
		Map<Topartists, Integer> users = new HashMap<Topartists, Integer>(); 
		for (String userString : pearsons.keySet()) {
			int userentry = entryOfUser(userString);
			for (Artist userartistlist : getTal().get(userentry).getItem()) {
				if (userartistlist.getName().equals(artist.getName())) {
					users.put(getTal().get(userentry), Integer.parseInt(userartistlist.getPlaycount()));
				}
			}
		}
		for (Entry<Topartists, Integer> user : users.entrySet()) {
			double rbaru = 0; // average playcount of user;
			int uTotal = 0; // target users total plays to calc rbara
			for (Artist currentuser : user.getKey().getItem()) {
				uTotal += Integer.parseInt(currentuser.getPlaycount());
			}
			rbaru = uTotal / user.getKey().getItem().size(); 
			double r = pearsons.get(user.getKey().getAttr().getUser());
			double rui = user.getValue();
			numerator += (rui - rbaru) * r;
			denominator += r;
		}
		Double pai = rbara + ((numerator) / (denominator));
		return pai.intValue();
	}

	public double pearson(Entry<String, Integer> entry, List<Artist> trainingArtist) {
		int n = 0; // number of data points = similarity;
		n = entry.getValue(); // similarity of the 2 users
		String userB = entry.getKey();
		int userBentry = entryOfUser(userB);
		// get values of common
		List<Integer> x = new ArrayList<Integer>();// all x values, target users rating in common
		List<Integer> y = new ArrayList<Integer>();// all x values, data users ating in common
		for (Artist artist1 : trainingArtist)
		{
			String a = artist1.getName();
			for (Artist artist2 : getTal().get(userBentry).getItem()) 
			{
				String b = artist2.getName();
				if (a.equals(b))// if they match add the ranking to the x and y array
				{
					x.add(Integer.parseInt(artist1.getPlaycount()));
					y.add(Integer.parseInt(artist2.getPlaycount()));
				}
			}
		}
		List<Integer> xy = new ArrayList<Integer>();// sum of x * y for same artist

		// calc xy values
		for (int i = 0; i < n; i++) {
			int xyval = x.get(i) * y.get(i);
			xy.add(xyval);
		}
		double sumx = 0; // sum of x values
		double sumy = 0; // sum of y values
		double sumxy = 0; // sum of xy values
		for (int i = 0; i < n; i++) {
			sumx = sumx + x.get(i);
			sumy = sumy + y.get(i);
			sumxy = sumxy + xy.get(i);
		}
		double sumXallSq = Math.pow(sumx, 2); // (sum(x))^2
		double sumYallSq = Math.pow(sumy, 2); // (sum(y))^2
		double sumXSq = 0; // sum (x^2)
		double sumYSq = 0; // sum (y^2)
		for (int i = 0; i < n; i++) {
			sumXSq += Math.pow(x.get(i), 2);
			sumYSq += Math.pow(y.get(i), 2);
		}
		double top = (sumxy - ((sumx * sumy) / n));
		double left = sqrt((sumXSq) - (sumXallSq / n));
		double right = sqrt((sumYSq) - (sumYallSq / n));
		double r = top / (left * right);
		return r;
	}

	private List<Entry<String, Integer>> sortMapByValues(Map<String, Integer> aMap, String username) {

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
			if (!(entry.getKey().equals(username))) {
				aMap2.put(entry.getKey(), entry.getValue());
			}

		}

		
		return aList;

	}

	public int similarity(List<Artist> userA, Topartists userB) {
		int matchesCount = 0;

		for (int i = 0; i < userA.size(); i++) { //use every other data point TEST DATA TODO
			boolean match = false;
			for (int j = 0; j < userB.getItem().size(); j++) {
				String a = userA.get(i).getName();
				String b = userB.getItem().get(j).getName();
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
