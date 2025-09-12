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
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.filestoragetransfer.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferError;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferErrorHome;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequestHome;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequestStatus;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.template.AppTemplateService;

public class FileStorageTransferService
{
    private static int _nRetryDelay = AppPropertiesService.getPropertyInt( "filestoragetransfer.RetryDelay", 3600 );
    private static int _nRetryLimit = AppPropertiesService.getPropertyInt( "filestoragetransfer.RetryLimit", 0 );
    private static boolean _bNotificationsEnabled = AppPropertiesService.getPropertyBoolean( "filestoragetransfer.NotificationsEnabled", false );

    private static String _sMailSubject = AppPropertiesService.getProperty( "filestoragetransfer.MailSubject" );
    private static String _sMailSender = AppPropertiesService.getProperty( "filestoragetransfer.MailSender" );
    private static String _sMailRecipient = AppPropertiesService.getProperty( "filestoragetransfer.MailRecipient" );
    private static String _sApplicationCode = AppPropertiesService.getProperty( "lutece.code" );
    private static String _sSiteName = AppPropertiesService.getProperty( "lutece.name" );

    private static String TEMPLATE_MAIL = "admin/plugins/filestoragetransfer/mail/mail_filestoragetransfer.html";

    private static final String MARK_REQUEST = "request";
    private static final String MARK_ERROR = "error";

    private static StringWriter sw = new StringWriter( );
    private static PrintWriter pw = new PrintWriter( sw );

    public static void TransferFileToTargetFileService( FileStorageTransferRequest request )
    {
        try
        {
            IFileStoreServiceProvider sourceFileStoreService = FileService.getInstance( ).getFileStoreServiceProvider( request.getSourceFileserviceproviderName( ) );
            IFileStoreServiceProvider targetFileStoreService = FileService.getInstance( ).getFileStoreServiceProvider( request.getTargetFileserviceproviderName( ) );

            String targetFileKey = sendFileToTargetFileService( request.getSourceFileKey( ), sourceFileStoreService,
                    targetFileStoreService );

            request.setTargetFileKey( targetFileKey );
            FileStorageTransferRequestHome.update( request );

            FileStorageTransferNotifierService _fileSwitcher = FileStorageTransferNotifierService.instance( );
            _fileSwitcher.notifyFileTransferListeners( request );

            deleteFileFromSourceFileService( request.getSourceFileKey( ), sourceFileStoreService );

            request.setRequestStatus( FileStorageTransferRequestStatus.STATUS_DONE );
            FileStorageTransferRequestHome.update( request );
        
        }
        catch( FileServiceException e )
        {
            HandleException( request, e, e.getResponseCode( ) != null ? e.getResponseCode( ) : 404 );
        }
        catch( IllegalStateException e )
        {
            HandleException( request, e, 520 );
        }
        catch( Exception e )
        {
            HandleException( request, e, 500 );
        }
    }

    private static String sendFileToTargetFileService( String strSourceFileKey, IFileStoreServiceProvider sourceFileStoreService, IFileStoreServiceProvider targetFileStoreService )
            throws FileServiceException
    {
        File fileToTransfer = sourceFileStoreService.getFile( strSourceFileKey );

        if ( fileToTransfer == null )
        {
            throw new FileServiceException( "File not found", 404, null );
        }

        String strTargetFileKey = targetFileStoreService.storeFile( fileToTransfer );
        return strTargetFileKey;

    }

    private static void deleteFileFromSourceFileService( String strSourceFileKey, IFileStoreServiceProvider sourceFileStoreService ) throws FileServiceException
    {
        if ( sourceFileStoreService == null )
        {
            throw new FileServiceException( "Source file service provider not found", 404, null );
        }

        sourceFileStoreService.delete( strSourceFileKey );
    }

    private static void deleteFileFromTargetFileService( String strTargetFileKey, IFileStoreServiceProvider targetFileStoreService ) throws FileServiceException
    {
        if ( targetFileStoreService == null )
        {
            throw new FileServiceException( "Target file service provider not found", 404, null );
        }
        targetFileStoreService.delete( strTargetFileKey );
    }

    private static void HandleException( FileStorageTransferRequest request, Exception e, int ResponseCode )
    {
        request.upRetryCount( );

        if ( request.getRetryCount( ) >= _nRetryLimit )
        {
            request.setRequestStatus( FileStorageTransferRequestStatus.STATUS_ERROR );
        }
        else
        {
            request.setRequestStatus( FileStorageTransferRequestStatus.STATUS_FAILED );
        }

        request.setExecutionTime( Timestamp.from( Instant.now( ).plusSeconds( _nRetryDelay ) ) );
        FileStorageTransferRequestHome.update( request );

        e.printStackTrace( pw );

        FileStorageTransferError error = new FileStorageTransferError( request.getId( ), ResponseCode, e.getMessage( ) == null ? "" : e.getMessage( ), sw.toString( ),
                Timestamp.from( Instant.now( ) ) );
        FileStorageTransferErrorHome.create( error );

        if ( _bNotificationsEnabled )
        {
            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_REQUEST, request );
            model.put( MARK_ERROR, error );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MAIL, null, model );
            if ( request.getContactMail( ) != null )
            {
                MailService.sendMailHtml( _sMailRecipient, _sApplicationCode + " " + _sSiteName, _sMailSender, _sMailSubject, template.getHtml( ) );
            }
            else
            {
                MailService.sendMailHtml( request.getContactMail( ), null, _sMailRecipient, _sApplicationCode + " " + _sSiteName, _sMailSender, _sMailSubject,
                        template.getHtml( ) );
            }
        }
    }

    public Timestamp getExecutionTime( )
    {
        return Timestamp.from( Instant.now( ) );
    }

    public Timestamp getDelayedExecutionTime( long retryDelay )
    {
        return Timestamp.from( Instant.now( ).plusSeconds( retryDelay ) );
    }

}
