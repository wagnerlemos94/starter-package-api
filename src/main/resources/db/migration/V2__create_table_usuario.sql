CREATE TABLE usuario (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       cpf VARCHAR(11) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(150) NOT NULL,
                       profile_id UUID NOT NULL,
                       active BOOLEAN NOT NULL DEFAULT TRUE,
                       CONSTRAINT fk_users_profile
                           FOREIGN KEY (profile_id)
                            REFERENCES perfil (id)
);
