package com.eh.openshiftvictim.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class ApplicationFilter
 */
public class ApplicationFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public ApplicationFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub

		// String sessionid = ((HttpServletRequest)
		// request).getSession().getId();
		// ((HttpServletResponse) response).setHeader("SET-COOKIE",
		// "JSESSIONID=" + sessionid + "; HttpOnly; secure; SameSite=strict");

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String file = req.getServletPath();

		if (!("/login.jsp".equals(file) || "/loginSql.jsp".equals(file) || "/loginLdap.jsp".equals(file)
				|| "/loginXml.jsp".equals(file) || "/forgotPassword.jsp".equals(file) || "/resetPassword.jsp".equals(file))
				&& ((HttpServletRequest) request).getSession().getAttribute("user") == null) {
			if (file != null && file.endsWith(".html") && "XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
				PrintWriter out = response.getWriter();
				resp.setStatus(401);
				resp.setContentType("text/plain");
				resp.setCharacterEncoding("UTF-8");
				out.print("Either user is not authenticated or session has expired!");
				out.flush();
			} else {
				request.setAttribute("errorMessage", "You are not allowed to access this content!");
				RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
				dispatcher.forward(request, response);
			}
		} else if (("/login.jsp".equals(file) || "/loginSql.jsp".equals(file) || "/loginLdap.jsp".equals(file)
				|| "/loginXml.jsp".equals(file))
				&& ((HttpServletRequest) request).getSession().getAttribute("user") != null) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");
			dispatcher.forward(request, response);
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
