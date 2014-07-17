To use the demo:
Run 'mvn install' in the directory containing the pom.xml file to create a war file and deploy in Tomcat.

Configuring the Simple Java Demo:
This demo only supports IP Authentication. Make sure that you are set up for IP Authentication.

To set the profile for the EDS API to work with, use the 'profile' parameter:

		```xml
		<init-param>
			<param-name>profile</param-name>
			<param-value>[your_profile]</param-value>
		</init-param>
		```

The demo supports the processing of messages in JSON or XML. To set the message format, use the 'message_format'
parameter. The available formats are XML or JSON. 

		```xml
		<init-param>
			<param-name>message_format</param-name>
			<param-value>XML</param-value>
			<!-- <param-value>JSON</param-value> -->
		</init-param>
		```

The demo supports running in guest or non-guest mode. To set the mode, change the 'is_guest' parameter. 
To set the application to run in guest mode, set the value to 'y'. To run in non-guest mode, set the value to 'y'

		```xml
		<init-param>
			<param-name>is_guest</param-name>
			<param-value>y</param-value>
		</init-param>
		```
