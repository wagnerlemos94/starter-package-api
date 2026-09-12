package br.com.digidatasistemas.starterPackage.constrants;

public abstract class Constrants {

    private Constrants() {
    }

    public static final String MSG_DADOS_INVALIDOS = "Dados inválidos.";
    public static final String MSG_ERRO_INTERNO = "Erro interno no servidor.";
    public static final String MSG_CONFLITO_INTEGRIDADE =
            "Não foi possível concluir a operação porque o recurso já existe ou está em uso.";

    public static final String MSG_AUTENTICACAO_NECESSARIA = "Autenticação necessária.";
    public static final String MSG_USUARIO_NAO_AUTENTICADO = "Usuário não autenticado.";
    public static final String MSG_USUARIO_SEM_PERMISSAO =
            "Usuário não tem permissão para acessar esta funcionalidade.";
    public static final String MSG_USUARIO_OU_SENHA_INVALIDOS = "Usuário ou senha inválidos.";
    public static final String MSG_USUARIO_INATIVO = "Usuário inativo.";
    public static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado.";
    public static final String MSG_USUARIO_JA_EXISTENTE =
            "Usuário com CPF %s já existe no sistema.";

    public static final String MSG_TOKEN_EXPIRADO = "Token expirado.";
    public static final String MSG_TOKEN_INVALIDO = "Token inválido.";
    public static final String MSG_TOKEN_NAO_SUPORTADO = "Token não suportado.";
    public static final String MSG_TOKEN_ASSINATURA_INVALIDA = "Assinatura do token inválida.";
    public static final String MSG_TOKEN_USUARIO_INVALIDO = "Token inválido ou usuário inativo.";
    public static final String MSG_FALHA_AUTENTICACAO = "Falha na autenticação.";

    public static final String MSG_NOME_OBRIGATORIO = "Nome é obrigatório.";
    public static final String MSG_SENHA_OBRIGATORIA = "Senha é obrigatória.";
    public static final String MSG_PERFIL_OBRIGATORIO = "Perfil é obrigatório.";
    public static final String MSG_ATIVO_OBRIGATORIO = "Ativo é obrigatório.";
    public static final String MSG_CHAVE_OBRIGATORIA = "Chave é obrigatória.";
    public static final String MSG_CPF_OBRIGATORIO = "CPF é obrigatório.";
    public static final String MSG_CPF_INVALIDO = "CPF deve conter exatamente 11 números.";
    public static final String MSG_NOME_MAXIMO_100 = "Nome deve possuir no máximo 100 caracteres.";
    public static final String MSG_NOME_MAXIMO_150 = "Nome deve possuir no máximo 150 caracteres.";
    public static final String MSG_DESCRICAO_MAXIMO_255 =
            "Descrição deve possuir no máximo 255 caracteres.";
    public static final String MSG_SENHA_TAMANHO_INVALIDO =
            "Senha deve possuir entre 8 e 72 caracteres.";
    public static final String MSG_CHAVE_MAXIMO_50 = "Chave deve possuir no máximo 50 caracteres.";
    public static final String MSG_CHAVE_FORMATO_INVALIDO =
            "Chave deve conter apenas letras maiúsculas, números e sublinhado.";

    public static final String MSG_PERFIL_NOME_EXISTENTE =
            "Já existe um perfil com o nome '%s'.";
    public static final String MSG_PERFIL_RECURSO_OBRIGATORIO = "Recurso do perfil é obrigatório.";
    public static final String MSG_PERFIL_RECURSO_DUPLICADO =
            "O mesmo recurso não pode ser informado mais de uma vez no perfil.";
    public static final String MSG_PERFIL_RECURSO_INATIVO =
            "Não é possível associar um recurso inativo ao perfil.";
    public static final String MSG_PERFIL_PERMISSAO_OBRIGATORIA =
            "Permissão do recurso é obrigatória.";
    public static final String MSG_PERFIL_PERMISSAO_DUPLICADA =
            "A mesma permissão não pode ser informada mais de uma vez para o recurso.";
    public static final String MSG_PERFIL_PERMISSAO_INATIVA =
            "Não é possível associar uma permissão inativa ao perfil.";

}
