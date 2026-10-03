INSERT INTO recurso (nome, chave, descricao, ativo)
VALUES ('Dashboard', 'DASHBOARD', 'Visualização do resumo de usuários e perfis', true)
ON CONFLICT (chave) DO NOTHING;
