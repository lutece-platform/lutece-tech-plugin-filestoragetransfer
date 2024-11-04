<jsp:useBean id="managetransfererrorError" scope="session" class="fr.paris.lutece.plugins.filestoragetransfer.web.ErrorJspBean" />
<% String strContent = managetransfererrorError.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
