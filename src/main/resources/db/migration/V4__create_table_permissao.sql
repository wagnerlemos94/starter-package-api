CREATE TABLE permissao (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             nome VARCHAR(100) NOT NULL,
                             chave VARCHAR(50) NOT NULL,
                             descricao VARCHAR(255),
                             ativo BOOLEAN NOT NULL DEFAULT TRUE,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT uk_permissions_key UNIQUE (chave)
);

INSERT INTO permissao (
    id,
    nome,
    chave,
    descricao
)
VALUES
    (
     '948ee5dc-e134-44df-a0a6-cc5e02524e18',
        'Visualizar',
        'VIEW',
        'Permite visualizar o recurso'
    ),
    (
     'bf29ea49-b5d5-4f10-8ce7-bd01aa32172b',
        'Criar',
        'CREATE',
        'Permite criar registros do recurso'
    ),
    (
     '63a8171b-67c3-4b6b-b69e-6ad2d09fe04d',
        'Atualizar',
        'UPDATE',
        'Permite atualizar registros do recurso'
    ),
    (
     '42d0a8ac-11f5-4435-a1c6-bf810a56f4f2',
        'Deletar',
        'DELETE',
        'Permite deletar registros do recurso'
    );