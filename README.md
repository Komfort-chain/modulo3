# **Módulo 3 — Lambda Kafka Consumer (Komfort Chain)**

O **Módulo 3** da suíte **Komfort Chain** implementa um serviço especializado em **consumo assíncrono de mensagens**, utilizando Apache Kafka como barramento de eventos.
O módulo foi estruturado para representar um componente de arquitetura orientada a eventos, funcionando como um consumidor independente, resiliente e de execução contínua.

Seguindo os mesmos padrões adotados nos módulos anteriores, este serviço utiliza princípios de **Clean Architecture**, **SOLID**, além de pipelines padronizados de **CI/CD**, análise estática com **SonarCloud**, verificação de vulnerabilidades com **OWASP Dependency-Check** e empacotamento com **Docker**.

---

## **Status do Projeto**

[![Full CI/CD](https://github.com/Komfort-chain/modulo3/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo3/actions/workflows/full-ci.yml)
[![Release](https://github.com/Komfort-chain/modulo3/actions/workflows/release.yml/badge.svg)](https://github.com/Komfort-chain/modulo3/actions/workflows/release.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo3\&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo3)
[![Maintainability](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo3\&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo3)
[![Docker Hub](https://img.shields.io/badge/DockerHub-magyodev/lambda--kafka-blue)](https://hub.docker.com/repository/docker/magyodev/lambda-kafka)

---

## **Tecnologias Utilizadas**

| Categoria        | Tecnologia / Ferramenta             |
| ---------------- | ----------------------------------- |
| Linguagem        | Java 21                             |
| Framework        | Spring Boot 3.5.7 + Spring Kafka    |
| Mensageria       | Apache Kafka 7.6 + Zookeeper 3.9    |
| Logs             | Logback                             |
| Build            | Maven Wrapper (mvnw)                |
| Testes           | JUnit 5 + Spring Boot Test          |
| Análise Estática | SonarCloud + OWASP Dependency Check |
| Containerização  | Docker e Docker Compose             |
| Arquitetura      | Event-Driven • Clean Architecture   |

---

## **Arquitetura**

O módulo segue o mesmo padrão arquitetural adotado no restante da suíte, organizando responsabilidades de forma clara entre camadas.

O fluxo de funcionamento é simples:

```
Produtor → Kafka Topic → Lambda Kafka Consumer
```

O serviço permanece em execução contínua, escutando o tópico configurado e processando eventos recebidos.

---

## **Organização das Pastas e Justificativa da Estrutura**

A estrutura segue exatamente a mesma divisão dos módulos 1 e 2, garantindo consistência e previsibilidade.

### **1. Raiz do módulo (`modulo3/`)**

Contém arquivos relacionados à infraestrutura geral:

* `docker-compose.yml`
* `.github/workflows/`
* `README.md`

Esses arquivos definem a orquestração de contêineres e os pipelines de automação.

---

### **2. Diretório principal da aplicação (`lambda-kafka/`)**

Contém o código da aplicação Java, organizado em camadas:

```
lambda-kafka/
├── application/
│   └── service/
├── domain/
├── infrastructure/
│   ├── config/
│   └── consumer/
└── presentation/
    └── handler/
```

#### **application/**

Contém serviços relacionados ao processamento de mensagens.

#### **domain/**

Modela a estrutura dos dados consumidos pela aplicação.

#### **infrastructure/**

Abriga configurações do Kafka e o listener responsável por receber eventos.

#### **presentation/**

Centraliza tratadores globais de exceções, padronizando respostas.

#### **resources/**

Contém arquivos de configuração (`application.yml`, logs etc.).

---

## **Estrutura Completa do Projeto**

```bash
modulo3/
├── docker-compose.yml
├── README.md
│
├── .github/workflows/
│   ├── full-ci.yml
│   └── release.yml
│
└── lambda-kafka/
    ├── pom.xml
    ├── mvnw / mvnw.cmd
    ├── Dockerfile
    │
    ├── src/main/java/com/cabos/lambda_kafka/
    │   ├── LambdaKafkaApplication.java
    │   ├── application/service/KafkaConsumerService.java
    │   ├── domain/Message.java
    │   ├── infrastructure/config/KafkaConfig.java
    │   ├── infrastructure/consumer/KafkaMessageListener.java
    │   └── presentation/handler/GlobalExceptionHandler.java
    │
    └── src/main/resources/
        ├── application.yml
        ├── application.properties
        └── logback-spring.xml
```

---

## **Execução Local**

### **1. Clonar o repositório**

```bash
git clone https://github.com/Komfort-chain/modulo3.git
cd modulo3
```

### **2. Subir o ambiente**

```bash
docker compose up --build -d
```

### **Serviços Disponíveis**

| Serviço      | Porta | Descrição               |
| ------------ | ----- | ----------------------- |
| Kafka        | 9092  | Barramento de eventos   |
| Zookeeper    | 2181  | Coordenação do Kafka    |
| Lambda Kafka | —     | Consumidor de mensagens |

---

## **Testando o Consumidor**

### **1. Enviar mensagem ao tópico**

```bash
docker exec -it kafka \
  kafka-console-producer.sh --broker-list localhost:9092 --topic mensagens
```

Digite:

```
Olá, Kafka!
```

### **2. Ver logs**

```bash
docker logs -f lambda-kafka
```

Saída esperada:

```
A mensagem chegou: Olá, Kafka!
```

---

## **Pipeline CI/CD**

A aplicação segue os mesmos padrões dos módulos anteriores.

### **Workflow Principal — `full-ci.yml`**

Responsável por:

1. Compilação e testes com Kafka real via containers
2. Análise estática no SonarCloud
3. Verificação de vulnerabilidades via OWASP
4. Geração de relatórios
5. Build e push automático para o Docker Hub

### **Workflow de Release — `release.yml`**

Executado quando uma tag `vX.Y.Z` é criada.

Realiza:

* build do projeto
* geração do changelog
* criação da release no GitHub
* publicação da imagem Docker versionada

---

## **Imagem Docker Oficial**

| Serviço      | Docker Hub                                                                                                                       |
| ------------ | -------------------------------------------------------------------------------------------------------------------------------- |
| Lambda Kafka | [https://hub.docker.com/repository/docker/magyodev/lambda-kafka](https://hub.docker.com/repository/docker/magyodev/lambda-kafka) |

Tags:

* `latest`
* `${run_number}`
* `vX.Y.Z`

---

## **Logs e Monitoramento**

O serviço utiliza log estruturado com Logback, seguindo o mesmo padrão dos demais módulos.
Todos os eventos recebidos são registrados para facilitar rastreamento e auditoria.

---

## **Contribuição**

1. Faça um fork do repositório
2. Crie uma branch: `feature/nova-funcionalidade`
3. Utilize commits semânticos
4. Abra um Pull Request para `main`

---

## **Autor**

**Alan de Lima Silva (MagyoDev)**

* GitHub: [https://github.com/MagyoDev](https://github.com/MagyoDev)
* Docker Hub: [https://hub.docker.com/u/magyodev](https://hub.docker.com/u/magyodev)
* E-mail: [magyodev@gmail.com](mailto:magyodev@gmail.com)
