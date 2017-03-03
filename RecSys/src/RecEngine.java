import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import artistsPOJO.Topartists;

/**
 * 
 */

/**
 * @author thomasgreen
 *
 */
public class RecEngine {

	private List<Topartists> tal;
	
	public RecEngine(){
		//constructor for Rec Engine
		Gson gson = new Gson();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File("rsc/topartists.json")));
		} catch (FileNotFoundException e) {
			System.out.println("That File Does Not Exist");
			e.printStackTrace();
		}

		Type collectionType = new TypeToken<List<Topartists>>() {}.getType();
		tal = gson.fromJson(reader, collectionType);
	}
	
	
	public static void main(String[] args) throws IOException {
		RecEngine engine = new RecEngine();
		
		//while(true){
			//String targetuser = engine.getUserID();
			//getUserData(targetuser); // not yet written
			
			//List<RatingUserMap> simRatings = new ArrayList<RatingUserMap>();
		
			Map<String, Integer> testmap = new HashMap<String, Integer>();
			
			for(int i =  0; i < engine.getTal().size(); i++)
			{
				int sim = engine.similarity(engine.getTal().get(0), engine.getTal().get(i ));
				System.out.println(sim + " " + engine.getTal().get(i).getAttr().getUser());
				//simRatings.add(new RatingUserMap(sim, engine.getTal().get(i).getAttr().getUser()));
				testmap.put(engine.getTal().get(i).getAttr().getUser(), sim);
			}
			
			

			
			
			engine.getNeighbours();
			engine.makeRecommendations();
		//}
		
		
	}
	
	

	private static void getUserData(String targetuser) {
		// TODO Auto-generated method stub
		
	}


	public void generateRating() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		int matchesCount= 0;
		
		
		for(int i = 0; i < userA.getArtist().size(); i++)
		{
			boolean match = false;
			for(int j = 0; j < userB.getArtist().size(); j++)
			{
				String a = userA.getArtist().get(i).getName();
				String b = userB.getArtist().get(j).getName();
				if(a.equals(b))
				{
					match = true;
				}
			}
			if(match)
			{
				matchesCount++;
			}
		}
		
	
		return matchesCount;
	}



	public void getNeighbours() {
		// TODO Auto-generated method stub
		
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
