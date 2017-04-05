import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import artistsPOJO.Artist;
import artistsPOJO.Topartists;

public class Evaluation {

	private List<Topartists> tal; //list of all users + data in file
	
	private Topartists activeUser; //the active user getting recommendation
	
	
	
	public static void main(String[] args) {
		
		Evaluation evaluation = new Evaluation();
		evaluation.run();
		
	}

	public Evaluation()
	{
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
	
	public void run()
	{
		//set up array DONT KNOW HOW
		
		//split the user (in another method)
		
		//run engine on theat user. it should return SOMETHING I DONT KNOW WHAT
		int active = 0;
		int test = 1000;
		
		
		
		RecEngine testEngine = new RecEngine(10);
		while (active < test)
		{
			activeUser = tal.get(active); //set the active user
			
			if(totalPlays(activeUser) == 0) //if this user has listened to no music
			{
				active++;
				continue; //skip this user
			}
			
			List<Artist> trainingArtist = new ArrayList<Artist>();
			
			for(int i = 0; i < activeUser.getArtist().size(); i = i +2)
			{
				trainingArtist.add(activeUser.getArtist().get(i)); //add all odd data to training list
			}
			
			List<Artist> testArtist = new ArrayList<Artist>();
			
			for(int i = 1; i < activeUser.getArtist().size(); i = i +2)
			{
				testArtist.add(activeUser.getArtist().get(i)); //add all odd data to training list
			}
			
			Map<Artist, Integer> recomended = new HashMap<Artist, Integer>();
			recomended = testEngine.recommend(trainingArtist);
			
			System.out.println();
			System.out.println("Recommendations for: " + activeUser.getAttr().getUser());
			for (Entry<Artist, Integer> entry : recomended.entrySet()) {
				System.out.println(
						"Aritst: " + entry.getKey().getName() + "\t \t \t Predicted Rating: " + entry.getValue());

			}
			
		}
		
		
	}
	
	private int totalPlays(Topartists topartists) {
		int total = 0;

		for (Artist artist : topartists.getArtist()) {
			total += Integer.parseInt(artist.getPlaycount());
		}

		return total;
	}
	
	
}
