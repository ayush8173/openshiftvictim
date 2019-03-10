package com.eh.openshiftvictim.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eh.openshiftvictim.model.Book;
import com.eh.openshiftvictim.model.BookComment;
import com.eh.openshiftvictim.model.CreditRequest;
import com.eh.openshiftvictim.model.User;
import com.eh.openshiftvictim.service.ApplicationService;
import com.eh.openshiftvictim.service.LoginService;
import com.eh.openshiftvictim.utility.ApplicationUtility;
import com.eh.openshiftvictim.utility.JsonResponse;

/**
 * Servlet implementation class LoginController
 */
public class AppController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final LoginService loginService = new LoginService();
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
		final String requestType = request.getParameter("requestType");

		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
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
		RequestDispatcher dispatcher = null;
		JsonResponse jsonResponse = new JsonResponse();
		final String requestType = request.getParameter("requestType");

		if ("login".equals(requestType)) {
			final String username = request.getParameter("username");
			final String password = request.getParameter("password");
			try {
				User user = loginService.doLogin(username, password);
				if (request.getSession(false) != null) {
					request.getSession(false).invalidate();
					HttpSession session = request.getSession(true);
					if (user.getUsername() != null) {
						session.setAttribute("user", user);
						dispatcher = request.getRequestDispatcher("home.jsp");
						session.setAttribute("currentPage", "home.jsp");
					} else {
						request.setAttribute("errorMessage", "Username or password is incorrect!");
					}
				} else {
					request.setAttribute("errorMessage",
							"Either your browser cookie is disbled or some unknown error happened!");
				}
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
		} else if ("logout".equals(requestType)) {
			if (request.getSession(false) != null) {
				request.getSession(false).invalidate();
			}
			request.setAttribute("logoutMessage", "You have been successfully logged out!");
			dispatcher = request.getRequestDispatcher("login.jsp");
			request.getSession().setAttribute("currentPage", "login.jsp");
			dispatcher.forward(request, response);
		} else if ("uploadBookImage".equals(requestType)) {			
			String relativeWebPath = "/resources/app/images/Books/";
			String absoluteDiskPath = getServletContext().getRealPath(relativeWebPath);
			
			ApplicationUtility.populateBookImageMap();
			for (String bookId : ApplicationUtility.bookImageMap.keySet()){
				try {
					applicationService.uploadBookImage(bookId, absoluteDiskPath + ApplicationUtility.bookImageMap.get(bookId));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (requestType != null && !"searchBooks".equals(requestType) && !"searchMyBooks".equals(requestType)
				&& !"displayBook".equals(requestType) && !"buyBook".equals(requestType)
				&& !"returnBook".equals(requestType) && !"postComment".equals(requestType)
				&& !"fetchMyProfile".equals(requestType) && !"fetchAllUsers".equals(requestType)
				&& ((HttpServletRequest) request).getSession().getAttribute("user") == null) {
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
		} else {
			if ("searchBooks".equals(requestType)) {
				try {
					final String bookNameParam = request.getParameter("bookNameParam");
					List<Book> bookList = applicationService.searchBook(bookNameParam);
					if (bookList != null && bookList.size() > 0) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(bookList);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Could not found any result!");
					}
				} catch (SQLException e) {
					jsonResponse.setStatus("failed");
					jsonResponse.setData(e.getMessage());
				}
			} else if ("searchMyBooks".equals(requestType)) {
				final String sortBy = request.getParameter("sortBy");
				final String username = ((User) request.getSession().getAttribute("user")).getUsername();
				try {
					List<Book> bookList = applicationService.searchUserBooks(username, sortBy);
					if (bookList != null && bookList.size() > 0) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(bookList);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Could not found any result!");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("displayBook".equals(requestType)) {
				final String bookId = request.getParameter("bookId");
				final String username = ((User) request.getSession().getAttribute("user")).getUsername();
				try {
					Book book = applicationService.searchBookById(bookId, username);
					if (book.getBookId() != null) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(book);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Could not found any result!");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("buyBook".equals(requestType) || "returnBook".equals(requestType)) {
				final String bookId = request.getParameter("bookId");
				final String bookPrice = request.getParameter("bookPrice");
				final String username = ((User) request.getSession().getAttribute("user")).getUsername();
				try {
					boolean boughtStatus = true;
					if ("buyBook".equals(requestType)) {
						boughtStatus = applicationService.buyBook(bookId, bookPrice, username);
					} else {
						applicationService.returnBook(bookId, bookPrice, username);
					}

					Book book = applicationService.searchBookById(bookId, username);
					if (book.getBookId() != null && boughtStatus) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(book);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("You don't have sufficient credit to buy this book!");
					}
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
				try {
					applicationService.postComment(bookId, bookComment);
					Book book = applicationService.searchBookById(bookId, username);
					if (book.getBookId() != null) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(book);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Could not found any result!");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("fetchMyProfile".equals(requestType)) {
				final String username = ((User) request.getSession().getAttribute("user")).getUsername();
				try {
					User user = applicationService.searchUser(username);
					if (user != null) {
						jsonResponse.setStatus("success");
						jsonResponse.setData(user);
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("Could not found any result!");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("requestCredits".equals(requestType)) {
				final String amount = request.getParameter("amount");
				final String username = ((User) request.getSession().getAttribute("user")).getUsername();
				try {
					boolean creditRequestExists = applicationService.addCreditRequest(amount, username);
					if (!creditRequestExists) {
						jsonResponse.setStatus("success");
						jsonResponse.setData("Credit request created successfully!");
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("A pending credit request already exists!");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("transferCredits".equals(requestType)) {
				final String toUsername = request.getParameter("toUsername");
				final String amount = request.getParameter("amount");
				final String username = ((User) request.getSession().getAttribute("user")).getUsername();
				try {
					boolean transferStatus = applicationService.transferCredits(amount, toUsername, username);
					if (transferStatus) {
						jsonResponse.setStatus("success");
						jsonResponse.setData("Credit transferred successfully!");
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData(
								"Either username doesn't exists or you don't have required credits to transfer!");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("fetchCreditRequests".equals(requestType)) {
				try {
					List<CreditRequest> creditRequestList = applicationService.fetchCreditRequests();
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
				final String approveReject = "approve".equals(request.getParameter("approveReject")) ? "APPROVED" : "REJECTED";
				final String username = request.getParameter("username");
				try {
					applicationService.processCreditRequest(approveReject, username);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("fetchAllUsers".equals(requestType)) {
				try {
					List<User> userList = applicationService.fetchAllUsers();
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
				try {
					boolean userExists = applicationService.addNewUser(cuUser);
					if (!userExists) {
						jsonResponse.setStatus("success");
						jsonResponse.setData("User with username " + cuUser.getUsername() + " created successfully!");
					} else {
						jsonResponse.setStatus("failed");
						jsonResponse.setData("User with username " + cuUser.getUsername() + " already exists!");
					}
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
