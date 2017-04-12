import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import artistsPOJO.Topartists;

public abstract class Recommender {

	private List<Topartists> tal; //list of all users + data in file

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

				topN = topNValue;
				
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
	
	
	public List<Topartists> getTal() {
		return tal;
	}

	public int getTopN() {
		return topN;
	}

	public void setTal(List<Topartists> tal) {
		this.tal = tal;
	}

	public void setTopN(int topN) {
		this.topN = topN;
	}

}
