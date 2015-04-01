package servlets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.ejb.EJB;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.ISocialEventService;

import test.GoogleCalendarService;

import entity.Event;
import entity.User;

/**
 * ViewImage.java
 * 
 * Servlet for providing images to JSP.
 * Currently only provides user profile image.
 *
 */
public class ViewImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@EJB
	private ISocialEventService service;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		int userID = Integer.parseInt(request.getParameter("user"));
		User user = service.getUser(userID);
		
		// Actual code for displaying the image from byte array.
		response.setContentType("image/jpg");
		response.addHeader("Content-Disposition","filename=getimage.jpg");
		response.getOutputStream().write(user.getImage());
		response.flushBuffer();
	}
}
