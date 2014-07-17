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

public class RetrieveResult {
	private ApiErrorMessage apiErrorMessage;
	private Record record;

	public ApiErrorMessage getApiErrorMessage() {
		return this.apiErrorMessage;
	}

	public void setApiErrorMessage(ApiErrorMessage apiErrorMessage) {
		this.apiErrorMessage = apiErrorMessage;
	}

	public Record getRecord() {
		return this.record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

}
