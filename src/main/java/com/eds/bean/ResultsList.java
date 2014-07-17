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

public class ResultsList {

	private String lookfor;
	private String type;
	private String queryString;
	private String hits;
	private String searchTime;
	private ApiErrorMessage apierrormessage;
	private ArrayList<Record> resultsList = new ArrayList<Record>();
	private ArrayList<Facet> facetsList = new ArrayList<Facet>();

	public ApiErrorMessage getApierrormessage() {
		return apierrormessage;
	}

	public void setApierrormessage(ApiErrorMessage apierrormessage) {
		this.apierrormessage = apierrormessage;
	}

	public String getLookfor() {
		return lookfor;
	}

	public void setLookfor(String lookfor) {
		this.lookfor = lookfor;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getHits() {
		return hits;
	}

	public String getSearchTime() {
		return searchTime;
	}

	public void setSearchTime(String searchTime) {
		this.searchTime = searchTime;
	}

	public void setHits(String hits) {
		this.hits = hits;
	}

	public ArrayList<Record> getResultsList() {
		return resultsList;
	}

	public void setResultsList(ArrayList<Record> resultsList) {
		this.resultsList = resultsList;
	}

	public ArrayList<Facet> getFacetsList() {
		return facetsList;
	}

	public void setFacetsList(ArrayList<Facet> facetsList) {
		this.facetsList = facetsList;
	}

}
