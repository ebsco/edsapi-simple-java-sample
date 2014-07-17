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

package com.eds.tokenManager;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.eds.Response.EDSAPI;
import com.eds.bean.SessionToken;

public class SessionTokenManager {

	private HttpServletRequest request;
	private EDSAPI edsapi;
	private String profile;
	private String isGuest;

	public SessionTokenManager(HttpServletRequest request, EDSAPI edsapi,
			String profile, String isGuest) {
		super();
		this.request = request;
		this.edsapi = edsapi;
		this.profile = profile;
		this.isGuest = isGuest;

	}

	public SessionToken ManageSessionToken() throws IOException {

		SessionToken sessiontoken = null;
		if (request.getSession().getAttribute("sessiontoken") != null)

		{
			sessiontoken = (SessionToken) request.getSession().getAttribute(
					"sessiontoken");

		}

		else {

			sessiontoken = edsapi.requestSessionToken(profile, isGuest);
			request.getSession().setAttribute("sessiontoken", sessiontoken);

		}

		return sessiontoken;

	}
}
