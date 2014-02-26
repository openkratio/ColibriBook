package es.openkratio.colibribook.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Party {

	@SerializedName(value = "id")
	private int id;
	@SerializedName(value = "logo")
	private String logoURL;
	@SerializedName(value = "members")
	private transient List<Member> members;
	@SerializedName(value = "name")
	private String name;
	@SerializedName(value = "resource_uri")
	private String resourceURI;
	@SerializedName(value = "validate")
	private boolean validate;
	@SerializedName(value = "web")
	private String webURL;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResourceURI() {
		return resourceURI;
	}

	public void setResourceURI(String resourceURI) {
		this.resourceURI = resourceURI;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String getWebURL() {
		return webURL;
	}

	public void setWebURL(String webURL) {
		this.webURL = webURL;
	}

}
