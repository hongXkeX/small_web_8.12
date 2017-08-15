package com.zk.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zk.dao.DBDao;
import com.zk.model.Person;
import com.zk.utils.JDBCUtils;

public class HelloServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String method = request.getParameter("method")==null?"":request.getParameter("method");
		method = new String(method.getBytes("ISO-8859-1"),"utf-8");
		switch (method) {
			case "register":
				register(request, response);
				break;
			case "login":
				login(request, response);
				break;
			default:
				break;
		}
	}

	private void login(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		String name = request.getParameter("name")==null?"":request.getParameter("name");
		String password = request.getParameter("password")==null?"":request.getParameter("password");
		name = new String(name.getBytes("ISO-8859-1"),"utf-8");
		password = new String(password.getBytes("ISO-8859-1"),"utf-8");
		DBDao<Person> dbDao = new DBDao<Person>();
		Person person = dbDao.selectByName(Person.class, name);
		if( person != null && person instanceof Person){
			if(name.equals(person.getName())&&password.equals(person.getPassword())){
				request.setAttribute("person", person);
				request.getRequestDispatcher("register_success.jsp").forward(request, response);
			}
		}
	}
	
	private void register(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		String name = request.getParameter("name")==null?"":request.getParameter("name");
		String password = request.getParameter("password")==null?"":request.getParameter("password");
		String tel = request.getParameter("tel")==null?"":request.getParameter("tel");
		String email = request.getParameter("email")==null?"":request.getParameter("email");
		name = new String(name.getBytes("ISO-8859-1"),"utf-8");
		password = new String(password.getBytes("ISO-8859-1"),"utf-8");
		tel = new String(tel.getBytes("ISO-8859-1"),"utf-8");
		email = new String(email.getBytes("ISO-8859-1"),"utf-8");
		Person persion = new Person(name, password, tel, email);
		DBDao<Person> dbDao = new DBDao<Person>();
		Person p = dbDao.selectByName(Person.class, name);
		if(p==null){
			dbDao.insert(persion);
		}
		request.setAttribute("person",persion);
		request.getRequestDispatcher("register_success.jsp").forward(request, response);
	}
}
