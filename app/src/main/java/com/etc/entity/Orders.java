package com.etc.entity;

import java.io.Serializable;

public class Orders implements Serializable {
	//所有属性
	private int ordersID;
	private Users users;
	private Hotel hotel;
	private String room_number;
	private String room_type;
	private float user_rate;
	private float hotel_rate;
	private String user_comment;
	private String hotel_comment;
	private String checkin_time;
	private String checkout_time;
	private float realprice;
	private String state;
	private String resident;
	private String res_tel;
	
	//getters and setters
	public int getOrdersID() {
		return ordersID;
	}
	public void setOrdersID(int ordersID) {
		this.ordersID = ordersID;
	}
	public Users getUsers() {
		return users;
	}
	public void setUsers(Users users) {
		this.users = users;
	}
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
	public String getRoom_number() {
		return room_number;
	}
	public void setRoom_number(String room_number) {
		this.room_number = room_number;
	}
	public String getRoom_type() {
		return room_type;
	}
	public void setRoom_type(String room_type) {
		this.room_type = room_type;
	}
	public float getUser_rate() {
		return user_rate;
	}
	public void setUser_rate(float user_rate) {
		this.user_rate = user_rate;
	}
	public float getHotel_rate() {
		return hotel_rate;
	}
	public void setHotel_rate(float hotel_rate) {
		this.hotel_rate = hotel_rate;
	}
	public String getUser_comment() {
		return user_comment;
	}
	public void setUser_comment(String user_comment) {
		this.user_comment = user_comment;
	}
	public String getHotel_comment() {
		return hotel_comment;
	}
	public void setHotel_comment(String hotel_comment) {
		this.hotel_comment = hotel_comment;
	}
	public String getCheckin_time() {
		return checkin_time;
	}
	public void setCheckin_time(String checkin_time) {
		this.checkin_time = checkin_time;
	}
	public String getCheckout_time() {
		return checkout_time;
	}
	public void setCheckout_time(String checkout_time) {
		this.checkout_time = checkout_time;
	}
	public float getRealprice() {
		return realprice;
	}
	public void setRealprice(float realprice) {
		this.realprice = realprice;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getResident() {
		return resident;
	}
	public void setResident(String resident) {
		this.resident = resident;
	}
	public String getRes_tel() {
		return res_tel;
	}
	public void setRes_tel(String res_tel) {
		this.res_tel = res_tel;
	}
	
}
