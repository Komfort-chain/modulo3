# Módulo 3 – Lambda Kafka Consumer (Komfort Chain)

Este repositório contém o **Módulo 3** do projeto final, focado em um **consumer de mensagens Kafka** implementado com **Spring Boot 3** e preparado para execução em **container Docker**, orquestrado via **Docker Compose** e integrado a um pipeline de **CI/CD com SonarCloud e OWASP Dependency Check**.

A aplicação simula um *Lambda Consumer* (processo enxuto, focado apenas em reagir a mensagens), lendo mensagens de um **tópico Kafka** e registrando-as em log.

---

## 1. Visão Geral da Solução

- **Objetivo**: 
  - Consumir mensagens de um tópico Kafka (`lambda-topic`) e processá-las de forma simples, registrando-as em log.
- **Stack principal**:
  - Java 21 + Spring Boot 3
  - Spring for Apache Kafka
  - Docker + Docker Compose
  - GitHub Actions (CI/CD com SonarCloud e OWASP)
- **Estilo “lambda”**:
  - Aplicação enxuta, com foco em **start → consumir mensagens → finalizar**, podendo ser usada como base para:
    - Execução contínua (consumer tradicional); ou
    - Empacotamento como imagem/container em ambientes serverless/orquestrados.

---

## 2. Arquitetura da Solução

Arquitetura em 3 camadas principais:

1. **Infraestrutura de Mensageria (Docker Compose)**  
   - `zookeeper`: gerencia metadados do cluster Kafka.  
   - `kafka`: broker responsável pelo tópico `lambda-topic`.  

2. **Aplicação Lambda Consumer (Spring Boot)**  
   - `LambdaConsumerApplication`: ponto de entrada da aplicação.  
   - `KafkaMessageListener`: componente que fica escutando o tópico Kafka e processando as mensagens.  

3. **Configuração Externa**  
   - `application.yml`: configurações da aplicação e do cliente Kafka (bootstrap servers, group-id, deserializers, tópico etc).

Essa separação permite evoluir o processamento de mensagens sem acoplar lógica de negócio à infraestrutura de mensageria.

---

## 3. Tecnologias Utilizadas

- **Linguagem / Framework**
  - Java 21  
  - Spring Boot 3.x  
  - Spring Kafka  

- **Mensageria**
  - Apache Kafka (via imagem `confluentinc/cp-kafka:7.5.0`)  
  - Zookeeper (para controle do broker Kafka)

- **Containerização**
  - Docker  
  - Docker Compose  

- **Qualidade e Segurança**
  - JUnit + Spring Kafka Test  
  - SonarCloud (análise estática, cobertura, code smells, bugs)  
  - OWASP Dependency Check (varredura de vulnerabilidades em dependências)

- **CI/CD**
  - GitHub Actions (`.github/workflows/full-ci.yml` e `release.yml`)

---

## 4. Estrutura de Pastas

Estrutura geral do repositório:

```text
modulo3/
├── docker-compose.yml              # Sobe Zookeeper, Kafka e o lambda-consumer
├── Dockerfile                      # Dockerfile para empacotar a aplicação
├── mvnw, mvnw.cmd, pom.xml         # Wrapper Maven e POM principal
├── README.md                       # Este arquivo
├── repositorio_completo.txt        # Dump de estrutura + conteúdo (apoio)
├── .github/
│   └── workflows/
│       ├── full-ci.yml             # Pipeline de build, testes, Sonar e OWASP
│       └── release.yml             # Pipeline de criação de Release no GitHub
├── .mvn/
│   └── wrapper/                    # Arquivos do Maven Wrapper
│       ├── maven-wrapper.jar
│       ├── maven-wrapper.properties
│       └── MavenWrapperDownloader.java
├── lambda-kafka/
│   └── target/                     # Artefatos gerados em build (usado no CI)
│       └── ...                    
├── src/
│   └── main/
│       ├── java/
│       │   └── com/cabos/komfortchain/mod3/
│       │       ├── LambdaConsumerApplication.java  # Ponto de entrada
│       │       └── kafka/
│       │           └── KafkaMessageListener.java   # Listener do tópico Kafka
│       └── resources/
│           └── application.yml     # Configuração da aplicação/Kafka
└── target/
    └── ...                         # Artefatos compilados (JAR, classes etc.)
````

### 4.1 Código Java

* `LambdaConsumerApplication.java`

  * Classe principal anotada com `@SpringBootApplication`.
  * Responsável por inicializar o contexto Spring e registrar os beans, incluindo o listener Kafka.

* `kafka/KafkaMessageListener.java`

  * Classe anotada com `@Component`.
  * Método `onMessage(...)` anotado com `@KafkaListener`, ligado ao tópico `lambda-topic`.
  * Responsável por:

    * Extrair o payload da mensagem do `ConsumerRecord`.
    * Montar uma mensagem de log amigável.
    * Imprimir no `System.out` e registrar via `Logger` (`slf4j`).

### 4.2 Configuração (`application.yml`)

```yaml
spring:
  application:
    name: lambda-consumer
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:kafka:9092}
    consumer:
      group-id: lambda-consumer-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer

app:
  kafka:
    topic: lambda-topic
