# Sistema de Termos de Internação

Projeto prático de Programação Orientada a Objetos — Engenharia de Software, SENAI Fatesg.
Autor: Alexandre Costa Araujo.

Sistema desktop em Java Swing que preenche o Contrato de Prestação de Serviços de Internação
Hospitalar (com os Anexos I a VI) a partir de um modelo Word com marcadores `{{...}}`, gera o
termo em Word e PDF, mostra a pré-visualização e imprime. Os dados ficam em arquivos CSV.

## Conteúdo

| Pasta / arquivo | O que é |
| --- | --- |
| `1-Documento_de_Analise.docx` | Tema, problema, objetivo, público-alvo, RF e RNF |
| `2-Diagrama_UML/` | Diagrama de classes (PDF) e arquivo editável (.drawio) |
| `3-Projeto_Java/AssinaturaDeContrato/` | Projeto NetBeans completo |
| `4-Relatorio_de_Testes.docx` | 5 casos de teste (entrada, esperado, obtido e situação) |

## Como executar

1. Abrir `3-Projeto_Java/AssinaturaDeContrato` no NetBeans (Java 25).
2. Executar o projeto (classe principal: `visao.TelaInicial`).
   As bibliotecas (Apache POI e PDFBox) já estão em `lib/terceiros`.
3. Para gerar o PDF do termo é preciso ter o LibreOffice ou o Microsoft Word instalado.

## Conceitos de POO aplicados

- **Herança:** `Pessoa` → `Paciente`, `Medico`; `ArquivoCsvDAO` (abstrata) → `MedicoDAO`, `ContratoDAO`
- **Interfaces:** `Identificavel`, `Persistivel<T>`, `ExportadorTermo`
- **Polimorfismo e sobrescrita:** cada DAO sobrescreve `paraColunas()` / `deColunas()`; `toString()` nas classes do modelo
- **Arquivos:** leitura e gravação com `java.nio.file` e tratamento de `IOException`

## Dados

`src/dados/medicos.csv` e `src/dados/contratos.csv` contêm apenas **dados fictícios** de exemplo
(2 médicos e 3 termos). Formato: texto UTF-8, uma linha por registro, colunas separadas por `;`
(`medicos.csv`: 4 colunas; `contratos.csv`: 41).
