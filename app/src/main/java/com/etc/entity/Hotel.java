package com.etc.entity;

import java.io.Serializable;
import java.util.List;

public class Hotel implements Serializable {
	
	//属性私有化
		private int hotelID;
		private String hotelname;
		private String realname;
		private String password;
		private String basic_info;
		private float safe_level;
		private float comment_level;
		private String type;
		private int visitor_number;
		private String province;
		private String city;
		private String location;
		private double x;
		private double y;
		private String photo;
		private String tel;
		private float lowestPrice;
		private List<Types> hotelTypes;
		private float com_level;
		
		//getter and setter
		public int getHotelID() {
			return hotelID;
		}
		public void setHotelID(int hotelID) {
			this.hotelID = hotelID;
		}
		public String getHotelname() {
			return hotelname;
		}
		public void setHotelname(String hotelname) {
			this.hotelname = hotelname;
		}
		public String getRealname() {
			return realname;
		}
		public void setRealname(String realname) {
			this.realname = realname;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getBasic_info() {
			return basic_info;
		}
		public void setBasic_info(String basic_info) {
			this.basic_info = basic_info;
		}
		public float getSafe_level() {
			return safe_level;
		}
		public void setSafe_level(float safe_level) {
			this.safe_level = safe_level;
		}
		public float getComment_level() {
			return comment_level;
		}
		public void setComment_level(float comment_level) {
			this.comment_level = comment_level;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public int getVisitor_number() {
			return visitor_number;
		}
		public void setVisitor_number(int visitor_number) {
			this.visitor_number = visitor_number;
		}
		public String getProvince() {
			return province;
		}
		public void setProvince(String province) {
			this.province = province;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		public double getX() {
			return x;
		}
		public void setX(double x) {
			this.x = x;
		}
		public double getY() {
			return y;
		}
		public void setY(double y) {
			this.y = y;
		}
		public String getPhoto() {
			return photo;
		}
		public void setPhoto(String photo) {
			this.photo = photo;
		}
		public String getTel() {
			return tel;
		}
		public void setTel(String tel) {
			this.tel = tel;
		}
		public List<Types> getHotelTypes() {
			return hotelTypes;
		}
		public void setHotelTypes(List<Types> hotelTypes) {
			this.hotelTypes = hotelTypes;
		}
		public float getLowestPrice() {
			return lowestPrice;
		}
		public void setLowestPrice(float lowestPrice) {
			this.lowestPrice = lowestPrice;
		}
		public float getCom_level() {
			return com_level;
		}
		public void setCom_level(float com_level) {
			this.com_level = com_level;
		}
}
