# Módulo 3 — Função Lambda com Kafka (Komfort Chain)

O **Módulo 3** integra a suíte **Komfort Chain** e tem como objetivo demonstrar a comunicação entre uma **função Lambda** e o **Apache Kafka**.  
A aplicação escuta mensagens publicadas em um tópico Kafka e exibe no console:

```
A mensagem chegou: <mensagem>
```

O módulo reforça os conceitos de **mensageria distribuída**, **processamento assíncrono** e **conteinerização de aplicações serverless**.

---

## Badges de Status

[![Docker Publish](https://github.com/Komfort-chain/modulo3/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/Komfort-chain/modulo3/actions/workflows/docker-publish.yml)
[![Docker Hub](https://img.shields.io/badge/DockerHub-magyodev/modulo3--lambda--kafka-blue)](https://hub.docker.com/repository/docker/magyodev/modulo3-lambda-kafka)
[![Java](https://img.shields.io/badge/Java-21-red)]()
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.7-brightgreen)]()

---

## Tecnologias Utilizadas

| Categoria    | Tecnologia                        |
| ------------ | --------------------------------- |
| Linguagem    | Java 21                           |
| Framework    | Spring Boot 3.5.7 (Spring Kafka)  |
| Mensageria   | Apache Kafka 7.5.1                |
| Orquestração | Docker e Docker Compose           |
| Logs         | Console + Docker Logs             |
| Build        | Maven (Wrapper)                   |
| CI/CD        | GitHub Actions + Docker Hub       |
| Arquitetura  | Clean Architecture / Event-driven |

---

## Estrutura do Projeto

```
lambda-kafka/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── src/
│   ├── main/java/com/cabos/lambda_kafka/
│   │   ├── consumer/
│   │   │   └── KafkaMessageListener.java
│   │   ├── application/
│   │   │   └── KafkaConsumerService.java
│   │   ├── config/
│   │   │   └── KafkaConfig.java
│   │   └── LambdaKafkaApplication.java
│   └── resources/
│       └── application.yml
```

Fluxo principal:

```
Kafka Topic → Lambda Consumer → Console Output
```

---

## Execução Local

### 1. Clonar o repositório

```bash
git clone https://github.com/Komfort-chain/modulo3.git
cd modulo3/lambda-kafka
```

### 2. Construir e subir os containers

```bash
docker compose build
docker compose up -d
```

Esses comandos executam:

- Inicialização do Kafka e do Zookeeper
- Construção da imagem da função Lambda
- Conexão automática da Lambda ao tópico Kafka

Verificar os serviços ativos:

```bash
docker ps
```

---

## Serviços Disponíveis

| Serviço          | Porta | Descrição                                   |
| ---------------- | ----- | ------------------------------------------- |
| Zookeeper        | 2181  | Coordenação do Kafka                        |
| Kafka Broker     | 9092  | Servidor de mensageria                      |
| Lambda Kafka App | —     | Consumidor de mensagens (sem endpoint HTTP) |

---

## Teste da Função

### 1. Enviar mensagem via Kafka CLI

```bash
docker exec -it kafka kafka-console-producer.sh   --broker-list localhost:9092 --topic meu-topico
```

Digite qualquer mensagem:

```
Mensagem de teste do módulo 3
```

Visualize a saída no log:

```bash
docker logs -f lambda-kafka
```

Resultado:

```
A mensagem chegou: Mensagem de teste do módulo 3
```

---

### 2. Teste via REST Proxy (opcional)

**POST** `http://localhost:8082/topics/meu-topico`  
**Header:** `Content-Type: application/vnd.kafka.json.v2+json`  
**Body:**

```json
{
  "records": [{ "value": "Mensagem publicada via REST Proxy" }]
}
```

Saída esperada:

```
A mensagem chegou: Mensagem publicada via REST Proxy
```

---

## Deploy Automatizado (CI/CD)

O pipeline de CI/CD utiliza o **GitHub Actions** e o **Docker Hub**.  
Cada _push_ na branch `main` executa automaticamente:

1. Compilação da aplicação com o Maven Wrapper
2. Construção da imagem Docker
3. Publicação da imagem no Docker Hub

### Arquivo de workflow (.github/workflows/docker-publish.yml)

```yaml
name: Lambda Kafka - Docker Publish

on:
  push:
    branches: ["main"]
  workflow_dispatch:

jobs:
  build-and-push:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21

      - name: Build project with Maven
        run: |
          cd lambda-kafka
          chmod +x mvnw
          ./mvnw clean package -DskipTests

      - uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_PASSWORD }}

      - uses: docker/build-push-action@v5
        with:
          context: ./lambda-kafka
          file: ./lambda-kafka/Dockerfile
          push: true
          tags: |
            magyodev/modulo3-lambda-kafka:latest
            magyodev/modulo3-lambda-kafka:${{ github.sha }}
```

Imagem disponível em:  
👉 [Docker Hub — magyodev/modulo3-lambda-kafka](https://hub.docker.com/repository/docker/magyodev/modulo3-lambda-kafka)

---

## Logs e Monitoramento

Visualizar logs em tempo real:

```bash
docker logs -f lambda-kafka
```

Cada mensagem consumida do tópico será exibida no console:

```
A mensagem chegou: <conteúdo da mensagem>
```

---

## Estrutura de Mensagens

**Tópico:** `meu-topico`  
**Formato:** Texto simples (String)

Exemplo:

```
"Nova mensagem enviada para a Lambda"
```

---

## Diagrama Simplificado

```
┌───────────────┐
│ Kafka Broker  │◄─── Produz mensagem
└──────┬────────┘
       │
       ▼
┌───────────────────────────────────┐
│ Lambda Kafka Consumer             │
│ Exibe: "A mensagem chegou: <msg>" │
└───────────────────────────────────┘
```

---

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch (`feature/minha-melhoria`)
3. Faça o commit das alterações
4. Envie um Pull Request

---

## Autor

**Alan de Lima Silva (MagyoDev)**

- GitHub: [https://github.com/MagyoDev](https://github.com/MagyoDev)
- Docker Hub: [https://hub.docker.com/u/magyodev](https://hub.docker.com/u/magyodev)
- E-mail: [magyodev@gmail.com](mailto:magyodev@gmail.com)
