CREATE TABLE perfil (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          nome VARCHAR(100) NOT NULL,
                          chave VARCHAR(100) NOT NULL,
                          descricao VARCHAR(255),
                          ativo BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT uk_profiles_key UNIQUE (chave)
);

INSERT INTO perfil (
    id,
    nome,
    chave,
    descricao,
    ativo
)
VALUES (
        'a747e317-12b2-4e97-82ac-d583ea704141',
           'ADMIN',
           'ADMIN',
           'Perfil de administrador do sistema',
           TRUE
       );