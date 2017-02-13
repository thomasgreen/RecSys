package artistsPOJO;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import FriendsPOJO.Image;

public class Artist {

	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("playcount")
	@Expose
	private String playcount;
	@SerializedName("mbid")
	@Expose
	private String mbid;
	@SerializedName("url")
	@Expose
	private String url;
	@SerializedName("streamable")
	@Expose
	private String streamable;
	@SerializedName("image")
	@Expose
	private List<Image> image = null;
	@SerializedName("@attr")
	@Expose
	private Attr attr;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlaycount() {
		return playcount;
	}

	public void setPlaycount(String playcount) {
		this.playcount = playcount;
	}

	public String getMbid() {
		return mbid;
	}

	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getStreamable() {
		return streamable;
	}

	public void setStreamable(String streamable) {
		this.streamable = streamable;
	}

	public List<Image> getImage() {
		return image;
	}

	public void setImage(List<Image> image) {
		this.image = image;
	}

	public Attr getAttr() {
		return attr;
	}

	public void setAttr(Attr attr) {
		this.attr = attr;
	}

}