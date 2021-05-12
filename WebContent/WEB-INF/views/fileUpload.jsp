<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ taglib prefix = "form" uri = "http://www.springframework.org/tags/form"%>

<html>

   <head>

   <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
   

      <title>File Upload Example</title>
   </head>
   
   <body>
      <form:form method = "POST" modelAttribute = "fileUpload"
         enctype = "multipart/form-data">
         Please select a file to upload : 
         <input type = "file" name = "file" />
         <input type = "submit" value = "upload" />
      </form:form>
   </body>
</html>