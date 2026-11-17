# Biblioteca API

API REST do Sistema de Empréstimos da Biblioteca do Campus, desenvolvida como mini-projeto do Bloco 2 de Tópicos Especiais II (Java + Spring Boot).

A API gerencia três recursos: **livros**, **leitores** (alunos e professores) e **empréstimos**. Os dados ficam em memória.

## Integrantes

- Alexandre Venturini

## Tecnologias

- Java 21
- Spring Boot 4.1.2 (Spring Web MVC e Validation)
- Lombok
- Maven (com Maven Wrapper)

## Como executar

Pré-requisito: JDK 21 instalado (a variável `JAVA_HOME` deve apontar para ele). Não é necessário ter o Maven instalado.

```bash
./mvnw spring-boot:run
```

No Windows (Prompt de Comando ou PowerShell):

```bash
mvnw.cmd spring-boot:run
```

A API sobe em `http://localhost:8080`.

## Carga inicial

Ao iniciar, a aplicação cadastra:

| Recurso | Dados |
|---|---|
| Livros | 1 - Código Limpo (3 exemplares), 2 - Refatoração (2 exemplares), 3 - Arquitetura Limpa (1 exemplar) |
| Leitores | 1 - Carlos Pereira (PROFESSOR), 2 - Ana Souza (ALUNO) |
| Empréstimos | 1 - Refatoração para Carlos Pereira, feito há 20 dias e **já atrasado** |

O empréstimo atrasado permite testar o bloqueio de novo empréstimo (RN05), o cálculo de multa (RN07) e o relatório de atrasados sem esperar dias.

## Endpoints

| Método | URI | Descrição | Sucesso |
|---|---|---|---|
| POST | /livros | cadastra um livro | 201 + Location |
| GET | /livros | lista livros; filtros opcionais `?autor=` e `?disponivel=true` | 200 |
| GET | /livros/{id} | busca um livro | 200 |
| PUT | /livros/{id} | atualiza um livro | 200 |
| DELETE | /livros/{id} | remove um livro | 204 |
| POST | /leitores | cadastra um leitor | 201 + Location |
| GET | /leitores | lista leitores; filtro opcional `?tipo=` | 200 |
| GET | /leitores/{id} | busca um leitor | 200 |
| PUT | /leitores/{id} | atualiza um leitor | 200 |
| DELETE | /leitores/{id} | remove um leitor | 204 |
| POST | /emprestimos | realiza um empréstimo | 201 + Location |
| GET | /emprestimos | lista empréstimos; filtros opcionais `?leitorId=` e `?ativos=true` | 200 |
| GET | /emprestimos/{id} | busca um empréstimo | 200 |
| POST | /emprestimos/{id}/devolucao | registra a devolução e calcula a multa | 200 |
| GET | /emprestimos/atrasados | lista os empréstimos ativos com prazo vencido | 200 |

### Exemplo

```http
POST /emprestimos
Content-Type: application/json

{ "livroId": 1, "leitorId": 2 }
```

```http
HTTP/1.1 201
Location: http://localhost:8080/emprestimos/2

{
  "id": 2,
  "livroId": 1,
  "titulo": "Código Limpo",
  "leitorId": 2,
  "leitor": "Ana Souza",
  "dataEmprestimo": "2026-11-19",
  "dataPrevistaDevolucao": "2026-11-26",
  "dataDevolucao": null,
  "multa": 0.00
}
```

## Regras de negócio

| Código | Regra | Se violada |
|---|---|---|
| RN01 | O ISBN não pode se repetir entre livros | 409 |
| RN02 | O e-mail não pode se repetir entre leitores | 409 |
| RN03 | Só há empréstimo se o livro tiver exemplar disponível | 409 |
| RN04 | Limite de empréstimos ativos por leitor: ALUNO 3, PROFESSOR 5 | 409 |
| RN05 | Leitor com algum empréstimo atrasado não pode fazer novo empréstimo | 409 |
| RN06 | Prazo de devolução: ALUNO 7 dias, PROFESSOR 14 dias | — |
| RN07 | Multa na devolução = dias de atraso × valor diário (ALUNO R$ 1,00; PROFESSOR R$ 0,50) | — |
| RN08 | Um empréstimo já devolvido não pode ser devolvido de novo | 409 |
| RN09 | Livro ou leitor com empréstimo ativo não pode ser removido | 409 |
| RN10 | Livro, leitor ou empréstimo inexistente | 404 |

