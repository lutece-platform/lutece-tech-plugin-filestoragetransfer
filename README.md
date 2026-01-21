![](https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=tech-plugin-filestoragetransfer-deploy)
[![Alerte](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-filestoragetransfer&metric=alert_status)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-filestoragetransfer)
[![Line of code](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-filestoragetransfer&metric=ncloc)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-filestoragetransfer)
[![Coverage](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-filestoragetransfer&metric=coverage)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-filestoragetransfer)

# Plugin FileStorageTransfer

## Introduction

The FileStorageTransfer plugin is a plugin for Lutece that provides file transfer mechanisms between different storage systems implementing the file service architecture (FileService). It allows moving or copying files from one storage service to another in a programmatic and configurable way.

The main features offered by this plugin are:
 
* File transfer between different storage systems supported by Lutece
* Logging system for transfer operations
* Administration console for configuration and monitoring of transfers


This plugin integrates into the Lutece ecosystem by leveraging the file service architecture (FileService) and allows orchestrating file migrations between different storage implementations. It can be used in data migration scenarios or content distribution.

## Configuration

The FileStorageTransfer plugin uses the following properties for its configuration:
| Property| Description| Possible values| Default value|
|-----------------|-----------------|-----------------|-----------------|
| filestoragetransfer.UploadLimit| Number of files to process per batch during transfers| Positive integer| 2|
| filestoragetransfer.RetryLimit| Maximum number of transfer attempts in case of failure| Positive integer| 3|
| filestoragetransfer.RetryDelay| Waiting time between two retries| Milliseconds| 86400|
| filestoragetransfer.NotificationsEnabled| Email notifications for file transfer errors| Boolean| true|
| filestoragetransfer.MailSubject| Subject of the transfer error email| String| File Storage Transfer error|
| filestoragetransfer.MailSender| Subject of the file transfer error email| String| webmaster@localhost|
| filestoragetransfer.MailRecipient| Recipient of the file transfer error email| String| webmaster@localhost|


The plugin defines the following administrative rights, identified in SQL scripts:

| Right ID| Name| Description| Level|
|-----------------|-----------------|-----------------|-----------------|
| FILESTORAGETRANSFER_REQUEST_MANAGEMENT| Storage transfer request management| Allows managing transfer operations between storage systems| 2|
| FILESTORAGETRANSFER_REQUEST_CREATION| Storage transfer management| Allows managing transfer operations between storage systems| 2|
| FILESTORAGETRANSFER_ERROR_MANAGEMENT| Storage transfer management| Allows managing transfer operations between storage systems| 2|

## Usage

The FileStorageTransfer plugin includes several services useful for its operation

 **FileStorageTransferService** 

Main service that orchestrates transfer operations between different storage systems.

 **Main Methods:** 

 `TransferFileToTargetFileService( FileStorageTransferRequest request )` - Transfers a file from a source storage service to a target storage service, does not return a new file identifier. **Parameters:** 
 
*  `request` - File transfer request containing the information necessary for the transfer.


 `TransferFileToTargetFileService( String strSourceFileKey, String strSourceFileServiceProvider, String strTargetFileServiceProvider )` - Transfers a file from a source storage service to a target storage service, returns a new file identifier. **Parameters:** 
 
*  `strSourceFileKey` - Key of the source file to transfer.
*  `strSourceFileServiceProvider` - Name of the source storage service.
*  `strTargetFileServiceProvider` - Name of the target storage service.


 **FileStorageTransferNotifierService** 

Service that manages the different storage providers available for transfers.

 **Main Methods:** 

 `notifyFileTransferListeners()` - Notifies all listeners of a file transfer

The FileStorageTransfer plugin offers a listener to monitor file transfer events. This listener is responsible for managing, for each storage resource, the change of identifier from the source resource to the target resource. **Main Class:** 
 
*  `IFileTransferListener` - Interface to implement to create a file transfer listener. **Main Methods:** 
 
* changeFileService(FileTransferRequest request) - Method called when changing a file identifier
* verifyFileStorageTransferContext( String fileTransferRequestContext ) - Method returning the name of the listener
* getName() - Method returning the name of the listener

 **Exemple d'implémentation:** 
```

                        public class MyFileTransferListener implements IFileTransferListener {

                            private static String _myFileTransferListenerContext = AppPropertiesService.getProperty( "context" );
    
                            @Override
                            public void changeFileService(FileTransferRequest fileTransferRequest) {
                                if ( !verifyFileStorageTransferContext ( fileTransferRequest.getContext )) {
                                    return
                                }
                                else {
                                    // Logique de changement de fichier
                                }
                            }
    
                            public boolean verifyFileStorageTransferContext( String fileTransferRequestContext ) {
                                return fileTransferRequestContext.equals( _myFileTransferListenerContext ) ? true : false;
                            }
                            
                            @Override
                            public String getName() {
                                return "MyFileTransferListener";
                            }
                    
```

The lutece-tech-module-workflow-filestoragetransfer module provides an implementation example for a form resource.



[Maven documentation and reports](https://dev.lutece.paris.fr/plugins/plugin-filestoragetransfer/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*