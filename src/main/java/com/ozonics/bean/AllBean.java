package com.ozonics.bean;

import org.springframework.web.multipart.MultipartFile;

public class AllBean {
	private String image_b64;
	private String file_name;
	private String product_name;
	private String category;
	private String level1;
	private String level2;
	private String level3;
	private Integer status;
	private String username;
	private String password;
	private String phone_num;
	private String segment;
	private Integer count;
	private String sub_category;
	private MultipartFile file;
	private String user_type;
	private String login_time;
	private String operation_type;
	private String folder;
	private String file_type;
	private Integer phone_pin;
	private String comments;
	private String email;
	private String message_by;
	private String message_to;
	private String message;
	private String file_id;
	private String logtime;
	private String msg_id;
	private String msg_status;
	private String[] message_to_arr;
	private Integer message_count;
	
	
	public Integer getMessage_count() {
		return message_count;
	}

	public void setMessage_count(Integer message_count) {
		this.message_count = message_count;
	}

	public String[] getMessage_to_arr() {
		return message_to_arr;
	}

	public void setMessage_to_arr(String[] message_to_arr) {
		this.message_to_arr = message_to_arr;
	}

	public String getMsg_status() {
		return msg_status;
	}

	public void setMsg_status(String msg_status) {
		this.msg_status = msg_status;
	}

	public String getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}

	public String getLogtime() {
		return logtime;
	}

	public void setLogtime(String logtime) {
		this.logtime = logtime;
	}

	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}

	public String getMessage_by() {
		return message_by;
	}

	public void setMessage_by(String message_by) {
		this.message_by = message_by;
	}

	public String getMessage_to() {
		return message_to;
	}

	public void setMessage_to(String message_to) {
		this.message_to = message_to;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Integer getPhone_pin() {
		return phone_pin;
	}

	public void setPhone_pin(Integer phone_pin) {
		this.phone_pin = phone_pin;
	}

	public String getFile_type() {
		return file_type;
	}

	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getOperation_type() {
		return operation_type;
	}

	public void setOperation_type(String operation_type) {
		this.operation_type = operation_type;
	}

	public String getLogin_time() {
		return login_time;
	}

	public void setLogin_time(String login_time) {
		this.login_time = login_time;
	}

	public String getUser_type() {
		return user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}

	public MultipartFile getFile() {

		return file;

	}

	public void setFile(MultipartFile file) {

		this.file = file;

	}

	public String getSub_category() {
		return sub_category;
	}

	public void setSub_category(String sub_category) {
		this.sub_category = sub_category;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone_num() {
		return phone_num;
	}

	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLevel1() {
		return level1;
	}

	public void setLevel1(String level1) {
		this.level1 = level1;
	}

	public String getLevel2() {
		return level2;
	}

	public void setLevel2(String level2) {
		this.level2 = level2;
	}

	public String getLevel3() {
		return level3;
	}

	public void setLevel3(String level3) {
		this.level3 = level3;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}


	public String getImage_b64() {
		return image_b64;
	}

	public void setImage_b64(String image_b64) {
		this.image_b64 = image_b64;
	}

}
