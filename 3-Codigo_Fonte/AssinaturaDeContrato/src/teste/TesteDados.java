package teste;

import controller.ContratoController;
import controller.MedicoController;
import documento.PastaDeTermos;
import dto.ContatoDeEmergenciaDTO;
import dto.ContratoDTO;
import dto.DeclaracaoDeSaudeDTO;
import dto.EnderecoDTO;
import dto.MedicoDTO;
import dto.PacienteDTO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import model.Contrato;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import persistencia.ContratoDAO;
import persistencia.MedicoDAO;
import util.Conversor;

/**
 * Casos de teste do sistema (rode com botão direito > Run File no NetBeans).
 *
 * Os testes passam pelo mesmo caminho das telas: DTO -> Controller -> Model
 * (regras) -> DAO (arquivo CSV). Usa pastas temporárias, então NÃO mexe nos
 * arquivos de src\dados nem na pasta de termos do usuário. Todos os dados são
 * fictícios. Cada teste imprime PASSOU/FALHOU e no fim sai o resumo.
 */
public class TesteDados {

    static int passou = 0, falhou = 0;
    static Path pasta;
    static ContratoController termos;
    static MedicoController medicos;

    public static void main(String[] args) throws Exception {
        t01();
        t02();
        t03();
        t04();
        t05();
        t06();
        t07();
        t08();
        t09();
        t10();
        t11();
        t12();
        t13();
        t14();
        t15();
        t16();
        t17();
        t18();
        t19();
        t20();
        t21();
        t22();
        t23();
        t24();
        t25();
        t26();
        t27();
        t28();
        t29();
        System.out.println("\nResumo: " + passou + " passou, " + falhou + " falhou.");
    }

    // ================================================================== apoio

    /** Pasta temporária nova + controllers apontando para ela ('sub' não existe: testa criar a pasta). */
    static void novoAmbiente() throws IOException {
        pasta = Files.createTempDirectory("termo");
        termos = new ContratoController(new ContratoDAO(arq("contratos.csv")),
                new PastaDeTermos(pasta.resolve("documentos").toFile()));
        medicos = new MedicoController(new MedicoDAO(arq("medicos.csv")));
    }

    static String arq(String nome) {
        return pasta.resolve("sub").resolve(nome).toString();
    }

    /** Simula o programa fechado e aberto de novo: controller novo, mesmo arquivo. */
    static ContratoController reabrir() {
        return new ContratoController(new ContratoDAO(arq("contratos.csv")),
                new PastaDeTermos(pasta.resolve("documentos").toFile()));
    }

    static MedicoDTO medico() {
        return new MedicoDTO(0, "Dr. Carlos Exemplo", "12345", "678");
    }

    static PacienteDTO paciente(String nome) {
        PacienteDTO p = new PacienteDTO();
        p.setNome(nome);
        p.setCpf("529.982.247-25");
        p.setRg("1234567");
        p.setDataDeNascimento("20/05/1990");
        p.setEstadoCivil("SOLTEIRO");
        p.setProfissao("Engenheiro");
        p.setAltura("1,75");
        p.setPeso("70,5");
        p.setTelefone("(62) 99999-0000");
        p.setEmail("paciente@exemplo.com");
        EnderecoDTO e = p.getEndereco();
        e.setRua("Rua Exemplo");
        e.setNumero("10");
        e.setComplemento("Qd 1 Lt 2");
        e.setSetor("Centro");
        e.setCep("74000-000");
        e.setCidade("Goiania");
        e.setEstado("go");
        return p;
    }

    static ContratoDTO termoManual(String nome, LocalDate dataProcedimento) {
        ContratoDTO t = new ContratoDTO();
        t.setPaciente(paciente(nome));
        t.setMedico(medico());
        t.setProcedimento("Artroscopia de joelho");
        t.setDataDoProcedimento(Conversor.paraTexto(dataProcedimento));
        t.setDataDaAssinatura(Conversor.paraTexto(dataProcedimento));
        t.setModoAssinatura("MANUAL");
        return t;
    }

