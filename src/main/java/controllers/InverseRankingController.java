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
 * 
 * Servlet implementation class goToPolicyMakerHomePage
 * 
 */
@WebServlet("/InverseRankingController")
public class InverseRankingController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private List<String> pList = null;
	private List<Ranking> rList = null;
	private List<Ranking> inverseList = null;
	private TemplateEngine templateEngine;
	
	boolean desc = true;
	
	@EJB(name = "managers/RankingManager")
    RankingManager manager;
	
	public InverseRankingController() {
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
		desc = !desc;
		request.getSession().setAttribute("desc", desc);
		pList = manager.getProductList();
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("pList", pList);
		String product = (String) request.getSession().getAttribute("prod");
		if (desc == false)
			rList = manager.getProductListDesc(product);
		else if (desc == true)
			rList = manager.getRanking(product);
		ctx.setVariable("rList", rList);
		templateEngine.process(path, ctx, response.getWriter());
		
	}
}