```

* `KAFKA_BOOTSTRAP_SERVERS` parametrizado via variável de ambiente (com default `kafka:9092`).
* `group-id` configurado para `lambda-consumer-group`.
* Tópico parametrizado em `app.kafka.topic`, usado no `@KafkaListener`.

---

## 5. Clean Architecture e SOLID

Mesmo sendo um módulo enxuto, o desenho segue os princípios vistos em aula:

### 5.1 Clean Architecture (na prática deste módulo)

* **Separação de camadas**:

  * **Camada de Aplicação / Entrada**:

    * `LambdaConsumerApplication` → inicialização da aplicação, sem regra de negócio.
  * **Camada de Interface com o Mundo Externo (Adapter)**:

    * `KafkaMessageListener` → ponto de contato com o Kafka (entrada de dados).
  * **Camada de Configuração / Infraestrutura**:

    * `application.yml` → externaliza endpoints, tópico, group-id e bootstrap servers.
    * `docker-compose.yml` e `Dockerfile` → sobem a infraestrutura e empacotam a aplicação.

* **Isolamento da lógica de consumo**:

  * Toda interação com Kafka está concentrada em `KafkaMessageListener`.
  * A lógica de processamento da mensagem (ainda simples) pode ser extraída para serviços de domínio posteriormente, sem alterar a infraestrutura.

### 5.2 Princípios SOLID

* **S – Single Responsibility Principle**

  * `LambdaConsumerApplication`: apenas inicializa o contexto Spring Boot.
  * `KafkaMessageListener`: apenas escuta e registra mensagens do tópico.
  * `docker-compose.yml` / `Dockerfile`: apenas definem infraestrutura.

* **O – Open/Closed Principle**

  * Novos comportamentos podem ser adicionados:

    * Novos listeners em outros tópicos.
    * Encaminhamento das mensagens para serviços de domínio, sem modificar o listener atual.

* **L – Liskov Substitution Principle**

  * Não há hierarquias de herança complexas neste módulo.
  * Porém, o uso de interfaces do Spring/Kafka já favorece a substituição futura por implementações mais específicas, se necessário.

* **I – Interface Segregation Principle**

  * A aplicação depende de contratos específicos (interfaces do Spring Kafka) em vez de grandes interfaces genéricas.

* **D – Dependency Inversion Principle**

  * O listener depende de abstrações fornecidas pelo Spring Kafka (`@KafkaListener`) e não da implementação direta do consumidor da API Kafka.
  * Configurações e endpoints são externalizados em `application.yml` e variáveis de ambiente, reduzindo acoplamento.

---

## 6. Execução do Projeto

### 6.1 Pré-requisitos

* Docker e Docker Compose instalados
* (Opcional para execução local sem Docker)

  * JDK 21
  * Maven (ou `mvnw`)

### 6.2 Executando com Docker Compose

Na raiz do projeto (`modulo3/`):

```bash
docker compose up -d --build
```

Isso irá subir:

* `zookeeper` na porta `2181`
* `kafka` na porta `9092`
* `lambda-consumer` com a aplicação Spring Boot

A aplicação começará a escutar o tópico configurado em `app.kafka.topic` (por padrão: `lambda-topic`).

Para ver os logs do consumer:

```bash
docker logs -f lambda_consumer
```

### 6.3 Executando Localmente (sem Docker)

1. Suba um cluster Kafka/Zookeeper local (pode ser via outro Docker Compose ou instalação local).
2. Ajuste `application.yml` ou a variável `KAFKA_BOOTSTRAP_SERVERS` para apontar para o seu broker.
3. Na raiz do projeto:

```bash
./mvnw spring-boot:run
# ou
mvn spring-boot:run
```

---

## 7. Enviando Mensagens para o Tópico

Com o ambiente do `docker-compose.yml` rodando, você pode produzir mensagens para o tópico `lambda-topic` de diversas formas.

Exemplo usando o console producer do Kafka dentro do container:

```bash
docker exec -it kafka bash

# Dentro do container:
kafka-console-producer \
  --broker-list kafka:9092 \
  --topic lambda-topic
```

Digite mensagens e pressione Enter.
No `lambda-consumer`, você verá logs no padrão:

```text
A mensagem chegou: <sua mensagem>
```

---

## 8. Qualidade de Código e CI/CD

### 8.1 Pipeline de CI – `full-ci.yml`

O workflow `Komfort Chain - CI/CD (Lambda Kafka)` executa:

1. **Build e Testes**

   * Sobe serviços de `zookeeper` e `kafka` no ambiente do GitHub Actions.
   * Executa `mvn clean verify` (sem pular testes).

2. **Análise SonarCloud**

   * Envia métricas de:

     * Cobertura de testes
     * Bugs, vulnerabilidades e code smells
     * Qualidade do código Java

3. **OWASP Dependency Check**

   * Gera relatórios sobre vulnerabilidades conhecidas nas dependências Maven.

4. **Docker Build & Push**

   * Empacota a aplicação (`lambda-kafka`) em uma imagem Docker.
   * Faz push da imagem para o Docker Hub, usando tags:

     * `latest`
     * `${{ github.run_number }}`

### 8.2 Workflow de Release – `release.yml`

* Disparado em `push` de tags do tipo `v*.*.*`.
* Passos:

  * Compila o artefato JAR com Maven.
  * Gera changelog automaticamente (`release_notes.md`).
  * Cria um **GitHub Release** com o artefato `lambda-kafka-*.jar` anexado.


