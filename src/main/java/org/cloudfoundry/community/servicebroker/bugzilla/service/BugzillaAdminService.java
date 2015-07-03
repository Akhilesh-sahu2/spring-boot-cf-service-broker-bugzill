package org.cloudfoundry.community.servicebroker.bugzilla.service;

import org.cloudfoundry.community.servicebroker.bugzilla.exception.MongoServiceException;
import org.cloudfoundry.community.servicebroker.bugzilla.model.BugzillaResource;
import org.cloudfoundry.community.servicebroker.bugzilla.model.BugzillaUser;
import org.cloudfoundry.community.servicebroker.bugzilla.model.Product;
import org.cloudfoundry.community.servicebroker.bugzilla.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.bugzilla.model.ServiceInstanceBinding;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.mongodb.MongoException;

/**
 * Utility class for manipulating a Mongo database.
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
@Service
//@PropertySource("classpath:org/cloudfoundry/community/servicebroker/bugzilla/service/bugzillaResource.properties")
public class BugzillaAdminService {

	public static final String BUGZILLA_SERVER  = "http://54.165.240.15:80/bugzilla/"; //Url for server where bugzilla installed
	public static final String BUGZILLA_API_KEY = "ANbauTDOY6Au73RbgyEJxrvTUTrF0zjX9NFpyOm0";//BUGZILLA_API_KEY which is genrate for the super admin user so that used any time with Rest for login 
	public static final String REST_PRODUCT_URL = BUGZILLA_SERVER+"rest.cgi/product?Bugzilla_api_key="+BUGZILLA_API_KEY;
	public static final String REST_COMPONENT_URL = BUGZILLA_SERVER+"rest.cgi/component?Bugzilla_api_key="+BUGZILLA_API_KEY;
	public static final String REST_GET_PRODUCT_DETAIL_URL = BUGZILLA_SERVER+"rest.cgi/product";
	public static final String REST_CREATE_USER_URL = BUGZILLA_SERVER+"rest.cgi/user?Bugzilla_api_key="+BUGZILLA_API_KEY;
	public static final String REST_UPDATE_USER_URL = BUGZILLA_SERVER+"rest.cgi/user/";//?Bugzilla_api_key="+BUGZILLA_API_KEY;
	
	public static final String REST_PRODUCT_GROUUP_URL = BUGZILLA_SERVER+"rest.cgi/group/name=";
	public static final String REST_GET_USER_BY_MATCH = BUGZILLA_SERVER+"rest.cgi/user";
	public static final String REST_PRODUCT_UNBIND = BUGZILLA_SERVER+"rest.cgi/product";
	public static final String REST_GROUP_UNBIND = BUGZILLA_SERVER+"rest.cgi/group";

	private Logger logger = LoggerFactory.getLogger(BugzillaAdminService.class);
	private URI url ;
    private static final String CHAR_LIST ="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RANDOM_STRING_LENGTH = 10;
         
    /**
     * This method generates random string and used for password
     * @return String
     */
    public String generatePassword(){
         
        StringBuffer randStr = new StringBuffer();
        for(int i=0; i<RANDOM_STRING_LENGTH; i++){
            int number = getRandomNumber();
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }
        
    /**
     * getRandomNumber used to get the random number to create a random string
     * @return int
     */
    private int getRandomNumber() {
        int randomInt = 0;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(CHAR_LIST.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }
	
	@Bean(name = "bugzillaResource")
	public BugzillaResource getBugzillaResource() {
		BugzillaResource bugzillaResource = new BugzillaResource();
		return bugzillaResource;
	}
	

	@Bean(name = "restTemplate")
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
	


/*	public static void main(String[] args) throws IOException, Exception {
		BugzillaAdminService bug = new BugzillaAdminService();
		// bug.getUser("dass");
		//bug.createProduct("ProductGroup1");
		//bug.getBugzillaProduct("TestProductCreation6");
		//bug.updateProductGroup("TestProductCreation6");
	//	bug.createBugzillaUser("akhilesh_sahu2@persistent.co.in");
		// bug.isUserExists2("admin@example.com");
		BugzillaUser user = new BugzillaUser();
		user.setEmail("akhilesh_sahu@persistent.co.in");
		user.setPassword("flower");
		bug.getLoggedInUser(user);
		String s = bug.generatePassword();
		System.out.println(s);
	}*/


	/**
	 * @name createProduct
	 * @description Method is used in provisioning of service to create
	 * the product for bugzilla by taking the service_instance as a name
	 * whenever the product is created at the same time with same name the group for that product is created by bugzilla 
	 * internally  
	 * @param productName
	 * @return Product
	 * @throws IOException
	 */

	public Product createProduct(String productName) throws IOException {
		if(getBugzillaProduct(productName).getProducts()!=null && getBugzillaProduct(productName).getProducts().size()!=0){
			updateProductGroup(productName);
			return getBugzillaProduct(productName).getProducts().get(0);
		}
		Product bugzillaProduct = new Product(); 
		setProperties();		
		try {
			logger.info("Bugzilla getBugzillaResource().getBugzillaServer()  "+getBugzillaResource().getBugzillaServer());
			url = new URI(REST_PRODUCT_URL);
			MultiValueMap<String, String> productData = new LinkedMultiValueMap<String, String>();
			productData.add("name", productName);
			productData.add("description", productName+" as description");
			productData.add("version", "1.0");
			HttpEntity<?> requestEntity = new HttpEntity<Object>(productData,createHeaders());
			ResponseEntity<String> responseEntity = getRestTemplate().exchange(url, HttpMethod.POST, requestEntity, String.class);
			ObjectMapper mapper = new ObjectMapper();
			bugzillaProduct = mapper.readValue(responseEntity.getBody(), Product.class);
			logger.info("Bugzilla Product Created  "+bugzillaProduct.getId());
			if(bugzillaProduct.getId()!=null){
				bugzillaProduct = getBugzillaProduct(productName).getProducts().get(0);				
				updateProductGroup(productName);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bugzillaProduct;
	}
	
	
	/**
	 * @name createProduct
	 * @description Method is used in provisioning of service to create
	 * the product for bugzilla by taking the service_instance as a name
	 * whenever the product is created at the same time with same name the group for that product is created by bugzilla 
	 * internally  
	 * @param productName
	 * @return Product
	 * @throws IOException
	 */

	public Product createComponent(String productName) throws IOException {
		
		Product bugzillaProduct = new Product(); 
		setProperties();		
		try {
			url = new URI(REST_COMPONENT_URL);
			MultiValueMap<String, String> componentData = new LinkedMultiValueMap<String, String>();
			componentData.add("product", productName);
			componentData.add("name", "New "+productName);
			componentData.add("description", productName+" as description");
			//componentData.add("default_assignee", "akhilesh_sahu@persistent.co.in");
			componentData.add("default_assignee", "admin@"+productName+".com");
			HttpEntity<?> requestEntity = new HttpEntity<Object>(componentData,createHeaders());
			ResponseEntity<String> responseEntity = getRestTemplate().exchange(url, HttpMethod.POST, requestEntity, String.class);
			ObjectMapper mapper = new ObjectMapper();
			bugzillaProduct = mapper.readValue(responseEntity.getBody(), Product.class);
			logger.info("Bugzilla Product Created  "+bugzillaProduct.getId());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bugzillaProduct;
	}
	
	/**
	 * @name getBugzillaProduct 
	 * @description this method is used to fetch the product from bugzilla 
	 * @param productName
	 * @return Product
	 */
	private Product getBugzillaProduct(String productName){
		Product bugzillaProduct = new Product();
		setProperties();		
		try {
			url = new URI(REST_GET_PRODUCT_DETAIL_URL+"?names="+productName);
			HttpEntity<?> requestEntity = new HttpEntity<Object>(createHeaders());
			ResponseEntity<String> responseEntity = getRestTemplate().exchange(url, HttpMethod.GET, requestEntity, String.class);
			ObjectMapper mapper = new ObjectMapper();
			bugzillaProduct = mapper.readValue(responseEntity.getBody(), Product.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bugzillaProduct;
	}
	
	/**
	 * @name updateProductGroup
	 * @description this method is used at the time when product is created, and used for updating the group with
	 * user_regexp so that whenever any user is created with the name as product name or group name then that user has 
	 * default access of that group
	 * @param productName
	 * @return
	 */
	private Boolean updateProductGroup(String productName){//productName as a group name
		try {
			setProperties();		
			url = new URI(REST_PRODUCT_GROUUP_URL+productName+"?Bugzilla_api_key="+BUGZILLA_API_KEY);
			System.out.println(url);
			String groupupdate ="{ \"names\": [\""+productName+"\"],\"user_regexp\": \"@"+productName+"\\\\.com$\"}";
			HttpEntity<?> requestEntity = new HttpEntity<Object>(groupupdate,createHeaders());
			System.out.println(groupupdate);
			ResponseEntity<String> responseEntity = getRestTemplate().exchange(url, HttpMethod.PUT, requestEntity, String.class);
			if(responseEntity.getBody()!=null){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}

	private HttpHeaders createHeaders() {
		return new HttpHeaders() {
			{
				set("Accept", "application/json");
			}
		};
	}

	
	private void setProperties() {
		/*Properties sysProperties = System.getProperties();
		sysProperties.put("proxyHost", "hjbc1.persistent.co.in");
		sysProperties.put("proxyPort", "8080");
		sysProperties.put("proxySet", "true");*/
	}
	
/**
 * @name createInstanceBinding
 * @description this method is used at the time of binding the service with application
 * @param serviceInstance
 * @param bindingId
 * @param serviceId
 * @param planId
 * @param appGuid
 * @return ServiceInstanceBinding with credential
 */
	public ServiceInstanceBinding createInstanceBinding(
			ServiceInstance serviceInstance, String bindingId,
			String serviceId, String planId, String appGuid) {
		Map<String, Object> credentials = new HashMap<String, Object>();
		String uri = new String();
		ServiceInstanceBinding serviceInstanceBinding;
			BugzillaUser bugzillaUser = createBugzillaUser( serviceInstance);
			bugzillaUser = getLoggedInUser(bugzillaUser);
			while (bugzillaUser.getId() != null) {
				if (bugzillaUser.getToken()!=null) {
					uri = "http://54.165.240.15:80/bugzilla/";
					credentials.put("uri", uri);
					credentials.put("host", new String("54.165.240.15"));
					credentials.put("port", new String("80"));
					credentials.put("UserName", bugzillaUser.getEmail());
					credentials.put("Password", bugzillaUser.getPassword());
					break;
				}
			}
			logger.info("Bugzilla createInstanceBinding bindingInstanceId "+serviceInstance);
			logger.info("Bugzilla createInstanceBinding  planId "+planId);
			logger.info("Bugzilla createInstanceBinding appGuid "+appGuid);
			serviceInstanceBinding = new ServiceInstanceBinding(bindingId,
					serviceInstance.getId(), credentials, null, appGuid);
			return serviceInstanceBinding;
	}
	
	/**
	 * @name createBugzillaUser
	 * @description this method is used to create user for bugzilla at the time of binding 
	 * as the name as admin@serviceid.com as the service id is used to create the product and group 
	 * so by default this user will associate that group and product
	 * @param serviceInstance
	 * @return
	 */

	public BugzillaUser createBugzillaUser(ServiceInstance serviceInstance) {
		String userMailId = "admin@"+serviceInstance.getId()+".com";
		final String password = generatePassword();
		BugzillaUser bugzillaUser = new BugzillaUser();
		setProperties();
		try {
			url = new URI(REST_CREATE_USER_URL);
			MultiValueMap<String, String> userData = new LinkedMultiValueMap<String, String>();
			userData.add("email", userMailId);//a valid email required
			userData.add("full_name",userMailId);
			userData.add("password",password );
			HttpEntity<?> requestEntity = new HttpEntity<Object>(userData,createHeaders());
			ResponseEntity<String> responseEntity = getRestTemplate().exchange(	url, HttpMethod.POST, requestEntity, String.class);			
			ObjectMapper mapper = new ObjectMapper();
			bugzillaUser = mapper.readValue(responseEntity.getBody(), BugzillaUser.class);
			if(bugzillaUser.getId()!=null){
				bugzillaUser.setEmail(userMailId);
				bugzillaUser.setPassword(password);
			}
			logger.info("Bugzilla User Created  "+bugzillaUser.getId());
			updateBugzillaUserAsAdmin(bugzillaUser);
			createComponent(serviceInstance.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
			return bugzillaUser;
	}
	
	public Boolean updateBugzillaUserAsAdmin(BugzillaUser bugzillaUser) {			
		setProperties();
		try {
			url = new URI(REST_UPDATE_USER_URL+bugzillaUser.getEmail()+"?Bugzilla_api_key="+BUGZILLA_API_KEY);
			System.out.println("in update group "+url.toASCIIString());
			String userdate ="{\"name\" : \""+bugzillaUser.getEmail()+"\", \"groups\" : { \"set\" : [\"admin\"] }}";
			HttpEntity<?> requestEntity = new HttpEntity<Object>(userdate,createHeaders());
			System.out.println(userdate);
			ResponseEntity<String> responseEntity = getRestTemplate().exchange(url, HttpMethod.PUT, requestEntity, String.class);
			if(responseEntity.getBody()!=null){
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			return false;
	}
	
	/**
	 * @name getLoggedInUser 
	 * @description this method return a logged in user in bugzilla system 
	 * @param bugzillaUser
	 * @return BugzillaUser
	 */
	
	public BugzillaUser getLoggedInUser(BugzillaUser bugzillaUser) {
		setProperties();
		try {
			url = new URI("http://54.165.240.15:80/bugzilla/rest.cgi/login?login="+bugzillaUser.getEmail()+"&password="+bugzillaUser.getPassword());
			HttpEntity<?> requestEntity = new HttpEntity<Object>(createHeaders());
			ResponseEntity<String> responseEntity = getRestTemplate().exchange(	url, HttpMethod.GET, requestEntity, String.class);			
			ObjectMapper mapper = new ObjectMapper();
			BugzillaUser loggedUser = mapper.readValue(responseEntity.getBody(), BugzillaUser.class);
			if(loggedUser.getToken()!=null){
				bugzillaUser.setToken(loggedUser.getToken());
				System.out.println(loggedUser.getToken());
			}					
		} catch (Exception e) {
			e.printStackTrace();
		}
			return bugzillaUser;
	}
	
	/**
	 * @name deleteInstanceBinding
	 * @description this method is used at the time of un binding of service from the application
	 * @param id
	 * @return ServiceInstanceBinding
	 */
	public ServiceInstanceBinding deleteInstanceBinding(String id) {
		ServiceInstanceBinding sib =null;
	   boolean isUnBind = UnBindUsers(id);		
		if(isUnBind){
			sib = new ServiceInstanceBinding(id, null, null,null, null);	
		}
		return sib;
	}
	
	/**
	 * @name UnBindUsers
	 * @description this method is used to unbind all user which is created at the time of binding for the service
	 * from bugzilla
	 * @param matchName
	 * @return boolean
	 */
	private boolean UnBindUsers(String matchName){
		try {
			setProperties();		
			url = new URI(REST_GET_USER_BY_MATCH+"?match="+matchName+"&Bugzilla_api_key="+BUGZILLA_API_KEY);
			
			HttpEntity<?> requestEntity = new HttpEntity<Object>(createHeaders());
			ResponseEntity<String> responseEntity = getRestTemplate().exchange(url, HttpMethod.GET, requestEntity, String.class);
			ObjectMapper mapper = new ObjectMapper();
			BugzillaUser bugzillaUser  = mapper.readValue(responseEntity.getBody(), BugzillaUser.class);

			if(bugzillaUser.getUsers()!=null && bugzillaUser.getUsers().size()!=0){
				for(BugzillaUser user :bugzillaUser.getUsers()){
					blockUser(user.getId());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
/**
 * @name blockUser
 * @description this method is supported method for UnBindUsers to block the user from the bugzilla
 * by setting login_denied_text as blocked
 * @param userId
 * @return boolean
 */
	private boolean blockUser(String userId){
		try {
			setProperties();		
			url = new URI(REST_GET_USER_BY_MATCH+"/"+userId+"?Bugzilla_api_key="+BUGZILLA_API_KEY);
			System.out.println(url);
			String userdate ="{ \"login_denied_text\": \"blocked\"}";
			HttpEntity<?> requestEntity = new HttpEntity<Object>(userdate,createHeaders());
			System.out.println(userdate);
			ResponseEntity<String> responseEntity = getRestTemplate().exchange(url, HttpMethod.PUT, requestEntity, String.class);
			if(responseEntity.getBody()!=null){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * @name deProvisionedProductAndGroup
	 * @description this method is used for deprovisioning of the service
	 * in deprovisioning product and group created in provisioning are getting blocked
	 * @param name
	 * @throws MongoServiceException
	 */
	
	public void deProvisionedProductAndGroup(String name)
			throws MongoServiceException {
		try {
			deProvisionedProduct(name);
			deProvisionedGroup(name);
		} catch (MongoException e) {
			throw handleException(e);
		}
	}
	
	/**
	 * @name deProvisionedProduct
	 * @description this method help to block the product by setting is_open flag as false in that product
	 * @param productname
	 * @return
	 */
	private boolean deProvisionedProduct(String productname){
		try {
			setProperties();		
			url = new URI(REST_PRODUCT_UNBIND+"/"+productname+"?Bugzilla_api_key="+BUGZILLA_API_KEY);
			System.out.println(url);
			String userdate ="{ \"is_open\": false}";
			HttpEntity<?> requestEntity = new HttpEntity<Object>(userdate,createHeaders());
			System.out.println(userdate);
			ResponseEntity<String> responseEntity = getRestTemplate().exchange(url, HttpMethod.PUT, requestEntity, String.class);
			if(responseEntity.getBody()!=null){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	/**
	 * @name deProvisionedGroup
	 * @description this method help to block the group by setting is_active flag as false in that product
	 * @param productname
	 * @return
	 */
	private boolean deProvisionedGroup(String grouptname){
		try {
			setProperties();		 
			url = new URI(REST_GROUP_UNBIND+"/"+grouptname+"?Bugzilla_api_key="+BUGZILLA_API_KEY);
			System.out.println(url);
			String userdate ="{ \"is_active\": false}";
			HttpEntity<?> requestEntity = new HttpEntity<Object>(userdate,createHeaders());
			System.out.println(userdate);
			ResponseEntity<String> responseEntity = getRestTemplate().exchange(url, HttpMethod.PUT, requestEntity, String.class);
			if(responseEntity.getBody()!=null){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;		
	}
	
	

	private MongoServiceException handleException(Exception e) {
		logger.warn(e.getLocalizedMessage(), e);
		return new MongoServiceException(e.getLocalizedMessage());
	}

}
