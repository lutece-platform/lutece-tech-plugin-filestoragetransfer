/*
 * Copyright (c) 2002-2024, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES LOSS OF USE, DATA, OR PROFITS OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.filestoragetransfer.web;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileTransferRequestHome;
import fr.paris.lutece.plugins.filestoragetransfer.web.RequestJspBean;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import java.util.List;
import java.io.IOException;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.web.LocalVariables;

/**
 * This is the business class test for the object Request
 */
public class RequestJspBeanTest extends LuteceTestCase
{
    private static final String OLDFILEKEY1 = "OldFileKey1";
    private static final String OLDFILEKEY2 = "OldFileKey2";
    private static final String SOURCEFILESERVICEPROVIDERNAME1 = "SourceFileserviceproviderName1";
    private static final String SOURCEFILESERVICEPROVIDERNAME2 = "SourceFileserviceproviderName2";
    private static final String TARGETFILESERVICEPROVIDERNAME1 = "TargetFileserviceproviderName1";
    private static final String TARGETFILESERVICEPROVIDERNAME2 = "TargetFileserviceproviderName2";
    private static final String REQUESTSTATUS1 = "RequestStatus1";
    private static final String REQUESTSTATUS2 = "RequestStatus2";

    public void testJspBeans( ) throws AccessDeniedException, IOException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        MockHttpServletResponse response = new MockHttpServletResponse( );
        MockServletConfig config = new MockServletConfig( );

        // display admin Request management JSP
        RequestJspBean jspbean = new RequestJspBean( );
        String html = jspbean.getManageRequests( request );
        assertNotNull( html );

        // display admin Request creation JSP
        html = jspbean.getCreateRequest( request );
        assertNotNull( html );

        // action create Request
        request = new MockHttpServletRequest( );

        response = new MockHttpServletResponse( );
        AdminUser adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );

        request.addParameter( "old_file_key", OLDFILEKEY1 );
        request.addParameter( "source_fileserviceprovider_name", SOURCEFILESERVICEPROVIDERNAME1 );
        request.addParameter( "target_fileserviceprovider_name", TARGETFILESERVICEPROVIDERNAME1 );
        request.addParameter( "request_status", REQUESTSTATUS1 );
        request.addParameter( "action", "createRequest" );
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "createRequest" ) );
        request.setMethod( "POST" );

        try
        {
            AdminAuthenticationService.getInstance( ).registerUser( request, adminUser );
            html = jspbean.processController( request, response );

            // MockResponse object does not redirect, result is always null
            assertNull( html );
        }
        catch( AccessDeniedException e )
        {
            fail( "access denied" );
        }
        catch( UserNotSignedException e )
        {
            fail( "user not signed in" );
        }

        // display modify Request JSP
        request = new MockHttpServletRequest( );
        request.addParameter( "old_file_key", OLDFILEKEY1 );
        request.addParameter( "source_fileserviceprovider_name", SOURCEFILESERVICEPROVIDERNAME1 );
        request.addParameter( "target_fileserviceprovider_name", TARGETFILESERVICEPROVIDERNAME1 );
        request.addParameter( "request_status", REQUESTSTATUS1 );
        List<Integer> listIds = FileTransferRequestHome.getIdRequestsList( );
        assertTrue( !listIds.isEmpty( ) );
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
        jspbean = new RequestJspBean( );

        assertNotNull( jspbean.getModifyRequest( request ) );

        // action modify Request
        request = new MockHttpServletRequest( );
        response = new MockHttpServletResponse( );

        adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );

        request.addParameter( "old_file_key", OLDFILEKEY2 );
        request.addParameter( "source_fileserviceprovider_name", SOURCEFILESERVICEPROVIDERNAME2 );
        request.addParameter( "target_fileserviceprovider_name", TARGETFILESERVICEPROVIDERNAME2 );
        request.addParameter( "request_status", REQUESTSTATUS2 );
        request.setRequestURI( "jsp/admin/plugins/example/ManageRequests.jsp" );
        // important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createRequest, qui est l'action par défaut
        request.addParameter( "action", "modifyRequest" );
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "modifyRequest" ) );

        try
        {
            AdminAuthenticationService.getInstance( ).registerUser( request, adminUser );
            html = jspbean.processController( request, response );

            // MockResponse object does not redirect, result is always null
            assertNull( html );
        }
        catch( AccessDeniedException e )
        {
            fail( "access denied" );
        }
        catch( UserNotSignedException e )
        {
            fail( "user not signed in" );
        }

        // get remove Request
        request = new MockHttpServletRequest( );
        // request.setRequestURI("jsp/admin/plugins/example/ManageRequests.jsp");
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
        jspbean = new RequestJspBean( );
        request.addParameter( "action", "confirmRemoveRequest" );
        assertNotNull( jspbean.getModifyRequest( request ) );

        // do remove Request
        request = new MockHttpServletRequest( );
        response = new MockHttpServletResponse( );
        request.setRequestURI( "jsp/admin/plugins/example/ManageRequestts.jsp" );
        // important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createRequest, qui est l'action par défaut
        request.addParameter( "action", "removeRequest" );
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "removeRequest" ) );
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
        request.setMethod( "POST" );
        adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );

        try
        {
            AdminAuthenticationService.getInstance( ).registerUser( request, adminUser );
            html = jspbean.processController( request, response );

            // MockResponse object does not redirect, result is always null
            assertNull( html );
        }
        catch( AccessDeniedException e )
        {
            fail( "access denied" );
        }
        catch( UserNotSignedException e )
        {
            fail( "user not signed in" );
        }

    }
}
