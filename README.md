# Starter Package API

API REST de referência para autenticação, usuários, perfis, recursos e permissões. O projeto usa Java 21, Spring Boot 3.5, Spring Security, JWT, PostgreSQL, Flyway e a biblioteca `crud-core`.

Repositórios relacionados:

- API: https://github.com/wagnerlemos94/starter-package-api
- Aplicação web consumidora: https://github.com/wagnerlemos94/starter-package-app

## Visão geral

A API roda por padrão em `http://localhost:8085/api`.

O endpoint de login é público. Todos os endpoints de negócio exigem um JWT no cabeçalho `Authorization` e uma permissão compatível com a operação.

| Domínio | Caminho | Chave de autorização |
|---|---|---|
| Usuários | `/user` | `USUARIO` |
| Perfis | `/profile` | `PERFIL` |
| Recursos | `/resource` | `RECURSO` |
| Permissões | `/permission` | `PERMISSOES` |

Cada chave é combinada com uma operação: `VIEW`, `CREATE`, `UPDATE` ou `DELETE`. Por exemplo, listar usuários exige `USUARIO:VIEW`.

## Requisitos

- Java 21
- PostgreSQL
- Maven, ou o Maven Wrapper incluído
- Credenciais do GitHub Packages com acesso a `br.com.digidata:crud-core:1.2.5`

Configure as credenciais usadas por `settings.xml`:

```powershell
$env:GITHUB_USERNAME = "seu-usuario"
$env:GITHUB_TOKEN = "seu-token"
```

## Configuração

| Variável | Obrigatória | Descrição | Exemplo |
|---|---:|---|---|
| `DB_URL` | Sim | Host, porta e banco PostgreSQL, sem o prefixo JDBC | `localhost:5432/starter_package` |
| `DB_USER` | Sim | Usuário do banco | `postgres` |
| `DB_PASS` | Sim | Senha do banco | `postgres` |
| `FLYWAY` | Sim | Ativa as migrations | `true` |
| `JWT_SECRET` | Sim | Segredo HMAC forte, com pelo menos 32 bytes | `uma-chave-longa-com-32-bytes-ou-mais` |
| `JWT_EXPIRATION` | Não | Expiração configurada em milissegundos; padrão `86400000` | `86400000` |
| `CORS_ALLOWED_ORIGINS` | Não | Origens autorizadas, separadas por vírgula | `http://localhost:3000,https://app.exemplo.com` |

O valor de `JWT_EXPIRATION` controla a validade efetiva do claim `exp` do JWT e também é retornado no login.

Exemplo para desenvolvimento no PowerShell:

```powershell
$env:DB_URL = "localhost:5432/starter_package"
$env:DB_USER = "postgres"
$env:DB_PASS = "postgres"
$env:FLYWAY = "true"
$env:JWT_SECRET = "substitua-por-uma-chave-segura-de-32-bytes"
$env:CORS_ALLOWED_ORIGINS = "http://localhost:3000,http://localhost:5173"
.\mvnw.cmd spring-boot:run --settings settings.xml
```

Informe em `CORS_ALLOWED_ORIGINS` somente as origens confiáveis que acessarão a API pelo navegador. Separe múltiplas origens por vírgula e não inclua caminhos, por exemplo: `https://app.exemplo.com`, e não `https://app.exemplo.com/login`.

As migrations criam o esquema, o perfil administrador, os quatro recursos e as permissões CRUD. Por segurança, o starter **não cria um usuário inicial nem fornece uma senha padrão**.

Antes do primeiro login, cada projeto deve definir sua própria estratégia de criação do administrador. Uma opção é adicionar uma migration exclusiva do projeto, usando CPF, nome e senha próprios. A senha deve ser armazenada como hash BCrypt, nunca em texto puro:

```sql
INSERT INTO usuario (id, cpf, password, name, active, profile_id)
VALUES (
    gen_random_uuid(),
    'CPF_DO_ADMINISTRADOR',
    'HASH_BCRYPT_DA_SENHA',
    'Administrador',
    TRUE,
    'a747e317-12b2-4e97-82ac-d583ea704141'
);
```

Não coloque uma credencial real no starter ou em um repositório público. Em produção, prefira um processo de bootstrap controlado ou uma credencial temporária que obrigue a troca da senha no primeiro acesso.

