package org.cloudfoundry.community.servicebroker.bugzilla.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/** 
 * Force the base spring boot packages to be searched for dependencies.
 * 
 * @author akhilesh_sahu
 *
 */

@Configuration
@ComponentScan(basePackages = "org.cloudfoundry.community.servicebroker.bugzilla")
public class BrokerConfig {

}
