package org.cloudfoundry.community.servicebroker.bugzilla.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.community.servicebroker.bugzilla.model.Catalog;
import org.cloudfoundry.community.servicebroker.bugzilla.model.Plan;
import org.cloudfoundry.community.servicebroker.bugzilla.model.ServiceDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogConfig {
	
	@Bean
	public Catalog catalog() {		
		return new Catalog( Arrays.asList(
				new ServiceDefinition(
					"Bugzilla", 
					"Bugzilla", 
					"A simple Bugzilla implementation", 
					true, 
					Arrays.asList(
							new Plan("Bugzilla-plan", 
									"Default Bugzilla Plan", 
									"This is a default Bugzilla plan.  All services are created equally.",
									getPlanMetadata())),
					Arrays.asList("Bugzilla", "document"),
					getServiceDefinitionMetadata(),
					null,null)));
	}
	
/* Used by Pivotal CF console */	
	
	private Map<String,Object> getServiceDefinitionMetadata() {
		Map<String,Object> sdMetadata = new HashMap<String,Object>();
		sdMetadata.put("displayName", "Bugzilla");
		sdMetadata.put("imageUrl","http://www.bugzilla.org/img/buggie.png");
		sdMetadata.put("longDescription","Bugzilla Service");
		sdMetadata.put("providerDisplayName","Pivotal");
		sdMetadata.put("documentationUrl","http://www.bugzilla.org/docs/");
		sdMetadata.put("supportUrl","http://www.bugzilla.org/");
		return sdMetadata;
	}
	
	private Map<String,Object> getPlanMetadata() {		
		Map<String,Object> planMetadata = new HashMap<String,Object>();
		planMetadata.put("costs", getCosts());
		planMetadata.put("bullets", getBullets());
		return planMetadata;
	}
	
	private List<Map<String,Object>> getCosts() {
		Map<String,Object> costsMap = new HashMap<String,Object>();
		
		Map<String,Object> amount = new HashMap<String,Object>();
		amount.put("usd", new Double(0.0));
	
		costsMap.put("amount", amount);
		costsMap.put("unit", "MONTHLY");
		
		return Arrays.asList(costsMap);
	}
	
	private List<String> getBullets() {
		return Arrays.asList("Uniqe user to Bugzilla", 
				"User as admin");
	}
	
}