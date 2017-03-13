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

	public RecEngine() {
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
	}

	public static void main(String[] args) throws IOException {
		RecEngine engine = new RecEngine();


		Map<String, Integer> testmap = new HashMap<String, Integer>();

		for (int i = 0; i < engine.getTal().size(); i++) {
			int sim = engine.similarity(engine.getTal().get(0), engine.getTal().get(i));
			testmap.put(engine.getTal().get(i).getAttr().getUser(), sim);
		}

		// SORT
		List<Entry<String, Integer>> sorted = engine.sortMapByValues(testmap);

		
		//get closest neighbours
		List<Entry<String, Integer>> nearest = new LinkedList<Entry<String, Integer>>(sorted.subList(0, 50));
		
		List<Double> pearsons = new ArrayList<Double>();
		
		for(int i = 0; i < nearest.size(); i++)
		{
			pearsons.add(engine.pearson(nearest.get(i)));

		}
		
		for(double d: pearsons)
		{
			System.out.println(d);
		}
		
		
		/*for (int i = 0; i < nearest.size(); i++) {
			System.out.println(nearest.get(i).getKey() + " - " + nearest.get(i).getValue());
		}*/
		

		//Map<>
		
		
		engine.makeRecommendations();
		// }

	}

	public double pearson(Entry<String, Integer> entry) {
		int n = 0; //number of data points = similarity;
		n = entry.getValue(); // similarity of the 2 users
		String userB = entry.getKey();
		
		int userBentry = 0;
		for(int i =0; i < tal.size(); i++)
		{
			boolean match = false;
			
			if(tal.get(i).getAttr().getUser() == userB)
			{
				match = true;
				
			}
			if(match)
			{
				userBentry = i;
				break;
				
			}
		}
		
		//get values of common
				
		List<Integer> x = new ArrayList<Integer>();// all x values, target users rating in common
		List<Integer> y = new ArrayList<Integer>();// all x values, data users rating in common
		
		
		
		for(int i = 0; i < tal.get(0).getArtist().size();i++)//for all the artists the first user likes
		{
			String a = tal.get(0).getArtist().get(i).getName();
			for(int j = 0; j < tal.get(userBentry).getArtist().size();j++) //and all the artists this user likes
			{
				
				String b = tal.get(userBentry).getArtist().get(j).getName();
				if(a.equals(b))//if they match add the ranking to the x and y array
				{
					
					x.add(i+1);
					y.add(j+1);
				}
			}
			
		}
		
		List<Integer> xy = new ArrayList<Integer>();//sum of x * y for same song
		
		//calc xy values
		for(int i = 0; i < n; i++)
		{
			int xyval = x.get(i) * y.get(i);
			
			xy.add(xyval);
			
		}
		
		
		int sumx = 0; //sum of x values
		int sumy = 0; //sum of y values
		int sumxy = 0; //sum of xy values
		
		
		for(int i = 0; i < n; i++)
		{
			sumx = sumx + x.get(i);
			sumy = sumy + y.get(i);
			sumxy = sumxy + xy.get(i);
		}
		
		
		int sumXallSq = sumx * sumx; // (sum(x))^2
		int sumYallSq = sumy * sumy; // (sum(y))^2
		
		
		
		int sumXSq = 0; // sum (x^2)
		
		int sumYSq = 0; // sum (y^2)
		
		
		for(int i = 0; i < n; i++)
		{
			sumXSq += x.get(i) * x.get(i);
			sumYSq += y.get(i) * y.get(i);
		}
		
		double top = (sumxy - ((sumx * sumy)/n));
		
		double left = sqrt((sumXSq) - (sumXallSq/n));
		double right = sqrt((sumYSq) - (sumYallSq/n));
		
		double r = top/(left*right);
	
		return r;
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
				// TODO Auto-generated method stub

				return o1.getValue().compareTo(o2.getValue());
			}

		});
		
		Collections.reverse(aList);

		// Storing the list into Linked HashMap to preserve the order of
		// insertion.
		Map<String, Integer> aMap2 = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> entry : aList) {
			aMap2.put(entry.getKey(), entry.getValue());
		}

		return aList;

	}

	@SuppressWarnings("unused")
	private static void getUserData(String targetuser) {
		// TODO Auto-generated method stub

	}

	public void generateRating() {
		String topsong = getTal().get(0).getArtist().get(0).getName();
		String user = tal.get(1).getAttr().getUser();
		System.out.println(user + " " + topsong);

		long rank = Integer.parseInt(tal.get(0).getArtist().get(56).getAttr().getRank());

		long total = tal.get(0).getArtist().size();

		float rating = rank / total;

		long finalrating = (long) (5 * (1 - rating)); // rating of the user

		System.out.println(finalrating);
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

	

	public void makeRecommendations() {
		// TODO Auto-generated method stub

	}

	public String getUserID() {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);

		System.out.println("Please Enter Your Last.FM Username: ");

		return in.nextLine();

		// in.close();

	}

	public List<Topartists> getTal() {
		return tal;
	}

	public void setTal(List<Topartists> tal) {
		this.tal = tal;
	}

}
