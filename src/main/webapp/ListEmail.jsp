<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Email Manager</title>
</head>
<body>

<%
    String subscribe = (String) request.getAttribute("subscribe");
    if (subscribe != null && !subscribe.isEmpty()) {
%>
<p>Adresse <%= subscribe %> inscrite.</p>
<hr/>
<a href="EmailServlet">Afficher la liste</a>
<%
        request.setAttribute("subscribe", "");
    }
%>

<%
    String unsubscribe = (String) request.getAttribute("unsubscribe");
    if (unsubscribe != null && !unsubscribe.isEmpty()) {
%>
<p>Adresse <%= unsubscribe %> supprimée.</p>
<hr/>
<a href="EmailServlet">Afficher la liste</a>
<%
        request.setAttribute("unsubscribe", "");
    }
%>

<% if (subscribe == null && unsubscribe == null) { %>
<h3>Membres :</h3>
<ul>
    <%
        List<String> emails = (List<String>) request.getAttribute("emailList");
        if (emails != null && !emails.isEmpty()) {
            for (String email : emails) {
    %>
    <li><%= email %></li>
    <%
            }
        }
    %>
</ul>
<hr/>

<h3>Manage Emails:</h3>
<form method="post" action="EmailServlet">
    <label>Entrer votre addresse email :  <input type="text" name="email"/></label><br/><br/>
    <input type="submit" name="action" value="Subscribe"/>
    <input type="submit" name="action" value="Unsubscribe"/>
</form>

<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage != null && !errorMessage.isEmpty()) {
%>
<p style="color:red;"><%= errorMessage %></p>
<%
        }
    }
%>

</body>
</html>
