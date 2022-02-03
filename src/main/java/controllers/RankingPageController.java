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
import java.util.Collections;

/**
 * Servlet implementation class goToPolicyMakerHomePage
 */
@WebServlet("/RankingPageController")
public class RankingPageController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private List<String> pList = null;
	private List<Ranking> rList = null;
	private List<Ranking> inverseList = null;
	private TemplateEngine templateEngine;
	private Production production = null;
	private boolean desc = true;
	
	@EJB(name = "managers/RankingManager")
    RankingManager manager;
	@EJB(name = "managers/ProductionManager")
    ProductionManager pmanager;
	
	public RankingPageController() {
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
		String path = "/rankingPage.html";
		pList = manager.getProductList();
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("pList", pList);
		String product = request.getParameter("product");
		request.getSession().setAttribute("prod", product);
		request.getSession().setAttribute("desc", desc);
		rList = manager.getRanking(product);
		ctx.setVariable("rList", rList);
		inverseList = manager.getProductListDesc(product);
		ctx.setVariable("inverseList", inverseList);
		templateEngine.process(path, ctx, response.getWriter());
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// obtain and escape params
		String idProduction = null;
		idProduction= StringEscapeUtils.escapeJava(request.getParameter("idPord"));
		int id = Integer.parseInt(idProduction);
		try {
			production = pmanager.getProduction(id);
		} catch (NonUniqueResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CredentialsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean created = manager.addNotification(production);
		String path;
		request.getSession().setAttribute("created", created);
		path = getServletContext().getContextPath() + "/RankingPageController";
		response.sendRedirect(path);
	}
}