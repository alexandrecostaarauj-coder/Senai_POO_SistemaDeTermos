package documento;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import model.ContatoDeEmergencia;
import model.Contrato;
import model.DeclaracaoDeSaude;
import model.Endereco;
import model.Medico;
import model.OpcaoDeImagem;
import model.Paciente;
import util.Conversor;

/**
 * Transforma um Contrato no "dicionário" marcador -> texto usado no modelo do Word.
 * Ex.: "PACIENTE_NOME" -> "Ana Paula Exemplo".
 * Valor null significa "não preenchido": o GeradorWord decide como deixar em branco.
 */
public class ValoresDoTermo {

    private static final DateTimeFormatter EXTENSO =
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.of("pt", "BR"));

    private ValoresDoTermo() {
    }

    public static Map<String, String> montar(Contrato c) {
        Map<String, String> v = new HashMap<>();
        Paciente p = c.getPaciente();
        v.put("PACIENTE_NOME", p.getNome());
        if (p.getEstadoCivil() != null) {
            v.put("ESTADO_CIVIL", p.getEstadoCivil().getDescricao());
        }
        v.put("PROFISSAO", p.getProfissao());
        v.put("RG", p.getRg());
        v.put("CPF", formatar(p.getCpf(), "###.###.###-##"));
        v.put("DATA_NASCIMENTO", data(p.getDataDeNascimento()));
        v.put("TELEFONE", p.getTelefone());
        v.put("EMAIL", p.getEmail());
        if (p.getAltura() > 0) {
            v.put("ALTURA", String.valueOf(p.getAltura()).replace('.', ',') + " m");
        }
        if (p.getPeso() > 0) {
            v.put("PESO", numero(p.getPeso()) + " kg");
        }
        Endereco e = p.getEndereco();
        if (e != null) {
            v.put("RUA", e.getRua());
            v.put("NUMERO", e.getNumero());
            v.put("COMPLEMENTO", e.getComplemento());
            v.put("SETOR", e.getSetor());
            v.put("CEP", formatar(e.getCep(), "#####-###"));
            v.put("CIDADE_UF", juntar(e.getCidade(), e.getEstado(), "/"));
        }

        Medico m = c.getMedico();
        v.put("MEDICO_NOME", m.getNome());
        v.put("MEDICO_CRM", texto(m.getCrm()));
        v.put("MEDICO_RQE", texto(m.getRqe()));

        v.put("PROCEDIMENTO", c.getProcedimento());
        v.put("DATA_PROCEDIMENTO", data(c.getDataDoProcedimento()));
        v.put("DATA_INTERNACAO", data(c.getDataDoProcedimento())); // internação = dia do procedimento
        v.put("DATA_ASSINATURA", data(c.getDataDaAssinatura()));
        if (c.getDataDaAssinatura() != null) {
            v.put("DATA_ASSINATURA_EXTENSO", "Goiânia-GO, " + c.getDataDaAssinatura().format(EXTENSO));
        }

        List<ContatoDeEmergencia> contatos = c.getContatosDeEmergencia();
        for (int i = 0; i < contatos.size(); i++) {
            ContatoDeEmergencia ct = contatos.get(i);
            String n = "C" + (i + 1) + "_";
            v.put(n + "NOME", ct.getNome());
            v.put(n + "RELACAO", ct.getParentesco());
            v.put(n + "TELEFONE", ct.getTelefone());
            v.put(n + "EMAIL", ct.getEmail());
        }

        OpcaoDeImagem img = c.getOpcaoDeImagem();
        if (img == OpcaoDeImagem.ANONIMA) {
            v.put("IMG_ANONIMA", "X");
        } else if (img == OpcaoDeImagem.IDENTIFICADA) {
            v.put("IMG_IDENTIFICADA", "X");
        } else if (img == OpcaoDeImagem.NAO_AUTORIZA) {
            v.put("IMG_NAO_AUTORIZA", "X");
        }

        DeclaracaoDeSaude d = c.getDeclaracaoDeSaude();
        if (d != null) {
            marcar(v, "FUMANTE", d.isFumante());
            marcar(v, "DEPENDENTE", d.isDependenteQuimico());
            marcar(v, "CARDIOPATA", d.isCardiopata());
            marcar(v, "GRAVIDEZ", d.isSuspeitaDeGravidez());
            marcar(v, "DIABETICO", d.isDiabetico());
            marcar(v, "SEDENTARIO", d.isSedentario());
            marcar(v, "HIPERTENSO", d.isHipertenso());
        }
        return v;
    }

    /** Coloca "X" no SIM ou no NÃO. */
    private static void marcar(Map<String, String> v, String item, boolean sim) {
        if (sim) {
            v.put(item + "_SIM", "X");
        } else {
            v.put(item + "_NAO", "X");
        }
    }

    private static String data(LocalDate d) {
        if (d == null) {
            return null;
        }
        return Conversor.paraTexto(d);
    }

    private static String texto(Object o) {
        if (o == null) {
            return null;
        }
        return o.toString();
    }

    private static String numero(double n) {
        if (n == Math.floor(n)) {
            return String.valueOf((long) n);
        }
        return String.valueOf(n).replace('.', ',');
    }

    private static String juntar(String a, String b, String separador) {
        if (a == null && b == null) {
            return null;
        }
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a + separador + b;
    }

    /** "52998224725" + "###.###.###-##" -> "529.982.247-25". Se não bater o tamanho, devolve como está. */
    private static String formatar(String digitos, String mascara) {
        if (digitos == null) {
            return null;
        }
        String d = digitos.replaceAll("\\D", "");
        int quantidade = mascara.length() - mascara.replace("#", "").length(); // quantos '#' a máscara tem
        if (d.length() != quantidade) {
            return digitos;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (char ch : mascara.toCharArray()) {
            if (ch == '#') {
                sb.append(d.charAt(i));
                i++;
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
