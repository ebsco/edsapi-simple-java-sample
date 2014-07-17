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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;

import com.eds.bean.ApiErrorMessage;
import com.eds.bean.BookJacket;
import com.eds.bean.CustomLink;
import com.eds.bean.Item;
import com.eds.bean.Record;
import com.eds.bean.Response;
import com.eds.bean.ResultsList;
import com.eds.bean.RetrieveResult;
import com.eds.bean.SessionToken;
import com.eds.helper.TransDataToHTML;

public class JSONProcessor implements IMessageProcessor {

	public static final String HTTP_Bad_Request = "400";

	public String GetContentType() {
		return "application/json";
	}

	/**
	 * Constructs a session token object from an EDS API Response
	 */
	public SessionToken buildSessionToken(Response response) {
		JSONObject object = null;
		SessionToken sessionToken = new SessionToken();
		if (null != response.getErrorStream()
				&& !response.getErrorStream().equals("")) {
			ApiErrorMessage errorMessage = ProcessErrors(
					response.getErrorNumber(), response.getErrorStream());
			sessionToken.setApiErrorMessage(errorMessage);
			return sessionToken;
		} else {
			try {
				BufferedReader reader = response.getRead();
				object = (JSONObject) new JSONTokener(reader).nextValue();
				sessionToken.setSessionToken(object.getString("SessionToken"));
			} catch (JSONException e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage.setErrorDescription("Error parsing info message");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				sessionToken.setApiErrorMessage(errorMessage);
			} catch (Exception e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing session response");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				sessionToken.setApiErrorMessage(errorMessage);
			}
		}
		return sessionToken;

	}

	/**
	 * Constructs a result list in response to an EDS API Search Request
	 */
	public ResultsList buildResultsList(Response response) {
		ResultsList resultsList = new ResultsList();
		if (null != response.getErrorStream()
				&& !response.getErrorStream().equals("")) {
			ApiErrorMessage errorMessage = ProcessErrors(
					response.getErrorNumber(), response.getErrorStream());
			resultsList.setApierrormessage(errorMessage);
			return resultsList;
		} else {
			try {
				BufferedReader reader = response.getRead();
				JSONObject object = (JSONObject) new JSONTokener(reader)
						.nextValue();

				JSONObject searchRequestGet = object
						.getJSONObject("SearchRequestGet");

				String QueryString = searchRequestGet.getString("QueryString");
				resultsList.setQueryString(QueryString);

				// set result list's attribute TotalHits and TotalSearchTime
				String TotalHits, TotalSearchTime;
				JSONObject searchResult = object.getJSONObject("SearchResult");
				JSONObject Statistics = searchResult
						.getJSONObject("Statistics");

				TotalHits = Statistics.getString("TotalHits");
				TotalSearchTime = Statistics.getString("TotalSearchTime");
				resultsList.setHits(TotalHits);
				resultsList.setSearchTime(TotalSearchTime);

				// set result list's attribute result list;
				ArrayList<Record> recordList = new ArrayList<Record>();
				JSONObject data = searchResult.optJSONObject("Data");
				if (null != data && data.has("Records")) {

					JSONArray records = data.optJSONArray("Records");
					if (null != records) {
						for (int i = 0; i < records.length(); i++) {
							JSONObject jsonRecord = records.getJSONObject(i);
							Record record = constructRecord(jsonRecord);
							recordList.add(record);
						}
					}
					resultsList.setResultsList(recordList);
				}
			} catch (JSONException e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error parsing search response message");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				resultsList.setApierrormessage(errorMessage);
			} catch (Exception e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing search response response");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				resultsList.setApierrormessage(errorMessage);
			}
		}
		return resultsList;
	}

	/**
	 * Builds an JSON UIDAuthRequest Message to send to the EDS API
	 */
	public String buildUIDAuthRequest(String username, String password) {
		return "{\"UserId\":\"" + username + "\",\"Password\":\"" + password
				+ "\"}";
	}

	/**
	 * Constructs a retrieve response object from the EDS API response JSON
	 * message
	 */
	public RetrieveResult buildRecord(Response response) {
		RetrieveResult retrieveResult = new RetrieveResult();
		if (null != response.getErrorStream()
				&& !response.getErrorStream().equals("")) {
			ApiErrorMessage errorMessage = ProcessErrors(
					response.getErrorNumber(), response.getErrorStream());
			retrieveResult.setApiErrorMessage(errorMessage);
			return retrieveResult;
		} else {
			try {
				BufferedReader reader = response.getRead();
				JSONObject object = (JSONObject) new JSONTokener(reader)
						.nextValue();
				JSONObject jsonObject = object.optJSONObject("Record");
				if (null != jsonObject) {
					Record record = constructRecord(jsonObject);
					retrieveResult.setRecord(record);
				}
			} catch (JSONException e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error parsing retrieve response message");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				retrieveResult.setApiErrorMessage(errorMessage);
			} catch (Exception e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing retrieve response");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				retrieveResult.setApiErrorMessage(errorMessage);
			}
		}
		return retrieveResult;

	}

