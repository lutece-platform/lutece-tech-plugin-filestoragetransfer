
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'FILESTORAGETRANSFER_REQUEST_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('FILESTORAGETRANSFER_REQUEST_MANAGEMENT','filestoragetransfer.adminFeature.ManageTransferRequest.name',1,'jsp/admin/plugins/filestoragetransfer/ManageRequests.jsp','filestoragetransfer.adminFeature.ManageTransferRequest.description',0,'filestoragetransfer',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'FILESTORAGETRANSFER_REQUEST_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('FILESTORAGETRANSFER_REQUEST_MANAGEMENT',1);

--
-- Data for table core_user_right
--

DELETE FROM core_user_right WHERE id_right = 'FILESTORAGETRANSFER_REQUEST_CREATION';
INSERT INTO core_user_right (id_right,id_user) VALUES ('FILESTORAGETRANSFER_REQUEST_CREATION',1);


--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'FILESTORAGETRANSFER_ERROR_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('FILESTORAGETRANSFER_ERROR_MANAGEMENT','filestoragetransfer.adminFeature.ManageTransferError.name',1,'jsp/admin/plugins/filestoragetransfer/ManageErrors.jsp','filestoragetransfer.adminFeature.ManageTransferError.description',0,'filestoragetransfer',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'FILESTORAGETRANSFER_ERROR_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('FILESTORAGETRANSFER_ERROR_MANAGEMENT',1);

