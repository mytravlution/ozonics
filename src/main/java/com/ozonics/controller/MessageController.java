package com.ozonics.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.ozonics.bean.AllBean;
import com.ozonics.dao.AdminDao;

@Controller
@EnableWebMvc

public class MessageController {
	@Autowired
	AdminDao adminDao;
	@RequestMapping("sendMessage")
	public void sendMessage(HttpServletRequest request, HttpServletResponse response, @RequestBody String json) throws IOException {
		JSONObject obj = new JSONObject(json);
		AllBean bean  = new AllBean();
		bean.setFile_id(obj.getString("file_id"));
		bean.setMessage(obj.getString("message"));
		bean.setMessage_by(obj.getString("message_by"));
		bean.setMessage_to(obj.getString("message_to"));
		//wsadsad
;
		int updateDb = adminDao.sendMessage(bean);
		JSONObject myobj = new JSONObject();
		if (updateDb == 1) {
			myobj .put("msg", "SUCCESS");
			myobj.put("status", 1);
		}  else {
			myobj.put("msg", "FAILURE");
			myobj.put("status", 0);
		}
		
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		pw.print(myobj);
		pw.flush();
		pw.close();
	}
	@RequestMapping("/showMessageByFile")
	public void showMessageByFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String file_id = request.getParameter("file_id");
		String email = request.getParameter("email");
		
		List<AllBean> list = adminDao.showMessageByFile(file_id);
		JSONObject myobj = new JSONObject();
		String file_name =adminDao.getFileNameById(file_id);
		int status = adminDao.changeMessageStatus(file_id, email);

		List<AllBean> usersList = adminDao.getAllUsers();
		if (status == 1) {
			myobj .put("msg", "SUCCESS");
			myobj.put("status", 1);
			myobj.put("data", list);
			myobj.put("users", usersList);
			myobj.put("fileName", file_name);
		}
		else {
			myobj.put("msg", "FAILURE");
			myobj.put("status", 0);
		}
		
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		pw.print(myobj);
		pw.flush();
		pw.close();
	}
	
	@RequestMapping("changeMessageStatus")
	public void changeMessageStatus(HttpServletRequest request, HttpServletResponse response, @RequestBody String json) throws IOException {
		JSONObject obj = new JSONObject(json);
		
		int status = adminDao.changeMessageStatus(obj.getString("msg_id"), obj.getString("username"));
		JSONObject myobj = new JSONObject();
		if (status == 1) {
			myobj .put("msg", "SUCCESS");
			myobj.put("status", 1);
		}  else {
			myobj.put("msg", "FAILURE");
			myobj.put("status", 0);
		}
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		pw.print(myobj);
		pw.flush();
		pw.close();
		
	}
}