	/**
	 * Constructs a record object from an EDS API JSON response
	 */
	private Record constructRecord(JSONObject jsonRecord) throws JSONException {
		Record record = new Record();

		String resultId = jsonRecord.getString("ResultId");
		String pLink = jsonRecord.optString("PLink");
		JSONObject header = jsonRecord.getJSONObject("Header");
		String dbId = header.getString("DbId");
		String dbLabel = header.getString("DbLabel");
		String an = header.getString("An");
		String pubType = header.optString("PubType");
		String pubTypeId = header.optString("PubTypeId");

		record.setAn(an);
		record.setpLink(pLink);
		record.setDbLabel(dbLabel);
		record.setPubType(pubType);
		record.setDbId(dbId);
		record.setResultId(resultId);
		record.setPubTypeId(pubTypeId);

		// set BookJacket list in the result
		ArrayList<BookJacket> bookJacketList = new ArrayList<BookJacket>();
		JSONArray imageInfoArray = jsonRecord.optJSONArray("ImageInfo");
		if (null != imageInfoArray) {
			for (int j = 0; j < imageInfoArray.length(); j++) {
				BookJacket bookJacket = new BookJacket();
				JSONObject Imageinfo = imageInfoArray.getJSONObject(j);
				String Size = Imageinfo.optString("Size");
				String Target = Imageinfo.optString("Target");
				bookJacket.setSize(Size);
				bookJacket.setTarget(Target);
				bookJacketList.add(bookJacket);
			}
			record.setBookJacketList(bookJacketList);
		}

		// set Custom link List in the result
		ArrayList<CustomLink> customLinkList = new ArrayList<CustomLink>();
		JSONArray customLinks = jsonRecord.optJSONArray("CustomLinks");
		if (null != customLinks) {
			for (int j = 0; j < customLinks.length(); j++) {

				CustomLink customlink = new CustomLink();
				JSONObject customLinkJSON = customLinks.getJSONObject(j);
				String customLinkURl = customLinkJSON.optString("Url");
				String customLinkName = customLinkJSON.optString("Name");
				String customLinkCategory = customLinkJSON
						.optString("Category");
				String customLinkText = customLinkJSON.optString("Text");
				String customLinkIcon = customLinkJSON.optString("Icon");
				String mouseOverText = customLinkJSON
						.optString("MouseOverText");

				customlink.setCategory(customLinkCategory);
				customlink.setIcon(customLinkIcon);
				customlink.setMouseOverText(mouseOverText);
				customlink.setName(customLinkName);
				customlink.setText(customLinkText);
				customlink.setUrl(customLinkURl);

				customLinkList.add(customlink);
			}
			record.setCustomLinkList(customLinkList);
		}

		// set Item in the result

		ArrayList<Item> itemList = new ArrayList<Item>();
		JSONArray items = jsonRecord.optJSONArray("Items");
		if (null != items) {
			for (int j = 0; j < items.length(); j++) {
				Item item = new Item();
				JSONObject itemJSON = items.getJSONObject(j);
				String itemData = itemJSON.optString("Data");
				String label = itemJSON.optString("Label");
				String group = itemJSON.optString("Group");

				itemData = Jsoup.parse(itemData).text().toString();
				itemData = TransDataToHTML.transDataToHTML(itemData);
				item.setData(itemData);
				item.setGroup(group);
				item.setLabel(label);
				itemList.add(item);
			}
			record.setItemList(itemList);
		}

		// Get Full Text Info
		JSONObject fullText = jsonRecord.optJSONObject("FullText");
		if (null != fullText) {
			JSONObject htmlFullText = fullText.optJSONObject("Text");
			if (null != htmlFullText) {
				String availability = htmlFullText.optString("Availability");
				record.setHtml(availability);
			}
		}
		return record;
	}

	/**
	 * Process error response messages
	 * 
	 * @param response
	 *            the error response message
	 * @return an ApiErrorMessage object
	 */
	/* Build an error message object */
	private ApiErrorMessage ProcessErrors(String errorNumber, String errorStream) {
		InputStream errorInputStream = new ByteArrayInputStream(
				errorStream.getBytes());
		InputStreamReader in = new InputStreamReader(errorInputStream);
		BufferedReader errorReader = new BufferedReader(in);
		ApiErrorMessage apiErrorMessage = new ApiErrorMessage();
		try {
			if (errorNumber.equals(HTTP_Bad_Request)) {
				JSONObject object = (JSONObject) new JSONTokener(errorReader)
						.nextValue();
				String DetailedErrorDescription = object
						.getString("DetailedErrorDescription");
				String ErrorDescription = object.getString("ErrorDescription");
				String ErrorNumber = object.getString("ErrorNumber");
				apiErrorMessage
						.setDetailedErrorDescription(DetailedErrorDescription);
				apiErrorMessage.setErrorDescription(ErrorDescription);
				apiErrorMessage.setErrorNumber(ErrorNumber);
			} else {
				String DetailedErrorDescription = errorStream;
				String ErrorNumber = errorNumber;
				String ErrorDescription = errorStream;
				apiErrorMessage
						.setDetailedErrorDescription(DetailedErrorDescription);
				apiErrorMessage.setErrorDescription(ErrorDescription);
				apiErrorMessage.setErrorNumber(ErrorNumber);
			}
		} catch (JSONException e) {
			apiErrorMessage
					.setErrorDescription("Error parsing response message");
			apiErrorMessage.setDetailedErrorDescription(e.getMessage());
		} catch (Exception e) {
			apiErrorMessage
					.setErrorDescription("Error processing error response");
			apiErrorMessage.setDetailedErrorDescription(e.getMessage());
		}
		return apiErrorMessage;
	}

}