    static ContratoDTO termoDigital(String nome) {
        ContratoDTO t = termoManual(nome, LocalDate.now().plusDays(1));
        t.setModoAssinatura("DIGITAL");
        t.getContatosDeEmergencia().add(new ContatoDeEmergenciaDTO("Maria Exemplo", "Mãe", "(62) 98888-1111", "maria@exemplo.com"));
        t.getContatosDeEmergencia().add(new ContatoDeEmergenciaDTO("João Exemplo", "Irmão", "(62) 97777-2222", ""));
        DeclaracaoDeSaudeDTO d = new DeclaracaoDeSaudeDTO();
        d.setFumante(true);
        d.setHipertenso(true);
        t.setDeclaracaoDeSaude(d);
        t.setOpcaoDeImagem("ANONIMA");
        return t;
    }

    static void ok(String nome, boolean condicao) {
        if (condicao) {
            passou++;
            System.out.println("PASSOU  " + nome);
        } else {
            falhou++;
            System.out.println("FALHOU  " + nome);
        }
    }

    /** Executa e devolve a mensagem da IllegalArgumentException (ou null se não lançou). */
    static String erro(Acao acao) {
        try {
            acao.executar();
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "OUTRO ERRO: " + e;
        }
    }

    interface Acao {
        void executar() throws Exception;
    }

    static String textoDoWord(File docx) throws IOException {
        try (InputStream in = Files.newInputStream(docx.toPath()); XWPFDocument doc = new XWPFDocument(in);
                XWPFWordExtractor ex = new XWPFWordExtractor(doc)) {
            return ex.getText();
        }
    }

    /** Quantas marcações "(X)" o termo tem (SIM/NÃO da declaração e opção de imagem). */
    static int marcacoesX(File docx) throws IOException {
        String texto = textoDoWord(docx).replace(" ", "");
        int total = 0;
        int i = texto.indexOf("(X)");
        while (i >= 0) {
            total++;
            i = texto.indexOf("(X)", i + 1);
        }
        return total;
    }

    // ================================================================== testes

    static void t01() throws Exception {
        novoAmbiente();
        int a = termos.cadastrar(termoManual("Ana Exemplo", LocalDate.now().plusDays(1)));
        int b = termos.cadastrar(termoManual("Bruno Exemplo", LocalDate.now().plusDays(1)));
        ok("T01 IDs sequenciais (1 e 2) e pasta criada", a == 1 && b == 2 && Files.exists(Path.of(arq("contratos.csv"))));
    }

    static void t02() throws Exception {
        novoAmbiente();
        termos.cadastrar(termoManual("Ana Exemplo", LocalDate.of(2030, 3, 15)));
        List<ContratoDTO> lidos = reabrir().listar();
        ContratoDTO t = lidos.get(0);
        PacienteDTO p = t.getPaciente();
        ok("T02 persistência após reiniciar (paciente, endereço com complemento, médico, datas)",
                lidos.size() == 1 && p.getNome().equals("Ana Exemplo") && p.getCpf().equals("52998224725")
                && p.getEndereco().getEstado().equals("GO") && p.getEndereco().getCep().equals("74000000")
                && p.getEndereco().getComplemento().equals("Qd 1 Lt 2") && p.getDataDeNascimento().equals("20/05/1990")
                && p.getAltura().equals("1,75") && t.getMedico().getNome().equals("Dr. Carlos Exemplo")
                && t.getMedico().getCrm().equals("12345") && t.getDataDoProcedimento().equals("15/03/2030"));
    }

    static void t03() throws Exception {
        novoAmbiente();
        ContratoDTO t = termoManual("Ana Exemplo", LocalDate.now().plusDays(1));
        t.getPaciente().setCpf("111.222.333-44");
        String msg = erro(() -> termos.cadastrar(t));
        ok("T03 CPF inválido rejeitado e nada gravado", msg != null && msg.startsWith("CPF inválido")
                && termos.listar().isEmpty());
    }

    static void t04() throws Exception {
        novoAmbiente();
        ContratoDTO t = new ContratoDTO();
        t.getPaciente().setNome("Somente Nome");
        t.setMedico(medico());
        t.setDataDoProcedimento("10/10/2030");
        t.setModoAssinatura("MANUAL");
        termos.cadastrar(t);
        ContratoDTO lido = reabrir().listar().get(0);
        PacienteDTO p = lido.getPaciente();
        ok("T04 só obrigatórios (nome, médico, data, modo); vazios voltam null",
                p.getCpf() == null && p.getEmail() == null && p.getEndereco().getRua() == null
                && lido.getProcedimento() == null && lido.getDataDaAssinatura().isEmpty() && p.getAltura().isEmpty());
    }

