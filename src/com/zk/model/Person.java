package com.zk.model;

public class Person {
	private String name;
	private String password;
	private String tel;
	private String email;
	
	public Person() {
		
	}

	public Person(String name, String password, String tel, String email) {
		this.name = name;
		this.password = password;
		this.tel = tel;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String passsword) {
		this.password = passsword;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Persion [name=" + name + ", password=" + password + ", tel=" + tel + ", email=" + email + "]";
	}
	
}
