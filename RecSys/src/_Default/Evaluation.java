package _Default;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import artistsPOJO.Artist;
import artistsPOJO.Topartists;
import tracksPOJO.Toptracks;
import tracksPOJO.Track;

public class Evaluation {

	private List<Topartists> tal; //list of all users + data in file
	
	private List<Toptracks> ttl;
	
	
	
	private int test; //number of users the model will be tested on
	
	
	public static void main(String[] args) {
		
		Evaluation evaluation = new Evaluation();
		evaluation.run();
		
	}

	public Evaluation()
	{
		Gson gson = new Gson();

		BufferedReader artistreader = null;
		try {
			artistreader = new BufferedReader(new FileReader(new File("rsc/topartists.json")));
		} catch (FileNotFoundException e) {
			System.out.println("That File Does Not Exist");
			e.printStackTrace();
		}

		Type artistType = new TypeToken<List<Topartists>>() {
		}.getType();
		tal = gson.fromJson(artistreader, artistType);
		
		

		BufferedReader trackreader = null;
		try {
			trackreader = new BufferedReader(new FileReader(new File("rsc/toptracks.json")));
		} catch (FileNotFoundException e) {
			System.out.println("That File Does Not Exist");
			e.printStackTrace();
		}

		Type trackType = new TypeToken<List<Toptracks>>() {
		}.getType();
		ttl = gson.fromJson(trackreader, trackType);
		
		
		
		test = 100;
	}
	
	public void run()
	{

		//BASELINE
		
	
		//Baseline baseline = new Baseline(10);
		
		//float baselineresult = runBaselineModel(baseline);
		
		
		
		
		AdvancedModel<Artist, Topartists> trackmodel = new ArtistModel(10, 17);
		
		float advancedresult = runModel(trackmodel, tal);
		//System.out.print(baselineresult);
		
		System.out.print(advancedresult);
		
		
		/*		Artist Models		
		

		//testing the k value
		
		int[] kTestValues = {1, 5, 10, 15, 20 ,25, 30, 45, 50};
		float[] kTestResults = new float[10];
		for(int i = 0; i < kTestValues.length; i++)
		{
			//ArtistModel testEngine = new ArtistModel(10, kTestValues[i]);
			//kTestResults[i] = runModel(testEngine);
			
		}
		
		for(float val: kTestResults)
		{
			System.out.println(val);
		}
		
		
		/*		Track Models		*/
		
		
		
		
	}
	
	private float runBaselineModel(Baseline baseline)
	{
		Topartists activeUser;
		
		Map<Artist, Integer> recommended = new LinkedHashMap<Artist, Integer>();
		
		recommended = baseline.recommend();
		
		for(Entry<Artist, Integer> entry: recommended.entrySet())
		{
			System.out.println(entry.getKey().getName() + " " + entry.getValue());
		}
		
		int active = 0;
		
		float[] precision = new float[1000];
		while (active < test)
		{
			activeUser = tal.get(active); //set the active user
			
			if(totalPlays(activeUser) < 100) //if this user has listened to no music
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
			
			generateFold(fold, activeUser);
			
			
			float precisionFold[] = new float[5]; //LOOK AT FOLD SIZES
			for(List<Artist> testFold : fold)
			{
				
				precisionFold[fold.indexOf(testFold)] = evaluate(recommended, testFold);
				
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
	
	private <T extends Item> void generateFold(List<List<T>> fold, TopItem<T> activeUser) {
		// TODO Auto-generated method stub
		for(int i = 0; i < activeUser.getItem().size() / 5; i++)
		{
			fold.get(0).add(activeUser.getItem().get(i)); //add all odd data to training list
		}
		
		for(int i = fold.get(0).size(); i < 2 *(activeUser.getItem().size() / 5); i++)
		{
			fold.get(1).add(activeUser.getItem().get(i)); //add all odd data to training list
		}
		
		for(int i = 2 * fold.get(0).size(); i < 3 *(activeUser.getItem().size() / 5); i++)
		{
			fold.get(2).add(activeUser.getItem().get(i)); //add all odd data to training list
		}
		
		for(int i = 3 * fold.get(0).size(); i < 4 *(activeUser.getItem().size() / 5); i++)
		{
			fold.get(3).add(activeUser.getItem().get(i)); //add all odd data to training list
		}
		
		for(int i = 4 * fold.get(0).size(); i < 5 *(activeUser.getItem().size() / 5); i++)
		{
			fold.get(4).add(activeUser.getItem().get(i)); //add all odd data to training list
		}
		
	}

	private <T extends Item, K extends TopItem<T>> float runModel(AdvancedModel<T, K> testEngine, List<K> userList)
	{
		int active = 0;
		
		TopItem<T> activeUser;
		
		float[] precision = new float[1000];
		while (active < test)
		{
			activeUser = userList.get(active); //set the active user
			
			if(totalPlays(activeUser) < 100) //if this user has listened to no music
			{
				active++;
				continue; //skip this user
			}
			
			List<T> fold1 = new ArrayList<T>();
			List<T> fold2 = new ArrayList<T>();
			List<T> fold3 = new ArrayList<T>();
			List<T> fold4 = new ArrayList<T>();
			List<T> fold5 = new ArrayList<T>();
			
			List<List<T>> fold = new ArrayList<List<T>>();
			
			fold.add(fold1);
			fold.add(fold2);
			fold.add(fold3);
			fold.add(fold4);
			fold.add(fold5);
			
			generateFold(fold, activeUser);
			
			float precisionFold[] = new float[5];
			for(List<T> testFold : fold)
			{
				//for each fold
				List<T> training = new ArrayList<T>();
				List<T> test = new ArrayList<T>();

				for(List<T> foldVal : fold)
				{
					if(foldVal.equals(testFold))
					{
						//empty contents into TEST ARTIST
						unfold(test, foldVal);
					}
					else
					{
						//empty contents into TRAINING ARTIST
						unfold(training, foldVal);
					}
				}
				
				
				
				//make follow code and generate average for each fold
				Map<T, Integer> recommended = new HashMap<T, Integer>();
				String username = activeUser.getAttr().getUser();
				recommended = testEngine.recommend(training, username);
				
				System.out.println();
				System.out.println("Recommendations for: " + activeUser.getAttr().getUser());
				
				precisionFold[fold.indexOf(testFold)] = evaluate(recommended, test);
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
	
	
	
	private <T> void unfold(List<T> list, List<T> foldVal) {
		// TODO Auto-generated method stub iterate of fold val and add to training artist
		for(T foldArtist: foldVal)
		{
			list.add(foldArtist);
		}
		
		
	}

	
	
	private <T extends Item> float evaluate(Map<T, Integer> recommended, List<T> testItem) {
		//this is where precision and stuff are calculated maybe
		float tp = 0;

		float fp = 0;

		List<Integer> actual = new ArrayList<Integer>();
		List<Integer> predicted = new ArrayList<Integer>();
		for (T recItem : recommended.keySet()) {
			boolean found = false;
			for (T activeuser : testItem) {

				if (recItem.getName().equals(activeuser.getName())) {
					found = true;
					actual.add(Integer.parseInt(activeuser.getAttr().getRank()));
					predicted.add(Integer.parseInt(recItem.getAttr().getRank()));

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


	private <T extends Item, K extends TopItem<T>> int totalPlays(K topitem) {
		int total = 0;

		for (T item : topitem.getItem()) {
			total += Integer.parseInt(item.getPlaycount());
		}

		return total;
	}
	
	
}