    static void t05() throws Exception {
        novoAmbiente();
        int id = termos.cadastrar(termoManual("Ana Exemplo", LocalDate.now().plusDays(1)));
        ContratoDTO t = termos.buscarPorId(id);
        t.getPaciente().setNome("Ana Corrigida Exemplo");
        boolean atualizou = termos.atualizar(t);
        ok("T05 atualizar (corrigir nome)", atualizou && reabrir().buscarPorId(id).getPaciente().getNome().equals("Ana Corrigida Exemplo")
                && termos.listar().size() == 1);
    }

    static void t06() throws Exception {
        novoAmbiente();
        for (int i = 0; i < 3; i++) {
            termos.cadastrar(termoManual("Paciente " + i, LocalDate.now().plusDays(1)));
        }
        boolean excluiu = termos.excluir(2);
        int novo = termos.cadastrar(termoManual("Novo Exemplo", LocalDate.now().plusDays(1)));
        ok("T06 excluir e novo ID = maior+1", excluiu && termos.buscarPorId(2) == null && novo == 4 && termos.listar().size() == 3);
    }

    static void t07() throws Exception {
        novoAmbiente();
        String msg = erro(() -> medicos.cadastrar(new MedicoDTO(0, "   ", "111", "")));
        ok("T07 médico sem nome rejeitado", "Nome do médico é obrigatório.".equals(msg) && medicos.listar().isEmpty());
    }

    static void t08() throws Exception {
        novoAmbiente();
        ok("T08 arquivo inexistente -> lista vazia", termos.listar().isEmpty() && medicos.listar().isEmpty());
    }

    static void t09() throws Exception {
        novoAmbiente();
        termos.cadastrar(termoManual("Ana Exemplo", LocalDate.now().plusDays(1)));
        Files.writeString(Path.of(arq("contratos.csv")), "2;10/10/2030;faltando colunas\n", StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.APPEND);
        boolean lancou = false;
        try {
            termos.listar();
        } catch (IOException e) {
            lancou = true;
        }
        ok("T09 linha com colunas faltando -> IOException (sem lista parcial)", lancou);
    }

    static void t10() throws Exception {
        novoAmbiente();
        String msg = erro(() -> termos.cadastrar(termoManual("Ana;Exemplo", LocalDate.now().plusDays(1))));
        ok("T10 ';' no nome rejeitado", msg != null && msg.contains("';'") && termos.listar().isEmpty());
    }

    static void t11() throws Exception {
        novoAmbiente();
        termos.cadastrar(termoManual("Ana Exemplo", LocalDate.now().plusDays(1)));
        ContratoDTO t = reabrir().listar().get(0);
        ok("T11 MANUAL: contatos, declaração e imagem em branco", t.getContatosDeEmergencia().isEmpty()
                && t.getDeclaracaoDeSaude() == null && t.getOpcaoDeImagem() == null && t.getModoAssinatura().equals("MANUAL"));
    }

    static void t12() throws Exception {
        novoAmbiente();
        ContratoDTO t = termoManual("Ana Exemplo", LocalDate.now().plusDays(1));
        t.setDataDoProcedimento("31/02/2026");
        String msg = erro(() -> termos.cadastrar(t));
        ok("T12 data 31/02/2026 rejeitada", msg != null && msg.startsWith("Data inválida"));
    }

    static void t13() throws Exception {
        novoAmbiente();
        ContratoDTO t = termoManual("Ana Exemplo", LocalDate.now().plusDays(1));
        t.setDataDoProcedimento("");
        ok("T13 sem data do procedimento rejeitado", "Informe a data do procedimento.".equals(erro(() -> termos.cadastrar(t))));
    }

