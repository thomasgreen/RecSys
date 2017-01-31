import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attr_ {

	@SerializedName("user")
	@Expose
	private String user;
	
	@SerializedName("page")
	@Expose
	private String page;
	
	@SerializedName("perPage")
	@Expose
	private String perPage;
	
	@SerializedName("totalPages")
	@Expose
	private String totalPages;
	
	@SerializedName("total")
	@Expose
	private String total;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Attr_ withUser(String user) {
		this.user = user;
		return this;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public Attr_ withPage(String page) {
		this.page = page;
		return this;
	}

	public String getPerPage() {
		return perPage;
	}

	public void setPerPage(String perPage) {
		this.perPage = perPage;
	}

	public Attr_ withPerPage(String perPage) {
		this.perPage = perPage;
		return this;
	}

	public String getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(String totalPages) {
		this.totalPages = totalPages;
	}

	public Attr_ withTotalPages(String totalPages) {
		this.totalPages = totalPages;
		return this;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public Attr_ withTotal(String total) {
		this.total = total;
		return this;
	}

}