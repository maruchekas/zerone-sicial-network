ALTER TABLE notification_type RENAME TO user_notification_settings;

ALTER TYPE notification_name RENAME TO notification_type;

ALTER TABLE notifications ADD COLUMN type notification_type;
ALTER TABLE notifications ADD COLUMN entity_id BIGINT;


