package FriendsPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetFriends {

@SerializedName("friends")
@Expose
private Friends friends;

public Friends getFriends() {
return friends;
}

public void setFriends(Friends friends) {
this.friends = friends;
}

}