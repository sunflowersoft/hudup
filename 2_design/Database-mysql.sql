USE hudup
;


DROP TABLE IF EXISTS hdp_context_template_0_profile
;
DROP TABLE IF EXISTS hdp_context
;
DROP TABLE IF EXISTS hdp_context_template
;
DROP TABLE IF EXISTS hdp_account
;
DROP TABLE IF EXISTS hdp_attribute_map
;
DROP TABLE IF EXISTS hdp_nominal
;
DROP TABLE IF EXISTS hdp_config
;
DROP TABLE IF EXISTS hdp_rating
;
DROP TABLE IF EXISTS hdp_user
;
DROP TABLE IF EXISTS hdp_item
;



CREATE TABLE hdp_context_template_0_profile
(
	ctx_value INTEGER NOT NULL,
	attribute_1_n FLOAT(0),
	PRIMARY KEY (ctx_value)
) 
;


CREATE TABLE hdp_context
(
	userid INTEGER NOT NULL,
	itemid INTEGER NOT NULL,
	ctx_templateid INTEGER NOT NULL,
	ctx_value VARCHAR(255),
	PRIMARY KEY (userid, itemid, ctx_templateid),
	KEY (ctx_templateid),
	KEY (userid, itemid)
) 
;


CREATE TABLE hdp_context_template
(
	ctx_templateid INTEGER NOT NULL,
	ctx_name VARCHAR(255) NOT NULL,
	ctx_type INTEGER NOT NULL,
	ctx_parent INTEGER,
	PRIMARY KEY (ctx_templateid),
	UNIQUE UQ_hdp_context_template_ctx_name(ctx_name)
) 
;


CREATE TABLE hdp_account
(
	account_name VARCHAR(255) NOT NULL,
	account_password VARCHAR(255) NOT NULL,
	account_privs VARCHAR(255) NOT NULL,
	PRIMARY KEY (account_name)
) 
;


CREATE TABLE hdp_attribute_map
(
	internal_unit VARCHAR(255) NOT NULL,
	internal_attribute_name VARCHAR(255) NOT NULL,
	internal_attribute_value VARCHAR(255) NOT NULL,
	external_unit VARCHAR(255) NOT NULL,
	external_attribute_name VARCHAR(255) NOT NULL,
	external_attribute_value VARCHAR(255) NOT NULL,
	PRIMARY KEY (internal_unit, internal_attribute_name, internal_attribute_value)
) 
;


CREATE TABLE hdp_nominal
(
	nominal_ref_unit VARCHAR(255) NOT NULL,
	attribute VARCHAR(255) NOT NULL,
	nominal_index INTEGER NOT NULL,
	nominal_value VARCHAR(255) NOT NULL,
	nominal_parent_index INTEGER,
	PRIMARY KEY (nominal_ref_unit, attribute, nominal_index)
) 
;


CREATE TABLE hdp_config
(
	attribute VARCHAR(255) NOT NULL,
	attribute_value VARCHAR(255) NOT NULL,
	PRIMARY KEY (attribute)
) 
;


CREATE TABLE hdp_rating
(
	userid INTEGER NOT NULL,
	itemid INTEGER NOT NULL,
	rating FLOAT(0) NOT NULL,
	rating_date DATE,
	PRIMARY KEY (userid, itemid),
	KEY (itemid),
	KEY (userid)
) 
;


CREATE TABLE hdp_user
(
	userid INTEGER NOT NULL,
	attribute_1_n INTEGER,
	PRIMARY KEY (userid)
) 
;


CREATE TABLE hdp_item
(
	itemid INTEGER NOT NULL,
	attribute_1_n INTEGER,
	PRIMARY KEY (itemid)
) 
;





ALTER TABLE hdp_context ADD CONSTRAINT FK_hdp_context_hdp_context_template 
	FOREIGN KEY (ctx_templateid) REFERENCES hdp_context_template (ctx_templateid)
	ON DELETE CASCADE ON UPDATE CASCADE
;

ALTER TABLE hdp_context ADD CONSTRAINT FK_hdp_context_hdp_rating 
	FOREIGN KEY (userid, itemid) REFERENCES hdp_rating (userid, itemid)
	ON DELETE CASCADE ON UPDATE CASCADE
;

ALTER TABLE hdp_rating ADD CONSTRAINT FK_hdp_rating_hdp_item 
	FOREIGN KEY (itemid) REFERENCES hdp_item (itemid)
	ON DELETE CASCADE ON UPDATE CASCADE
;

ALTER TABLE hdp_rating ADD CONSTRAINT FK_hdp_rating_hdp_user 
	FOREIGN KEY (userid) REFERENCES hdp_user (userid)
	ON UPDATE CASCADE
;
