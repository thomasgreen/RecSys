package tracksPOJO;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import _Default.TopItem;
import artistsPOJO.Attr_;

public class Toptracks extends TopItem<Track>{

@SerializedName("track")
@Expose
private List<Track> track = null;
@SerializedName("@attr")
@Expose
private Attr_ attr;

public List<Track> getItem() {
return track;
}

public void setTrack(List<Track> track) {
this.track = track;
}

public Attr_ getAttr() {
return attr;
}

public void setAttr(Attr_ attr) {
this.attr = attr;
}

}