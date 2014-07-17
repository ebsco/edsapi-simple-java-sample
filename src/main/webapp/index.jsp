
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<link rel="stylesheet" href="css/style.css" type="text/css" media="screen" />
<div class="basicSearch">
<form action="Search" method="get">
    <p>
        <input type="text" name="lookfor"  style="width: 350px;" id="lookfor" /> 
        <input type="submit" value="Search" />
    </p>
    <table>
        <tr>
            <td>
                <input type="radio" id="type-keyword" name="type" value="keyword" checked="checked"/>
                <label for="type-keyword">Keyword</label>
            </td>
            <td>
                <input type="radio" id="type-author" name="type" value="Author" />
                <label for="type-author">Author</label>
            </td>
            <td>
                <input type="radio" id="type-title" name="type" value="title" />
                <label for="type-title">Title</label>
                <input type="hidden" name="facet" value="addexpander(fulltext)" />                         
            </td>
        </tr>
     
    </table>
    

</form>

</div>

