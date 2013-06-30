package es.openkratio.colibribook.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Member {

	@SerializedName(value = "avatar")
	private String avatarUrl; // Size: 132x165
	@SerializedName(value = "congress_web")
	private String congressWeb;
	@SerializedName(value = "division")
	private String division;
	@SerializedName(value = "email")
	private String email;
	@SerializedName(value = "id")
	private int id;
	@SerializedName(value = "name")
	private String name;
	@SerializedName(value = "parties")
	private transient List<Party> parties;
	@SerializedName(value = "resource_uri")
	private String resourceURI;
	@SerializedName(value = "second_name")
	private String secondName;
	@SerializedName(value = "twitter")
	private String twitterUrl;
	@SerializedName(value = "validate")
	private boolean validate;
	@SerializedName(value = "web")
	private String webpage;

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getCongressWeb() {
		return congressWeb;
	}

	public void setCongressWeb(String congressWeb) {
		this.congressWeb = congressWeb;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Party> getParties() {
		return parties;
	}

	public void setParties(List<Party> parties) {
		this.parties = parties;
	}

	public String getResourceURI() {
		return resourceURI;
	}

	public void setResourceURI(String resourceURI) {
		this.resourceURI = resourceURI;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getTwitterUrl() {
		return twitterUrl;
	}

	public String getTwitterUser() {
		String[] parts = twitterUrl.split("/");
		return parts[parts.length - 1];
	}

	public void setTwitterUser(String twitterUrl) {
		this.twitterUrl = twitterUrl;
	}

	public boolean isValidate() {
		return validate;
	}

	public int isValidateInt() {
		int validated = this.validate ? 1 : 0;
		return validated;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String getWebpage() {
		return webpage;
	}

	public void setWebpage(String webpage) {
		this.webpage = webpage;
	}

}
