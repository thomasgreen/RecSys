import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

	public Artist withName(String name) {
		this.name = name;
		return this;
	}

	public String getPlaycount() {
		return playcount;
	}

	public void setPlaycount(String playcount) {
		this.playcount = playcount;
	}

	public Artist withPlaycount(String playcount) {
		this.playcount = playcount;
		return this;
	}

	public String getMbid() {
		return mbid;
	}

	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

	public Artist withMbid(String mbid) {
		this.mbid = mbid;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Artist withUrl(String url) {
		this.url = url;
		return this;
	}

	public String getStreamable() {
		return streamable;
	}

	public void setStreamable(String streamable) {
		this.streamable = streamable;
	}

	public Artist withStreamable(String streamable) {
		this.streamable = streamable;
		return this;
	}

	public List<Image> getImage() {
		return image;
	}

	public void setImage(List<Image> image) {
		this.image = image;
	}

	public Artist withImage(List<Image> image) {
		this.image = image;
		return this;
	}

	public Attr getAttr() {
		return attr;
	}

	public void setAttr(Attr attr) {
		this.attr = attr;
	}

	public Artist withAttr(Attr attr) {
		this.attr = attr;
		return this;
	}

}