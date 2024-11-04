# Plugin FileStorageTransfer

## Description
Le plugin **FileStorageTransfer** est un plugin pour la plateforme Lutece permettant de transférer des fichiers entre différents systèmes de stockage. 
Ce plugin s'intègre avec d'autres modules et plugins Lutece pour supporter la gestion de fichiers sur différentes infrastructures.

## Fonctionnalités
- Transfert de fichiers entre plusieurs systèmes de stockage.
- Gestion asynchrone dun transfert des fichiers.
- Gestion du rejeu.

## Configuration
```properties
# Nombre maximal de rejeu en cas d'échec
filestoragetransfer.RetryLimit=3 

# Nombre maximal de fichier transféré en une exécution
filestoragetransfer.UploadLimit=2

# Délai avant rejeu en cas d'échec
filestoragetransfer.RetryDelay=86400

# Activer les notifications par mail
filestoragetransfer.NotificationsEnabled=true

# Mail de l'expéditeur
filestoragetransfer.MailSender=webmaster@localhost

# Mail du destinataire (webmaster)
filestoragetransfer.MailRecipient=webmaster@localhost

# Sujet du mail
filestoragetransfer.MailSubject=File Storage Transfer error
```