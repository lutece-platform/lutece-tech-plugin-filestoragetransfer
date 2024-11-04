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

package fr.paris.lutece.plugins.filestoragetransfer.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides Data Access methods for Request objects
 */
public final class FileTransferRequestDAO implements IFileTransferRequestDAO
{
    // Constants
    private static final String SQL_QUERY_INSERT = "INSERT INTO filestoragetransfer_request ( old_file_key, source_fileserviceprovider_name, new_file_key, target_fileserviceprovider_name, request_status, retry_count ,execution_time, creation_time, request_context, contact_mail ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM filestoragetransfer_request WHERE id_request = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE filestoragetransfer_request SET old_file_key = ?, source_fileserviceprovider_name = ?, new_file_key = ?, target_fileserviceprovider_name = ?, request_status = ?, retry_count = ?, execution_time = ?, creation_time = ?, request_context = ?, contact_mail = ? WHERE id_request = ?";

    private static final String SQL_QUERY_SELECTALL = "SELECT id_request, old_file_key, source_fileserviceprovider_name, new_file_key, target_fileserviceprovider_name, request_status, retry_count, execution_time, creation_time, request_context, contact_mail FROM filestoragetransfer_request";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_request FROM filestoragetransfer_request";

    private static final String SQL_QUERY_SELECTALL_BY_IDS = SQL_QUERY_SELECTALL + " WHERE id_request IN (  ";
    private static final String SQL_QUERY_SELECT_BY_ID = SQL_QUERY_SELECTALL + " WHERE id_request = ?";

    private static final String SQL_QUERY_SELECT_BY_STATUS = SQL_QUERY_SELECTALL + " WHERE request_status = ?";
    private static final String SQL_QUERY_SELECTALL_ID_BY_STATUS = SQL_QUERY_SELECTALL_ID + " WHERE request_status = ?";

