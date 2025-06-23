CREATE TABLE "security".clients (
	id uuid NOT NULL,
	"name" varchar(75) NOT NULL,
	private_key_path varchar(255) NOT NULL,
	public_key_path varchar(255) NOT NULL,
	is_active bool NOT NULL,
	CONSTRAINT pk_security_clients PRIMARY KEY (id),
	CONSTRAINT uq_security_clients_name UNIQUE (name)
);

CREATE TABLE "security".users (
	id uuid NOT NULL,
    provider varchar(50) NOT NULL,
	username varchar(500) NOT NULL,
	email varchar(500) NOT NULL,
	"password" varchar(255) NULL,
	email_confirmed bool NOT NULL,
	"locked" bool NOT NULL,
	enabled bool NOT NULL,
	client_id uuid NOT NULL,
	created_at timestamptz(6) NOT NULL,
    last_modified_at timestamptz(6) NULL,
    CONSTRAINT pk_security_users PRIMARY KEY (id),
	CONSTRAINT uq_security_users_email UNIQUE (email, client_id),
	CONSTRAINT uq_security_users_username UNIQUE (username, client_id),
	CONSTRAINT ch_security_users_provider CHECK (((provider)::text = ANY ((ARRAY['LOCAL'::character varying, 'GOOGLE'::character varying])::text[])))
);

CREATE TABLE "security".roles (
	id uuid NOT NULL,
	"name" varchar(75) NOT NULL,
	CONSTRAINT pk_security_roles PRIMARY KEY (id),
	CONSTRAINT uq_security_roles_name UNIQUE (name)
);

CREATE TABLE "security".permissions (
	id uuid NOT NULL,
	"name" varchar(75) NOT NULL,
	CONSTRAINT pk_security_permissions PRIMARY KEY (id),
	CONSTRAINT uq_security_permissions_name UNIQUE (name)
);

CREATE TABLE "security".role_permission (
	role_id uuid NOT NULL,
	permission_id uuid NOT NULL,
	CONSTRAINT pk_security_role_permission PRIMARY KEY (role_id, permission_id),
	CONSTRAINT fk_security_role_permission_security_permissions FOREIGN KEY (permission_id) REFERENCES "security".permissions(id),
	CONSTRAINT fk_security_role_permission_security_roles FOREIGN KEY (role_id) REFERENCES "security".roles(id)
);

CREATE TABLE "security".user_role (
	user_id uuid NOT NULL,
	role_id uuid NOT NULL,
	CONSTRAINT pk_security_user_role PRIMARY KEY (user_id, role_id),
	CONSTRAINT fk_security_user_role_security_users FOREIGN KEY (user_id) REFERENCES "security".users(id),
	CONSTRAINT fk_security_user_role_security_roles FOREIGN KEY (role_id) REFERENCES "security".roles(id)
);

CREATE TABLE "security".refresh_tokens (
	id uuid NOT NULL,
	user_id uuid NOT NULL,
	"token" varchar(100) NOT NULL,
	expire_at timestamptz(6) NOT NULL,
	CONSTRAINT pk_security_refresh_tokens PRIMARY KEY (id),
	CONSTRAINT fk_security_refresh_tokens_security_users FOREIGN KEY (user_id) REFERENCES "security".users(id),
	CONSTRAINT uq_security_refresh_tokens_token UNIQUE (token)
);

CREATE TABLE "security".verification_tokens (
	id uuid NOT NULL,
	user_id uuid NOT NULL,
	"token" varchar(100) NOT NULL,
	expire_at timestamptz(6) NOT NULL,
	CONSTRAINT pk_security_verification_tokens PRIMARY KEY (id),
	CONSTRAINT fk_security_verification_tokens_security_users FOREIGN KEY (user_id) REFERENCES "security".users(id),
	CONSTRAINT uq_security_verification_tokens_token UNIQUE (token)
);

CREATE TABLE "security".password_reset_tokens (
	id uuid NOT NULL,
	expire_at timestamptz(6) NOT NULL,
	"token" varchar(100) NOT NULL,
	user_id uuid NOT NULL,
	CONSTRAINT pk_security_password_reset_tokens PRIMARY KEY (id),
	CONSTRAINT fk_security_password_reset_tokens_security_users FOREIGN KEY (user_id) REFERENCES "security".users(id),
	CONSTRAINT uq_security_password_reset_tokens_token UNIQUE (token)

);