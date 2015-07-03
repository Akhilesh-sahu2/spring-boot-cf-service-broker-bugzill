package org.cloudfoundry.community.servicebroker.bugzilla.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.cloudfoundry.community.servicebroker.bugzilla.exception.*;
import org.cloudfoundry.community.servicebroker.bugzilla.model.*;
import org.cloudfoundry.community.servicebroker.bugzilla.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * See: http://docs.cloudfoundry.com/docs/running/architecture/services/writing-service.html
 * 
 * @author sgreenberg@gopivotal.com
 */
@Controller
public class ServiceInstanceController extends BaseController {

	public static final String BASE_PATH = "/v2/service_instances";
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceController.class);
	
	private BugzillaServiceInstanceService service;
	private BugzillaCatalogService catalogService;
	
	@Autowired
 	public ServiceInstanceController(BugzillaServiceInstanceService service, BugzillaCatalogService catalogService) {
 		this.service = service;
 		this.catalogService = catalogService;
 	}
	
	@RequestMapping(value = BASE_PATH, method = RequestMethod.GET)
	public @ResponseBody List<ServiceInstance> getServiceInstances() {
		logger.debug("in getServiceInstances 1");
		logger.debug("GET: " + BASE_PATH + ", getServiceInstances()");
		return service.getAllServiceInstances();
	}
		
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.PUT)
	public ResponseEntity<CreateServiceInstanceResponse> createServiceInstance(
			@PathVariable("instanceId") String serviceInstanceId, 
			@Valid @RequestBody CreateServiceInstanceRequest request) throws
			ServiceDefinitionDoesNotExistException,
			ServiceInstanceExistsException,
			ServiceBrokerException {
		logger.debug("PUT: " + BASE_PATH + "/{instanceId}" 
				+ ", createServiceInstance(), serviceInstanceId = " + serviceInstanceId);
		logger.debug("Helloooooooooooooo " + BASE_PATH + "/{instanceId}" 
				+ ", createServiceInstance(), serviceInstanceId = " + serviceInstanceId+"plan_id = "+request.getPlanId()+"organization_guid = "+request.getOrganizationGuid());
		logger.info("createServiceInstance Name "+request.getName());
		logger.info("createServiceInstance getServiceGuId "+request.getServiceGuId());
		logger.info("createServiceInstance getSpaceGuid "+request.getSpaceGuid());
		ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());
		System.out.println("Bugzilla getBugzillaResource().getBugzillaServer()  "+getBugzillaResource().getBugzillaServer());
		ServiceInstance instance = service.createServiceInstance(
				svc, 
				serviceInstanceId, 
				request.getPlanId(),
				request.getOrganizationGuid(), 
				request.getSpaceGuid());
		logger.debug("ServiceInstance Created: " + instance.getId());
		System.out.println("ServiceInstance Created: ");
        return new ResponseEntity<CreateServiceInstanceResponse>(
        		new CreateServiceInstanceResponse(instance), 
        		HttpStatus.CREATED);
	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceInstance(
			@PathVariable("instanceId") String instanceId, 
			@RequestParam("service_id") String serviceId,
			@RequestParam("plan_id") String planId) throws ServiceBrokerException {
		logger.debug("in deleteServiceInstance 1");
		logger.debug( "DELETE: " + BASE_PATH + "/{instanceId}" 
				+ ", deleteServiceInstanceBinding(), serviceInstanceId = " + instanceId 
				+ ", serviceId = " + serviceId
				+ ", planId = " + planId);
		ServiceInstance instance = service.deleteServiceInstance(instanceId);
		logger.debug("ServiceInstance Deleted: " + instanceId);
        return new ResponseEntity<String>("{}", HttpStatus.OK);
	}
	
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}"+"/service_bindings"+"/{bindingId}", method = RequestMethod.PUT)
	public ResponseEntity<ServiceInstanceBindingResponse> bindingServiceInstance(
			@PathVariable("instanceId") String serviceInstanceId, 
			@PathVariable("bindingId") String bindingInstanceId,
			@Valid @RequestBody ServiceInstanceBindingRequest request) throws
			ServiceDefinitionDoesNotExistException,
			ServiceInstanceExistsException,
			ServiceBrokerException, ServiceInstanceBindingExistsException {
		    logger.info("Bugzilla in bindingServiceInstance 1 service_id "+request.getServiceDefinitionId());
		
			ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());
			logger.info("Bugzilla in bindingServiceInstance 2");
		    ServiceInstance serviceInstance=new ServiceInstance(serviceInstanceId, "Bugzilla", request.getPlanId(), null, null, null);

			ServiceInstanceBinding serviceBinding;
			logger.info("Bugzilla bindingServiceInstance bindingInstanceId "+bindingInstanceId);
			logger.info("Bugzilla bindingServiceInstance serviceInstance "+serviceInstance);
			logger.info("Bugzilla bindingServiceInstance  request.getServiceDefinitionId()"+request.getServiceDefinitionId());
			logger.info("Bugzilla bindingServiceInstance request.getPlanId() "+request.getPlanId());
			logger.info("Bugzilla bindingServiceInstance request.getAppGuid() "+request.getAppGuid());
			logger.info("Bugzilla bindingServiceInstance request.getServiceInstanceId() "+request.getServiceInstanceGuId());
			serviceBinding = service.createServiceInstanceBinding(bindingInstanceId, serviceInstance, request.getServiceDefinitionId(), request.getPlanId(), request.getAppGuid());
			logger.info("Bugzilla bindingServiceInstance After service binding "+serviceBinding.getAppGuid());
		  return new ResponseEntity<ServiceInstanceBindingResponse>(new ServiceInstanceBindingResponse(serviceBinding),HttpStatus.CREATED);

	}
	
	
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}"+"/service_bindings"+"/{bindingId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceBindingInstance(
			@PathVariable("instanceId") String instanceId, 
			@PathVariable("bindingId") String bindingId,
			@RequestParam("service_id") String serviceId,
			@RequestParam("plan_id") String planId) throws ServiceBrokerException {
		logger.debug( "DELETE: " + BASE_PATH + "/{instanceId}" 
				+ ", deleteServiceInstanceBinding(), serviceInstanceId = " + instanceId 
				+ ", serviceId = " + serviceId
				+ ", planId = " + planId);		
		ServiceInstanceBinding serviceInstanceBinding=service.deleteServiceInstanceBinding(instanceId);
		logger.debug("ServiceInstance Unbinding: " + instanceId);
        return new ResponseEntity<String>("{}", HttpStatus.OK);
	}
	
	
	
	
	@ExceptionHandler(ServiceDefinitionDoesNotExistException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceDefinitionDoesNotExistException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(ServiceInstanceExistsException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceInstanceExistsException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
	
	@Bean(name = "bugzillaResource")
	public BugzillaResource getBugzillaResource() {
		BugzillaResource bugzillaResource = new BugzillaResource();
		return bugzillaResource;
	}
	
}
