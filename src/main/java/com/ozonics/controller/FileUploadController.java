package com.ozonics.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.ozonics.bean.StorageService;
import com.ozonics.controller.FileModel;


@Controller
@EnableWebMvc
public class FileUploadController {
	   @Autowired
	   ServletContext context; 
 StorageService storageService;

 @RequestMapping(value = "/fileUploadPage", method = RequestMethod.GET)
 public ModelAndView fileUploadPage() {
    FileModel file = new FileModel();
    ModelAndView modelAndView = new ModelAndView("fileUpload", "command", file);
    return modelAndView;
 }
 
 @RequestMapping(value="/fileUploadPage", method = RequestMethod.POST)
 public String fileUpload(@Validated FileModel file, BindingResult result, ModelMap model) throws IOException {
	   String path = "/home/garima/Documents";
    if (result.hasErrors()) {
       System.out.println("validation errors");
       return "fileUploadPage";
    } else {            
       System.out.println("Fetching file");
       MultipartFile multipartFile = file.getFile();
       String uploadPath = path + File.separator;
       System.out.println(uploadPath);
       //Now do something with file...
       FileCopyUtils.copy(file.getFile().getBytes(), new File(uploadPath+file.getFile().getOriginalFilename()));
       String fileName = multipartFile.getOriginalFilename();
       model.addAttribute("fileName", fileName);
       return "success";
    }
 }
	

}