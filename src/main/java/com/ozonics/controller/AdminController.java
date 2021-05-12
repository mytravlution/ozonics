package com.ozonics.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ozonics.bean.AllBean;
import com.ozonics.dao.AdminDao;

public class AdminController {
	@Autowired
	AdminDao adminDao;
	

	
	
}
