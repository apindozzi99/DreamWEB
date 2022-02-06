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
@WebServlet("/AgronomistreportDetailPageController")
public class AgronomistreportDetailPageController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Agronomistreport report;
	private List<Production> beforeList;
	private List<Production> afterList;

	private TemplateEngine templateEngine;
	
	@EJB(name = "managers/AgronomistReportManager")
    AgronomistReportManager manager;
	
	public AgronomistreportDetailPageController() {
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
		String path = "/agronomistReportDetailPage.html";
		String sId = null;
		sId = StringEscapeUtils.escapeJava(request.getParameter("idReport"));
		int id = Integer.parseInt(sId);
		try {
			report = manager.getDetail(id);
		} catch (NonUniqueResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CredentialsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		beforeList = manager.getProductionBefore(report.getFieldBean(), report.getDate());
		afterList = manager.getProductionAfter(report.getFieldBean(), report.getDate());
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("report", report);
		ctx.setVariable("beforeList", beforeList);
		ctx.setVariable("afterList", afterList);
		templateEngine.process(path, ctx, response.getWriter());
	}
}