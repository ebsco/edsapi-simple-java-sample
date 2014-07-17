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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eds.Response.EDSAPI;
import com.eds.bean.ApiErrorMessage;
import com.eds.bean.RetrieveResult;
import com.eds.bean.SessionToken;
import com.eds.tokenManager.SessionTokenManager;

public class Retrieve extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String an = request.getParameter("an");
		String dbid = request.getParameter("dbid");
		HttpSession session = request.getSession();

		String query = "an=" + an + "&dbid=" + dbid;
		// Get endpoint and messageFormat from java servlet application scope
		String endpoint = (String) getServletContext().getAttribute("endpoint");
		String messageFormat = (String) getServletContext().getAttribute(
				"messageFormat");
		String profile = (String) getServletContext().getAttribute("profile");
		String isGuest = (String) getServletContext().getAttribute("isGuest");

		EDSAPI api = new EDSAPI(endpoint, messageFormat);

		SessionTokenManager stm = new SessionTokenManager(request, api,
				profile, isGuest);
		SessionToken sessionToken = stm.ManageSessionToken();

		RetrieveResult record = null;
		ApiErrorMessage errorMessage = null;
		try {
			record = api.requestRetrieve(sessionToken, query);
			if (null != record.getApiErrorMessage()
					&& null != record.getApiErrorMessage().getErrorNumber()
					&& !record.getApiErrorMessage().getErrorNumber().isEmpty()) {
				// Add additional logic here to handle errors appropriately
				// for
				// your usage. Correct the problem
				// and retry if appropriate
				switch (Integer.parseInt(record.getApiErrorMessage()
						.getErrorNumber())) {
				case 108: // session token missing
				case 109: // session token invalid
					SessionToken token = api.requestSessionToken(profile,
							isGuest);
					if (null != token && null != token.getSessionToken()
							&& !token.getSessionToken().isEmpty()) {
						sessionToken = stm.ManageSessionToken();
						request.getSession().setAttribute("session_token",
								token.getSessionToken());
						record = api.requestRetrieve(sessionToken, query);
					}
					break;
				}
			}
		} catch (Exception e) {
			errorMessage = new ApiErrorMessage();
			errorMessage.setErrorDescription("An unknown error occurred");
		}
		if (null == record) {
			errorMessage = new ApiErrorMessage();
			errorMessage
					.setErrorDescription("Error encountered during search.");
		} else if (null != record.getApiErrorMessage())
			errorMessage = record.getApiErrorMessage().clone();

		// set the result list on the session
		session.setAttribute("record", record);
		String page = "record.jsp";
		if (null != errorMessage) {
			page = "error.jsp";
			session.setAttribute("errorMessage", errorMessage);
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
