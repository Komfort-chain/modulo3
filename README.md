# **Módulo 3 — Lambda Kafka Consumer (Komfort Chain)**

O **Módulo 3** da suíte **Komfort Chain** representa um serviço assíncrono especializado em **consumo contínuo de mensagens Kafka**.
Ele simula o comportamento de uma *lambda function* orientada a eventos: um processo leve, independente e sempre pronto para reagir a novos registros publicados em um tópico.

Esse módulo reforça o padrão de **arquitetura orientada a eventos (Event-Driven Architecture)** dentro da suíte Komfort Chain, mantendo os princípios de **Clean Architecture**, **SOLID**, testabilidade, padronização de camadas e integração completa com pipelines de **CI/CD**, **SonarCloud**, **OWASP Dependency-Check** e Docker.

---

## **Status do Projeto**

[![Full CI/CD](https://github.com/Komfort-chain/modulo3/actions/workflows/full-ci.yml/badge.svg)](https://github.com/Komfort-chain/modulo3/actions/workflows/full-ci.yml)
[![Release](https://github.com/Komfort-chain/modulo3/actions/workflows/release.yml/badge.svg)](https://github.com/Komfort-chain/modulo3/actions/workflows/release.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo3\&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo3)
[![Maintainability](https://sonarcloud.io/api/project_badges/measure?project=Komfort-chain_modulo3\&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Komfort-chain_modulo3)
[![Docker Hub](https://img.shields.io/badge/DockerHub-magyodev%2Flambda--kafka-blue)](https://hub.docker.com/r/magyodev/lambda-kafka)

---

## **Tecnologias Utilizadas**

| Categoria        | Tecnologia / Ferramenta             |
| ---------------- | ----------------------------------- |
| Linguagem        | Java 21                             |
| Framework        | Spring Boot 3.5.7 + Spring Kafka    |
| Mensageria       | Apache Kafka + Zookeeper            |
| Logs             | Logback                             |
| Testes           | JUnit 5 + Spring Boot Test          |
| Análise Estática | SonarCloud + OWASP Dependency-Check |
| Build            | Maven Wrapper (mvnw)                |
| Containerização  | Docker e Docker Compose             |
| Arquitetura      | Event-Driven • Clean Architecture   |

---

## **Arquitetura Geral**

Este módulo trabalha como um consumidor isolado de mensagens Kafka.
O fluxo operacional é:

```text
Produtor → Tópico Kafka → Lambda Kafka Consumer
```

O serviço:

* sobe como um processo persistente;
* conecta-se ao tópico configurado;
* aguarda eventos de forma contínua;
* registra os dados recebidos nos logs.

Esse modelo segue exatamente a ideia de uma *lambda de consumo* — simples, independente, reativa e de baixa complexidade.

---

## **Relação com Clean Architecture**

O serviço mantém a mesma divisão em camadas utilizada nos módulos 1 e 2:

* **presentation** → interface com o mundo externo (tratamento de erros)
* **application** → coordena o processamento de mensagens
* **domain** → modelos representando os dados recebidos
* **infrastructure** → configurações técnicas e listener Kafka

Essa separação facilita:

* testes;
* substituição de componentes (por exemplo, outro barramento de eventos);
* isolamento do domínio;
* manutenção simples e previsível.

---

## **Relação com SOLID**

* **SRP**: cada classe tem apenas uma responsabilidade clara.
* **OCP**: novos listeners, tópicos ou formas de processamento podem ser adicionados sem alterar classes já existentes.
* **DIP**: o serviço depende de abstrações e não de detalhes técnicos.
* **ISP**: cada componente usa apenas o necessário das classes externas.
* **LSP**: substituição de implementações continua válida entre camadas.

---

## **Organização da Estrutura de Pastas**

A estrutura é padronizada com os demais módulos:

```
modulo3/
├── docker-compose.yml
├── .github/workflows/
├── README.md
└── lambda-kafka/
    ├── pom.xml
    ├── Dockerfile
    ├── mvnw / mvnw.cmd
    ├── src/main/java/com/cabos/lambda_kafka/
    └── src/main/resources/
```

Agora, a explicação completa por camada:

---

## **1. application/** — Lógica de Processamento

```
application/
└── service/
    └── KafkaConsumerService.java
```

**KafkaConsumerService**

* Representa a camada de aplicação.
* Recebe mensagens já tratadas pelo listener.
* Único local onde a lógica de negócio (mesmo que simples) ocorre.
* Mantém o modelo de “use case” do Clean Architecture.

Mesmo em cenários simples como este, manter a camada ajuda a escalar futuramente (por exemplo, validar mensagens, transformar payloads ou enviar notificações).

---

## **2. domain/** — Modelo dos Dados

```
domain/
└── Message.java
```

**Message.java**

* É o modelo que representa os dados recebidos do Kafka.
* Não depende de Spring, nem de Kafka.
* Mantém o domínio puro.

---

## **3. infrastructure/** — Kafka e Implementações Técnicas

```
infrastructure/
├── config/
│   └── KafkaConfig.java
└── consumer/
    └── KafkaMessageListener.java
```

### **config/KafkaConfig.java**

* Configura o consumidor Kafka.
* Define propriedades como desserialização, bootstrap servers, etc.
* É a parte que “sabe” como o Kafka funciona.

### **consumer/KafkaMessageListener.java**

* É o listener real do tópico.
* Usa `@KafkaListener` para reagir automaticamente a novas mensagens.
* Recebe o evento cru, converte e delega ao `KafkaConsumerService`.

Esse listener corresponde ao **adaptador de entrada** na arquitetura hexagonal — ele é o “driver” acionado pelo Kafka.

---

## **4. presentation/** — Tratamento de Erros

```
presentation/
└── handler/
    └── GlobalExceptionHandler.java
```

Mesmo sendo apenas um consumidor, manter essa camada garante:

* padronização entre módulos;
* tratamento central de erros inesperados;
* logs mais claros e estruturados.

---

## **5. resources/** — Configurações

```
src/main/resources/
├── application.yml
├── application.properties
└── logback-spring.xml
```

### application.yml

Configurações principais do Spring e Kafka.

### logback-spring.xml

Garantem padronização de logs, no mesmo formato dos módulos 1 e 2.

---

## **Execução Local**

### 1. Clonar

```bash
git clone https://github.com/Komfort-chain/modulo3.git
cd modulo3
```

### 2. Subir o ambiente (Kafka + Consumer)

```bash
docker compose up -d --build
```

### Serviços disponíveis

| Serviço      | Porta | Descrição             |
| ------------ | ----- | --------------------- |
| Zookeeper    | 2181  | Coordenação do Kafka  |
| Kafka        | 9092  | Broker de mensagens   |
| Lambda Kafka | —     | Consumidor de eventos |

---

## **Testando o Consumidor**

### Enviar mensagem

```bash
docker exec -it kafka \
  kafka-console-producer.sh --broker-list localhost:9092 --topic mensagens
```

Digite:

```
mensagem de teste
```

### Ler logs

```bash
docker logs -f lambda-kafka
```

Saída esperada:

```
A mensagem chegou: mensagem de teste
```

---

## **CI/CD**

### full-ci.yml

Responsável por:

* build + testes
* subir Kafka real para testes
* SonarCloud
* OWASP Dependency-Check
* Docker build & push

### release.yml

Executado ao criar tag:

* changelog automático
* build completo
* artefato JAR
* imagem Docker versionada

---

## **Imagem Docker Oficial**

```
magyodev/lambda-kafka
```

Tags:

* `latest`
* `${run_number}`
* `vX.Y.Z`

---

## **Logs e Monitoramento**

O serviço registra:

* mensagens recebidas;
* exceções globais;
* contexto de execução;
* inicialização do Kafka Consumer.

---

## **Contribuição**

1. Faça um fork
2. Crie uma branch: `feature/nova-melhoria`
3. Use commits semânticos
4. Abra um PR

---

## **Autor**

**Alan de Lima Silva (MagyoDev)**
* GitHub: [https://github.com/MagyoDev](https://github.com/MagyoDev)
* Docker Hub: [https://hub.docker.com/u/magyodev](https://hub.docker.com/u/magyodev)
* Email: [magyodev@gmail.com](mailto:magyodev@gmail.com)