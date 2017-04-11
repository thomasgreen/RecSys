import java.util.List;
import java.util.Map;

import artistsPOJO.Artist;

public abstract class Recommender {

	
	public abstract Map<Artist, Integer> recommend(List<Artist> trainingArtist, String username);
}