## Autenticação

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "cpf": "00000000000",
  "password": "sua-senha"
}
```

Resposta `200 OK`:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresInToken": 86400000,
  "nome": "Nome do usuário",
  "username": "00000000000",
  "resource": {
    "USUARIO": ["VIEW", "CREATE", "UPDATE", "DELETE"],
    "PERFIL": ["VIEW"]
  }
}
```

`expiresInToken` informa, em milissegundos, o mesmo período configurado em `JWT_EXPIRATION`. A data absoluta de expiração também está no claim `exp` do JWT.

Credenciais inválidas retornam `401 Unauthorized`:

```json
{
  "timestamp": "2026-08-31T12:00:00",
  "status": 401,
  "error": "UNAUTHORIZED",
  "message": "Usuário ou senha inválidos.",
  "path": "/api/auth/login",
  "details": []
}
```

### Usar o token

Envie o token em todas as rotas protegidas:

```http
Authorization: Bearer <token>
```

```bash
curl http://localhost:8085/api/user \
  -H "Authorization: Bearer SEU_TOKEN"
```

### Obter o usuário autenticado

Services e outros componentes podem receber `UsuarioAutenticado` por injeção de dependência. O componente não armazena estado; ele consulta o `SecurityContext` da requisição atual.

```java
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final UsuarioAutenticado usuarioAutenticado;

    public void criarPedido() {
        UUID usuarioId = usuarioAutenticado.getId();
        Usuario usuario = usuarioAutenticado.get();
    }
}
```

Os métodos disponíveis são `get()`, `getId()` e `getCpf()`. Quando não existir um `Usuario` autenticado, o componente lança `AccessDeniedException`.

Os relacionamentos JPA são carregados sob demanda (`LAZY`) para evitar que consultas simples tragam automaticamente todo o grafo de perfil, recursos e permissões. O Open Session in View está desativado; os services inicializam dentro de transações somente os relacionamentos necessários para autenticação e conversão dos DTOs.

## Contrato CRUD

Os quatro recursos seguem o mesmo conjunto de rotas, fornecido por `crud-core`:

| Método | Caminho | Ação | Permissão | Resposta esperada |
|---|---|---|---|---|
| `GET` | `/{recurso}` | Lista registros | `VIEW` | `200` com array |
| `GET` | `/{recurso}/{id}` | Busca por UUID | `VIEW` | `200` com objeto |
| `POST` | `/{recurso}` | Cria registro | `CREATE` | Objeto criado |
| `PUT` | `/{recurso}/{id}` | Atualiza registro | `UPDATE` | Objeto atualizado |
| `DELETE` | `/{recurso}/{id}` | Exclui registro | `DELETE` | Corpo vazio ou resposta da biblioteca |

`{id}` é sempre um UUID. Um registro inexistente produz `404`; falta de permissão produz `403`.

## Usuários

Base: `/api/user`

Payload de criação e atualização:

```json
{
  "cpf": "00000000000",
  "name": "Maria da Silva",
  "profileId": "a747e317-12b2-4e97-82ac-d583ea704141",
  "active": true
}
```

Resposta:

```json
{
  "id": "9c95cb22-cc70-48ef-ad47-9fd68d635ad8",
  "cpf": "00000000000",
  "name": "Maria da Silva",
  "active": true,
  "profile": {
    "id": "a747e317-12b2-4e97-82ac-d583ea704141",
    "nome": "ADMIN",
    "chave": "ADMIN",
    "descricao": "Perfil de administrador do sistema",
    "ativo": true,
    "perfilRecursoResponse": []
  }
}
```

O DTO público não aceita senha e o campo `password` da resposta é ignorado na serialização. A criação depende de o serviço definir a senha internamente; verifique esse fluxo antes de expor o endpoint em produção.

## Perfis

Base: `/api/profile`

`perfilRecurso` é um mapa em que a chave é o UUID do recurso e o valor é a lista de UUIDs das permissões concedidas.

Na criação e atualização, a API carrega recursos e permissões pelo banco antes de associá-los ao perfil. IDs inexistentes, recursos ou permissões inativos e valores duplicados são rejeitados. Uma lista ausente é tratada como vazia; na atualização isso remove as associações que não foram enviadas. A chave do perfil é recalculada sempre que seu nome muda.

