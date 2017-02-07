package UserPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfo {
	// created using http://www.jsonschema2pojo.org/
	@SerializedName("user")
	@Expose
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}