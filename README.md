# **Módulo 3 — Lambda Consumer com Apache Kafka (Komfort Chain)**

O **Módulo 3** da suíte **Komfort Chain** demonstra um cenário real de **consumo assíncrono de eventos**, utilizando uma aplicação estilo **Lambda** (serverless containerizada) integrada ao **Apache Kafka**.

A aplicação conecta-se a um tópico Kafka, permanece em execução contínua e registra no console todas as mensagens recebidas, reforçando conceitos essenciais de **event-driven architecture**, **mensageria distribuída**, **resiliência** e **observabilidade**.

---

## **Status do Projeto**

[![Full CI/CD](https://github.com/Komfort-chain/modulo3/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo3/actions/workflows/full-ci.yml)
[![Release](https://github.com/Komfort-chain/modulo3/actions/workflows/release.yml/badge.svg)](https://github.com/Komfort-chain/modulo3/actions/workflows/release.yml)
[![Docker Hub](https://img.shields.io/badge/DockerHub-magyodev/modulo3--lambda--kafka-blue)](https://hub.docker.com/repository/docker/magyodev/modulo3-lambda-kafka)
[![SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo3&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo3)
![Java 21](https://img.shields.io/badge/Java-21-red)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.7-brightgreen)

---

## **Tecnologias Utilizadas**

| Categoria            | Ferramenta / Tecnologia                   |
| -------------------- | ----------------------------------------- |
| **Linguagem**        | Java 21                                   |
| **Framework**        | Spring Boot 3.5.7 + Spring Kafka          |
| **Mensageria**       | Apache Kafka 7.6.0 + Zookeeper            |
| **Log**              | Logback + Console                         |
| **Testes**           | JUnit 5 + Spring Boot Test                |
| **Análise Estática** | SonarCloud + OWASP Dependency-Check       |
| **Containerização**  | Docker + Docker Compose                   |
| **Arquitetura**      | Event-Driven • Clean Architecture • SOLID |

---

## **Arquitetura**

Este módulo implementa um único serviço responsável por **consumir eventos de um tópico Kafka**, agindo como um *Lambda Listener*.

### **Fluxo Arquitetural**

```
Produtor (qualquer origem)
              │
              ▼
       ┌──────────────┐
       │  Kafka Topic │
       └───────┬──────┘
               │
               ▼
 ┌────────────────────────────┐
 │ Lambda Kafka Consumer      │
 │ Logs: "A mensagem chegou"  │
 └────────────────────────────┘
```

A aplicação inicia automaticamente, se conecta ao broker e fica aguardando novos eventos.

---

## **Estrutura do Projeto**

```
modulo3/
├── docker-compose.yml
├── README.md
├── .github/
│   └── workflows/
│       ├── full-ci.yml        # CI completo (Kafka Test, SonarCloud, OWASP)
│       └── release.yml        # Criação de Releases + Docker Hub
└── lambda-kafka/
    ├── Dockerfile
    ├── pom.xml
    ├── mvnw / mvnw.cmd
    ├── src/
    │   ├── main/java/com/cabos/lambda_kafka/
    │   │   ├── application/service/KafkaConsumerService.java
    │   │   ├── infrastructure/config/KafkaConfig.java
    │   │   ├── infrastructure/consumer/KafkaMessageListener.java
    │   │   ├── presentation/handler/GlobalExceptionHandler.java
    │   │   └── LambdaKafkaApplication.java
    │   ├── main/resources/
    │   │   ├── application.yml
    │   │   ├── application.properties
    │   │   ├── logback-spring.xml
    │   └── test/java/com/cabos/lambda_kafka/
    │       └── LambdaKafkaApplicationTests.java
```

---

## **Execução Local**

### **1. Clonar o repositório**

```bash
git clone https://github.com/Komfort-chain/modulo3.git
cd modulo3
```

### **2. Construir e subir os containers**

```bash
docker compose build
docker compose up -d
```

### **Serviços iniciados**

| Serviço          | Porta | Descrição               |
| ---------------- | ----- | ----------------------- |
| Kafka Broker     | 9092  | Broker principal        |
| Zookeeper        | 2181  | Coordenação do Kafka    |
| Lambda Kafka App | —     | Consumidor de mensagens |

Verificar os serviços ativos:

```bash
docker ps
```

---

## **Testes**

### **1. Enviar mensagens com Kafka CLI**

Abra um terminal:

```bash
docker exec -it kafka \
  kafka-console-producer.sh --broker-list localhost:9092 --topic meu-topico
```

Digite qualquer mensagem:

```
Olá módulo 3!
```

### **2. Visualizar logs do consumidor**

```bash
docker logs -f lambda-kafka
```

Saída esperada:

```
A mensagem chegou: Olá módulo 3!
```

---

## **Workflows CI/CD**

O projeto possui **dois workflows principais**:

---

### **1. full-ci.yml — Pipeline Principal**

Executa automaticamente em cada push ou pull request:

| Etapa                      | Descrição                                                                                 |
| -------------------------- | ----------------------------------------------------------------------------------------- |
| **Build & Tests**          | Compila o Lambda Kafka e executa testes unitários e de integração (Kafka container real). |
| **SonarCloud**             | Aplica análise estática, code smells, bugs e duplicações.                                 |
| **OWASP Dependency-Check** | Varredura de vulnerabilidades com fallback offline.                                       |
| **Upload Reports**         | Exporta Jacoco e Surefire Reports como artefatos.                                         |
| **Docker Build & Push**    | Gera imagem da Lambda Kafka e publica tags `latest` e `run_number` no Docker Hub.         |

---

### **2. release.yml — Automação de Release**

Gatilhos:

* Criação de Tag
* Criação de Release no GitHub

Ações:

* Build completo do projeto
* Build & Push exclusivo com a tag da release
* Publicação no Docker Hub com semver (`v1.0.0`)

---

## **Imagem Docker Oficial**

A imagem da Lambda Kafka é publicada automaticamente no Docker Hub:

| Componente            | Imagem                          |
| --------------------- | ------------------------------- |
| Lambda Kafka Consumer | `magyodev/modulo3-lambda-kafka` |

Tags disponíveis:

* `latest`
* `${run_number}` (CI/CD)
* `vX.Y.Z` (Releases)

---

## **Observabilidade**

Visualização de logs:

```bash
docker logs -f lambda-kafka
```

O serviço utiliza **Logback**, com configuração padronizada para:

* Log estruturado
* Timestamp
* Contexto da execução
* Tratamento de exceções (GlobalExceptionHandler)

---

## **Contribuição**

1. Faça um fork do repositório
2. Crie sua branch:

   ```
   feature/nova-melhoria
   ```
3. Commit semântico
4. Abra um Pull Request para `main`

---

## **Autor**

**Alan de Lima Silva (MagyoDev)**
- GitHub: [https://github.com/MagyoDev](https://github.com/MagyoDev)
- Docker Hub: [https://hub.docker.com/u/magyodev](https://hub.docker.com/u/magyodev)
- E-mail: [magyodev@gmail.com](mailto:magyodev@gmail.com)