    static void t14() throws Exception {
        novoAmbiente();
        termos.cadastrar(termoDigital("Ana Exemplo"));
        ContratoDTO t = reabrir().listar().get(0);
        List<ContatoDeEmergenciaDTO> c = t.getContatosDeEmergencia();
        DeclaracaoDeSaudeDTO d = t.getDeclaracaoDeSaude();
        ok("T14 DIGITAL completo: 2 contatos (com e-mail), declaração e imagem voltam iguais",
                c.size() == 2 && c.get(0).getEmail().equals("maria@exemplo.com") && c.get(1).getEmail() == null
                && c.get(1).getParentesco().equals("Irmão") && d.isFumante() && d.isHipertenso() && !d.isDiabetico()
                && t.getOpcaoDeImagem().equals("ANONIMA"));
    }

    static void t15() throws Exception {
        novoAmbiente();
        ContratoDTO t = termoDigital("Ana Exemplo");
        t.getContatosDeEmergencia().clear();
        t.getContatosDeEmergencia().add(new ContatoDeEmergenciaDTO("", " ", "", "")); // tela com contato em branco
        ok("T15 DIGITAL sem contato de emergência rejeitado",
                "Assinatura digital: informe pelo menos 1 contato de emergência.".equals(erro(() -> termos.cadastrar(t))));
    }

    static void t16() throws Exception {
        novoAmbiente();
        ContratoDTO t = termoDigital("Ana Exemplo");
        t.setDeclaracaoDeSaude(null);
        ok("T16 DIGITAL sem declaração de saúde rejeitado",
                "Assinatura digital: preencha a declaração de saúde.".equals(erro(() -> termos.cadastrar(t))));
    }

    static void t17() throws Exception {
        novoAmbiente();
        ContratoDTO t = termoDigital("Ana Exemplo");
        t.getContatosDeEmergencia().add(new ContatoDeEmergenciaDTO("Terceiro Exemplo", "Tio", "(62) 96666-3333", ""));
        ok("T17 3 contatos rejeitado (máximo 2)",
                "Informe no máximo 2 contatos de emergência.".equals(erro(() -> termos.cadastrar(t))));
    }

    static void t18() throws Exception {
        novoAmbiente();
        ContratoDTO t = termoDigital("Ana Exemplo");
        t.getContatosDeEmergencia().get(1).setTelefone("");
        ok("T18 contato sem telefone rejeitado",
                "Telefone do contato de emergência é obrigatório.".equals(erro(() -> termos.cadastrar(t))));
    }

    static void t19() throws Exception {
        novoAmbiente();
        ContratoDTO t = termoManual("Ana Exemplo", LocalDate.now().plusDays(1));
        t.setModoAssinatura(null);
        ok("T19 contrato sem modo de assinatura rejeitado",
                "Selecione o modo de assinatura.".equals(erro(() -> termos.cadastrar(t))));
    }

    static void t20() throws Exception {
        novoAmbiente();
        for (int i = 1; i <= 21; i++) {
            termos.cadastrar(termoManual("Paciente " + i, LocalDate.now().minusDays(30 - i)));
        }
        List<Integer> removidosNo21 = termos.getUltimosRemovidos();
        termos.cadastrar(termoManual("Paciente 22", LocalDate.now().minusDays(1)));
        List<ContratoDTO> lista = termos.listar();
        ok("T20 limite de 20: ao salvar o 21º e o 22º, apaga os 2 mais antigos",
                lista.size() == 20 && lista.get(0).getId() == 3 && removidosNo21.equals(List.of(1))
                && termos.getUltimosRemovidos().equals(List.of(2)));
    }

    static void t21() throws Exception {
        novoAmbiente();
        for (int i = 1; i <= 21; i++) {
            termos.cadastrar(termoManual("Paciente " + i, LocalDate.now().plusDays(i)));
        }
        ok("T21 trava: com todas as cirurgias por vir, nenhum termo é apagado",
                termos.listar().size() == 21 && termos.getUltimosRemovidos().isEmpty());
    }

    static void t22() throws Exception {
        novoAmbiente();
        termos.cadastrar(termoManual("Remarcada Exemplo", LocalDate.now().plusDays(10))); // nº 1, cirurgia futura
        for (int i = 2; i <= 21; i++) {
            termos.cadastrar(termoManual("Paciente " + i, LocalDate.now().minusDays(5)));
        }
        ok("T22 o termo mais antigo de cirurgia futura (remarcada) é mantido; apaga o próximo que já passou",
                termos.buscarPorId(1) != null && termos.buscarPorId(2) == null && termos.listar().size() == 20);
    }

