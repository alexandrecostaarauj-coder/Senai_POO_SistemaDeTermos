package util;

/**
 * Validações reutilizáveis. Todo método lança IllegalArgumentException
 * com uma mensagem pronta para aparecer num JOptionPane.
 *
 * Regra do projeto: só NOME do paciente e NOME do médico são obrigatórios.
 * Os demais campos podem ficar vazios (null), mas, se preenchidos, têm
 * que ser válidos.
 */
public class Validador {

    private Validador() { }

    /**
     * Limpa o texto e garante que ele cabe no CSV.
     * Devolve null se estiver vazio. Rejeita ';' e quebra de linha,
     * porque eles quebrariam o split(";") ao ler o arquivo.
     */
    public static String texto(String campo, String valor) {
        if (valor == null) {
            return null;
        }
        String limpo = valor.trim();
        if (limpo.isEmpty()) {
            return null;
        }
        if (limpo.contains(";") || limpo.contains("\n") || limpo.contains("\r")) {
            throw new IllegalArgumentException(campo + " não pode conter ';' nem quebra de linha.");
        }
        return limpo;
    }

    public static String obrigatorio(String campo, String valor) {
        String limpo = texto(campo, valor);
        if (limpo == null) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        return limpo;
    }

    /** Aceita 11 dígitos (com ou sem pontos/traço) e confere os dígitos verificadores. */
    public static String cpf(String valor) {
        String t = texto("CPF", valor);
        if (t == null) {
            return null;
        }
        String d = t.replaceAll("[.\\-]", "");
        if (!d.matches("\\d{11}") || d.matches("(\\d)\\1{10}")) {
            throw new IllegalArgumentException("CPF inválido: " + t);
        }
        int d1 = digitoVerificador(d, 9);
        int d2 = digitoVerificador(d, 10);
        if (d1 != d.charAt(9) - '0' || d2 != d.charAt(10) - '0') {
            throw new IllegalArgumentException("CPF inválido (dígito verificador): " + t);
        }
        return d;
    }

    private static int digitoVerificador(String d, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (d.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = (soma * 10) % 11;
        if (resto == 10) {
            return 0;
        }
        return resto;
    }

    public static String cep(String valor) {
        String t = texto("CEP", valor);
        if (t == null) {
            return null;
        }
        String d = t.replace("-", "");
        if (!d.matches("\\d{8}")) {
            throw new IllegalArgumentException("CEP deve ter 8 dígitos: " + t);
        }
        return d;
    }

    public static String email(String valor) {
        String t = texto("E-mail", valor);
        if (t == null) {
            return null;
        }
        if (!t.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException("E-mail inválido: " + t);
        }
        return t;
    }

    public static String uf(String valor) {
        String t = texto("Estado (UF)", valor);
        if (t == null) {
            return null;
        }
        if (!t.matches("[A-Za-z]{2}")) {
            throw new IllegalArgumentException("UF deve ter 2 letras: " + t);
        }
        return t.toUpperCase();
    }

    /** Altura em metros. 0 = não informada. */
    public static double altura(double valor) {
        if (valor != 0 && (valor < 0.30 || valor > 2.80)) {
            throw new IllegalArgumentException("Altura deve estar entre 0,30 m e 2,80 m (em metros).");
        }
        return valor;
    }

    /** Peso em kg. 0 = não informado. */
    public static double peso(double valor) {
        if (valor != 0 && (valor < 1 || valor > 700)) {
            throw new IllegalArgumentException("Peso deve estar entre 1 kg e 700 kg.");
        }
        return valor;
    }

    public static java.time.LocalDate dataNascimento(java.time.LocalDate data) {
        if (data == null) {
            return null;
        }
        if (data.isAfter(java.time.LocalDate.now()) || data.getYear() < 1900) {
            throw new IllegalArgumentException("Data de nascimento inválida: " + Conversor.paraTexto(data));
        }
        return data;
    }

    /** CRM/RQE: opcionais, mas se existirem devem ser positivos. */
    public static Integer positivo(String campo, Integer valor) {
        if (valor != null && valor <= 0) {
            throw new IllegalArgumentException(campo + " deve ser um número positivo.");
        }
        return valor;
    }

    /**
     * Confere todos os dados do paciente. Só o nome é obrigatório.
     * Devolve o CPF só com números (ou null), para comparar na busca.
     */
    public static void paciente(model.Paciente p) {
        if (p == null) {
            throw new IllegalArgumentException("Preencha os dados do paciente.");
        }
        obrigatorio("Nome do paciente", p.getNome());
        texto("Profissão", p.getProfissao());
        cpf(p.getCpf());
        texto("RG", p.getRg());
        dataNascimento(p.getDataDeNascimento());
        email(p.getEmail());
        texto("Telefone", p.getTelefone());
        altura(p.getAltura());
        peso(p.getPeso());
        model.Endereco e = p.getEndereco();
        if (e != null) {
            texto("Endereço", e.getRua());
            texto("Número", e.getNumero());
            texto("Complemento", e.getComplemento());
            texto("Setor", e.getSetor());
            cep(e.getCep());
            texto("Cidade", e.getCidade());
            uf(e.getEstado());
        }
    }

    /**
     * Regras do contrato (a tela chama antes de gerar o documento e o DAO antes de salvar).
     * Sempre: paciente válido, médico com nome, data do procedimento, modo de assinatura,
     *         no máximo 2 contatos e cada contato informado com nome e telefone.
     * DIGITAL: também exige pelo menos 1 contato, declaração de saúde e opção de imagem.
     * MANUAL: o resto pode ficar vazio (sai em branco para preencher à mão).
     */
    public static void contrato(model.Contrato c) {
        paciente(c.getPaciente());
        if (c.getMedico() == null || texto("Médico", c.getMedico().getNome()) == null) {
            throw new IllegalArgumentException("Selecione o médico responsável.");
        }
        if (c.getDataDoProcedimento() == null) {
            throw new IllegalArgumentException("Informe a data do procedimento.");
        }
        if (c.getModoAssinatura() == null) {
            throw new IllegalArgumentException("Selecione o modo de assinatura.");
        }
        texto("Procedimento", c.getProcedimento());

        java.util.List<model.ContatoDeEmergencia> contatos = c.getContatosDeEmergencia();
        if (contatos.size() > model.Contrato.MAXIMO_CONTATOS) {
            throw new IllegalArgumentException("Informe no máximo " + model.Contrato.MAXIMO_CONTATOS + " contatos de emergência.");
        }
        for (int i = 0; i < contatos.size(); i++) {
            model.ContatoDeEmergencia contato = contatos.get(i);
            obrigatorio("Nome do contato " + (i + 1), contato.getNome());
            texto("Parentesco do contato " + (i + 1), contato.getParentesco());
            obrigatorio("Telefone do contato " + (i + 1), contato.getTelefone());
            email(contato.getEmail());
        }

        if (c.getModoAssinatura() == model.ModoAssinatura.DIGITAL) {
            if (contatos.isEmpty()) {
                throw new IllegalArgumentException("Assinatura digital: informe pelo menos 1 contato de emergência.");
            }
            if (c.getDeclaracaoDeSaude() == null) {
                throw new IllegalArgumentException("Assinatura digital: preencha a declaração de saúde.");
            }
            if (c.getOpcaoDeImagem() == null) {
                throw new IllegalArgumentException("Assinatura digital: escolha a opção de uso de imagem.");
            }
        }
    }
}
