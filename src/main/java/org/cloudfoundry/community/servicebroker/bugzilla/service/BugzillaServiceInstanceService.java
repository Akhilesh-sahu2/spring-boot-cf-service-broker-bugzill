package org.cloudfoundry.community.servicebroker.bugzilla.service;


import java.io.IOException;
import java.util.List;

import org.cloudfoundry.community.servicebroker.bugzilla.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.bugzilla.exception.ServiceInstanceBindingExistsException;
import org.cloudfoundry.community.servicebroker.bugzilla.exception.ServiceInstanceExistsException;
import org.cloudfoundry.community.servicebroker.bugzilla.model.Product;
import org.cloudfoundry.community.servicebroker.bugzilla.model.ServiceDefinition;
import org.cloudfoundry.community.servicebroker.bugzilla.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.bugzilla.model.ServiceInstanceBinding;
import org.cloudfoundry.community.servicebroker.bugzilla.service.ServiceInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.cloudfoundry.community.servicebroker.bugzilla.exception.MongoServiceException;
import org.cloudfoundry.community.servicebroker.bugzilla.repository.*;



/**
 * Mongo impl to manage service instances.  Creating a service does the following:
 * creates a new database,
 * saves the ServiceInstance info to the 
 *  
 * @author sgreenberg@gopivotal.com
 *
 */
@Service
public class BugzillaServiceInstanceService implements ServiceInstanceService,ServiceInstanceBindingService {
	private static final Logger logger = LoggerFactory.getLogger(BugzillaServiceInstanceService.class);
	private BugzillaAdminService bugzilla;
	
	private MongoServiceInstanceRepository repository;
	
	@Autowired
	public BugzillaServiceInstanceService(BugzillaAdminService bugzilla) {
		this.bugzilla = bugzilla;
	}
	
	@Override
	public List<ServiceInstance> getAllServiceInstances() {
		
		logger.info("in getAllServiceInstances ");
		return repository.findAll();
	}

	@Override
	public ServiceInstance createServiceInstance(ServiceDefinition service,
			String serviceInstanceId, String planId, String organizationGuid,
			String spaceGuid) 
			throws ServiceInstanceExistsException, ServiceBrokerException {
	
		ServiceInstance instance = new ServiceInstance(serviceInstanceId, "Bugzilla",
				planId, organizationGuid, spaceGuid, null);
	
		Product product =new Product();
		try {
			product = bugzilla.createProduct(instance.getId());
			logger.info("serviceDefinition id here is product.getId()  "+product.getId());
			logger.info("serviceDefinition id here is product.getName()  "+product.getName());
			System.out.println(product.getName());
		
		if (product.getId() == null) {
			throw new ServiceBrokerException("Failed to create new Bugzilla Product: " + instance.getId());
		}
		instance.setDashboardUrl(product.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
		return instance;		
	}
	

	@Override
	public ServiceInstance getServiceInstance(String id) {
		return repository.findOne(id);
	}

	@Override
	public ServiceInstance deleteServiceInstance(String id) throws MongoServiceException {
		bugzilla.deProvisionedProductAndGroup(id);
		ServiceInstance instance=null;
		return instance;
	}

	@Override
	public ServiceInstanceBinding createServiceInstanceBinding(
			String bindingId, ServiceInstance serviceInstance,
			String serviceId, String planId, String appGuid)
			throws ServiceInstanceBindingExistsException,
			ServiceBrokerException {
		// TODO Auto-generated method stub
		ServiceInstanceBinding sib=bugzilla.createInstanceBinding(serviceInstance,bindingId,serviceId,planId,appGuid);		
		return sib;
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceInstanceBinding deleteServiceInstanceBinding(String id)
			throws ServiceBrokerException {
		// TODO Auto-generated method stub
		ServiceInstanceBinding sib=bugzilla.deleteInstanceBinding(id);
		// TODO Auto-generated method stub
		return sib;
	}

}