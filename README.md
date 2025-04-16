# Desafio Engenharia de Software - Itaú

- Autor: John Enderson
- Release Data: 04/2025
- Linguagem: Java
- Framework: Spring boot
- Status: Em desenvolvimento

## O Desafio

### Requisitos
- Realizar a integração com o Sistema de Pagamentos PIX;
- Criar uma arquitetura que permita a realização do PIX;
- Precisamos armazenar todas as informações que serão comunicadas com o Banco Central;
- Será necessario disponibilizar essa funcionalidade para um Front-end.

### Regras para realização do PIX
1. Realizar uma chamada para a API de Fraudes, onde o retorno poderá ser positivo ou negativo;
2. Comunicar com o banco Central atavés de MQ (Message Queue)

## 🚀 Tecnologias utilizadas
- Java 21
- Maven
- LocalStack (Para simulação do Amazon SQS)
- Mockoon (Para mock das APIS de Fraude e de PIX)
- MongoDB
- Framework Spring:
  - Spring Boot 3.4.4
  - Spring Cloud OpenFeign
  - Spring Boot Docker Compose (apenas no profile dev)
  - Spring Validation
  - Tomcat (Integrado ao Spring Boot)

## 📌 Arquitetura desenvolvida

Essa arquitetura foi desenvolvida para simular o fluxo de pagamento via PIX utilizando serviços da AWS, com APIs REST, filas SQS e persistência de dados. Para facilitar os testes, alguns componentes foram mockados, como a API anti-fraude e a integração com o Banco Central.

![img.png](architecture/img.png)

## 🔧 Como executar o projeto

1. Clone o repositório:
```bash
git clone git@github.com:johnenderson/challenge-itau.git
cd challenge-itau
```
2. Suba os containers Docker:

Antes de iniciar as aplicações Spring Boot, execute o seguinte comando na raiz do repositório:
```declarative
docker compose up
```
Esse comando iniciará os seguintes serviços:
- Um container com **MongoDB**, disponível na porta **27017**
- Um container com **Mockoon**, disponível na porta **9090**
- Um container com **LocalStack**, disponível na porta **4566**

3. Execute as aplicações Spring Boot:

```bash
# Perfil padrão (Default)
mvn spring-boot:run

# Com perfil específico. Ex.: Dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
As aplicações Spring Boot serão iniciadas nas seguintes portas:
- `pixms`: **8080**
- `pix-sub`: **8090**
- `pix-confirm`: **9000**

## 💡 Highlights do desafio

1. **Uso do Docker para simulação de serviços** \
O projeto utiliza Docker para simular os seguintes serviços:
   - AWS SQS (via LocalStack), representando o serviço de mensageria da AWS.
   - Mockoon, para simulação de APIs externas que exigem autenticação.
   - MongoDB, como banco de dados NoSQL.
2. **Arquitetura pensada para a AWS** \
A solução foi inicialmente desenhada com foco na nuvem AWS, considerando o uso de serviços como SQS e outras soluções gerenciadas.
3. **Tratamento de regras de negócio com Problem Details** (`RFC 7807`) \
O serviço `pixms` utiliza o padrão Problem Details para retornar respostas padronizadas em casos de violação de regras de negócio.
4. **Interceptação de exceções com `RestControllerAdvice`** \
Foi implementado um interceptor global de exceções utilizando a anotação `@RestControllerAdvice`. Esse componente captura exceções lançadas nos controladores e as transforma em respostas amigáveis e padronizadas para o cliente da API.
5. **APIs mockadas com autenticação** \
As APIs simuladas com o Mockoon exigem autenticação, permitindo testar fluxos de segurança e validação mesmo em ambientes de desenvolvimento.
6. **Proposta de melhoria na arquitetura da solução** \
   1. Visando maior robustez e escalabilidade, propõe-se a adoção de padrões de resiliência, como:
   - Circuit Breaker: evita sobrecarga em serviços externos ao interromper chamadas temporariamente quando há falhas repetidas.
   - Retry: permite a repetição automática de chamadas em caso de falhas temporárias, com controle de tentativas e tempo de espera entre elas.
7. **Uso de Conventional Commits** \
O projeto adota o padrão Conventional Commits para padronizar as mensagens de commit.

## Referencias
 - [Manual de Padrões para Iniciaçãp do Pix](https://www.bcb.gov.br/content/estabilidadefinanceira/pix/Regulamento_Pix/II_ManualdePadroesparaIniciacaodoPix.pdf)
 - [API-DICT](https://www.bcb.gov.br/content/estabilidadefinanceira/pix/API-DICT.html)