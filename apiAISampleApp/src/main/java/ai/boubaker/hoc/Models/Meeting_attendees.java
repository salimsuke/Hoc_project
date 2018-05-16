package ai.boubaker.hoc.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bouba on 27-Feb-18.
 */

public class Meeting_attendees {
    @SerializedName("id_meeting")
    @Expose
    int id_meeting;
    @SerializedName("id_visitor")
    @Expose
    int id_visitor;
    public Meeting_attendees(){

    }
    public int getId_meeting() {
        return id_meeting;
    }

    public void setId_meeting(int id_meeting) {
        this.id_meeting = id_meeting;
    }

    public int getId_visitor() {
        return id_visitor;
    }

    public void setId_visitor(int id_visitor) {
        this.id_visitor = id_visitor;
    }

    public Meeting_attendees(int id_meeting, int id_visitor) {
        this.id_meeting = id_meeting;
        this.id_visitor = id_visitor;
    }
}
