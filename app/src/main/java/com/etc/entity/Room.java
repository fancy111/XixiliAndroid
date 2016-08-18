package com.etc.entity;

import java.io.Serializable;

public class Room implements Serializable {
	//私有属性
	private String room_number;
	private Hotel hotel;
	private Types types;
	private boolean checked;
	
	//setters and getters
	public String getRoom_number() {
		return room_number;
	}
	public void setRoom_number(String room_number) {
		this.room_number = room_number;
	}
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
	public Types getTypes() {
		return types;
	}
	public void setTypes(Types types) {
		this.types = types;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
