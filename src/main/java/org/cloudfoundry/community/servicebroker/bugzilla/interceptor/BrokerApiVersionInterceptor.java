package org.cloudfoundry.community.servicebroker.bugzilla.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cloudfoundry.community.servicebroker.bugzilla.exception.ServiceBrokerApiVersionException;
import org.cloudfoundry.community.servicebroker.bugzilla.model.BrokerApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class BrokerApiVersionInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired(required = false)
	private BrokerApiVersion version;
	
	public BrokerApiVersionInterceptor() {}
	
	public BrokerApiVersionInterceptor(BrokerApiVersion version) {
		this.version = version;
	}
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws ServiceBrokerApiVersionException {	
		if (version != null) {
			String apiVersion = request.getHeader(version.getBrokerApiVersionHeader());
			if (!version.getApiVersion().equals(apiVersion)) {
				throw new ServiceBrokerApiVersionException(version.getApiVersion(), apiVersion);
			} 
		}
		return true;
	}
	
}
