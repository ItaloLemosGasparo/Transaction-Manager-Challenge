# Transaction Manager

Aplicação para armazenar e recuperar transações de compra, com suporte para conversão de moeda utilizando taxas de
câmbio.

## Descrição Geral

Este projeto é parte de um desafio técnico e implementa os seguintes requisitos:

1. **Armazenar uma Transação**:
    - Descrição (máximo de 50 caracteres).
    - Data da transação (formato válido).
    - Valor da compra em dólares (positivo e arredondado para dois centavos).
    - Identificador único.

2. **Recuperar uma Transação Convertida**:
    - Conversão do valor da transação para moedas suportadas usando a API de Taxas de Câmbio
      do [Treasury Reporting Rates](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange).

## Tecnologias Utilizadas

- **Java 23**
- **Spring Boot 3.4.1**
- **H2 Database**
- **Lombok**
- **Spring Data JPA**: Para interagir com o banco de dados de forma simplificada.
- **Hibernate Validator (Jakarta Validation)**: Para validação de campos.
- **Jackson Databind**: Para serialização e desserialização de JSON.

## Estrutura do Projeto

```plaintext
src/
├── main/
│   ├── java/com/vrsoftware/checkout/transaction_manager/
│   │   ├── aop/            # Aspectos
│   │   ├── config/         # Configurações da aplicação
│   │   ├── controller/     # Endpoints REST
│   │   ├── dto/            # Objetos de transferência de dados
│   │   ├── exceptions/     # Tratamento de erros
│   │   ├── mapper/         # Conversões entre DTO e Model
│   │   ├── model/          # Entidades do sistema
│   │   ├── repository/     # Interação com o banco de dados
│   │   ├── service/        # "Lógica"
│   │   ├── utils/          # Funções auxiliares
│   │   └── validator/      # Validações
└── test/
    ├── unit/               # Testes unitários
    └── integration/        # Testes de integração