    static void t23() throws Exception {
        novoAmbiente();
        termos.cadastrar(termoManual("Ana Exemplo", LocalDate.now().plusDays(1)));
        ContratoDTO outro = termoManual("Outro Exemplo", LocalDate.now().plusDays(1));
        outro.getPaciente().setCpf("");
        termos.cadastrar(outro);
        termos.cadastrar(termoManual("Ana Exemplo Atualizada", LocalDate.now().plusDays(2)));
        ContratoDTO achado = termos.buscarUltimoPorCpf("52998224725");
        ok("T23 busca por CPF traz o termo mais recente do paciente (e null se não achar)",
                achado != null && achado.getId() == 3 && termos.buscarUltimoPorCpf("123.456.789-09") == null);
    }

    static void t24() throws Exception {
        novoAmbiente();
        int idMedico = medicos.cadastrar(medico());
        ContratoDTO t = termoManual("Ana Exemplo", LocalDate.now().plusDays(1));
        t.setMedico(medicos.listar().get(0));
        termos.cadastrar(t);
        medicos.excluir(idMedico);
        ContratoDTO lido = reabrir().listar().get(0);
        ok("T24 excluir o médico do cadastro não afeta o termo (cópia guardada)",
                medicos.listar().isEmpty() && lido.getMedico().getNome().equals("Dr. Carlos Exemplo")
                && lido.getMedico().getCrm().equals("12345"));
    }

    static void t25() throws Exception {
        novoAmbiente();
        medicos.cadastrar(medico());
        String msg = erro(() -> medicos.cadastrar(new MedicoDTO(0, "Dra. Outra Exemplo", "12345", "")));
        MedicoDTO proprio = medicos.listar().get(0);
        proprio.setRqe("999");
        boolean atualizou = medicos.atualizar(proprio);
        ok("T25 CRM repetido rejeitado (mas atualizar o próprio médico funciona)",
                msg != null && msg.startsWith("Já existe médico com o CRM 12345") && atualizou
                && medicos.listar().size() == 1 && medicos.listar().get(0).getRqe().equals("999"));
    }

    static void t26() throws Exception {
        novoAmbiente();
        int id = termos.cadastrar(termoDigital("Ana Exemplo"));
        File docx = termos.gerarWord(id);
        String texto = textoDoWord(docx);
        ok("T26 Word gerado: nenhum {{marcador}} sobrando, dados do paciente, médico e contato no texto",
                docx.exists() && !texto.contains("{{") && texto.contains("Ana Exemplo") && texto.contains("529.982.247-25")
                && texto.contains("Dr. Carlos Exemplo") && texto.contains("Maria Exemplo") && marcacoesX(docx) > 0);
    }

    static void t27() throws Exception {
        novoAmbiente();
        int id = termos.cadastrar(termoManual("Ana Exemplo", LocalDate.now().plusDays(1)));
        File docx = termos.gerarWord(id);
        String texto = textoDoWord(docx);
        ok("T27 MANUAL: contatos e SIM/NÃO em branco, imagem sem X",
                !texto.contains("{{") && !texto.contains("Maria Exemplo") && marcacoesX(docx) == 0);
    }

    static void t28() throws Exception {
        novoAmbiente();
        int id = termos.cadastrar(termoManual("Ana Exemplo", LocalDate.now().plusDays(1)));
        File docx = termos.gerarWord(id);
        boolean existia = docx.exists();
        termos.excluir(id);
        ok("T28 apagar os arquivos do termo (usado ao excluir e no limite de 20)", existia && !docx.exists());
    }

    static void t29() {
        LocalDate sexta = LocalDate.of(2026, 11, 13);
        LocalDate quarta = LocalDate.of(2026, 11, 11);
        ok("T29 data sugerida: sexta -> segunda; quarta -> quinta",
                Contrato.proximoDiaUtil(sexta).equals(LocalDate.of(2026, 11, 16))
                && Contrato.proximoDiaUtil(quarta).equals(LocalDate.of(2026, 11, 12)));
    }
}
