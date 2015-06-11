package com.champs21.spellingbee;

import com.google.gson.annotations.SerializedName;

/**
 * Created by BLACK HAT on 09-Jun-15.
 */
public class LeaderBoardModel {


    @SerializedName("user_fullname")
    private String userFullname;

    @SerializedName("school_name")
    private String schoolName;

    public String getName() {
        return userFullname;
    }

    public void setName(String userFullname) {
        this.userFullname = userFullname;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
