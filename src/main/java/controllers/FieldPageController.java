package controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;

import managers.*;
import model.*;
import exceptions.CredentialsException;

import java.util.List;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * Servlet implementation class goToPolicyMakerHomePage
 */
@WebServlet("/FieldPageController")
public class FieldPageController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private List<Field> fList = null;
	private Field field = null;
	private TemplateEngine templateEngine;
	
	@EJB(name = "managers/FieldManager")
    FieldManager manager;
	
	public FieldPageController() {
		super();
		// TODO Auto-generated constructor stub
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "/fieldPage.html";
		fList = manager.getAllFields();
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("fList", fList);
		templateEngine.process(path, ctx, response.getWriter());
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// obtain and escape params
		String sLocation = null;
		sLocation = StringEscapeUtils.escapeJava(request.getParameter("fieldLocation"));
		float location = Float.parseFloat(sLocation);
		try {
			field = manager.getField(location);
		} catch (NonUniqueResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CredentialsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String path;
		System.out.println(location);
		request.getSession().setAttribute("field", field);
		path = getServletContext().getContextPath() + "/ProductionPageController";
		response.sendRedirect(path);
	}

}
