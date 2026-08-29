CREATE TABLE profiles (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name VARCHAR(100) NOT NULL,
                          key VARCHAR(100) NOT NULL,
                          description VARCHAR(255),
                          active BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT uk_profiles_key UNIQUE (key)
);

INSERT INTO profiles (
    id,
    name,
    key,
    description,
    active
)
VALUES (
        'a747e317-12b2-4e97-82ac-d583ea704141',
           'ADMIN',
           'ADMIN',
           'Perfil de administrador do sistema',
           TRUE
       );