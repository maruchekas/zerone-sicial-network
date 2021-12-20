
CREATE TABLE block_history (
  id BIGINT AUTO_INCREMENT NOT NULL,
  time TIMESTAMP NOT NULL,
  person_id BIGINT NOT NULL,
  post_id BIGINT,
  comment_id BIGINT,
  action VARCHAR(255) NOT NULL,
  CONSTRAINT pk_block_history PRIMARY KEY (id)
);

CREATE TABLE friendship_statuses (
  id INT AUTO_INCREMENT NOT NULL,
  time TIMESTAMP NULL,
  code BIGINT NOT NULL,
  name VARCHAR(255) NULL,
  CONSTRAINT pk_friendship_statuses PRIMARY KEY (id)
);

CREATE TABLE friendship (
  id BIGINT AUTO_INCREMENT NOT NULL,
  status_id INTEGER NOT NULL,
  src_person_id BIGINT NOT NULL,
  dst_person_id BIGINT NOT NULL,
  CONSTRAINT pk_friendship PRIMARY KEY (id)
);

CREATE TABLE messages (
  id BIGINT AUTO_INCREMENT NOT NULL,
  time TIMESTAMP ,
  message_text TEXT NOT NULL,
  read_status VARCHAR(255) NOT NULL,
  author_id BIGINT NOT NULL,
  recipient_id BIGINT NOT NULL,
  CONSTRAINT pk_messages PRIMARY KEY (id)
);


CREATE TABLE notification_type (
  id INT AUTO_INCREMENT NOT NULL,
  code BOOLEAN NOT NULL,
  name VARCHAR(255) NOT NULL,
  CONSTRAINT pk_notification_type PRIMARY KEY (id)
);

CREATE TABLE notifications (
  id BIGINT AUTO_INCREMENT NOT NULL,
  sent_time TIMESTAMP,
  contact VARCHAR(255) NOT NULL,
  notification_type_id INT NOT NULL,
  person_id BIGINT NOT NULL,
  CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE TABLE persons (
  id BIGINT AUTO_INCREMENT NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  reg_date TIMESTAMP,
  birth_date TIMESTAMP,
  email VARCHAR(255) NOT NULL,
  phone VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  photo VARCHAR(255),
  about TEXT,
  town VARCHAR(255),
  country VARCHAR(255),
  confirmation_code VARCHAR(255) NOT NULL,
  is_approved INT NOT NULL,
  messages_permission VARCHAR(255) NOT NULL,
  user_type VARCHAR(255),
  last_online_time TIMESTAMP,
  is_blocked INT NOT NULL,
  CONSTRAINT pk_persons PRIMARY KEY (id)
);
CREATE TABLE post_comments (
  id BIGINT AUTO_INCREMENT NOT NULL,
  time TIMESTAMP,
  parent_id BIGINT,
  comment_text TEXT NOT NULL,
  block_id BIGINT,
  post_id BIGINT,
  author_id BIGINT NOT NULL,
  is_blocked BOOLEAN NOT NULL,
  CONSTRAINT pk_post_comments PRIMARY KEY (id)
);

CREATE TABLE post_files (
  id BIGINT AUTO_INCREMENT NOT NULL,
  name VARCHAR(255) NOT NULL,
  path VARCHAR(255) NOT NULL,
  post_id BIGINT NOT NULL,
  CONSTRAINT pk_post_files PRIMARY KEY (id)
);

CREATE TABLE post_likes (
  id BIGINT AUTO_INCREMENT NOT NULL,
  time TIMESTAMP,
  person_id BIGINT NOT NULL,
  post_id BIGINT NOT NULL,
  CONSTRAINT pk_post_likes PRIMARY KEY (id)
);

CREATE TABLE posts (
  id BIGINT AUTO_INCREMENT NOT NULL,
  time TIMESTAMP,
  title VARCHAR(255) NOT NULL,
  post_text TEXT NOT NULL,
  is_blocked INT NOT NULL,
  block_id BIGINT,
  author_id BIGINT NOT NULL,
  CONSTRAINT pk_posts PRIMARY KEY (id)
);

CREATE TABLE tags (
  id BIGINT AUTO_INCREMENT NOT NULL,
  tag VARCHAR(255) NOT NULL,
  CONSTRAINT pk_tag PRIMARY KEY (id)
);
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  type VARCHAR(255) NOT NULL,
  CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE post2tag (
  id BIGINT AUTO_INCREMENT NOT NULL,
  post_id BIGINT not null,
  tag_id BIGINT not null,
  CONSTRAINT pk_post2tag PRIMARY KEY (id)
  );
