package tracksPOJO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetTopTracks {

@SerializedName("toptracks")
@Expose
private Toptracks toptracks;

public Toptracks getToptracks() {
return toptracks;
}

public void setToptracks(Toptracks toptracks) {
this.toptracks = toptracks;
}

}