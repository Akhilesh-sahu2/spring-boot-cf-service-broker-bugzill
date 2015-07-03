package org.cloudfoundry.community.servicebroker.bugzilla.service;

import java.util.HashMap;
import java.util.Map;


import org.cloudfoundry.community.servicebroker.bugzilla.model.Catalog;
import org.cloudfoundry.community.servicebroker.bugzilla.model.ServiceDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Mongo impl to manage service instances.  Creating a service does the following:
 * creates a new database,
 * saves the ServiceInstance info to the Mongo repository.
 *  
 * @author sgreenberg@gopivotal.com
 *
 */
@Service
public class BugzillaCatalogService implements CatalogService {

	private Catalog catalog;
	private Map<String,ServiceDefinition> serviceDefs = new HashMap<String,ServiceDefinition>();
	
	@Autowired
	public BugzillaCatalogService(Catalog catalog) {
		this.catalog = catalog;
		initializeMap();
	}
	
	private void initializeMap() {
		
		for (ServiceDefinition def: catalog.getServiceDefinitions()) {
			serviceDefs.put(def.getId(), def);
		}
	}
	
	@Override
	public Catalog getCatalog() {
		return catalog;
	}

	@Override
	public ServiceDefinition getServiceDefinition(String serviceId) {
		return serviceDefs.get(serviceId);
	}
}