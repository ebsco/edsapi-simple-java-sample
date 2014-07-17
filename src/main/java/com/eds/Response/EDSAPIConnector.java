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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.eds.bean.Response;
import com.eds.bean.SessionToken;
import com.eds.helper.BuildErrorStream;

public class EDSAPIConnector {

	private IMessageProcessor _messageProcessor = null;

	public EDSAPIConnector(IMessageProcessor messageProcessor) {
		this._messageProcessor = messageProcessor;
	}

	public Response sendCreateSessionRequest(String endpoint, String profile,
			String isGuest) throws IOException {

		String url = endpoint + "/createsession";
		String params = "profile=" + profile + "&org=&guest=" + isGuest;
		url = url + "?" + params;
		BufferedReader reader = null;
		String errorStream = "";
		Response response = new Response();
		String errorNumber = "";
		HttpURLConnection connection = null;

		try {

			URL geturl = new URL(url);
			connection = (HttpURLConnection) geturl.openConnection();
			connection.setRequestProperty("Accept",
					this._messageProcessor.GetContentType());
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

		} catch (IOException ioe) {

			InputStream error = ((HttpURLConnection) connection)
					.getErrorStream();

			try {
				errorNumber = ((HttpURLConnection) connection)
						.getResponseCode() + "";
			} catch (IOException e) {

				errorNumber = "unknown";
			}
			BuildErrorStream bes = new BuildErrorStream(error);
			errorStream = bes.getErrorStream();

		}

		response.setErrorStream(errorStream);
		response.setRead(reader);
		response.setErrorNumber(errorNumber);

		return response;

	}

	public Response sendSearchRequest(String endpoint, String params,
			String sessionToken) {

		String url = endpoint + "/Search";
		url = url + "?" + params;
		URLConnection connection = null;
		BufferedReader reader = null;
		String errorStream = "";
		Response response = new Response();
		String errorNumber = "";

		try {

			URL geturl = new URL(url);
			connection = (URLConnection) geturl.openConnection();
			String token = (null == sessionToken) ? "" : sessionToken;
			connection.setRequestProperty("x-sessionToken", token);
			connection.setRequestProperty("Accept",
					this._messageProcessor.GetContentType());
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

		} catch (IOException ioe) {

			InputStream error = ((HttpURLConnection) connection)
					.getErrorStream();

			try {
				errorNumber = ((HttpURLConnection) connection)
						.getResponseCode() + "";
			} catch (IOException e) {

				errorNumber = "unknown";
			}

			BuildErrorStream bes = new BuildErrorStream(error);
			errorStream = bes.getErrorStream();

		}

		response.setErrorStream(errorStream);
		response.setRead(reader);
		response.setErrorNumber(errorNumber);

		return response;

	}

	public Response sendRetrieveRequest(String endpoint, String params,
			SessionToken sessionToken) throws IOException {

		String url = endpoint + "/Retrieve";
		url = url + "?" + params;
		URLConnection connection = null;
		BufferedReader reader = null;
		String errorStream = "";
		Response response = new Response();
		String errorNumber = "";

		try {

			URL geturl = new URL(url);
			connection = (URLConnection) geturl.openConnection();
			connection.setRequestProperty("x-sessionToken",
					sessionToken.getSessionToken());
			connection.setRequestProperty("Accept",
					this._messageProcessor.GetContentType());
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

		} catch (IOException ioe) {

			InputStream error = ((HttpURLConnection) connection)
					.getErrorStream();

			try {
				errorNumber = ((HttpURLConnection) connection)
						.getResponseCode() + "";
			} catch (IOException e) {

				errorNumber = "unknown";
			}

			BuildErrorStream bes = new BuildErrorStream(error);
			errorStream = bes.getErrorStream();

		}

		response.setErrorStream(errorStream);
		response.setRead(reader);
		response.setErrorNumber(errorNumber);

		return response;

	}

}
