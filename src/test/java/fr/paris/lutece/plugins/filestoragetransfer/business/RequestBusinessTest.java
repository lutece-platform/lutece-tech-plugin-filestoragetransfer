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
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *"
 * License 1.0
 */

package fr.paris.lutece.plugins.filestoragetransfer.business;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileTransferRequestHome;
import fr.paris.lutece.test.LuteceTestCase;

import java.util.Optional;

import org.bouncycastle.cert.ocsp.Req;

/**
 * This is the business class test for the object Request
 */
public class RequestBusinessTest extends LuteceTestCase
{
    private static final String OLDFILEKEY1 = "OldFileKey1";
    private static final String OLDFILEKEY2 = "OldFileKey2";
    private static final String SOURCEFILESERVICEPROVIDERNAME1 = "SourceFileserviceproviderName1";
    private static final String SOURCEFILESERVICEPROVIDERNAME2 = "SourceFileserviceproviderName2";
    private static final String TARGETFILESERVICEPROVIDERNAME1 = "TargetFileserviceproviderName1";
    private static final String TARGETFILESERVICEPROVIDERNAME2 = "TargetFileserviceproviderName2";

    /**
     * test Request
     */
    public void testBusiness( )
    {
        // Initialize an object
        FileTransferRequest request = new FileTransferRequest( );
        request.setOldFileKey( OLDFILEKEY1 );
        request.setSourceFileserviceproviderName( SOURCEFILESERVICEPROVIDERNAME1 );
        request.setTargetFileserviceproviderName( TARGETFILESERVICEPROVIDERNAME1 );
        request.setRequestStatus( RequestStatus.STATUS_TODO );

        // Create test
        FileTransferRequestHome.create( request );
        Optional<FileTransferRequest> optRequestStored = FileTransferRequestHome.findByPrimaryKey( request.getId( ) );
        FileTransferRequest requestStored = optRequestStored.orElse( new FileTransferRequest( ) );
        assertEquals( requestStored.getOldFileKey( ), request.getOldFileKey( ) );
        assertEquals( requestStored.getSourceFileserviceproviderName( ), request.getSourceFileserviceproviderName( ) );
        assertEquals( requestStored.getTargetFileserviceproviderName( ), request.getTargetFileserviceproviderName( ) );
        assertEquals( requestStored.getRequestStatus( ), request.getRequestStatus( ) );

        // Update test
        request.setOldFileKey( OLDFILEKEY2 );
        request.setSourceFileserviceproviderName( SOURCEFILESERVICEPROVIDERNAME2 );
        request.setTargetFileserviceproviderName( TARGETFILESERVICEPROVIDERNAME2 );
        request.setRequestStatus( RequestStatus.STATUS_DONE );
        FileTransferRequestHome.update( request );
        optRequestStored = FileTransferRequestHome.findByPrimaryKey( request.getId( ) );
        requestStored = optRequestStored.orElse( new FileTransferRequest( ) );

        assertEquals( requestStored.getOldFileKey( ), request.getOldFileKey( ) );
        assertEquals( requestStored.getSourceFileserviceproviderName( ), request.getSourceFileserviceproviderName( ) );
        assertEquals( requestStored.getTargetFileserviceproviderName( ), request.getTargetFileserviceproviderName( ) );
        assertEquals( requestStored.getRequestStatus( ), request.getRequestStatus( ) );

        // List test
        FileTransferRequestHome.getRequestsList( );

        // Delete test
        FileTransferRequestHome.remove( request.getId( ) );
        optRequestStored = FileTransferRequestHome.findByPrimaryKey( request.getId( ) );
        requestStored = optRequestStored.orElse( null );
        assertNull( requestStored );

    }

}
