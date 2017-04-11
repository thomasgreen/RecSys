import java.util.List;
import java.util.Map;

import artistsPOJO.Artist;

public abstract class AdvancedModel extends Recommender {

	public AdvancedModel(int topNValue) {
		super(topNValue);
	}
	
	
	public abstract Map<Artist, Integer> recommend(List<Artist> trainingArtist, String username);


}
