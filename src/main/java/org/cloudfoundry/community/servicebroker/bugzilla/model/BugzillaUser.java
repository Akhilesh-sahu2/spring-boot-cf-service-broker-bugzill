package org.cloudfoundry.community.servicebroker.bugzilla.model;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BugzillaUser {
	private String id;
	private String email;
	private String name;
	private String full_name;
	private String password;
	private String login_denied_text;
	private String token;
	
	private ArrayList<BugzillaUser> Users ;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogin_denied_text() {
		return login_denied_text;
	}

	public void setLogin_denied_text(String login_denied_text) {
		this.login_denied_text = login_denied_text;
	}

	public ArrayList<BugzillaUser> getUsers() {
		return Users;
	}

	public void setUsers(ArrayList<BugzillaUser> users) {
		Users = users;
	}
	
	

}
