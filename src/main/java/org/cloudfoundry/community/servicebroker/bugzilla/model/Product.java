package org.cloudfoundry.community.servicebroker.bugzilla.model;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Product {
	private String id;

	private String name; //   Required string The name of this product. Must be globally unique within Bugzilla.
	private String description; // Required string A description for this product. Allows some simple HTML.
	private String version; //    Required string The default version for this product.
	private boolean has_unconfirmed; // boolean Allow the UNCONFIRMED status to be set on bugs in this product. Default: true.
	private String classification; // string The name of the Classification which contains this product.
	private String default_milestone; // string The default milestone for this product. Default '---'.
	private boolean is_open; // boolean True if the product is currently allowing bugs to be entered into it. Default: true.
	private boolean create_series; // boolean True if you want series for New Charts to be created for this new product. Default: true.
	private ArrayList<Product> products ;
	
	
	
	public ArrayList<Product> getProducts() {
		return products;
	}
	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public boolean isHas_unconfirmed() {
		return has_unconfirmed;
	}
	public void setHas_unconfirmed(boolean has_unconfirmed) {
		this.has_unconfirmed = has_unconfirmed;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public String getDefault_milestone() {
		return default_milestone;
	}
	public void setDefault_milestone(String default_milestone) {
		this.default_milestone = default_milestone;
	}
	public boolean isIs_open() {
		return is_open;
	}
	public void setIs_open(boolean is_open) {
		this.is_open = is_open;
	}
	public boolean isCreate_series() {
		return create_series;
	}
	public void setCreate_series(boolean create_series) {
		this.create_series = create_series;
	}


	
}
