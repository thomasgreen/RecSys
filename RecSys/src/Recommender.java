import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import artistsPOJO.Topartists;

public abstract class Recommender {

	private List<Topartists> tal; //list of all users + data in file

	private Topartists activeUser; //the active user getting recommendation

	private int topN; //how many recommendations to leave 

	
	
	public Recommender(int topNValue)
	{
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
				topN = topNValue;
				
	}
	
	
	
	public List<Topartists> getTal() {
		return tal;
	}
	public Topartists getActiveUser() {
		return activeUser;
	}
	public int getTopN() {
		return topN;
	}

	public void setTal(List<Topartists> tal) {
		this.tal = tal;
	}
	public void setActiveUser(Topartists activeUser) {
		this.activeUser = activeUser;
	}
	public void setTopN(int topN) {
		this.topN = topN;
	}

}
