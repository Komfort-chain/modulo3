# Módulo 3 — Função Lambda com Kafka (Komfort Chain)

O **Módulo 3** integra a suíte **Komfort Chain** e demonstra a comunicação entre uma **função Lambda** e o **Apache Kafka**.  
A aplicação consome mensagens publicadas em um tópico Kafka e exibe no console:

```
A mensagem chegou: <mensagem>
```

O módulo reforça os conceitos de **mensageria distribuída**, **processamento assíncrono** e **conteinerização de aplicações serverless**.

---

## Badges de Status

[![CI/CD](https://github.com/Komfort-chain/modulo3/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo3/actions/workflows/full-ci.yml)
[![Release](https://github.com/Komfort-chain/modulo3/actions/workflows/release.yml/badge.svg)](https://github.com/Komfort-chain/modulo3/actions/workflows/release.yml)
[![Docker Hub](https://img.shields.io/badge/DockerHub-magyodev/lambda--kafka-blue)](https://hub.docker.com/repository/docker/magyodev/lambda-kafka)
[![SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo3&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo3)
![Java 21](https://img.shields.io/badge/Java-21-red)
![Spring Boot 3.5.7](https://img.shields.io/badge/Spring_Boot-3.5.7-brightgreen)

---

## Tecnologias Utilizadas

| Categoria     | Tecnologia                           |
| ------------- | ------------------------------------ |
| Linguagem     | Java 21                              |
| Framework     | Spring Boot 3.5.7 (Spring Kafka)     |
| Mensageria    | Apache Kafka 7.5.1                   |
| Orquestração  | Docker e Docker Compose              |
| Logs          | Console + Logback                    |
| Build         | Maven (Wrapper)                      |
| Análise Local | SonarQube Community Edition (Docker) |
| Análise Cloud | SonarCloud + OWASP Dependency-Check  |
| Arquitetura   | Clean Architecture / Event-driven    |

---

## Estrutura do Projeto

```
modulo3/
├── docker-compose.yml
├── README.md
├── .github/
│   └── workflows/
│       ├── full-ci.yml
│       └── release.yml
└── lambda-kafka/
    ├── Dockerfile
    ├── pom.xml
    ├── mvnw
    ├── mvnw.cmd
    ├── src/
    │   ├── main/java/com/cabos/lambda_kafka/
    │   │   ├── application/service/KafkaConsumerService.java
    │   │   ├── domain/Message.java
    │   │   ├── infrastructure/config/KafkaConfig.java
    │   │   ├── infrastructure/consumer/KafkaMessageListener.java
    │   │   ├── presentation/handler/GlobalExceptionHandler.java
    │   │   └── LambdaKafkaApplication.java
    │   ├── main/resources/
    │   │   ├── application.yml
    │   │   ├── application.properties
    │   │   ├── logback-spring.xml
    │   └── test/java/com/cabos/lambda_kafka/LambdaKafkaApplicationTests.java
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
- Construção da imagem Docker da função Lambda
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

Resultado esperado:

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

## Pipeline CI/CD

O pipeline automatizado utiliza o **GitHub Actions**, **SonarCloud**, **SonarQube Local** e **Docker Hub**.  
Cada _push_ na branch `main` executa automaticamente:

1. Compilação e testes com Maven Wrapper
2. Análise de qualidade com SonarQube local (modo offline)
3. Análise remota com SonarCloud
4. Verificação de vulnerabilidades via OWASP Dependency-Check
5. Construção e publicação da imagem Docker no Docker Hub

---

## Análise com SonarQube (Local)

Para executar a análise de qualidade de código localmente:

### 1. Subir o container do SonarQube

```bash
docker run -d --name sonarqube   -p 9000:9000   sonarqube:community
```

Acesse no navegador:  
[http://localhost:9000](http://localhost:9000)  
Credenciais padrão:

- Usuário: `admin`
- Senha: `admin`

### 2. Rodar a análise via Maven

```bash
cd lambda-kafka
./mvnw clean verify sonar:sonar   -Dsonar.projectKey=modulo3-lambda-kafka   -Dsonar.host.url=http://localhost:9000   -Dsonar.login=seu_token_local
```

### 3. Gerar relatórios OWASP

```bash
./mvnw org.owasp:dependency-check-maven:check
```

Os relatórios serão gerados em:

```
lambda-kafka/target/dependency-check-report.html
```

---

## Qualidade e Segurança

| Categoria         | Ferramenta / Ação                            | Descrição                                                     |
| ----------------- | -------------------------------------------- | ------------------------------------------------------------- |
| Análise Local     | SonarQube (Community Edition)                | Avaliação de bugs, code smells e duplicações de forma offline |
| Análise Cloud     | [SonarCloud](https://sonarcloud.io)          | Métricas públicas e badges de qualidade                       |
| Segurança         | [OWASP Dependency-Check](https://owasp.org/) | Verificação de vulnerabilidades em dependências Maven         |
| Build Seguro      | Maven + GitHub Actions                       | Ambiente isolado para execução e testes                       |
| Deploy Automático | Docker Hub                                   | Publicação contínua das imagens da suíte Komfort Chain        |

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
│ Kafka Broker  │◄── Produz mensagem
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
