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
import fr.paris.lutece.plugins.filestoragetransfer.business.FileRequestError;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileRequestErrorHome;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileTransferRequestHome;
import fr.paris.lutece.plugins.filestoragetransfer.daemon.TransferFileDaemon;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.html.AbstractPaginator;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Error features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageErrors.jsp", controllerPath = "jsp/admin/plugins/filestoragetransfer/", right = "FILESTORAGETRANSFER_ERROR_MANAGEMENT" )
public class ErrorJspBean extends AbstractPaginatorJspBean<Integer, FileRequestError>
{

    // Rights
    public static final String RIGHT_MANAGETRANSFERERROR = "FILESTORAGETRANSFER_ERROR_MANAGEMENT";

    // Templates
    private static final String TEMPLATE_MANAGE_ERRORS = "/admin/plugins/filestoragetransfer/manage_errors.html";
    private static final String TEMPLATE_VIEW_ERROR = "/admin/plugins/filestoragetransfer/view_error.html";

    // Actions
    private static final String ACTION_REPLAY_TRANSFER = "replayTransfer";

    // Parameters
    private static final String PARAMETER_ID_ERROR = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_ERRORS = "filestoragetransfer.manage_errors.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_VIEW_ERROR = "filestoragetransfer.view_error.pageTitle";

    // Markers
    private static final String MARK_ERROR_LIST = "error_list";
    private static final String MARK_ERROR = "error";

    private static final String JSP_MANAGE_ERRORS = "jsp/admin/plugins/filestoragetransfer/ManageErrors.jsp";

    // Views
    private static final String VIEW_MANAGE_ERRORS = "manageErrors";
    private static final String VIEW_ERROR = "viewError";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private FileRequestError _error;
    private List<Integer> _listIdErrors;
    
    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_ERRORS, defaultView = true )
    public String getManageErrors( HttpServletRequest request )
    {
        _error = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdErrors.isEmpty( ) )
        {
            _listIdErrors = FileRequestErrorHome.getIdErrorsList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_ERROR_LIST, _listIdErrors, JSP_MANAGE_ERRORS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_ERRORS, TEMPLATE_MANAGE_ERRORS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<FileRequestError> getItemsFromIds( List<Integer> listIds )
    {
        List<FileRequestError> listError = FileRequestErrorHome.getErrorsListByIds( listIds );

        // keep original order
        return listError.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    @Override
    int getPluginDefaultNumberOfItemPerPage( )
    {
        return AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
    }

    /**
     * reset the _listIdErrors list
     */
    public void resetListId( )
    {
        _listIdErrors = new ArrayList<>( );
    }

    /**
     * Returns the form to update info about a error
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_ERROR )
    public String getError( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ERROR ) );

        if ( _error == null || ( _error.getId( ) != nId ) )
        {
            Optional<FileRequestError> optError = FileRequestErrorHome.findByPrimaryKey( nId );
            _error = optError.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_ERROR, _error );
        return getPage( PROPERTY_PAGE_TITLE_VIEW_ERROR, TEMPLATE_VIEW_ERROR, model );
    }

    @Action( ACTION_REPLAY_TRANSFER )
    public String doReplayTransfer( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _error, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_REPLAY_TRANSFER ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        FileTransferRequest requestToReplay = FileTransferRequestHome.findByPrimaryKey( _error.getIdRequest( ) ).get( );

        Map<String, Object> model = getModel( );
        model.put( MARK_ERROR, _error );
        
        return redirectView( request, VIEW_ERROR );
    }
}
