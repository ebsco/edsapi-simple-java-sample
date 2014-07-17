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

package com.eds.bean;

import java.util.ArrayList;

public class Record {

	private String resultId;
	private String pubType;
	private String dbId;
	private String dbLabel;
	private String an;
	private String pLink;
	private String pdf;
	private String html;
	private String pubTypeId;
	private ArrayList<BookJacket> bookJacketList = new ArrayList<BookJacket>();
	private ArrayList<CustomLink> CustomLinkList = new ArrayList<CustomLink>();
	private ArrayList<Item> itemList = new ArrayList<Item>();
	private ApiErrorMessage ApiErrorMessage = null;

	public String getPubTypeId() {
		return pubTypeId;
	}

	public void setPubTypeId(String pubTypeId) {
		this.pubTypeId = pubTypeId;
	}

	public String getResultId() {
		return resultId;
	}

	public void setResultId(String resultId) {
		this.resultId = resultId;
	}

	public String getPubType() {
		return pubType;
	}

	public void setPubType(String pubType) {
		this.pubType = pubType;
	}

	public String getDbId() {
		return dbId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
	}

	public String getDbLabel() {
		return dbLabel;
	}

	public void setDbLabel(String dbLabel) {
		this.dbLabel = dbLabel;
	}

	public String getAn() {
		return an;
	}

	public void setAn(String an) {
		this.an = an;
	}

	public String getpLink() {
		return pLink;
	}

	public void setpLink(String pLink) {
		this.pLink = pLink;
	}

	public String getPdf() {
		return pdf;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public ArrayList<BookJacket> getBookJacketList() {
		return bookJacketList;
	}

	public void setBookJacketList(ArrayList<BookJacket> bookJacketList) {
		this.bookJacketList = bookJacketList;
	}

	public ArrayList<CustomLink> getCustomLinkList() {
		return CustomLinkList;
	}

	public void setCustomLinkList(ArrayList<CustomLink> customLinkList) {
		CustomLinkList = customLinkList;
	}

	public ArrayList<Item> getItemList() {
		return itemList;
	}

	public void setItemList(ArrayList<Item> itemList) {
		this.itemList = itemList;
	}

	public ApiErrorMessage getApiErrorMessage() {
		return ApiErrorMessage;
	}

	public void setApiErrorMessage(ApiErrorMessage apiErrorMessage) {
		this.ApiErrorMessage = apiErrorMessage;
	}

}
