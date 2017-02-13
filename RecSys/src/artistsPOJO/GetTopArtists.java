
package artistsPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetTopArtists {

	@SerializedName("topartists")
	@Expose
	private Topartists topartists;

	public Topartists getTopartists() {
		return topartists;
	}

	public void setTopartists(Topartists topartists) {
		this.topartists = topartists;
	}

}