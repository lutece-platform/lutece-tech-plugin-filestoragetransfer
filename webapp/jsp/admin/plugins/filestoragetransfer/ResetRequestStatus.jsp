<jsp:useBean id="managetransferrequestRequest" scope="session" class="fr.paris.lutece.plugins.filestoragetransfer.web.RequestJspBean" />
<% String strContent = managetransferrequestRequest.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