```json
{
  "nome": "GESTOR",
  "descricao": "Perfil de gestão",
  "ativo": true,
  "perfilRecurso": {
    "f66f9494-0f1d-460d-a29c-288b99a5ca44": [
      "948ee5dc-e134-44df-a0a6-cc5e02524e18",
      "bf29ea49-b5d5-4f10-8ce7-bd01aa32172b"
    ]
  }
}
```

Resposta resumida:

```json
{
  "id": "6bdb7d67-09cc-4ab3-9f50-307474a790f7",
  "nome": "GESTOR",
  "chave": "GESTOR",
  "descricao": "Perfil de gestão",
  "ativo": true,
  "perfilRecursoResponse": [
    {
      "id": "52e94585-ae4d-4593-b01e-a72efea7a34a",
      "recursoId": "f66f9494-0f1d-460d-a29c-288b99a5ca44",
      "recurso": "Usuario",
      "permissoes": [
        {
          "id": "948ee5dc-e134-44df-a0a6-cc5e02524e18",
          "nome": "Visualizar",
          "chave": "VIEW",
          "descricao": "Permite visualizar o recurso",
          "ativo": true
        }
      ]
    }
  ]
}
```

A chave do perfil é derivada de `nome.toUpperCase()`.

## Recursos

Base: `/api/resource`

```json
{
  "nome": "Relatorio",
  "descricao": "Acesso aos relatórios",
  "ativo": true
}
```

```json
{
  "id": "fb21b3f8-025b-47a5-bec3-f125db31de76",
  "nome": "Relatorio",
  "chave": "RELATORIO",
  "descricao": "Acesso aos relatórios",
  "ativo": true
}
```

A chave do recurso é derivada de `nome.toUpperCase()`.

## Permissões

Base: `/api/permission`

```json
{
  "nome": "Exportar",
  "chave": "EXPORT",
  "descricao": "Permite exportar dados",
  "ativo": true
}
```

```json
{
  "id": "6ecbf674-e97c-4102-8d96-c349dd5df03b",
  "nome": "Exportar",
  "chave": "EXPORT",
  "descricao": "Permite exportar dados",
  "ativo": true
}
```

O frontend atual usa apenas `GET /permission` e `GET /permission/{id}`, embora a API exponha o contrato CRUD completo.

## Erros

```json
{
  "timestamp": "2026-08-31T12:00:00",
  "status": 403,
  "error": "FORBIDDEN",
  "message": "Usuário não tem permissão para acessar essa funcinalidade.",
  "path": "/api/user",
  "details": ["Usuário não possui a permissão: USUARIO:VIEW"]
}
```

| Status | Situação |
|---:|---|
| `400` | Validação ou regra de negócio inválida |
| `401` | Login inválido ou token ausente, inválido ou expirado |
| `403` | Usuário autenticado sem a permissão necessária |
| `404` | Registro não encontrado |
| `500` | Erro não tratado |

## Integração com o frontend

No `starter-package-app`, configure:

```env
NEXT_PUBLIC_BASE_URL=http://localhost:8085/api
```

O app armazena o JWT como `accessToken` no `localStorage` e o envia como Bearer token. O login converte o campo de formulário `senha` para `password`, como esperado pela API.

## Regras de arquitetura

O projeto usa ArchUnit para verificar automaticamente a separação entre controllers e services. As regras estão em `src/test/java/br/com/digidatasistemas/starterPackage/ArchitectureTest.java` e são executadas junto com os demais testes.

As seguintes regras são obrigatórias:

- Toda classe dentro de `service.implement` deve implementar pelo menos uma interface, própria do projeto ou fornecida por uma biblioteca.
- Controllers não podem depender diretamente de classes localizadas em `service.implement`; a injeção deve usar uma interface.

Além do teste, o perfil `dev` executa `ArchitectureStartupValidator` automaticamente durante a inicialização do Spring. O validador examina todas as classes nos pacotes de controller e implementação de service, mesmo que elas ainda não tenham uma anotação do Spring. Se alguma regra for violada, a aplicação encerra antes de ficar disponível, inclusive quando for iniciada diretamente pelo botão **Run** da IDE.

O perfil padrão da aplicação é `dev`. Em produção, defina explicitamente:

```env
SPRING_PROFILES_ACTIVE=prod
```

O validador de inicialização não é ativado no perfil `prod`; as mesmas regras continuam protegidas pelos testes do ArchUnit durante o build.

Exemplo permitido:

```java
public UsuarioController(IUsuarioService<Usuario> service) {
    this.service = service;
}
```

