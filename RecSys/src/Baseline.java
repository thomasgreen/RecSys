import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import artistsPOJO.Artist;
import artistsPOJO.Topartists;

public class Baseline extends Recommender{

	
	
	public Baseline(int topNValue)
	{
		super(topNValue);
	}
	
	
	
	public Map<Artist, Integer> recommend()
	{
		
		///for every user
		//for every artist
		//checkunqie add to array and add up plays
		List<Artist> artistlist = new ArrayList<Artist>();
		List<Integer> artistplays = new ArrayList<Integer>();
		
		
		
		for(Topartists topartists : getTal()) //for each user
		{
			for(Artist artist : topartists.getArtist())
			{
				
				if(artistUnique(artist, artistlist)) //add plays
				{
					artistlist.add(artist);
					artistplays.add(Integer.parseInt(artist.getPlaycount()));
					
				}
				else
				{
					int index = -1;
					for (Artist artistinList : artistlist) {
						if (artistinList.getName().equals(artist.getName())) {
							index = artistlist.indexOf(artistinList);
						}
					}
					//find artist in list and update play count
					int newplays = Integer.parseInt(artist.getPlaycount());
					

					int oldplays = artistplays.get(index);
					
					artistplays.set(index, oldplays + newplays);
				}
				System.out.println(artist.getAttr().getRank());
			}
			
		}

		Map<Artist, Integer> artistPlayMap = new HashMap<Artist, Integer>();

		for(int i = 0; i < artistlist.size(); i++)
		{
			artistPlayMap.put(artistlist.get(i), artistplays.get(i));
		}
		
		Map<Artist, Integer> sortedrec = new LinkedHashMap<Artist, Integer>();
		
		
		sortedrec = sortByValue(artistPlayMap);
		
		return sortedrec;
	}
	
	
	


	private boolean artistUnique(Artist artist, List<Artist> nearestArtists) {

		for (Artist artistinList : nearestArtists) {
			if (artistinList.getName().equals(artist.getName())) {
				return false;
			}
		}
		return true;
	}

}
