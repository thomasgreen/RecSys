package _Default;

import java.util.*;
import java.util.Map.Entry;


import artistsPOJO.Artist;
import artistsPOJO.Topartists;
import tracksPOJO.Toptracks;
import tracksPOJO.Track;

import static java.lang.Math.sqrt;

/**
 * 
 */

/**
 * @author thomasgreen
 *
 */
public class TrackModel extends AdvancedModel<Track, Toptracks>{

	private int neighbours; //how many neighbours to use to generate

	public TrackModel(int topNValue, int neighboursValue) {
		super(topNValue);
		neighbours = neighboursValue;
	}

	@Override
	public Map<Track, Integer> recommend(List<Track> training, String username) {

			
			Map<String, Integer> testmap = new HashMap<String, Integer>();
			for (Toptracks toptracks : getTtl()) {
				if(toptracks.getAttr().getUser().equals(username))
				{
					continue; //skip this user from the neighbours if it is the same user
				}
				int sim = similarity(training, toptracks);

				if (totalPlays(toptracks) > 100) {
					if (!(toptracks.getAttr().getUser().equals(username))) {
						testmap.put(toptracks.getAttr().getUser(), sim);
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
				if (nearest.get(i).getValue() > 0) {
					pearsons.put(nearest.get(i).getKey(), pearson(nearest.get(i), training));

				}

			}
			for (Entry<String, Double> entry : pearsons.entrySet()) {
				System.out.println(entry.getKey() + " " + entry.getValue());
			}
			List<Track> nearestTracks = new ArrayList<Track>();
			/**
			 * get a list of artists to generate ratings for
			 */
			for (Entry<String, Integer> nearestIter : nearest) // for each nearest neighbour map
			{
				/**
				 * get the users place in the array of toptracks get its top
				 * tracks and add them to the array if the artist with same
				 * name is not already there
				 */

				int userentry = entryOfUser(nearestIter.getKey());

				for (Track track : getTtl().get(userentry).getItem()) {
					// for each artist in the users top 100 add it to the list
					// of
					// nearest artists if not already in
					if (isUnique(track, nearestTracks) && isDuplicate(track, nearestTracks)) {
						// is not in the list already, add it
						
						nearestTracks.add(track);
					}
				}
			}
			Map<Track, Integer> reclonglist = new HashMap<Track, Integer>(); // hashmap
																				// to
																				// hold
																				// artists
																				// and
																				// their
																				// predictive
																				// rating
			for (Track track : nearestTracks) {
				int rating = predictedrating(pearsons, track, training);

				reclonglist.put(track, rating);
			}
			Map<Track, Integer> sortedrec = new TreeMap<Track, Integer>();
			sortedrec = sortByValue(reclonglist);

			
			return sortedrec;

	}

	public int totalPlays(Toptracks toptracks) {
		int total = 0;

		for (Track track : toptracks.getItem()) {
			total += Integer.parseInt(track.getPlaycount());
		}

		return total;
	}

	public boolean isUnique(Track track, List<Track> nearestTracks) {

		for (Track trackinList : nearestTracks) {
			if (trackinList.getName().equals(track.getName())) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isDuplicate(Track track, List<Track> training)
	{
		for (Track artistinList : training) {
			if (artistinList.getName().equals(track.getName())) {
				return false;
			}
		}
		return true;
	}

	public int predictedrating(Map<String, Double> pearsons, Track track, List<Track> training) {

		double rbara = 0; // average rating given by active user.

		int tuTotal = 0; // target users total plays to calc rbara
		for (Track targetUser : training) {
			tuTotal += Integer.parseInt(targetUser.getPlaycount());
		}

		rbara = tuTotal / training.size();
		// for each user who has rated the artist

		double numerator = 0;

		double denominator = 0; // sum of r ratings used

		Map<Toptracks, Integer> users = new HashMap<Toptracks, Integer>(); // users
																				// who
																				// have
																				// reviewed
																				// this
																				// artist/item
		for (String userString : pearsons.keySet()) {
			// find the user in the
			int userentry = entryOfUser(userString);

			for (Track usertracklist : getTtl().get(userentry).getItem()) {

				if (usertracklist.getName().equals(track.getName())) {

					users.put(getTtl().get(userentry), Integer.parseInt(usertracklist.getPlaycount()));
				}

			}

		}

		// Calculate Sum of Wai (sum of pearsons), denominator

		for (Entry<Toptracks, Integer> user : users.entrySet()) {
			double rbaru = 0; // average playcount of user;
			int uTotal = 0; // target users total plays to calc rbara
			for (Track currentuser : user.getKey().getItem()) {
				uTotal += Integer.parseInt(currentuser.getPlaycount());
			}

			rbaru = uTotal / user.getKey().getItem().size(); //TODO NEEDS TO BE TOTAL
			double r = pearsons.get(user.getKey().getAttr().getUser());

			double rui = user.getValue();

			numerator += (rui - rbaru) * r; //TODO maybe wrong, think i need to do this at the end with arrays

			denominator += r;
		}

		Double pai = rbara + ((numerator) / (denominator));

		return pai.intValue();
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

	public int similarity(List<Track> training, Toptracks userB) {
		int matchesCount = 0;

		for (int i = 0; i < training.size(); i++) { //use every other data point TEST DATA TODO
			boolean match = false;
			for (int j = 0; j < userB.getItem().size(); j++) {
				String a = training.get(i).getName();
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

	
	public double pearson(Entry<String, Integer> entry, List<Track> training) {
		int n = 0; // number of data points = similarity;
		n = entry.getValue(); // similarity of the 2 users
		String userB = entry.getKey();

		int userBentry = entryOfUser(userB);

		// get values of common

		List<Integer> x = new ArrayList<Integer>();// all x values, target users
													// rating in common
		List<Integer> y = new ArrayList<Integer>();// all x values, data users
													// rating in common

		for (int i = 0; i < training.size(); i++)// for all
																	// the
		// artists the
		// first user
		// likes
		{
			String a = training.get(i).getName();
			for (int j = 0; j < getTtl().get(userBentry).getItem().size(); j++) // and
																				// all
																				// the
																				// artists
																				// this
																				// user
																				// likes
			{

				String b = getTtl().get(userBentry).getItem().get(j).getName();
				if (a.equals(b))// if they match add the ranking to the x and y
								// array
				{

					x.add(Integer.parseInt(training.get(i).getPlaycount()));
					y.add(Integer.parseInt(getTtl().get(userBentry).getItem().get(j).getPlaycount()));
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


}
