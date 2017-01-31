import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Topartists {

	@SerializedName("artist")
	@Expose
	private List<Artist> artist = null;
	
	@SerializedName("@attr")
	@Expose
	private Attr_ attr;

	public List<Artist> getArtist() {
		return artist;
	}

	public void setArtist(List<Artist> artist) {
		this.artist = artist;
	}

	public Attr_ getAttr() {
		return attr;
	}

	public void setAttr(Attr_ attr) {
		this.attr = attr;
	}

}