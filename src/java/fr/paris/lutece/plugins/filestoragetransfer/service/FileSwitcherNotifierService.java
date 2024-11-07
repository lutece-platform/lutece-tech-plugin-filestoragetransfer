package fr.paris.lutece.plugins.filestoragetransfer.service;

import java.util.List;

import fr.paris.lutece.plugins.filestoragetransfer.business.FileTransferRequest;
import fr.paris.lutece.plugins.filestoragetransfer.service.listener.IFileTransferListener;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

public final class FileSwitcherNotifierService {
    
    // singleton
    private static FileSwitcherNotifierService _singleton;

    private static List<IFileTransferListener> _fileTransferListeners;

    private FileSwitcherNotifierService ( ) {

        _fileTransferListeners = SpringContextService.getBeansOfType( IFileTransferListener.class );

        StringBuilder sbLog = new StringBuilder( );
        sbLog.append( "FileTransferListener - loading listeners  : " );

        for ( final IFileTransferListener listener : _fileTransferListeners )
        {
            sbLog.append( "\n\t\t\t\t - " ).append( listener.getName( ) );
        }

        AppLogService.debug( sbLog.toString( ) );

    }

    public void notifyFileTransferListeners( FileTransferRequest request ) {
        _fileTransferListeners.forEach( listener -> listener.changeFileService( request ) );
    }

    public static FileSwitcherNotifierService instance( )
    {
        if ( _singleton == null )
        {
            _singleton = new FileSwitcherNotifierService( );
        }
        return _singleton;
    }

}