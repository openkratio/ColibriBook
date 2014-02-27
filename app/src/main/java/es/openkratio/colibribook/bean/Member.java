package es.openkratio.colibribook.bean;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Member {

    @SerializedName(value = "avatar")
    private String avatarUrl; // Size: 132x165
    @SerializedName(value = "congress_id")
    private int congressId;
    @SerializedName(value = "congress_web")
    private String congressWeb;
    @SerializedName(value = "division")
    private String division;
    @SerializedName(value = "email")
    private String email;
    @SerializedName(value = "id")
    private int id;
    @SerializedName(value = "inscription_date")
    private transient Date inscriptionDate;
    @SerializedName(value = "name")
    private String name;
    private transient int partyId;
    @SerializedName(value = "resource_uri")
    private String resourceURI;
    @SerializedName(value = "second_name")
    private String secondName;
    @SerializedName(value = "termination_date")
    private transient Date terminationDate;
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

    public int getCongressId() {
        return congressId;
    }

    public void setCongressId(int congressId) {
        this.congressId = congressId;
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

    public Date getInscriptionDate() {
        return inscriptionDate;
    }

    public void setInscriptionDate(Date inscriptionDate) {
        this.inscriptionDate = inscriptionDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPartyId() {
        return partyId;
    }

    public void setPartyId(int partyId) {
        this.partyId = partyId;
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

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public String getTwitterUser() {
        if (twitterUrl != null) {
            String[] parts = twitterUrl.split("/");
            return parts[parts.length - 1];
        } else return null;
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
