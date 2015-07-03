package org.cloudfoundry.community.servicebroker.bugzilla.repository;

import org.cloudfoundry.community.servicebroker.bugzilla.model.ServiceInstanceBinding;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for ServiceInstanceBinding objects
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
public interface MongoServiceInstanceBindingRepository extends MongoRepository<ServiceInstanceBinding, String> {

}
