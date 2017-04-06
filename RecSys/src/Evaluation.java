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
			
			List<Artist> fold1 = new ArrayList<Artist>();
			List<Artist> fold2 = new ArrayList<Artist>();
			List<Artist> fold3 = new ArrayList<Artist>();
			List<Artist> fold4 = new ArrayList<Artist>();
			List<Artist> fold5 = new ArrayList<Artist>();
			
			List<List<Artist>> fold = new ArrayList<List<Artist>>();
			
			fold.add(fold1);
			fold.add(fold2);
			fold.add(fold3);
			fold.add(fold4);
			fold.add(fold5);
			for(int i = 0; i < activeUser.getArtist().size() / 5; i++)
			{
				fold1.add(activeUser.getArtist().get(i)); //add all odd data to training list
			}
			
			for(int i = fold1.size(); i < 2 *(activeUser.getArtist().size() / 5); i++)
			{
				fold2.add(activeUser.getArtist().get(i)); //add all odd data to training list
			}
			
			for(int i = fold1.size(); i < 3 *(activeUser.getArtist().size() / 5); i++)
			{
				fold3.add(activeUser.getArtist().get(i)); //add all odd data to training list
			}
			
			for(int i = fold1.size(); i < 4 *(activeUser.getArtist().size() / 5); i++)
			{
				fold4.add(activeUser.getArtist().get(i)); //add all odd data to training list
			}
			
			for(int i = fold1.size(); i < 5 *(activeUser.getArtist().size() / 5); i++)
			{
				fold5.add(activeUser.getArtist().get(i)); //add all odd data to training list
			}
			

			float precisionFold[] = new float[5];
			for(List<Artist> testFold : fold)
			{
				//for each fold
				List<Artist> trainingArtist = new ArrayList<Artist>();
				List<Artist> testArtist = new ArrayList<Artist>();

				for(List<Artist> foldVal : fold)
				{
					if(foldVal.equals(testFold))
					{
						//empty contents into TEST ARTIST
						unfold(testArtist, foldVal);
					}
					else
					{
						//empty contents into TRAINING ARTIST
						unfold(trainingArtist, foldVal);
					}
				}
				
				
				
				//make follow code and generate average for each fold
				Map<Artist, Integer> recommended = new HashMap<Artist, Integer>();
				String username = activeUser.getAttr().getUser();
				recommended = testEngine.recommend(trainingArtist, username);
				
				System.out.println();
				System.out.println("Recommendations for: " + activeUser.getAttr().getUser());
				for (Entry<Artist, Integer> entry : recommended.entrySet()) {
					System.out.println(
							"Aritst: " + entry.getKey().getName() + "\t \t \t Predicted Rating: " + entry.getValue());

				}
				precisionFold[fold.indexOf(testFold)] = evaluate(recommended, testArtist);
			}

			float precisionFoldSum = 0;
			for(float val : precisionFold)
			{
				precisionFoldSum += val;
			}
			
			float precisionFoldAvg = precisionFoldSum / 5;
			
			precision[active] = precisionFoldAvg;
			active++;
			//will be in loop below
			
		}
		
		float precisionSum = 0;
		for(float val : precision)
		{
			precisionSum += val;
		}
		
		float precisionAvg = precisionSum / precision.length;
		
		return precisionAvg;
	}
	
	private void unfold(List<Artist> list, List<Artist> foldVal) {
		// TODO Auto-generated method stub iterate of fold val and add to training artist
		for(Artist foldArtist: foldVal)
		{
			list.add(foldArtist);
		}
		
		
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