Os valores de prazo, limite e multa ficam no `application.yml`:

```yaml
biblioteca:
  politicas:
    aluno:
      prazo-dias: 7
      limite-emprestimos: 3
      multa-diaria: 1.00
    professor:
      prazo-dias: 14
      limite-emprestimos: 5
      multa-diaria: 0.50
```

## Respostas de erro

Todos os erros seguem o formato `ProblemDetail` (RFC 9457), com `Content-Type: application/problem+json`. Erros de validação trazem também o mapa `erros`, com a mensagem de cada campo inválido:

```json
{
  "title": "Erro de validação",
  "status": 400,
  "detail": "Um ou mais campos estão inválidos",
  "instance": "/livros",
  "erros": {
    "isbn": "O ISBN deve ter 13 dígitos",
    "quantidadeTotal": "A quantidade deve ser maior que zero"
  }
}
```

## Collection do Postman

O arquivo `postman/biblioteca.postman_collection.json` traz requisições de sucesso e de erro (400, 404 e 409) para os três recursos, cada uma com um teste do status esperado.

Para usar: no Postman, **Import** → selecione o arquivo. A variável `baseUrl` já vem como `http://localhost:8080`. Execute as requisições na ordem (ou pelo **Run collection**) logo após subir a aplicação, pois elas partem dos dados da carga inicial.

## Estrutura do projeto

```
src/main/java/com/example/biblioteca_api/
├── config/        Clock e carga inicial
├── controller/    endpoints REST
├── dto/           records de entrada (Request) e saída (Response)
├── exception/     exceções de domínio e tratador global de erros
├── model/         entidades Livro, Leitor, Emprestimo e o enum TipoLeitor
├── repository/    interfaces e implementações em memória
└── service/       regras de negócio
    └── politica/  políticas de empréstimo por tipo de leitor (Strategy)
```

## Principais decisões de projeto

- **Camadas:** Controller → Service → Repository. Os controllers só traduzem HTTP e delegam; todas as regras de negócio ficam nos services.
- **Repository como interface:** cada repositório é uma interface com nomes do Spring Data (`findAll`, `findById`, `save`, `deleteById`, `existsBy...`, `countBy...`) e uma implementação em memória com `ConcurrentHashMap` e `AtomicLong`. Assim, ao migrar para banco de dados no Bloco 3, só a implementação do repositório muda.
- **DTOs com `record`:** a API recebe `...Request` e devolve `...Response`; as entidades nunca são expostas. A resposta do empréstimo já traz o título do livro e o nome do leitor.
- **Strategy por tipo de leitor:** a interface `PoliticaEmprestimo` tem uma implementação para ALUNO e outra para PROFESSOR. O Spring injeta todas em uma lista, que o `EmprestimoService` transforma em um `Map<TipoLeitor, PoliticaEmprestimo>` no construtor. Não há `if` nem `switch` sobre o tipo do leitor; um novo tipo exige apenas uma nova classe.
- **Configuração externa:** prazos, limites e multas vêm do `application.yml` via `@Value`.
- **Datas com `Clock`:** um bean `Clock` (fuso `America/Sao_Paulo`) é injetado onde há datas; o código nunca chama `LocalDate.now()` sem argumento, o que deixa as regras de data testáveis.
- **Injeção por construtor:** com campos `final` e `@RequiredArgsConstructor` do Lombok (ou construtor explícito no `EmprestimoService`, que monta o mapa de políticas). Não há `@Autowired` em atributos.
- **Exceções de domínio:** `RecursoNaoEncontradoException` (404) e `ConflitoException` (409), ambas unchecked e filhas de `NegocioException`, tratadas por um único `@RestControllerAdvice` que estende `ResponseEntityExceptionHandler`.
- **Quantidade disponível calculada:** `quantidadeDisponivel` não é armazenada; é calculada como `quantidadeTotal` menos os empréstimos ativos do livro.
- **Atualização de livro:** além do ISBN único, um `PUT` não pode reduzir `quantidadeTotal` abaixo do número de empréstimos ativos do livro (409).
- **Multa com `BigDecimal`:** valores monetários com duas casas decimais, sem erros de arredondamento de `double`.
