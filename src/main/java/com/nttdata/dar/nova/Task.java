package com.nttdata.dar.nova;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author jalvarco
 *
 */
@Entity
public class Task {
	
	/**
	 * |_ ID: Unique value autoincremented
	 * |_ Description: String value
	 * |_ Status: task state
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	
	@Column(columnDefinition = "varchar(256)")
	private String description;
	
	@Column(columnDefinition = "varchar(100) default 'New'")
	private String status;
	
	/**
	 * Default constructor
	 */
	public Task() {
		
	}
	
	/**
	 * Constructor to create new tasks
	 * 
	 * @param description
	 */

	public Task(String description) {
		super();
		this.description = description;
		this.status = "New";
	}
	
	/**
	 * Constructor to create new task with state
	 * 
	 * @param description
	 * @param status
	 */
	public Task(String description, String status) {
		super();
		this.description = description;
		this.status = status;
	}
	
	/**
	 * @param id
	 * @param description
	 * @param status
	 */
	public Task(Long id, String description, String status) {
		super();
		this.id = id;
		this.description = description;
		this.status = status;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
