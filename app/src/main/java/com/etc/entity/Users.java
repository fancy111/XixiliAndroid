package com.etc.entity;

import java.io.Serializable;

public class Users implements Serializable{

	private int userID;
	private String username;
	private String password;
	private String photo;
	private String regtime;
	private float credit_level;
	private int score;
	private String str;
	
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getRegtime() {
		return regtime;
	}
	public void setRegtime(String regtime) {
		this.regtime = regtime;
	}
	public float getCredit_level() {
		return credit_level;
	}
	public void setCredit_level(float credit_level) {
		this.credit_level = credit_level;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	
	
}
