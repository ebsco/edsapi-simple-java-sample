/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eds.servlets;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eds.Response.EDSAPI;
import com.eds.bean.ApiErrorMessage;
import com.eds.bean.ResultsList;
import com.eds.bean.SessionToken;
import com.eds.tokenManager.SessionTokenManager;

public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String lookfor;
	private String type;
	private String endpoint;
	private String messageFormat = "";

	@SuppressWarnings("deprecation")
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		lookfor = request.getParameter("lookfor");
		type = request.getParameter("type");
		lookfor = URLDecoder.decode(lookfor);
		ResultsList resultsList = null;

		String query = "";
		lookfor = lookfor.trim();
		String term = lookfor;
		if (term != null && !term.equals("")) {

			term = term.replace(",", "\\,");
			term = term.replace(":", "\\:");
			term = term.replace("(", "\\(");
			term = term.replace(")", "\\)");

			String fieldCode = fieldCodeSelect(type);

			if (fieldCode.equals("")) {
				query = term;
			} else {
				query = fieldCode + ":" + term;

			}

		}
		query = URLEncoder.encode(query);

		if (term.equals("")) {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("index.jsp");
			dispatcher.forward(request, response);
		}

		else {
			ApiErrorMessage errorMessage = null;
			endpoint = (String) getServletContext().getAttribute("endpoint");
			messageFormat = (String) getServletContext().getAttribute(
					"messageFormat");
			String profile = (String) getServletContext().getAttribute(
					"profile");
			String isGuest = (String) getServletContext().getAttribute(
					"isGuest");

			EDSAPI api = new EDSAPI(endpoint, messageFormat);
			SessionTokenManager stm = new SessionTokenManager(request, api,
					profile, isGuest);
			SessionToken sessionToken = stm.ManageSessionToken();
			if (null != sessionToken.getApiErrorMessage())
				errorMessage = sessionToken.getApiErrorMessage();
			else {
				try {
					resultsList = api.requestSearch(sessionToken, query);
					if (null != resultsList.getApierrormessage()
							&& null != resultsList.getApierrormessage()
									.getErrorNumber()
							&& !resultsList.getApierrormessage()
									.getErrorNumber().isEmpty()) {
						// Add additional logic here to handle errors
						// appropriately
						// for
						// your usage. Correct the problem
						// and retry if appropriate
						switch (Integer.parseInt(resultsList
								.getApierrormessage().getErrorNumber())) {
						case 108: // session token missing
						case 109: // session token invalid
							SessionToken token = api.requestSessionToken(
									profile, isGuest);
							if (null != token
									&& null != token.getSessionToken()
									&& !token.getSessionToken().isEmpty()) {
								sessionToken = stm.ManageSessionToken();
								request.getSession().setAttribute(
										"session_token",
										token.getSessionToken());
								resultsList = api.requestSearch(sessionToken,
										query);
							}
							break;
						}
					}
				} catch (Exception e) {
					errorMessage = new ApiErrorMessage();
					errorMessage
							.setErrorDescription("An unknown error occurred");
				}

				if (null == resultsList) {
					errorMessage = new ApiErrorMessage();
					errorMessage
							.setErrorDescription("Error encountered during search.");
				} else if (null != resultsList.getApierrormessage())
					errorMessage = resultsList.getApierrormessage().clone();

				// set the result list on the session
				resultsList.setLookfor(lookfor);
				resultsList.setType(type);
				session.setAttribute("resultsList", resultsList);
			}
			String page = "resultsList.jsp";
			if (null != errorMessage) {
				page = "error.jsp";
				request.getSession().setAttribute("errorMessage", errorMessage);
			}
			RequestDispatcher dispatcher = request.getRequestDispatcher(page);
			dispatcher.forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	protected String fieldCodeSelect(String type) {
		if (type.equals("Author")) {
			return "AU";
		} else if (type.equals("title")) {
			return "TI";
		} else if (type.equals("keyword")) {
			return "";
		} else {
			return type;
		}
	}
}
