<!DOCTYPE html >
<html>
<head>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%> 
<%@page import="com.eds.bean.*" language="java" %> 
<%@page import="java.net.URLEncoder" %> <%@page import="java.util.ArrayList" %><%@page import="com.eds.helper.*"%>

<% RetrieveResult retrieveResult = (RetrieveResult)request.getSession().getAttribute("record");
Record record = retrieveResult.getRecord(); %> 

<%String picurl="";%>

<link rel="stylesheet" href="css/style.css" type="text/css" media="screen" />

</head>
<body>
<%
    if(retrieveResult.getApiErrorMessage()!=null)
{
	ApiErrorMessage aem=retrieveResult.getApiErrorMessage();
    String errorString=aem.getErrorDescription();
    		out.println(errorString);
}
else{
%>

<!-- Here we address record -->
<div class="center">
<div class="record" align="left" >
<div class="topbar">
<a href="javascript:history.back();" class="back"><%out.println("<< Results List");%></a>
</div>

<%ArrayList<Item> itemList=record.getItemList();%>
<% ArrayList<CustomLink> CustomLinkList=record.getCustomLinkList();%>
<%
    ArrayList<BookJacket> bookJacketList=record.getBookJacketList();
%>


<table>
<tr>
<td>

<% 
String data="";
for(int i=0;i<itemList.size();i++){
	
	Item item=itemList.get(i);	
	data =item.getData();
 
%>

<% 
if(item.getGroup().equals("Ti"))
{
%>
<h1><%out.println(data);%></h1>
	
<%}else{

if(item.getGroup().equals("Au")||item.getGroup().equals("Su")||item.getGroup().equals("Ca"))
{
	 BuildSearchLink bsl=new BuildSearchLink();
	 String value=bsl.buildSearchLink(item.getData());
%>
	<table>
	<tr>
	<td width="20%" align="left"><div><strong><%out.println(item.getLabel()+":");%></strong></div></td>
	<td><%=value%><br/></td>
	</tr>
		
<%}

else{%>
	
	
	<tr>
	<td width="20%" align="left"><div ><strong><%out.println(item.getLabel()+":");%></strong></div></td>
	<td width="80%" align="left"><div ><%out.println(data);%></div></td>
	</tr>


<%}


}}%>
</table>
</td>

<td>



<%
String bookJacket ="";

			
if(record.getBookJacketList().size()>0){
	                 
	               %>
	             
                    <% for(int b=0;b<record.getBookJacketList().size();b++){
                    	
                    	
                            if(record.getBookJacketList().get(b).getSize().equals("medium")){
                            	
                            	
                            	bookJacket = record.getBookJacketList().get(b).getTarget();
                                //break;
                            } 
                       }
   %>                                          
  
  <div class="center">
  <a href=""> <img src="<%=bookJacket%>"  width="150px" height="200px" style="border:2px solid white"/></a> 
  </div>
               <% }
               
else{%><%}}%> 

</td>
</tr>

</table>
</div>
</div>



</body>

</html>