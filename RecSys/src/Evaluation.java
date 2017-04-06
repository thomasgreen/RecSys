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
	
	private int test; //number of users the model will be tested on
	
	
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
		test = 200;
	}
	
	public void run()
	{
		//set up array DONT KNOW HOW
		
		//split the user (in another method)
		
		//run engine on theat user. it should return SOMETHING I DONT KNOW WHAT
		
		/*		Artist Models		*/
		
		//testing the k value
		
		int[] kTestValues = {1, 5, 10, 15, 20 ,25, 30, 45, 50};
		float[] kTestResults = new float[9];
		for(int i = 0; i < kTestValues.length; i++)
		{
			RecEngine testEngine = new RecEngine(10, kTestValues[i]);
			kTestResults[i] = runModel(testEngine);
			
		}
		
		for(float val: kTestResults)
		{
			System.out.println(val);
		}
		
		
		/*		Track Models		*/
		
		
		
	}
	
	private float runModel(RecEngine testEngine)
	{
		int active = 0;
		
		float[] precision = new float[1000];
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
			
			Map<Artist, Integer> recommended = new HashMap<Artist, Integer>();
			String username = activeUser.getAttr().getUser();
			recommended = testEngine.recommend(trainingArtist, username);
			
			System.out.println();
			System.out.println("Recommendations for: " + activeUser.getAttr().getUser());
			for (Entry<Artist, Integer> entry : recommended.entrySet()) {
				System.out.println(
						"Aritst: " + entry.getKey().getName() + "\t \t \t Predicted Rating: " + entry.getValue());

			}
			
			precision[active] = evaluate(recommended, testArtist);
			active++;
		}
		
		float precisionSum = 0;
		for(float val : precision)
		{
			precisionSum += val;
		}
		
		float precisionAvg = precisionSum / precision.length;
		
		return precisionAvg;
	}
	
	private float evaluate(Map<Artist, Integer> recommended, List<Artist> testArtist) {
		//this is where precision and stuff are calculated maybe
		float tp = 0;

		float fp = 0;

		List<Integer> actual = new ArrayList<Integer>();
		List<Integer> predicted = new ArrayList<Integer>();
		for (Artist recartist : recommended.keySet()) {
			boolean found = false;
			for (Artist activeuser : testArtist) {

				if (recartist.getName().equals(activeuser.getName())) {
					found = true;
					actual.add(Integer.parseInt(activeuser.getAttr().getRank()));
					predicted.add(Integer.parseInt(recartist.getAttr().getRank()));

				}

			}
			if (found) // true positive (recommended correctly
			{
				tp++;

			} else { // false positive (recommended incorrectly)
				fp++;
			}
		}

		

		float precision = tp / (tp + fp);
		return precision;
	}

	private int totalPlays(Topartists topartists) {
		int total = 0;

		for (Artist artist : topartists.getArtist()) {
			total += Integer.parseInt(artist.getPlaycount());
		}

		return total;
	}
	
	
}
