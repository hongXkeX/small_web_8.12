<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
  </head>
  
  <body>
  <h2>用户注册</h2>
  <hr>
   <form action="hello" method="post">
   	姓名：<input type="text" name="name" /><br>
   	密码：<input type="password" name="password" /><br>
   	电话：<input type="text" name="tel" /><br>
   	邮箱：<input type="text" name="email" /><br>
   	<input type="submit" value="注册">
   	<input type="reset" value="重置">
   </form>
   
	<input type="hidden" name="method" value="register"/>
	
</body>
  
</html>
