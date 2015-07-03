package org.cloudfoundry.community.servicebroker.bugzilla.repository;

import org.cloudfoundry.community.servicebroker.bugzilla.model.ServiceInstance;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for ServiceInstance objects
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
public interface MongoServiceInstanceRepository extends MongoRepository<ServiceInstance, String> {

}