    private static final String SQL_FILTER_SELECT_TIMESTAMP_BEFORE= " AND execution_time < ?";
    private static final String SQL_FILTER_ORDER_BY_TIMESTAMP = " ORDER BY execution_time ASC";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( FileTransferRequest request, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, request.getOldFileKey( ) );
            daoUtil.setString( nIndex++, request.getSourceFileserviceproviderName( ) );
            daoUtil.setString( nIndex++, request.getNewFileKey( ) );
            daoUtil.setString( nIndex++, request.getTargetFileserviceproviderName( ) );
            daoUtil.setString( nIndex++, request.getRequestStatus( ).getValue() );
            daoUtil.setInt( nIndex++, request.getRetryCount( ) );
            daoUtil.setTimestamp( nIndex++, request.getExecutionTime( ) );
            daoUtil.setTimestamp( nIndex++, request.getCreationTime( ) );
            daoUtil.setString( nIndex++, request.getRequestContext( ) );
            daoUtil.setString( nIndex, request.getContactMail( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                request.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<FileTransferRequest> load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            FileTransferRequest request = null;

            if ( daoUtil.next( ) )
            {
                request = loadFromDaoUtil( daoUtil );
            }

            return Optional.ofNullable( request );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( FileTransferRequest request, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, request.getOldFileKey( ) );
            daoUtil.setString( nIndex++, request.getSourceFileserviceproviderName( ) );
            daoUtil.setString( nIndex++, request.getNewFileKey( ) );
            daoUtil.setString( nIndex++, request.getTargetFileserviceproviderName( ) );
            daoUtil.setString( nIndex++, request.getRequestStatus( ).getValue() );
            daoUtil.setInt( nIndex++, request.getRetryCount( ) );
            daoUtil.setTimestamp( nIndex++, request.getExecutionTime( ) );
            daoUtil.setTimestamp( nIndex++, request.getCreationTime( ) );
            daoUtil.setString( nIndex++, request.getRequestContext( ) );
            daoUtil.setString( nIndex++, request.getContactMail( ) );
            daoUtil.setInt( nIndex, request.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FileTransferRequest> selectRequestsList( Plugin plugin )
    {
        List<FileTransferRequest> requestList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                requestList.add( loadFromDaoUtil( daoUtil ) );
            }

            return requestList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdRequestsList( Plugin plugin )
    {
        List<Integer> requestList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                requestList.add( daoUtil.getInt( 1 ) );
            }

            return requestList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectRequestsReferenceList( Plugin plugin )
    {
        ReferenceList requestList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                requestList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return requestList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FileTransferRequest> selectRequestsListByIds( Plugin plugin, List<Integer> listIds )
    {
        List<FileTransferRequest> requestList = new ArrayList<>( );

        StringBuilder builder = new StringBuilder( );

        if ( !listIds.isEmpty( ) )
        {
            for ( int i = 0; i < listIds.size( ); i++ )
            {
                builder.append( "?," );
            }

            String placeHolders = builder.deleteCharAt( builder.length( ) - 1 ).toString( );
            String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";

            try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
            {
                int index = 1;
                for ( Integer n : listIds )
                {
                    daoUtil.setInt( index++, n );
                }

                daoUtil.executeQuery( );
                while ( daoUtil.next( ) )
                {
                    requestList.add( loadFromDaoUtil( daoUtil ) );
                }

                daoUtil.free( );

            }
        }
        return requestList;

    }

        /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdRequestsListByStatus(Plugin _plugin, String RequestStatus)
    {
        List<Integer> requestList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID_BY_STATUS, _plugin ) )
        {
            daoUtil.setString(1, RequestStatus);
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                requestList.add( daoUtil.getInt( 1 ) );
            }

            return requestList;
        }
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public List<FileTransferRequest> selectRequestsListByStatus( Plugin plugin, String RequestStatus )
    {
        List<FileTransferRequest> requestList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_STATUS, plugin ) )
        {
            daoUtil.setString( 1, RequestStatus );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                requestList.add( loadFromDaoUtil( daoUtil ) );
            }

            daoUtil.free( );

        }
        return requestList;

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FileTransferRequest> selectRequestsListToExecute( Plugin plugin, Timestamp executionTime, int limit )
    {
        List<FileTransferRequest> requestList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_STATUS + SQL_FILTER_SELECT_TIMESTAMP_BEFORE + SQL_FILTER_ORDER_BY_TIMESTAMP, plugin ) )
        {
            daoUtil.setString( 1, RequestStatus.STATUS_FAILED.getValue() );
            daoUtil.setTimestamp( 2, executionTime );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ))
            {
                requestList.add( loadFromDaoUtil( daoUtil ) );
                if(requestList.size() <= limit && limit > 0) {
                    break;
                }
            }
            
            daoUtil.setString( 1, RequestStatus.STATUS_TODO.getValue() );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ))
            {
                requestList.add( loadFromDaoUtil( daoUtil ) );
                if(requestList.size() <= limit && limit > 0) {
                    break;
                }
            }

            daoUtil.free( );

        }
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_STATUS + SQL_FILTER_SELECT_TIMESTAMP_BEFORE + SQL_FILTER_ORDER_BY_TIMESTAMP, plugin ) )
        {
            daoUtil.setString( 1, RequestStatus.STATUS_TODO.getValue() );
            daoUtil.setTimestamp( 2, executionTime );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) && requestList.size() <= limit && limit > 0)
            {
                requestList.add( loadFromDaoUtil( daoUtil ) );
            }

            daoUtil.free( );

        }
        return requestList;

    }

    private FileTransferRequest loadFromDaoUtil( DAOUtil daoUtil )
    {

        FileTransferRequest request = new FileTransferRequest( );
        int nIndex = 1;

        request.setId( daoUtil.getInt( nIndex++ ) );
        request.setOldFileKey( daoUtil.getString( nIndex++ ) );
        request.setSourceFileserviceproviderName( daoUtil.getString( nIndex++ ) );
        request.setNewFileKey( daoUtil.getString( nIndex++ ) );
        request.setTargetFileserviceproviderName( daoUtil.getString( nIndex++ ) );
        request.setRequestStatus( RequestStatus.getRequestStatusByValue( daoUtil.getString( nIndex++ ) )  );
        request.setRetryCount( daoUtil.getInt( nIndex++ ) );
        request.setExecutionTime( daoUtil.getTimestamp( nIndex++ ) );
        request.setCreationTime( daoUtil.getTimestamp( nIndex++ ) );
        request.setRequestContext( daoUtil.getString( nIndex++ ) );
        request.setContactMail( daoUtil.getString( nIndex ) );

        return request;
    }
}