Exemplo bloqueado pelo ArchUnit:

```java
public UsuarioController(UsuarioService service) {
    this.service = service;
}
```

Execute apenas as verificações arquiteturais no PowerShell:

```powershell
.\mvnw.cmd -Dtest=ArchitectureTest test --settings settings.xml
```

No Git Bash ou Linux:

```bash
./mvnw -Dtest=ArchitectureTest test --settings settings.xml
```

Para executar todos os testes:

```powershell
.\mvnw.cmd test --settings settings.xml
```

O JaCoCo gera o relatório de cobertura em `target/site/jacoco/index.html`. Para executar os testes e também validar os limites mínimos de cobertura:

```powershell
.\mvnw.cmd verify --settings settings.xml
```

O build falha quando a cobertura total ficar abaixo de **85% das linhas** ou **75% dos branches**. Assim, novos códigos sem testes não reduzem silenciosamente a cobertura do projeto.

Para executar somente a suíte de integração da segurança e do contrato HTTP:

```powershell
.\mvnw.cmd -Dtest=SecurityApiIntegrationTest test --settings settings.xml
```

Essa suíte utiliza `MockMvc`, a configuração real do Spring Security e serviços simulados. Ela não acessa o PostgreSQL e define o perfil `test` apenas dentro do próprio teste.

O Maven precisa das variáveis `GITHUB_USERNAME` e `GITHUB_TOKEN` para baixar a dependência privada `crud-core` do GitHub Packages. Sem credenciais válidas, a compilação termina com `401 Unauthorized` antes de executar o ArchUnit.

## Docker

```bash
docker build \
  --build-arg GITHUB_TOKEN="$GITHUB_TOKEN" \
  -t starter-package-api .
```

```bash
docker run --rm -p 8085:8085 \
  -e DB_URL=host.docker.internal:5432/starter_package \
  -e DB_USER=postgres \
  -e DB_PASS=postgres \
  -e FLYWAY=true \
  -e JWT_SECRET=uma-chave-longa-com-32-bytes-ou-mais \
  -e CORS_ALLOWED_ORIGINS=https://app.exemplo.com \
  starter-package-api
```

## Swagger e OpenAPI

Com a aplicação em execução, a documentação interativa fica disponível em:

- Swagger UI: http://localhost:8085/api/swagger-ui.html
- OpenAPI JSON: http://localhost:8085/api/v3/api-docs
- OpenAPI YAML: http://localhost:8085/api/v3/api-docs.yaml

Para testar endpoints protegidos no Swagger UI:

1. Execute `POST /auth/login`.
2. Copie o valor de `token` da resposta.
3. Clique em **Authorize**.
4. Informe apenas o token; o Swagger adiciona o prefixo `Bearer` automaticamente.

O login e os arquivos da documentação são públicos. Os endpoints CRUD continuam protegidos pelo JWT e pelas permissões da aplicação.

## Contrato de erros

Todos os erros da API, inclusive falhas do filtro JWT, seguem o mesmo formato:

```json
{
  "timestamp": "2026-09-05T12:00:00",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Dados inválidos.",
  "path": "/api/user",
  "errorId": "b38c67d3-1ae1-4765-a1e3-401ef177dcea",
  "errors": [
    { "field": "cpf", "message": "CPF deve conter exatamente 11 números" }
  ]
}
```

Erros de validação retornam `400`, recursos não encontrados `404`, duplicidades e violações de integridade `409`, falhas de autenticação `401` e autorização `403`. Em erros internos, detalhes técnicos ficam somente no log e podem ser localizados pelo `errorId`.

### Health check

O Spring Boot Actuator disponibiliza o health check público em:

```http
GET /api/actuator/health
```

Quando a aplicação estiver saudável, a resposta será:

```json
{
  "status": "UP"
}
```

Somente o endpoint `health` é exposto e seus detalhes internos não são enviados na resposta.

## Observações para produção

- Defina um processo seguro e específico do projeto para criar o primeiro administrador; o starter não fornece usuário padrão.
- Injete segredos JWT pelo ambiente ou por um cofre de segredos.
- Configure `CORS_ALLOWED_ORIGINS` somente com os domínios confiáveis de cada ambiente.
- Confirme o fluxo de definição de senha na criação de usuários.
- Adicione testes de integração para autenticação, CRUD e autorização por recurso.
