package com.c6server.entity;

public class LoginEntity {

    String nick;
    Integer count;
    byte[] nickEncoded;
    byte[] passEncoded;

    public LoginEntity() {
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public byte[] getNickEncoded() {
        return nickEncoded;
    }

    public void setNickEncoded(byte[] nickEncoded) {
        this.nickEncoded = nickEncoded;
    }

    public byte[] getPassEncoded() {
        return passEncoded;
    }

    public void setPassEncoded(byte[] passEncoded) {
        this.passEncoded = passEncoded;
    }
}
