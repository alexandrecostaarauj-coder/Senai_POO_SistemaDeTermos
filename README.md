# Sistema de Termos de Internação

Projeto prático de Programação Orientada a Objetos — Engenharia de Software, SENAI Fatesg.
Autor: Alexandre Costa Araujo.

Sistema desktop em Java Swing que preenche o Contrato de Prestação de Serviços de Internação
Hospitalar (com os Anexos I a VI) a partir de um modelo Word com marcadores `{{...}}`, gera o
termo em Word e PDF, mostra a pré-visualização e imprime. Os dados ficam em arquivos CSV.

## Conteúdo

| Pasta | O que é |
| --- | --- |
| `1-Documento_de_Requisitos/` | Tema, problema, objetivo, escopo, RF, RNF e arquitetura MVC + DTO |
| `2-Diagrama_UML/` | Diagrama de classes em PDF/PNG (3 páginas) e arquivo editável (.drawio) |
| `3-Codigo_Fonte/AssinaturaDeContrato/` | Projeto NetBeans completo |
| `4-Testes/` | Relatório de testes e saída dos 29 cenários (todos aprovados) |

## Como executar

1. Abrir `3-Codigo_Fonte/AssinaturaDeContrato` no NetBeans (Java 25).
2. Clean and Build e Run (classe principal: `visao.TelaInicial`).
   As bibliotecas (Apache POI e PDFBox) já estão em `lib/terceiros`.
3. Para gerar o PDF do termo é preciso ter o LibreOffice ou o Microsoft Word instalado.
4. Testes: `src/teste/TesteDados.java` → Run File.

## Arquitetura (MVC + DTO)

```
Tela (visao) -> DTO -> Controller -> Model (regras) -> DAO -> arquivo CSV
```

- **visao:** telas Swing; só conhecem controller e DTO
- **dto:** só transportam os dados (`MedicoDTO`, `ContratoDTO`, `PacienteDTO`, `EnderecoDTO`, `ContatoDeEmergenciaDTO`, `DeclaracaoDeSaudeDTO`)
- **controller:** `MedicoController` e `ContratoController` convertem DTO ⇄ model e chamam o DAO
- **model:** entidades e **todas as regras** (setters validam; `Contrato.validar()`, limite de 20 termos, próximo dia útil)
- **persistencia:** `Persistivel<T>`, `ArquivoCsvDAO<T>`, `MedicoDAO`, `ContratoDAO`
- **documento / util:** geração do Word e PDF; `Conversor` e `CpfUtil`

## Conceitos de POO aplicados

- **Herança:** `Pessoa` → `Paciente`, `Medico`; `ArquivoCsvDAO` (abstrata) → `MedicoDAO`, `ContratoDAO`
- **Interfaces:** `Identificavel`, `Persistivel<T>`, `ExportadorTermo`
- **Polimorfismo e sobrescrita:** cada DAO sobrescreve `paraColunas()` / `deColunas()`; `Paciente` e `Medico` sobrescrevem `rotuloDoNome()`; `toString()` nas classes do modelo
- **Arquivos:** leitura e gravação com `java.nio.file` e tratamento de `IOException`

## Dados

`src/dados/medicos.csv` e `src/dados/contratos.csv` contêm apenas **dados fictícios** de exemplo
(2 médicos e 3 termos). Formato: texto UTF-8, uma linha por registro, colunas separadas por `;`
(`medicos.csv`: 4 colunas; `contratos.csv`: 41).
