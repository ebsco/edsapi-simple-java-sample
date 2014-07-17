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

package com.eds.Response;

import java.io.IOException;

import com.eds.bean.Response;
import com.eds.bean.ResultsList;
import com.eds.bean.RetrieveResult;
import com.eds.bean.SessionToken;

public class EDSAPI {

	private String endpoint;

	public EDSAPI(String endpoint, String messagingFormat) {

		this.endpoint = endpoint;
		this.messageProcessor = this.getResponseFormat(messagingFormat);
		this.edsApiConnector = new EDSAPIConnector(messageProcessor);
	}

	private EDSAPIConnector edsApiConnector = null;
	private IMessageProcessor messageProcessor = null;

	public SessionToken requestSessionToken(String profile, String isGuest)
			throws IOException {

		Response response = edsApiConnector.sendCreateSessionRequest(endpoint,
				profile, isGuest);
		SessionToken sessionToken = messageProcessor
				.buildSessionToken(response);
		return sessionToken;
	}

	public ResultsList requestSearch(SessionToken sessionToken, String query) {

		String params = "query=" + query;
		ResultsList resultsList = null;
		Response response = edsApiConnector.sendSearchRequest(endpoint, params,
				sessionToken.getSessionToken());
		resultsList = messageProcessor.buildResultsList(response);

		return resultsList;
	}

	public RetrieveResult requestRetrieve(SessionToken sessionToken,
			String query) throws IOException {

		String params = query;
		Response response = null;

		response = edsApiConnector.sendRetrieveRequest(endpoint, params,
				sessionToken);

		RetrieveResult retrieveResult = messageProcessor.buildRecord(response);

		return retrieveResult;
	}

	private IMessageProcessor getResponseFormat(String format) {
		if (null == format)
			format = "XML";
		if (0 == format.compareToIgnoreCase("JSON"))
			return new JSONProcessor();
		else
			return new XMLProcessor();
	}

}
