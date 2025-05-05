package fr.paris.lutece.plugins.filestoragetransfer.service;

import java.util.List;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.service.listener.IFileStorageTransferListener;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

public final class FileStorageTransferNotifierService
{

    // singleton
    private static FileStorageTransferNotifierService _singleton;

    private static List<IFileStorageTransferListener> _fileTransferListeners;

    private FileStorageTransferNotifierService( )
    {

        _fileTransferListeners = SpringContextService.getBeansOfType( IFileStorageTransferListener.class );

        StringBuilder sbLog = new StringBuilder( );
        sbLog.append( "FileTransferListener - loading listeners  : " );

        for ( final IFileStorageTransferListener listener : _fileTransferListeners )
        {
            sbLog.append( "\n\t\t\t\t - " ).append( listener.getName( ) );
        }

        AppLogService.debug( sbLog.toString( ) );

    }

    public void notifyFileTransferListeners( FileStorageTransferRequest request )
    {
        _fileTransferListeners.forEach( listener -> listener.changeFileService( request ) );
    }

    public static FileStorageTransferNotifierService instance( )
    {
        if ( _singleton == null )
        {
            _singleton = new FileStorageTransferNotifierService( );
        }
        return _singleton;
    }

}
