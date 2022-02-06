package controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;


import model.*;
import managers.*;
import exceptions.*;

import javax.persistence.NonUniqueResultException;

/**
 * Servlet implementation class ServletTest
 */
@WebServlet(name="CheckLogin", 
urlPatterns="/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private TemplateEngine templateEngine;

	@EJB(name = "managers/LoginManager")
    LoginManager manager;
	@EJB(name = "managers/PolicyMakerManager")
	PolicyMakerManager pmanager;
	
    public CheckLogin() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// obtain and escape params
		String email = null;
		String pwd = null;
		try {
			email = StringEscapeUtils.escapeJava(request.getParameter("loginEmail"));
			pwd = StringEscapeUtils.escapeJava(request.getParameter("loginPassword"));
			if (email == null || pwd == null || email.isEmpty() || pwd.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}

		} catch (Exception e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}
		Usr usr;
		Policymaker pm;
		try {
			// query db to authenticate for user
			usr = manager.checkCredentials(email, pwd);
			pm= pmanager.getPolicyMaker(usr);
		} catch (CredentialsException | NonUniqueResultException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not check credentials");
			return;
		}

		// If the user exists, add info to the session and go to home page, otherwise
		// show login page with error message

		String path;
		if (usr == null || pm==null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Incorrect username or password");
			path = "/loginPage.html";
			templateEngine.process(path, ctx, response.getWriter());
		} else {
			request.getSession().setAttribute("usr", usr);
			request.getSession().setAttribute("pm",pm);
			path = getServletContext().getContextPath() + "/GoToPolicyMakerPage";
			response.sendRedirect(path);
			System.out.println("Correct");
		}

	}

	public void destroy() {
	}

}
