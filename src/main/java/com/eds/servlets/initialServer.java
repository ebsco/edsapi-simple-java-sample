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
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class initialServer extends HttpServlet {

	private static final long serialVersionUID = -14380037979438659L;

	private String endpoint;
	private String messageFormat;
	private String profile;
	private String isGuest;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		endpoint = getServletConfig().getInitParameter("endpoint");
		messageFormat = getServletConfig().getInitParameter("message_format");
		profile = getServletConfig().getInitParameter("profile");
		isGuest = getServletConfig().getInitParameter("is_guest");
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ServletContext application = getServletContext();
		application.setAttribute("endpoint", endpoint);
		application.setAttribute("messageFormat", messageFormat);
		application.setAttribute("isGuest", isGuest);
		application.setAttribute("profile", profile);

		RequestDispatcher dispatcher = request
				.getRequestDispatcher("index.jsp");
		dispatcher.forward(request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
