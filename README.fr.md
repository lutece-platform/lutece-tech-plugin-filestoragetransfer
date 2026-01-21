![](https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=tech-plugin-filestoragetransfer-deploy)
[![Alerte](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-filestoragetransfer&metric=alert_status)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-filestoragetransfer)
[![Line of code](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-filestoragetransfer&metric=ncloc)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-filestoragetransfer)
[![Coverage](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-filestoragetransfer&metric=coverage)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-filestoragetransfer)

# Plugin FileStorageTransfer

## Introduction

Le plugin FileStorageTransfer est un plugin pour Lutece qui fournit des mécanismes de transfert de fichiers entre différents systèmes de stockage implémentant l'architecture de service de fichiers (FileService). Il permet de déplacer ou copier des fichiers d'un service de stockage à un autre de manière programmatique et configurable.

Les principales fonctionnalités offertes par ce plugin sont:
 
* Transfert de fichiers entre différents systèmes de stockage supportés par Lutece
* Système de journalisation des opérations de transfert
* Console d'administration pour la configuration et le suivi des transferts


Ce plugin s'intègre dans l'écosystème Lutece en s'appuyant sur l'architecture de service de fichiers (FileService) et permet d'orchestrer des migrations de fichiers entre différentes implémentations de stockage. Il peut être utilisé dans des scénarios de migration de données ou de distribution de contenu.

## Configuration

Le plugin FileStorageTransfer utilise les propriétés suivantes pour sa configuration:
| Propriété| Description| Valeurs possibles| Valeur par défaut|
|-----------------|-----------------|-----------------|-----------------|
| filestoragetransfer.UploadLimit| Nombre de fichiers à traiter par lot lors des transferts| Entier positif| 2|
| filestoragetransfer.RetryLimit| Nombre de tentative maximale de transfert en cas d'échec.| Entier positif| 3|
| filestoragetransfer.RetryDelay| Temps d'attente entre deux rejeu| Millisecondes| 86400|
| filestoragetransfer.NotificationsEnabled| Notifications mail d'erreurs de transfert de fichiers| Booléen| true|
| filestoragetransfer.MailSubject| Sujet du mail d'erreur de transfert| Chaîne de caractère| File Storage Transfer error|
| filestoragetransfer.MailSender| Sujet du mail d'erreur de transfert d'un fichier| Chaîne de caractère| webmaster@localhost|
| filestoragetransfer.MailRecipient| Destinataire du mail d'erreur de transfert d'un fichier| Chaîne de caractère| webmaster@localhost|


Le plugin définit les droits administratifs suivants, identifiés dans les scripts SQL:

| ID du droit| Nom| Description| Niveau|
|-----------------|-----------------|-----------------|-----------------|
| FILESTORAGETRANSFER_REQUEST_MANAGEMENT| Gestion des requêtes de transferts de stockage| Permet de gérer les opérations de transfert entre systèmes de stockage| 2|
| FILESTORAGETRANSFER_REQUEST_CREATION| Gestion des transferts de stockage| Permet de gérer les opérations de transfert entre systèmes de stockage| 2|
| FILESTORAGETRANSFER_ERROR_MANAGEMENT| Gestion des transferts de stockage| Permet de gérer les opérations de transfert entre systèmes de stockage| 2|

## Usage

Le plugin FileStorageTransfer comprends plusieurs services utiles à son utilisation

 **FileStorageTransferService** 

Service principal qui orchestre les opérations de transfert entre différents systèmes de stockage.

 **Méthodes principales:** 

 `TransferFileToTargetFileService( FileStorageTransferRequest request )` - Transfère un fichier d'un service de stockage source vers un service de stockage cible, ne retourne pas de nouvel identifiant de fichier. **Paramètres:** 
 
*  `request` - Requête de transfert de fichier contenant les informations nécessaires au transfert.


 `TransferFileToTargetFileService( String strSourceFileKey, String strSourceFileServiceProvider, String strTargetFileServiceProvider )` - Transfère un fichier d'un service de stockage source vers un service de stockage cible, retourne un nouvel identifiant de fichier. **Paramètres:** 
 
*  `strSourceFileKey` - Clé du fichier source à transférer.
*  `strSourceFileServiceProvider` - Nom du service de stockage source.
*  `strTargetFileServiceProvider` - Nom du service de stockage cible.


 **FileStorageTransferNotifierService** 

Service qui gère les différents fournisseurs de stockage disponibles pour les transferts.

 **Méthodes principales:** 

 `notifyFileTransferListeners()` - Notifie l'ensemble des listeners du transfert d'un fichier

Le plugin FileStorageTransfer propose un listener pour surveiller les événements de transfert de fichiers.Ce listener est responsable de gérer pour chaque ressource de stockage le changement d'identifiant de la ressource source vers la ressource cible. Au transfert d'une ressource, l'ensemble des listeners sont appelés, le contexte de la ressource doit être vérifié pour s'assurer qu'il s'agisse bien d'un fichier d'une ressource géré par ce listener.
 **Classe principale:**  `IFileTransferListener` - Interface à implémenter pour créer un listener de transfert de fichiers. **Méthodes principales:** 
 
* changeFileService( FileTransferRequest fileTransferRequest ) - Méthode appelée lors du changement d'identifiant de fichier
* verifyFileStorageTransferContext( String fileTransferRequestContext ) - Méthode retournant le nom du listener
* getName() - Méthode retournant le nom du listener
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
                    }
                
```

Le module `lutece-tech-module-workflow-filestoragetransfer` propose un exemple d'implémentation pour une ressource d'un formulaire.


[Maven documentation and reports](https://dev.lutece.paris.fr/plugins/plugin-filestoragetransfer/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*