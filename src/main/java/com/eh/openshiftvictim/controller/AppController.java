package com.eh.openshiftvictim.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.xml.sax.SAXException;

import com.eh.openshiftvictim.exception.BookStoreException;
import com.eh.openshiftvictim.model.Book;
import com.eh.openshiftvictim.model.BookComment;
import com.eh.openshiftvictim.model.CreditRequest;
import com.eh.openshiftvictim.model.User;
import com.eh.openshiftvictim.service.ApplicationService;
import com.eh.openshiftvictim.service.UserService;
import com.eh.openshiftvictim.utility.ApplicationUtility;
import com.eh.openshiftvictim.utility.JaxbConvertor;
import com.eh.openshiftvictim.utility.JsonResponse;
import com.eh.openshiftvictim.utility.SecureGenerator;
import com.eh.openshiftvictim.utility.XstreamConvertor;

/**
 * Servlet implementation class LoginController
 */
public class AppController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final UserService userService = new UserService();
	final ApplicationService applicationService = new ApplicationService();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AppController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher dispatcher = null;
		JsonResponse jsonResponse = new JsonResponse();
		final String requestType = request.getParameter("requestType");

		// For Havij Demo - Start
		final String havijBookId = request.getParameter("id");
		if (havijBookId != null) {
			try {
				List<Book> bookList = applicationService.searchBook(false, true, havijBookId);
				if (bookList != null && bookList.size() > 0) {
					jsonResponse.setStatus("success");
					jsonResponse.setData(bookList);
				} else {
					jsonResponse.setStatus("failed");
					jsonResponse.setData("Could not found any result!");
				}
			} catch (BookStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				jsonResponse.setStatus("failed");
				jsonResponse.setData(e.getMessage());
			} catch (SQLException e) {
				jsonResponse.setStatus("failed");
				jsonResponse.setData(e.getMessage());
			}
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out.print(jsonResponse.getJsonResponseString(jsonResponse));
			out.flush();
		}
		// For Havij Demo - End

		else if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			doPost(request, response);
		} else {
			if (requestType != null && !"".equals(requestType)) {
				doPost(request, response);
			} else if (request.getSession().getAttribute("currentPage") != null) {
				String currentPage = (String) request.getSession().getAttribute("currentPage");
				dispatcher = request.getRequestDispatcher(currentPage);
				dispatcher.forward(request, response);
			} else {
				dispatcher = request.getRequestDispatcher("login.jsp");
				request.getSession().setAttribute("currentPage", "login.jsp");
				dispatcher.forward(request, response);
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		User loggedInUser = null;
		boolean isSecure = false;
		RequestDispatcher dispatcher = null;
		JsonResponse jsonResponse = new JsonResponse();
		final String requestType = request.getParameter("requestType");

		if (request.getSession().getAttribute("user") != null) {
			loggedInUser = (User) request.getSession().getAttribute("user");
		}
		if (request.getSession().getAttribute("isSecure") != null) {
			isSecure = (boolean) request.getSession().getAttribute("isSecure");
		}

		if ("login".equals(requestType) || "loginSql".equals(requestType) || "loginLdap".equals(requestType)
				|| "loginXml".equals(requestType)) {
			if (request.getSession().getAttribute("user") != null) {
				dispatcher = request.getRequestDispatcher("home.jsp");
				dispatcher.forward(request, response);
			} else {
				final String username = request.getParameter("username");
				final String password = request.getParameter("password");
				final String rememberMe = request.getParameter("rememberMe");
				final String securityType = request.getParameter("securityType");
				isSecure = "secure".equals(securityType) ? true : false;

				try {
					User user = null;
					if ("login".equals(requestType)) {
						user = userService.doLogin(isSecure, username, password);
					} else if ("loginLdap".equals(requestType)) {
						user = userService.doLoginLdap(username, password);
					} else if ("loginXml".equals(requestType)) {
						String xmlPath = getServletContext().getRealPath("/resources/app/xml/");
						user = userService.doLoginXml(xmlPath, username, password);
					}
					if (request.getSession(false) != null) {
						if (isSecure) {
							request.getSession(false).invalidate();
						}
						HttpSession session = request.getSession(true);
						if (user != null && user.getUsername() != null) {
							session.setAttribute("user", user);
							session.setAttribute("isSecure", isSecure);
							dispatcher = request.getRequestDispatcher("home.jsp");
							session.setAttribute("currentPage", "home.jsp");

							if ("true".equals(rememberMe)) {
								if (isSecure) {
									String rememberMeCookieValue = SecureGenerator.generateSecureString(10);
									String rememberMeCookieHash = SecureGenerator
											.generateStringHash(rememberMeCookieValue);
									userService.addUserToken(isSecure, user.getUsername(), "REMEMBER_ME",
											rememberMeCookieHash);
									Cookie usernameCookie = new Cookie("username", username);
									Cookie rememberMeCookie = new Cookie("remember_me", rememberMeCookieValue);
									usernameCookie.setMaxAge(604800);
									rememberMeCookie.setMaxAge(604800);
									response.addCookie(usernameCookie);
									response.addCookie(rememberMeCookie);
								} else {
									Cookie usernameCookie = new Cookie("username", username);
									Cookie passwordCookie = new Cookie("password", password);
									usernameCookie.setMaxAge(604800);
									passwordCookie.setMaxAge(604800);
									response.addCookie(usernameCookie);
									response.addCookie(passwordCookie);
								}
							}
						} else {
							request.setAttribute("errorMessage", "Username or password is incorrect!");
						}
					} else {
						request.setAttribute("errorMessage",
								"Either your browser cookie is disbled or some unknown error happened!");
					}
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				} catch (LdapException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				} catch (CursorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				}
				if (dispatcher == null) {
					dispatcher = request.getRequestDispatcher("login.jsp");
					request.getSession().setAttribute("currentPage", "login.jsp");
				}
				dispatcher.forward(request, response);
			}
		} else if ("loginBypass".equals(requestType)) {
			if (request.getSession().getAttribute("user") != null) {
				dispatcher = request.getRequestDispatcher("home.jsp");
				dispatcher.forward(request, response);
			} else {
				final String username = request.getParameter("username");
				final String rememberMe = request.getParameter("rememberMe");
				final String securityType = request.getParameter("securityType");
				isSecure = "secure".equals(securityType) ? true : false;

				try {
					if (userService.validateUserToken(isSecure, username, "REMEMBER_ME",
							SecureGenerator.generateStringHash(rememberMe))) {
						User user = userService.searchUser(isSecure, username);
						if (request.getSession(false) != null) {
							if (isSecure) {
								request.getSession(false).invalidate();
							}
							HttpSession session = request.getSession(true);
							if (user != null && user.getUsername() != null) {
								session.setAttribute("user", user);
								session.setAttribute("isSecure", isSecure);
								dispatcher = request.getRequestDispatcher("home.jsp");
								session.setAttribute("currentPage", "home.jsp");
							} else {
								request.setAttribute("errorMessage", "Unknown error happened! Please login again.");
							}
						} else {
							request.setAttribute("errorMessage",
									"Either your browser cookie is disbled or some unknown error happened!");
						}
					} else {
						Cookie usernameCookie = new Cookie("username", "");
						Cookie passwordCookie = new Cookie("password", "");
						Cookie rememberMeCookie = new Cookie("remember_me", "");
						usernameCookie.setMaxAge(0);
						passwordCookie.setMaxAge(0);
						rememberMeCookie.setMaxAge(0);
						response.addCookie(usernameCookie);
						response.addCookie(passwordCookie);
						response.addCookie(rememberMeCookie);
						request.getSession().invalidate();
						request.setAttribute("errorMessage", "RememberMe token not valid! Please login again.");
						dispatcher = request.getRequestDispatcher("login.jsp");
						request.getSession().setAttribute("currentPage", "login.jsp");
					}
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				}
				if (dispatcher == null) {
					dispatcher = request.getRequestDispatcher("login.jsp");
					request.getSession().setAttribute("currentPage", "login.jsp");
				}
				dispatcher.forward(request, response);
			}
		} else if ("logout".equals(requestType)) {
			try {
				userService.deleteUserToken(isSecure, loggedInUser.getUsername(), "REMEMBER_ME");
			} catch (BookStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				request.setAttribute("errorMessage", e.getMessage());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Cookie usernameCookie = new Cookie("username", "");
			Cookie passwordCookie = new Cookie("password", "");
			Cookie rememberMeCookie = new Cookie("remember_me", "");
			usernameCookie.setMaxAge(0);
			passwordCookie.setMaxAge(0);
			rememberMeCookie.setMaxAge(0);
			response.addCookie(usernameCookie);
			response.addCookie(passwordCookie);
			response.addCookie(rememberMeCookie);
			request.getSession().invalidate();
			request.setAttribute("logoutMessage", "You have been successfully logged out!");
			dispatcher = request.getRequestDispatcher("login.jsp");
			request.getSession().setAttribute("currentPage", "login.jsp");
			dispatcher.forward(request, response);
		} else if ("accountActivation".equals(requestType)) {
			final String username = request.getParameter("username");
			final String passwordResetToken = request.getParameter("token");
			final String fromInSecure = request.getParameter("FIS");
			try {
				if (userService.validateAccountActivationToken(username, passwordResetToken)) {
					if (!"Y".equalsIgnoreCase(fromInSecure)) {
						request.getSession().setAttribute("passwordResetUsername", username);
						request.setAttribute("logoutMessage", "Please provide your new password!");
						dispatcher = request.getRequestDispatcher("resetPassword.jsp");
						request.getSession().setAttribute("currentPage", "resetPassword.jsp");
					} else {
						request.setAttribute("logoutMessage",
								"Your account has been successfully activated. Please login your password!");
					}
				} else {
					request.setAttribute("errorMessage",
							"Account activation reset link has expired. Please re-initiate account creation!");
				}
			} catch (BookStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				request.setAttribute("errorMessage", e.getMessage());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (dispatcher == null) {
				dispatcher = request.getRequestDispatcher("login.jsp");
				request.getSession().setAttribute("currentPage", "login.jsp");
			}
			dispatcher.forward(request, response);
		} else if ("forgotPassword".equals(requestType)) {
			final String username = request.getParameter("username");
			final String securityType = request.getParameter("securityType");
			boolean isSecureForgotPassword = "secure".equals(securityType) ? true : false;
			String successMessage = null;
			try {
				successMessage = userService.forgotPassword(isSecureForgotPassword, username);
				request.setAttribute("logoutMessage", successMessage);
			} catch (BookStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				request.setAttribute("errorMessage", e.getMessage());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			request.getSession().invalidate();
			dispatcher = request.getRequestDispatcher("forgotPassword.jsp");
			request.getSession().setAttribute("currentPage", "forgotPassword.jsp");
			dispatcher.forward(request, response);
		} else if ("passwordReset".equals(requestType)) {
			final String username = request.getParameter("username");
			final String passwordResetToken = request.getParameter("token");
			try {
				if (userService.validatePasswordResetToken(username, passwordResetToken)) {
					request.getSession().setAttribute("passwordResetUsername", username);
					request.setAttribute("logoutMessage", "Please provide your new password!");
					dispatcher = request.getRequestDispatcher("resetPassword.jsp");
					request.getSession().setAttribute("currentPage", "resetPassword.jsp");
				} else {
					request.setAttribute("errorMessage",
							"Password reset link has expired. Please re-initiate password reset!");
				}
			} catch (BookStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				request.setAttribute("errorMessage", e.getMessage());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (dispatcher == null) {
				dispatcher = request.getRequestDispatcher("forgotPassword.jsp");
				request.getSession().setAttribute("currentPage", "forgotPassword.jsp");
			}
			dispatcher.forward(request, response);
		} else if ("resetPassword".equals(requestType)) {
			final String username = (String) request.getSession().getAttribute("passwordResetUsername");
			final String newPassword1 = request.getParameter("newPassword1");
			final String newPassword2 = request.getParameter("newPassword2");
			try {
				userService.updateUserPassword(username, newPassword1, newPassword2);
				request.setAttribute("logoutMessage",
						"Your password has been reset successfully. Please login with your new password!");
			} catch (BookStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				request.setAttribute("errorMessage", e.getMessage());
				dispatcher = request.getRequestDispatcher("resetPassword.jsp");
				request.getSession().setAttribute("currentPage", "resetPassword.jsp");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (dispatcher == null) {
				dispatcher = request.getRequestDispatcher("login.jsp");
				request.getSession().setAttribute("currentPage", "login.jsp");
			}
			dispatcher.forward(request, response);
		} else if ("uploadBookImage".equals(requestType)) {
			String relativeWebPath = "/resources/app/images/Books/";
			String absoluteDiskPath = getServletContext().getRealPath(relativeWebPath);

			ApplicationUtility.populateBookImageMap();
			for (String bookId : ApplicationUtility.bookImageMap.keySet()) {
				try {
					applicationService.uploadBookImage(isSecure, bookId,
							absoluteDiskPath + ApplicationUtility.bookImageMap.get(bookId));
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("errorMessage", e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if ("wsPostComment".equals(requestType)) {
			final String xmlInput = request.getParameter("xmlInput");
			final Book book = JaxbConvertor.xmlToObject(xmlInput);
			try {
				applicationService.logXmlInput(false, xmlInput);
				applicationService.postComment(false, book);
			} catch (BookStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("wsCreateUser".equals(requestType)) {
			final String xmlInput = request.getParameter("xmlInput");
			final User user = (User) XstreamConvertor.xmlToObject(xmlInput);
			try {
				applicationService.logXmlInput(false, xmlInput);
				userService.addNewUser(false, user, user.getPassword());
			} catch (BookStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (requestType != null && ((HttpServletRequest) request).getSession().getAttribute("user") == null) {
			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
				jsonResponse.setStatus("failed");
				jsonResponse.setData("Either user is not authenticated or session has expired!");
				PrintWriter out = response.getWriter();
				response.setStatus(401);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				out.print(jsonResponse.getJsonResponseString(jsonResponse));
				out.flush();
			} else {
				request.setAttribute("errorMessage", "You are not allowed to access this content!");
				dispatcher = request.getRequestDispatcher("login.jsp");
				request.getSession().setAttribute("currentPage", "login.jsp");
				dispatcher.forward(request, response);
			}
		} else if ("homeSearch".equals(requestType)) {
			final String searchParam = request.getParameter("searchParam");
			request.setAttribute("successMessage", "You search for: " + searchParam);
			dispatcher = request.getRequestDispatcher("home.jsp");
			request.getSession().setAttribute("currentPage", "home.jsp");
			dispatcher.forward(request, response);
		} else {
			if ("searchBooks".equals(requestType)) {
				try {
					final boolean isBookId = "bookId".equalsIgnoreCase(request.getParameter("searchParamType")) ? true
							: false;
					final String searchParam = isBookId ? request.getParameter("bookIdParam")
							: request.getParameter("bookTitleParam");
					List<Book> bookList = applicationService.searchBook(isSecure, isBookId, searchParam);
					if (bookList != null && bookList.size() > 0) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(bookList);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Could not found any result!");
					}
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				} catch (SQLException e) {
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				}
			} else if ("checkBookExist".equals(requestType)) {
				try {
					final String bookId = request.getParameter("bookIdParam");
					boolean bookExist = applicationService.checkBookExist(isSecure, bookId);
					if (bookExist) {
						jsonResponse.setStatus("success");
						jsonResponse.setData("Book " + bookId + " exists!");
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Book " + bookId + " doesn't exist!");
					}
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				} catch (SQLException e) {
					jsonResponse.setStatus("failed");
					// jsonResponse.setData(e.getMessage());
					jsonResponse.setData("An unexpected error occured!");
				}
			} else if ("searchMyBooks".equals(requestType)) {
				final String sortBy = request.getParameter("sortBy");
				final String username = ((User) request.getSession().getAttribute("user")).getUsername();
				try {
					List<Book> bookList = applicationService.searchUserBooks(isSecure, username, sortBy);
					if (bookList != null && bookList.size() > 0) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(bookList);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Could not found any result!");
					}
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("displayBook".equals(requestType)) {
				final String bookId = request.getParameter("bookId");
				final String username = ((User) request.getSession().getAttribute("user")).getUsername();
				try {
					Book book = applicationService.searchBookForDisplay(isSecure, bookId, username);
					if (book.getBookId() != null) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(book);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Could not found any result!");
					}
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("buyBook".equals(requestType) || "returnBook".equals(requestType)) {
				final String bookId = request.getParameter("bookId");
				final String bookPrice = request.getParameter("bookPrice");
				final String username = ((User) request.getSession().getAttribute("user")).getUsername();
				try {
					if ("buyBook".equals(requestType)) {
						applicationService.buyBook(isSecure, bookId, bookPrice, username);
					} else {
						applicationService.returnBook(isSecure, bookId, bookPrice, username);
					}

					Book book = applicationService.searchBookForDisplay(isSecure, bookId, username);
					if (book.getBookId() != null) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(book);
					}
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("postComment".equals(requestType)) {
				final String bookId = request.getParameter("bookId");
				final String username = ((User) request.getSession().getAttribute("user")).getUsername();

				final BookComment bookComment = new BookComment();
				bookComment.setCommentor(username);
				bookComment.setComment(request.getParameter("bookComment"));

				final List<BookComment> bookComments = new ArrayList<BookComment>();
				bookComments.add(bookComment);

				final Book bookWithComment = new Book();
				bookWithComment.setBookId(bookId);
				bookWithComment.setBookComment(bookComments);
				try {
					applicationService.postComment(isSecure, bookWithComment);
					Book book = applicationService.searchBookForDisplay(isSecure, bookId, username);
					if (book.getBookId() != null) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(book);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Could not found any result!");
					}
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("fetchMyProfile".equals(requestType)) {
				final String username = ((User) request.getSession().getAttribute("user")).getUsername();
				try {
					User user = userService.searchUser(isSecure, username);
					if (user != null) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(user);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Could not found any result!");
					}
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("transferCredits".equals(requestType)) {
				final String toUsername = request.getParameter("toUsername");
				final String amount = request.getParameter("amount");
				final String username = ((User) request.getSession().getAttribute("user")).getUsername();
				final String password = request.getParameter("password");
				try {
					userService.transferCredits(isSecure, amount, toUsername, username, password);
					jsonResponse.setStatus("success");
					jsonResponse.setData("Credit transferred successfully!");
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("requestCredits".equals(requestType)) {
				final String amount = request.getParameter("amount");
				final String username = ((User) request.getSession().getAttribute("user")).getUsername();
				try {
					userService.addCreditRequest(isSecure, amount, username);
					jsonResponse.setStatus("success");
					jsonResponse.setData("Credit request created successfully!");
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("fetchCreditRequests".equals(requestType)) {
				try {
					List<CreditRequest> creditRequestList = userService.fetchCreditRequests(isSecure);
					if (creditRequestList != null && creditRequestList.size() > 0) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(creditRequestList);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Could not found any result!");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("processCreditRequests".equals(requestType)) {
				final String approveReject = "approve".equals(request.getParameter("approveReject")) ? "APPROVED"
						: "REJECTED";
				final String username = request.getParameter("username");
				try {
					userService.processCreditRequest(isSecure, approveReject, username);
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("fetchAllUsers".equals(requestType)) {
				try {
					List<User> userList = userService.fetchAllUsers(isSecure);
					if (userList != null && userList.size() > 0) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(userList);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Could not found any result!");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("createUser".equals(requestType)) {
				User cuUser = new User();
				cuUser.setUsername(request.getParameter("cuUsername"));
				cuUser.setPassword(request.getParameter("cuPassword"));
				cuUser.setFirstName(request.getParameter("cuFirstName"));
				cuUser.setLastName(request.getParameter("cuLastName"));
				cuUser.setEmail(request.getParameter("cuEmail"));
				try {
					userService.addNewUser(isSecure, cuUser, request.getParameter("cuPassword2"));
					jsonResponse.setStatus("success");
					jsonResponse.setData("User with username '" + cuUser.getUsername() + "' created successfully!");
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("fetchAllFiles".equals(requestType)) {
				try {
					Map<String, String> fileMap = applicationService.fetchAllFiles(isSecure);
					if (fileMap != null && fileMap.size() > 0) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(fileMap);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Could not found any result!");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("createFile".equals(requestType)) {
				final String absoluteDiskJarsPath = getServletContext().getRealPath("/resources/app/jars/");
				final String absoluteDiskTempPath = getServletContext().getRealPath("/resources/app/temp/");
				final String fileName = Long.toString((new Date()).getTime()) + ".txt";
				final String username = request.getParameter("username");
				try {
					applicationService.createFile(isSecure, absoluteDiskJarsPath, absoluteDiskTempPath, fileName,
							username);
					jsonResponse.setStatus("success");
					jsonResponse.setData("File with file name " + fileName + " successfully created!");
				} catch (BookStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out.print(jsonResponse.getJsonResponseString(jsonResponse));
			out.flush();
		}
	}

}
