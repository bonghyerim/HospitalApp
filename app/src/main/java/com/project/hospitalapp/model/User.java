package com.project.hospitalapp.model;

public class User {

    private String email;
    private String password;
    private String nickname;

    // 디폴트생성자도 무조건 만들어준다


    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


}
