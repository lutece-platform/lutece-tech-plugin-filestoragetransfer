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

package fr.paris.lutece.plugins.filestoragetransfer.web;

import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileRequestError;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileRequestErrorHome;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileTransferRequestHome;
import fr.paris.lutece.plugins.filestoragetransfer.business.RequestStatus;
import fr.paris.lutece.plugins.filestoragetransfer.service.FileSwitcherService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.html.AbstractPaginator;

import java.util.Comparator;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


/**
 * This class provides the user interface to manage Request features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageRequests.jsp", controllerPath = "jsp/admin/plugins/filestoragetransfer/", right = "FILESTORAGETRANSFER_REQUEST_MANAGEMENT" )
public class RequestJspBean extends AbstractPaginatorJspBean<Integer, FileTransferRequest>
{

    // Rights
    public static final String RIGHT_MANAGETRANSFERREQUEST = "FILESTORAGETRANSFER_REQUEST_MANAGEMENT";
    public static final String RIGHT_CREATETRANSFERREQUEST = "FILESTORAGETRANSFER_REQUEST_CREATION";

    // Templates
    private static final String TEMPLATE_MANAGE_REQUESTS = "/admin/plugins/filestoragetransfer/manage_requests.html";
    private static final String TEMPLATE_CREATE_REQUEST = "/admin/plugins/filestoragetransfer/create_request.html";
    private static final String TEMPLATE_MODIFY_REQUEST = "/admin/plugins/filestoragetransfer/modify_request.html";
    private static final String TEMPLATE_VIEW_REQUEST = "/admin/plugins/filestoragetransfer/view_request.html";

    // Parameters
    private static final String PARAMETER_ID_REQUEST = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_REQUESTS = "filestoragetransfer.manage_requests.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_REQUEST = "filestoragetransfer.modify_request.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_REQUEST = "filestoragetransfer.create_request.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_VIEW_REQUEST = "filestoragetransfer.view_request.pageTitle";

    // Markers
    private static final String MARK_REQUEST_LIST = "request_list";
    private static final String MARK_REQUEST = "request";
    private static final String MARK_FILESERVICES_LIST = "fileservice_list";
    private static final String MARK_ERROR_LIST = "error_list";
    private static final String MARK_REQUEST_STATUS_LIST = "request_status_list";
    private static final String MARK_SELECTED_REQUEST_STATUS_FILTER = "selected_request_status_filter";
    private static final String MARK_LOCALE = "locale";

    private static final String JSP_MANAGE_REQUESTS = "jsp/admin/plugins/filestoragetransfer/ManageRequests.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_REQUEST = "filestoragetransfer.message.confirmRemoveRequest";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "filestoragetransfer.model.entity.request.attribute.";

    // Views
    private static final String VIEW_MANAGE_REQUESTS = "manageRequests";
    private static final String VIEW_CREATE_REQUEST = "createRequest";
    private static final String VIEW_MODIFY_REQUEST = "modifyRequest";
    private static final String VIEW_VIEW_REQUEST = "viewRequest";

    // Actions
    private static final String ACTION_CREATE_REQUEST = "createRequest";
    private static final String ACTION_MODIFY_REQUEST = "modifyRequest";
    private static final String ACTION_REMOVE_REQUEST = "removeRequest";
    private static final String ACTION_CONFIRM_REMOVE_REQUEST = "confirmRemoveRequest";
    private static final String ACTION_FILTER_REQUEST = "filterRequest";
    private static final String ACTION_RESET_FILE_TRANSFER_REQUEST_STATUS = "resetFileRequestStatus";
    private static final String ACTION_PLAY_FILE_TRANSFER_REQUEST = "playFileTransferRequest";
    private static final String ACTION_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS = "resetStatusOfAllErrorRequests";

    // Infos
    private static final String INFO_REQUEST_CREATED = "filestoragetransfer.info.request.created";
    private static final String INFO_REQUEST_UPDATED = "filestoragetransfer.info.request.updated";
    private static final String INFO_RESET_FILE_TRANSFER_REQUEST_STATUS = "filestoragetransfer.info.request.status.reset";
    private static final String INFO_REQUEST_PLAYED = "filestoragetransfer.info.request.played";
    private static final String INFO_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS = "filestoragetransfer.info.request.status.reset.all";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private FileTransferRequest _fileTransferRequest;
    private List<Integer> _listIdFileTransferRequests;
    private RequestStatus _statusFilter = RequestStatus.STATUS_UNKNOWN;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_REQUESTS, defaultView = true )
    public String getManageRequests( HttpServletRequest request )
    {
        _fileTransferRequest = null;

        List<RequestStatus> requestStatusList = RequestStatus.getAllStatus();

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdFileTransferRequests.isEmpty( ) )
        {
            if ( !_statusFilter.equals( RequestStatus.STATUS_UNKNOWN ) )
            {
                _listIdFileTransferRequests = FileTransferRequestHome.getIdRequestsListByStatus( _statusFilter.getValue() );
            }
            else {
                _listIdFileTransferRequests = FileTransferRequestHome.getIdRequestsList( );
            }
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_REQUEST_LIST, _listIdFileTransferRequests, JSP_MANAGE_REQUESTS );
        model.put( MARK_REQUEST_STATUS_LIST, requestStatusList );
        model.put( MARK_SELECTED_REQUEST_STATUS_FILTER, _statusFilter);
        model.put( MARK_LOCALE, getLocale( ) );
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_REQUESTS, TEMPLATE_MANAGE_REQUESTS, model );
    }

    @Action( ACTION_FILTER_REQUEST )
    public String dofilterRequest( HttpServletRequest request ) throws AccessDeniedException
    {
        String statusFilterValue = request.getParameter( "request_status_filter" );
        if(statusFilterValue != "none") {
            _statusFilter = RequestStatus.getRequestStatusByValue( statusFilterValue );   
        }
        else {
            _statusFilter = RequestStatus.STATUS_UNKNOWN;
        }
        return redirectView( request, VIEW_MANAGE_REQUESTS );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<FileTransferRequest> getItemsFromIds( List<Integer> listIds )
    {
        List<FileTransferRequest> listRequest = FileTransferRequestHome.getRequestsListByIds( listIds );
        // keep original order
        return listRequest.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    @Override
    int getPluginDefaultNumberOfItemPerPage( )
    {
        return AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
    }

    /**
     * reset the _listIdRequests list
     */
    public void resetListId( )
    {
        _listIdFileTransferRequests = new ArrayList<>( );
    }

    /**
     * Returns the form to create a request
     *
     * @param request
     *            The Http request
     * @return the html code of the request form
     */
    @View( VIEW_CREATE_REQUEST )
    public String getCreateRequest( HttpServletRequest request )
    {

        _fileTransferRequest = ( _fileTransferRequest != null ) ? _fileTransferRequest : new FileTransferRequest( );

        List<String> listFileServices = SpringContextService.getBeansOfType( IFileStoreServiceProvider.class )
            .stream( ).map( IFileStoreServiceProvider::getName ).collect( Collectors.toList( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_REQUEST, _fileTransferRequest );
        model.put ( MARK_FILESERVICES_LIST, listFileServices );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_REQUEST ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_REQUEST, TEMPLATE_CREATE_REQUEST, model );
    }

        /**
     * Returns the form to create a request
     *
     * @param request
     *            The Http request
     * @return the html code of the request form
     */
    @View( VIEW_VIEW_REQUEST )
    public String getViewRequest( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_REQUEST ) );

        if ( _fileTransferRequest == null || ( _fileTransferRequest.getId( ) != nId ) )
        {
            Optional<FileTransferRequest> optRequest = FileTransferRequestHome.findByPrimaryKey( nId );
            _fileTransferRequest = optRequest.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }
        //_fileTransferRequest.setLocalStatus( getLocale() );
        List<FileRequestError> errorList = FileRequestErrorHome.getErrorsListByRequestId( _fileTransferRequest.getId() );

        Map<String, Object> model = getModel( );
        model.put( MARK_REQUEST, _fileTransferRequest );
        model.put( MARK_ERROR_LIST, errorList );

        return getPage( PROPERTY_PAGE_TITLE_VIEW_REQUEST, TEMPLATE_VIEW_REQUEST, model );
    }


    /**
     * Process the data capture form of a new request
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_REQUEST )
    public String doCreateRequest( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _fileTransferRequest, request, getLocale( ) );

        Timestamp currentTime = Timestamp.from( Instant.now() );

        _fileTransferRequest.setRequestStatus( RequestStatus.STATUS_TODO );
        _fileTransferRequest.setRetryCount( 0 );
        _fileTransferRequest.setCreationTime(currentTime );
        _fileTransferRequest.setExecutionTime( currentTime);

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_REQUEST ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _fileTransferRequest, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_REQUEST );
        }

        FileTransferRequestHome.create( _fileTransferRequest );
        addInfo( INFO_REQUEST_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_REQUESTS );
    }

    /**
     * Manages the removal form of a request whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_REQUEST )
    public String getConfirmRemoveRequest( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_REQUEST ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_REQUEST ) );
        url.addParameter( PARAMETER_ID_REQUEST, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_REQUEST, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        FileRequestErrorHome.getErrorsListByRequestId( nId ).forEach( error -> {
            FileRequestErrorHome.remove( error.getId( ) );
        });

        FileTransferRequestHome.remove( nId );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a request
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage requests
     */
    @Action( ACTION_RESET_FILE_TRANSFER_REQUEST_STATUS )
    public String doResetFileRequestStatus( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_REQUEST ) );

        if ( _fileTransferRequest == null || ( _fileTransferRequest.getId( ) != nId ) )
        {
            Optional<FileTransferRequest> optRequest = FileTransferRequestHome.findByPrimaryKey( nId );
            _fileTransferRequest = optRequest.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        _fileTransferRequest.setRequestStatus( RequestStatus.STATUS_TODO );
        _fileTransferRequest.setRetryCount(0);
        _fileTransferRequest.setExecutionTime( Timestamp.from(Instant.now()) );

        FileTransferRequestHome.update(_fileTransferRequest);
        addInfo( INFO_RESET_FILE_TRANSFER_REQUEST_STATUS, getLocale( ) );
        resetListId( );
        
        return redirectView( request, VIEW_MANAGE_REQUESTS );
    }

    @Action( ACTION_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS )
    public String doResetAllFileRequestStatus( HttpServletRequest request )
    {
        List<FileTransferRequest> fileRequestList = FileTransferRequestHome.getRequestsListByStatus( RequestStatus.STATUS_ERROR.getValue() );

        fileRequestList.forEach( fileRequest -> {
            fileRequest.setRequestStatus( RequestStatus.STATUS_TODO );
            FileTransferRequestHome.update(fileRequest);
        });

        addInfo( INFO_RESET_ALL_FILE_TRANSFER_REQUEST_STATUS, getLocale( ) );
        resetListId( );
        return redirectView( request, VIEW_MANAGE_REQUESTS );
    }

    @Action( ACTION_PLAY_FILE_TRANSFER_REQUEST )
    public String doPlayFileTransferRequest( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_REQUEST ) );

        if ( _fileTransferRequest == null || ( _fileTransferRequest.getId( ) != nId ) )
        {
            Optional<FileTransferRequest> optRequest = FileTransferRequestHome.findByPrimaryKey( nId );
            _fileTransferRequest = optRequest.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        FileSwitcherService.TransferFileToNewFileService( _fileTransferRequest );
        addInfo( INFO_REQUEST_PLAYED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_REQUESTS );
    }

    /**
     * Returns the form to update info about a request
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_REQUEST )
    public String getModifyRequest( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_REQUEST ) );

        if ( _fileTransferRequest == null || ( _fileTransferRequest.getId( ) != nId ) )
        {
            Optional<FileTransferRequest> optRequest = FileTransferRequestHome.findByPrimaryKey( nId );
            _fileTransferRequest = optRequest.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }
        //_fileTransferRequest.setLocalStatus(getLocale());
        List<String> listFileServices = SpringContextService.getBeansOfType( IFileStoreServiceProvider.class )
        .stream( ).map( IFileStoreServiceProvider::getName ).collect( Collectors.toList( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_REQUEST, _fileTransferRequest );
        model.put ( MARK_FILESERVICES_LIST, listFileServices );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_REQUEST ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_REQUEST, TEMPLATE_MODIFY_REQUEST, model );
    }

    /**
     * Process the change form of a request
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_REQUEST )
    public String doModifyRequest( HttpServletRequest request ) throws AccessDeniedException
    {        populate( _fileTransferRequest, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_REQUEST ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _fileTransferRequest, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_REQUEST, PARAMETER_ID_REQUEST, _fileTransferRequest.getId( ) );
        }

        FileTransferRequestHome.update( _fileTransferRequest );
        addInfo( INFO_REQUEST_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_REQUESTS );
    }
}
