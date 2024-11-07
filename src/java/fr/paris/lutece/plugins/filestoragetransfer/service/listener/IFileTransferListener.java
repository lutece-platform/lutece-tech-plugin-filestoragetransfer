package fr.paris.lutece.plugins.filestoragetransfer.service.listener;

import fr.paris.lutece.portal.service.util.LuteceService;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileTransferRequest;

public interface IFileTransferListener extends LuteceService
{
    /**
     * Notify the listener for a file service switch.
     * 
     * @param idTransferRequest
     *            the transfer request identifier.
     */
    void changeFileService( FileTransferRequest FileTransferRequest );

    /**
     * Get the name of the listener.
     * 
     * @return the name of the listener.
     */
    String getName();
}