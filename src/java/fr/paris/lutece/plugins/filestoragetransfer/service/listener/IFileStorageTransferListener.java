package fr.paris.lutece.plugins.filestoragetransfer.service.listener;

import fr.paris.lutece.portal.service.util.LuteceService;
import fr.paris.lutece.plugins.filestoragetransfer.business.FileStorageTransferRequest;

public interface IFileStorageTransferListener extends LuteceService
{
    /**
     * Notify the listener for a file service switch.
     * 
     * @param fileTransferRequest
     *            the transfer request identifier.
     */
    void changeFileService( FileStorageTransferRequest fileTransferRequest );

        /**
     * Notify the listener for a file service switch.
     * 
     * @param idTransferRequest
     *            the transfer request identifier.
     */
    boolean verifyFileStorageTransferContext( String fileTransferRequestContext );

    /**
     * Get the name of the listener.
     * 
     * @return the name of the listener.
     */
    String getName( );
}
