package es.openkratio.colibribook.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by PulidF01 on 27/02/14.
 */
public class MemberResponse {

    private List<GroupMemberResponse> objects;

    public int count() {
        return objects.size();
    }

    public int getId(int index){
        return objects.get(index).getId();
    }

    public Member getMember(int index){
        return objects.get(index).getMember();
    }

    public int getPartyId(int index){
        String[] parts = objects.get(index).getPartyResourceUrl().split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }

    class GroupMemberResponse {

        private int id;
        @SerializedName("party")
        private String partyResourceUrl;
        private Member member;

        private int getId() {return id;}
        private String getPartyResourceUrl() {return partyResourceUrl;}
        private Member getMember() {return member;}


    }
}


