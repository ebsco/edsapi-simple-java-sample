<html>
<head>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="com.eds.bean.*" language="java" %>  
<%@page import="com.eds.helper.*" language="java" %>
<%@page import="org.jsoup.Jsoup" %> 
<%@page import="java.util.ArrayList" %>
<%@page import="java.net.URLEncoder" %>
<%@page import="java.net.URLDecoder" %>


<link rel="stylesheet" href="css/pubtype-icons.css" type="text/css" media="screen" />
<link rel="stylesheet" href="css/style.css" type="text/css" media="screen" />
<% ResultsList resultsList = (ResultsList) request.getSession().getAttribute("resultsList"); %>
<%
    ArrayList<Record> list=resultsList.getResultsList(); 

String value=resultsList.getLookfor();
// here we remove quote just need to show the lookfor on the input text, jsp will treat the input value still as the a string.
value=StringEscape.unEscapeString(value);
%>
</head>

<div class="searchResult">
<form action="Search"> 
  <%
       
   %>
     <input type="text" name="lookfor" style="width: 350px;" id="lookfor" value='<%=value%>'> 
      
              
<%
                         String selectKeyword = "" ;
                        String selectAuthor = "";
                        String selectTitle = "";
                       if(resultsList.getType().equals("keyword")){
                     	  selectKeyword = "selected=\"selected\"";
                       }else if(resultsList.getType().equals("Author")){
                     	  selectAuthor = "selected=\"selected\"";
                       }else if(resultsList.getType().equals("title")){
                     	  selectTitle = "selected=\"selected\"";
                       }
                     %>
        <select name="type">
        <option id="type-keyword"  value="keyword" <%out.println(selectKeyword);%> >Keyword</option>
        <option id="type-author"  value="Author" <%out.println(selectAuthor);%> >Author</option>
        <option id="type-title" value="title" <%out.println(selectTitle);%> >Title</option>     
        </select>
        <input type="submit" value="Search" />
       
  
</form>
<%
    if(resultsList.getApierrormessage()!=null)
{
	ApiErrorMessage aem=resultsList.getApierrormessage();
    String errorString=aem.getErrorDescription();
    		out.println(errorString);
}
else{
%>


<%
    if(Integer.parseInt(resultsList.getHits())<=0){
%>
        <div class="result-table-row">
            <div class="table-cell">
                <h2><i>No results were found.</i></h2>
            </div>
        </div>
    <%
        }else{
    %>
          <div><p style="font-size:15px"><%
              out.println(resultsList.getHits()+" Results");
          %></p></div>
        <%
            for(int i=0; i< resultsList.getResultsList().size();i++){ 
                       Record result = resultsList.getResultsList().get(i);
        %>
<!-- start tag of result content -->

<div class="each-result">
<table>
<tr>
<td width="10%">

<!-- ResultID -->
<div class="resultId" align="center"><% out.println(result.getResultId()+"."); %></div>

<!-- PubType -->
<% if(result.getPubType()!= null){ %>

<!-- PubImg -->

<div class="resultPubImg" >

<%
    String bookJacket ="";
if(result.getBookJacketList().size()>0){
%>
	             
                    <%
	                                     for(int b=0;b<result.getBookJacketList().size();b++){
	                                                             if(result.getBookJacketList().get(b).getSize().equals("thumb")){
	                                                             	bookJacket = result.getBookJacketList().get(b).getTarget();
	                                                                 //break;
	                                                             } 
	                                                        }
	                                 %>                                          
  
  <div class="center">
  <a href=""> <img src="<%=bookJacket%>"  width="65px" height="90px" style="border:2px solid white"/>  </a> 
  </div>




               <%
                   }
               //PubType  Here we use PNG and pubID to get image information and link    
                            
               else{
               	String dicIcon=result.getPubTypeId();
               	String pubIcon="pt-"+dicIcon;
               %>
    <div class="center">
	<span class="pt-icon <%=pubIcon%>" ></span>	
	</div>
<%}

               
       %>    
             
</div>

 

<div class="resultPubType"  align="center"><p><%out.println(result.getPubType());%></p></div>

<%}%>

</td>
<!-- Get author,abstract,subject information -->
  <%  String title = "";
                        String author = "";
                        String abstracts = "";
                        String source ="";
                        String subject = "";
                        String Description="";
                        String Categories="";
                        String PublicationInformation="";
                     
                        for(int c=0; c< result.getItemList().size();c++){
                            Item item = result.getItemList().get(c);
                                                      
                            if(item.getGroup().equals("Ti")){                         	
	
                            	title=item.getData(); 

                
                            }  	
                            if(item.getGroup().equals("Au")){
                            	author =item.getData();
                            	author=author.replace("<br />", ";  ");
                          

                            }
                          
                            if(item.getGroup().equals("Ab")){
                            	abstracts= item.getData();
                            
                            
                            	
                            }
                            if(item.getGroup().equals("Su")){
                          
                            	subject=item.getData();
                            	subject=subject.replace("<br />", ";  ");
                            	
                            } 
  
                       }%>

<td >



<% String an=result.getAn();String dbid=result.getDbId();%>
<!-- Title -->



<div class="title" style="overflow:hidden"><a href='Retrieve?an=<%=an%>&dbid=<%=dbid%>'><%=title%></a></div><br/>

<!-- Author -->

 <% if(!author.equals("")){
	 
	 BuildSearchLink bsl=new BuildSearchLink();
	 String authorvalue=bsl.buildSearchLink(author);
%>
	 <div class="author" style="overflow:hidden; text-overflow: ellipsis"><span><strong>Author:</strong></span><%=authorvalue%></div><br/>
 <% } %> 



<!-- Subject -->


 <% if(!subject.equals("")){
	     BuildSearchLink bsl=new BuildSearchLink();
		 String subjectvalue=bsl.buildSearchLink(subject);
 %>
 
 <div class="author" style="overflow:hidden; text-overflow: ellipsis"><span><strong>Subject:</strong></span><%=subjectvalue%></div><br/>
 
 
                         <%}%>  

<!-- Abstract -->
                       <%if(!abstracts.equals("")){ 
                       
                    
                       %>
                        <div class="abstract" style="overflow:hidden;text-overflow: ellipsis">
                          
                            
                            <% out.println("<span>"+"Abstract:"+abstracts+"</span>"); %><br>
                        
                        </div>

 
                 <% } 

else{}
%>
</td>
</tr>
</table>


</div>


<%}
    }}%>


</div>
</html>