/*CREATE KEYSPACE wemebox WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };*/


CREATE TABLE user_account (user_account_id timeuuid PRIMARY KEY,username text, password text, email text, active boolean, created_date timestamp, email_verified boolean, profile_verified boolean);
CREATE TABLE user_username (username text PRIMARY KEY, user_account_id timeuuid);
CREATE TABLE user_email (email text PRIMARY KEY, user_account_id timeuuid);
CREATE TABLE user_log (user_account_id timeuuid PRIMARY KEY, login_time timestamp, ip text, os text, browser text);

CREATE TABLE user_session(session_id text PRIMARY KEY, user_account_id timeuuid, session_expire_time timestamp);

CREATE TABLE email_verification(verification_key text PRIMARY KEY, user_account_id timeuuid, created_date timestamp);



CREATE TABLE boxes(box_id timeuuid PRIMARY KEY, box_handle text, description_short text,description_md text, description_html text,create_time timestamp,user_account_id timeuuid, active boolean, subscribers int);
CREATE TABLE post(post_id timeuuid PRIMARY KEY, post_short text, post_md text, post_html text, post_type text, post_time timestamp, likes int, views int, box_id timeuuid);

CREATE TABLE subscribers(box_id timeuuid PRIMARY KEY, user_account_id timeuuid);

CREATE TABLE post_box(box_id timeuuid PRIMARY KEY, post_id timeuuid);

CREATE TABLE inbox(user_account_id timeuuid PRIMARY KEY, post_id timeuuid);

CREATE TABLE user_boxes(user_account_id timeuuid PRIMARY KEY, box_id timeuuid);
