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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jsoup.Jsoup;
import org.xml.sax.InputSource;

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

public class XMLProcessor implements IMessageProcessor {
	public static final String HTTP_BAD_REQUEST = "400";

	public String GetContentType() {
		return "application/XML";
	}

	/**
	 * Constructs a session token object from an EDS API Response
	 */
	public SessionToken buildSessionToken(Response response) {
		BufferedReader reader = response.getRead();
		String sessionTokenXML = "";
		// Check for errors
		SessionToken sessionToken = new SessionToken();
		if (null != response.getErrorStream()
				&& !response.getErrorStream().isEmpty()) {
			sessionToken.setApiErrorMessage(ProcessError(
					response.getErrorNumber(), response.getErrorStream()));
		} else {
			if (null != reader) {
				try {
					String line = "";
					while ((line = reader.readLine()) != null) {
						sessionTokenXML += line;
					}
				} catch (IOException e) {
					ApiErrorMessage errorMessage = new ApiErrorMessage();
					errorMessage
							.setErrorDescription("Error processing create session response");
					errorMessage.setDetailedErrorDescription(e.getMessage());
					sessionToken.setApiErrorMessage(errorMessage);
				}
			}
			/*
			 * Parse String to XML and the get the value
			 */
			try {
				StringReader stringReader = new StringReader(sessionTokenXML);
				InputSource inputSource = new InputSource(stringReader);
				Document doc = (new SAXBuilder()).build(inputSource);
				Element root = doc.getRootElement();
				Content content = root.getContent().get(0);

				if (content.getValue() != null) {
					sessionToken.setSessionToken(content.getValue());
				}
			} catch (Exception e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing search response");
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
		String resultsListXML = "";

		if (null != response.getErrorStream()
				&& !response.getErrorStream().isEmpty()) {
			resultsList.setApierrormessage(ProcessError(
					response.getErrorNumber(), response.getErrorStream()));
		} else {
			BufferedReader reader = response.getRead();
			if (null != reader) {
				try {
					String line = "";
					while ((line = reader.readLine()) != null) {
						resultsListXML += line;
					}
				} catch (IOException e) {
					ApiErrorMessage errorMessage = new ApiErrorMessage();
					errorMessage
							.setErrorDescription("Error processing resultList response");
					errorMessage.setDetailedErrorDescription(e.getMessage());
					resultsList.setApierrormessage(errorMessage);
				}
			}
			try {
				StringReader stringReader = new StringReader(resultsListXML);
				InputSource inputSource = new InputSource(stringReader);
				Document doc = (new SAXBuilder()).build(inputSource);
				// root element (level 1), handle resultsList
				Element searchResponseMessageGet = doc.getRootElement();

				// level 2 elements
				if (null == searchResponseMessageGet)
					return resultsList;

				Element searchRequestGet = searchResponseMessageGet.getChild(
						"SearchRequestGet",
						searchResponseMessageGet.getNamespace());
				Element searchResult = searchResponseMessageGet
						.getChild("SearchResult",
								searchResponseMessageGet.getNamespace());
				// level 3 elements
				if (null != searchRequestGet) {
					Element queryString = searchRequestGet.getChild(
							"QueryString", searchRequestGet.getNamespace());
					// Get Query String
					String querystring = queryString.getContent(0).getValue();
					resultsList.setQueryString(querystring);
				}

				Element statistics = searchResult.getChild("Statistics",
						searchResult.getNamespace());
				Element data = searchResult.getChild("Data",
						searchResult.getNamespace());

				/*
				 * In next steps, elements will be analyzed separately
				 */

				// Get Total Hits and Total Search Time
				String totalHits = "0";
				if (null != statistics) {
					totalHits = statistics.getContent(0).getValue();
					String totalSearchTime = statistics.getContent(1)
							.getValue();
					resultsList.setHits(totalHits);
					resultsList.setSearchTime(totalSearchTime);
				}

				if (Integer.parseInt(totalHits) > 0 && null != data) {
					// Get Results
					Element records = data.getChild("Records",
							data.getNamespace());
					if (null != records && null != records.getChildren()) {
						List<Element> recordsList = records.getChildren();
						for (int i = 0; i < recordsList.size(); i++) {
							Element xmlRecord = (Element) recordsList.get(i);
							if (null == xmlRecord)
								continue;
							Record record = new Record();
							record = constructRecord(xmlRecord);
							resultsList.getResultsList().add(record);
						}
					}
				}
			} catch (Exception e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing search response");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				resultsList.setApierrormessage(errorMessage);
			}
		}
		return resultsList;

	}

	/**
	 * Builds an XML UIDAuthRequest Message to send to the EDS API
	 */
	public String buildUIDAuthRequest(String userName, String password) {
		return "<UIDAuthRequestMessage xmlns=\"http://www.ebscohost.com/services/public/AuthService/Response/2012/06/01\">"
				+ "<UserId>"
				+ userName
				+ "</UserId>"
				+ "<Password>"
				+ password
				+ "</Password>"
				+ "<InterfaceId></InterfaceId>"
				+ "</UIDAuthRequestMessage>";
	}

	/**
	 * Constructs a retrieve response object from the EDS API response XML
	 * message
	 */
	public RetrieveResult buildRecord(Response response) {

		RetrieveResult retrieveResult = new RetrieveResult();
		BufferedReader reader = response.getRead();
		String RecordXML = "";
		if (null != response.getErrorStream()
				&& !response.getErrorStream().isEmpty()) {
			retrieveResult.setApiErrorMessage(ProcessError(
					response.getErrorNumber(), response.getErrorStream()));
		} else {
			try {
				String line = "";
				while ((line = reader.readLine()) != null) {
					RecordXML += line;
				}
			} catch (IOException e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing resultList response");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				retrieveResult.setApiErrorMessage(errorMessage);
			}
			try {
				StringReader stringReader = new StringReader(RecordXML);
				InputSource inputSource = new InputSource(stringReader);
				Document doc = (new SAXBuilder()).build(inputSource);

				// ---------------begin to handle record

				// root element (level 1)
				Element data = doc.getRootElement();
				// level 2 elements

				// Get Results
				Element xmlRecord = data
						.getChild("Record", data.getNamespace());
				Record record = null;
				if (null != xmlRecord)
					record = constructRecord(xmlRecord, true);
				retrieveResult.setRecord(record);
			} catch (Exception e) {
				ApiErrorMessage errorMessage = new ApiErrorMessage();
				errorMessage
						.setErrorDescription("Error processing search response");
				errorMessage.setDetailedErrorDescription(e.getMessage());
				retrieveResult.setApiErrorMessage(errorMessage);
			}
		}
		return retrieveResult;

	}

	/**
	 * Constructs a record object from an EDS API XML response
	 */

	private Record constructRecord(Element xmlRecord) {
		return constructRecord(xmlRecord, false);
	}

	private Record constructRecord(Element xmlRecord, Boolean parse) {
		Record record = new Record();

		// Get Record Id
		String resultId = xmlRecord.getChildText("ResultId",
				xmlRecord.getNamespace());
		record.setResultId(resultId);

		// Get Header Info
		Element header = xmlRecord.getChild("Header", xmlRecord.getNamespace());
		if (null != header) {
			String dbId = header.getChildText("DbId", header.getNamespace());
			String dbLabel = header.getChildText("DbLabel",
					header.getNamespace());
			String an = header.getChildText("An", header.getNamespace());
			String pubType = header.getChildText("PubType",
					header.getNamespace());
			String pubTypeId = header.getChildText("PubTypeId",
					header.getNamespace());

			record.setDbId(dbId);
			record.setDbLabel(dbLabel);
			record.setPubTypeId(pubTypeId);
			record.setAn(an);
			record.setPubType(pubType);
		}
		// Get PLink
		String pLink = xmlRecord
				.getChildText("PLink", xmlRecord.getNamespace());
		record.setpLink(pLink);

		// Get ImageInfo

		Element imageInfo = xmlRecord.getChild("ImageInfo",
				xmlRecord.getNamespace());

		if (imageInfo != null) {
			List<Element> coverArts = imageInfo.getChildren();
			for (int b = 0; b < coverArts.size(); b++) {
				Element coverArt = (Element) coverArts.get(b);
				if (null != coverArt) {
					BookJacket bookJacket = new BookJacket();
					String size = coverArt.getChildText("Size",
							coverArt.getNamespace());
					String target = coverArt.getChildText("Target",
							coverArt.getNamespace());
					bookJacket.setSize(size);
					bookJacket.setTarget(target);
					record.getBookJacketList().add(bookJacket);
				}
			}
		}

		// Get Custom Links
		Element customLinks = xmlRecord.getChild("CustomLinks",
				xmlRecord.getNamespace());
		if (customLinks != null) {
			List<Element> customLinksList = customLinks.getChildren();
			for (int c = 0; c < customLinksList.size(); c++) {
				Element cl = (Element) customLinksList.get(c);
				if (null != cl) {
					String clurl = cl.getChildText("Url", cl.getNamespace());
					String name = cl.getChildText("Name", cl.getNamespace());
					String category = cl.getChildText("Category",
							cl.getNamespace());
					String text = cl.getChildText("Text", cl.getNamespace());
					String icon = cl.getChildText("Icon", cl.getNamespace());
					String mouseOverText = cl.getChildText("MouseOverText",
							cl.getNamespace());

					CustomLink customLink = new CustomLink();
					customLink.setUrl(clurl);
					customLink.setName(name);
					customLink.setCategory(category);
					customLink.setText(text);
					customLink.setIcon(icon);
					customLink.setMouseOverText(mouseOverText);
					record.getCustomLinkList().add(customLink);
				}
			}
		}

		// Get Full Text Info
		Element fullText = xmlRecord.getChild("FullText",
				xmlRecord.getNamespace());
		if (null != fullText) {
			Element htmlFullText = fullText.getChild("Text",
					fullText.getNamespace());
			if (null != htmlFullText) {
				String availability = htmlFullText.getChildText("Availability",
						fullText.getNamespace());
				record.setHtml(availability);
			}
		}

		// Process Items
		Element items = xmlRecord.getChild("Items", xmlRecord.getNamespace());
		if (null != items) {
			List<Element> itemList = items.getChildren();
			for (int j = 0; j < itemList.size(); j++) {
				Element itemElement = (Element) itemList.get(j);
				Item item = new Item();
				String label = itemElement.getChildText("Label",
						itemElement.getNamespace());
				String group = itemElement.getChildText("Group",
						itemElement.getNamespace());
				String itemData = itemElement.getChildText("Data",
						itemElement.getNamespace());

				if (parse)
					itemData = Jsoup.parse(itemData).text().toString();
				// translate data to valid HTML tags
				itemData = TransDataToHTML.transDataToHTML(itemData);
				item.setLabel(label);
				item.setGroup(group);
				item.setData(itemData);
				record.getItemList().add(item);
			}
		}
		return record;
	}

	public ApiErrorMessage ProcessError(String errorNumber, String errorStream) {
		ByteArrayInputStream errorInputStream = new ByteArrayInputStream(
				errorStream.getBytes());
		InputStreamReader in = new InputStreamReader(errorInputStream);
		BufferedReader errorreader = new BufferedReader(in);
		ApiErrorMessage apiErrorMessage = new ApiErrorMessage();
		try {
			if (errorNumber.equals(HTTP_BAD_REQUEST)) {
				String line = "";
				String resultListErrorStream = "";

				try {
					while ((line = errorreader.readLine()) != null) {
						resultListErrorStream += line;
					}
				} catch (IOException e) {
					apiErrorMessage.setErrorDescription("Error occurred");
					apiErrorMessage.setDetailedErrorDescription(e.getMessage());
					return apiErrorMessage;
				}
				StringReader stringReader = new StringReader(
						resultListErrorStream);
				InputSource inputSource = new InputSource(stringReader);
				Document doc = (new SAXBuilder()).build(inputSource);

				if (doc.getRootElement().getName() == "APIErrorMessage") {
					Element root = doc.getRootElement();
					String detailedErrorDescription = root.getChildText(
							"DetailedErrorDescription", root.getNamespace());
					String errorDescription = root.getChildText(
							"ErrorDescription", root.getNamespace());
					String errorNum = root.getChildText("ErrorNumber",
							root.getNamespace());

					apiErrorMessage
							.setDetailedErrorDescription(detailedErrorDescription);
					apiErrorMessage.setErrorDescription(errorDescription);
					apiErrorMessage.setErrorNumber(errorNum);

				} else {
					apiErrorMessage.setDetailedErrorDescription(errorStream);
					apiErrorMessage.setErrorDescription(errorStream);
					apiErrorMessage.setErrorNumber(errorNumber);
				}
			}
		} catch (Exception e) {
			apiErrorMessage.setErrorDescription("Error occurred");
			apiErrorMessage.setDetailedErrorDescription(e.getMessage());
		}
		return apiErrorMessage;
	}

}
