package org.cloudfoundry.community.servicebroker.bugzilla.model;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;




@Configuration
@PropertySource("classpath:bugzillaResource.properties")
@Import(PropertyPlaceholderConfig.class)
public class BugzillaResource {

	 @Value("${bugzillaServer}")
	 private String bugzillaServer;
	 @Value("${bugzillaApiKey}")
	 private String bugzillaApiKey;
	 @Value("${restProductUrl}")
	 private String restProductUrl;
	 
	 
	public String getBugzillaServer() {
		return bugzillaServer;
	}
	public void setBugzillaServer(String bugzillaServer) {
		this.bugzillaServer = bugzillaServer;
	}
	public String getBugzillaApiKey() {
		return bugzillaApiKey;
	}
	public void setBugzillaApiKey(String bugzillaApiKey) {
		this.bugzillaApiKey = bugzillaApiKey;
	}
	public String getRestProductUrl() {
		return restProductUrl;
	}
	public void setRestProductUrl(String restProductUrl) {
		this.restProductUrl = restProductUrl;
	}
	
//	public static void main(String args[]){
//		BugzillaResource bug = new BugzillaResource();
//		System.out.println(bug.getBugzillaApiKey());
//		//System.out.println("hi "+ bug.env.getProperty("bugzillaServer"));
//		System.out.println(bug.getBugzillaResource().getBugzillaApiKey());
//	}
	
	
/*	public static void main(String[] args) {
		try {
			File file = new File("F:\\BluemixSTS\\spring-boot-cf-service-broker-bugzilla\\src\\main\\java\\org\\cloudfoundry\\community\\servicebroker\\bugzilla\\service\\bugzillaResource.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			Enumeration enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);
				System.out.println(key + ": " + value);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	 
	 
}
