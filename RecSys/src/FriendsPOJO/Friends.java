package FriendsPOJO;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import UserPOJO.User;

public class Friends {

	@SerializedName("user")
	@Expose
	private List<User> user = null;
	@SerializedName("@attr")
	@Expose
	private Attr attr;

	public List<User> getUser() {
		return user;
	}

	public void setUser(List<User> user) {
		this.user = user;
	}

	public Attr getAttr() {
		return attr;
	}

	public void setAttr(Attr attr) {
		this.attr = attr;
	}


}