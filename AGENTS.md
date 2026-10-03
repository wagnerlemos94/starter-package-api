# Instruções do backend

## Arquitetura

- Stack: Java 21, Spring Boot 3.5, Spring Security, JWT, PostgreSQL, Flyway e `crud-core` 2.0.0. Confira as versões no `pom.xml` ao alterar dependências.
- Pacote base: `br.com.digidatasistemas.starterPackage`.
- Controllers recebem interfaces de service; não devem depender de classes em `service.implement`.
- Services anotados com `@Service` em `service.implement` implementam interfaces próprias ou da biblioteca. As regras são verificadas por ArchUnit e pelo validador de inicialização no perfil `dev`.
- Reutilize `BaseCrudController`, `CrudService` e os contratos de `crud-core`. Confira as assinaturas reais da biblioteca ao atualizar sua versão.
- DTOs de request/response ficam em `controller/dto`. Regras de negócio e normalização pertencem aos services.

## Contratos e regras de negócio

- IDs dos recursos administrativos são UUIDs.
- A listagem é `list(Pageable)` no controller e `findAll(Pageable)` no service. Preserve a checagem `VIEW` antes da consulta.
- `PageResponse` contém `content`, `page`, `size`, `totalElements` e `totalPages`; a biblioteca limita o tamanho da página a 100.
- Relacionamentos são `LAZY`, com Open Session in View desativado. Inicialize dentro da transação somente os relacionamentos necessários à operação e aos DTOs.
- A resposta de usuário contém `perfil` como nome do perfil e `perfilId` como UUID. Não substitua esses campos por um objeto de perfil completo.
- A chave do perfil é gerada na criação e preservada quando seu nome é atualizado. Recursos e permissões também restringem os campos atualizáveis por `updatableProperties()`.
- Na associação de recursos e permissões a perfis, carregue as entidades pelo banco e preserve as validações de existência, atividade e duplicidade.
- Vincule ao perfil somente recursos com pelo menos uma permissão. Na atualização, uma lista vazia ou nula de permissões remove o vínculo existente com aquele recurso.
- Senhas são armazenadas com BCrypt e nunca retornadas. Na atualização de usuário, senha ausente mantém a senha atual.
- `GET/PUT /usuario/me` usam o ID de `UsuarioAutenticado` e permitem editar somente nome e senha, sem exigir permissões administrativas. Troca da própria senha exige validar a senha atual; preserve CPF, perfil e status.
- Preserve o contrato de erros de `ErrorResponse`, incluindo `errorId` e `errors` por campo. Exceções genéricas não devem expor informações internas.
- Confira o tipo antes de tratar uma exceção específica; não faça casts de `Exception` sem verificação.
- Preserve JWT e autorização por recurso/operação: `USUARIO`, `PERFIL`, `RECURSO`, `PERMISSOES` e `VIEW`, `CREATE`, `UPDATE`, `DELETE`.

## Testes e execução

Execute os comandos a partir de `backend/`, com Java 21:

```powershell
.\mvnw.cmd test --settings settings.xml
.\mvnw.cmd clean verify --settings settings.xml
```

- Para alterações de código, use `clean verify` na validação final: recompila fontes e testes, executa a suíte e verifica cobertura.
- O JaCoCo exige pelo menos 85% das linhas e 75% dos branches. Não reduza os limites para contornar falhas.
- Nos testes paginados, use `PageRequest` e `PageImpl`; simule `repository.findAll(pageable)` e confira conteúdo, metadados e relacionamentos relevantes.
- Atualize expectativas quando o contrato mudar, preservando a cobertura de comportamento e os testes de segurança.
- Testes usam o perfil `test`, H2 ou mocks; não dependem de PostgreSQL de produção.
- `crud-core` é obtido pelo GitHub Packages. Use as credenciais de ambiente indicadas no README, sem exibi-las. Se o cache local estiver completo, `-o` permite rodar offline.
- Se o caminho do repositório Maven estiver incorreto, informe `-Dmaven.repo.local=<cache-local>` no comando; não altere configurações globais sem necessidade.
