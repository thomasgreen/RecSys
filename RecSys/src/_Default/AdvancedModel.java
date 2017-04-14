package _Default;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import artistsPOJO.Artist;
import artistsPOJO.Topartists;
import tracksPOJO.Toptracks;
import tracksPOJO.Track;

public abstract class AdvancedModel<T, K> extends Recommender {
	//T represents the items used: artists or tracks
	//K represents the item of the second user to find similarity
	public AdvancedModel(int topNValue) {
		super(topNValue);
	}
	
	
	public abstract Map<T, Integer> recommend(List<T> trainingItems, String username);

	public abstract double pearson(Entry<String, Integer> entry, List<T> training);
	
	public abstract int similarity(List<T> training, K userB);
	
	public abstract int totalPlays(K topitem);
	
	public abstract boolean isUnique(T item, List<T> nearestItem);
	
	public abstract boolean isDuplicate(T item, List<T> trainingItems);
	
	public abstract int predictedrating(Map<String, Double> pearsons, T item, List<T> trainingItems);

	public int entryOfUser(String userB) {
		for (int i = 0; i < getTal().size(); i++) {
			boolean match = false;

			if (getTal().get(i).getAttr().getUser().equals(userB)) {
				match = true;

			}
			if (match) {
				return i;

			}

		}
		return 0;
	}
	
}
