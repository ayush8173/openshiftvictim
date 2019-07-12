package com.eh.openshiftvictim.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eh.openshiftvictim.model.User;
import com.eh.openshiftvictim.utility.ApplicationUtility;

/**
 * Servlet Filter implementation class ApplicationFilter
 */
public class AuthorizationFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public AuthorizationFilter() {
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
		boolean isSecure = false;
		boolean isAdmin = false;
		String sessionCsrfToken = null;
		String csrfToken = null;
		User loggedInUser = null;

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		final String file = req.getServletPath();
		final String requestType = request.getParameter("requestType");

		if (req.getSession().getAttribute("isSecure") != null) {
			isSecure = (boolean) req.getSession().getAttribute("isSecure");
		}

		if (isSecure) {
			sessionCsrfToken = (String) req.getSession().getAttribute("sessionCsrfToken");
			csrfToken = req.getParameter("csrfToken");

			if (req.getSession().getAttribute("user") != null) {
				loggedInUser = (User) req.getSession().getAttribute("user");
				isAdmin = loggedInUser.getRoles().contains("APP_ADMIN") ? true : false;
			}

			if (!isAdmin
					&& (ApplicationUtility.checkAdminFile(file) || ApplicationUtility.checkAdminRequest(requestType))) {
				PrintWriter out = response.getWriter();
				resp.setStatus(401);
				resp.setContentType("text/plain");
				resp.setCharacterEncoding("UTF-8");
				out.print("You are not authorized to access this content!");
				out.flush();
				return;
			}

			if (ApplicationUtility.checkCsrfRequest(requestType) && !sessionCsrfToken.equals(csrfToken)) {
				PrintWriter out = response.getWriter();
				resp.setStatus(401);
				resp.setContentType("text/plain");
				resp.setCharacterEncoding("UTF-8");
				out.print("Request blocked due to possible CSRF attack!");
				out.flush();
				return;
			}
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
