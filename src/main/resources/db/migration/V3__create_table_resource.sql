CREATE TABLE resources (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           name VARCHAR(100) NOT NULL,
                           key VARCHAR(100) NOT NULL,
                           description VARCHAR(255),
                           active BOOLEAN NOT NULL DEFAULT TRUE,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT uk_resources_key UNIQUE (key)
);


INSERT INTO public.resources
(id, "name", "key", description, active, created_at, updated_at)
VALUES('f66f9494-0f1d-460d-a29c-288b99a5ca44'::uuid, 'Usuario', 'USUARIO', 'Gerenciamento de usuários', true, '2026-08-20 10:56:02.237', '2026-08-20 10:56:02.237');
INSERT INTO public.resources
(id, "name", "key", description, active, created_at, updated_at)
VALUES('380664a0-be26-44c5-b54e-3016e36c076c'::uuid, 'Perfil', 'PERFIL', 'Gerenciamento de perfies', true, '2026-08-20 10:56:02.237', '2026-08-20 10:56:02.237');
INSERT INTO public.resources
(id, "name", "key", description, active, created_at, updated_at)
VALUES('6670892a-3b3c-42a5-b7f8-6008367632e2'::uuid, 'Permissoes', 'PERMISSOES', 'Gerenciamento de permissões', true, '2026-08-20 10:56:02.237', '2026-08-20 10:56:02.237');
INSERT INTO public.resources
(id, "name", "key", description, active, created_at, updated_at)
VALUES('99258ad9-42d6-44ae-a9bb-4177bec0b837'::uuid, 'Recurso', 'RECURSO', 'Gerenciamento de recursos', true, '2026-08-20 10:56:02.237', '2026-08-20 10:56:02.237');