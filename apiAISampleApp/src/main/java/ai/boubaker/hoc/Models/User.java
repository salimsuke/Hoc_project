package ai.boubaker.hoc.Models;

public class User {
    String nickname;
    String profileUrl;
    int userId;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public int getUserId() {return userId; }

    public void setUserId(int userId) {this.userId = userId;}
}
