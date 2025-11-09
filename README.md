# 🧩 Módulo 3 — Função Lambda com Kafka (Komfort Chain)

O **Módulo 3** faz parte da suíte **Komfort Chain**, e tem como objetivo demonstrar a integração entre **Kafka** e uma **função Lambda**.  
A aplicação escuta mensagens publicadas em um tópico Kafka e exibe no console a saída:

```

A mensagem chegou: <mensagem>

```

Este módulo foi desenvolvido para reforçar o entendimento sobre **mensageria, processamento assíncrono e conteinerização de funções serverless**.

---

## 🧠 Tecnologias Utilizadas

| Categoria        | Tecnologia                |
| ---------------- | -------------------------- |
| Linguagem        | Java 21                    |
| Framework        | Spring Boot 3.5.7 (Spring Kafka) |
| Mensageria       | Apache Kafka 7.5.1         |
| Orquestração     | Docker e Docker Compose    |
| Logs             | Console + Docker Logs      |
| Build            | Maven                      |
| Deploy CI/CD     | GitHub Actions + Docker Hub |
| Arquitetura      | Clean Architecture / Event-driven |

---

## ⚙️ Estrutura do Projeto

```

lambda-kafka/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── src/
│   ├── main/java/com/cabos/lambda/
│   │   ├── consumer/
│   │   │   └── KafkaConsumerService.java
│   │   ├── LambdaKafkaApplication.java
│   │   └── config/
│   │       └── KafkaConfig.java
│   └── resources/
│       └── application.yml

```

Fluxo:
```

Kafka Topic → Lambda Consumer → Console Output

````

---

## 🚀 Como Executar Localmente

### 1️⃣ Clonar o repositório
````bash
git clone https://github.com/Komfort-chain/modulo3.git
cd modulo3/lambda-kafka
````

### 2️⃣ Buildar e subir containers

````bash
docker compose build
docker compose up -d
````

Esses comandos:

* Sobem o Kafka e o Zookeeper;
* Constroem a imagem da função Lambda;
* Iniciam a Lambda já conectada ao tópico Kafka.

Verifique se todos estão ativos:

````bash
docker ps
````

---

## 🧾 Serviços Disponíveis

| Serviço          | Porta | Descrição                                   |
| ---------------- | ----- | ------------------------------------------- |
| Zookeeper        | 2181  | Coordenação do Kafka                        |
| Kafka Broker     | 9092  | Servidor de mensageria                      |
| Lambda Kafka App | —     | Consumidor de mensagens (sem endpoint HTTP) |

---

## 📡 Testando a Função

### 🔹 1️⃣ Enviar mensagem via Kafka CLI

Com o Kafka rodando no container, execute:

````bash
docker exec -it kafka kafka-console-producer.sh \
  --broker-list localhost:9092 --topic meu-topico
````

Digite qualquer mensagem:

````
Mensagem de teste do módulo 3
````

Verifique o log da Lambda:

````bash
docker logs -f lambda-kafka
````

Saída esperada:

````
A mensagem chegou: Mensagem de teste do módulo 3
````

---

### 🔹 2️⃣ Testar via REST Proxy (opcional)

Se estiver usando **Confluent REST Proxy**, envie a mensagem com o Postman:

**POST**

````
http://localhost:8082/topics/meu-topico
````

**Headers**

````
Content-Type: application/vnd.kafka.json.v2+json
````

**Body**

````json
{
  "records": [
    { "value": "Olá Lambda do módulo 3!" }
  ]
}
````

Resultado nos logs:

````
A mensagem chegou: Olá Lambda do módulo 3!
````

---

### 🔹 3️⃣ Teste alternativo via endpoint temporário (opcional)

Durante o desenvolvimento, é possível expor um endpoint para simular mensagens:

**POST**

````
http://localhost:8080/test
````

**Body**

````json
"Mensagem simulada"
````

Log esperado:

````
A mensagem chegou: Mensagem simulada
````

---

## 🐳 Deploy e Publicação no Docker Hub

### Workflow do GitHub Actions

O pipeline de CI/CD faz:

1. **Build** da imagem Docker da Lambda;
2. **Login** no Docker Hub via secrets;
3. **Push** da imagem com tag `latest`.

Exemplo de trecho no `.github/workflows/docker.yml`:

````yaml
name: Build and Push Lambda Image

on:
  push:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v3

      - name: Build Docker image
        run: docker build -t magyodev/modulo3-lambda-kafka .

      - name: Push to Docker Hub
        run: |
          echo "${{ secrets.DOCKERHUB_PASSWORD }}" | docker login -u "${{ secrets.DOCKERHUB_USERNAME }}" --password-stdin
          docker push magyodev/modulo3-lambda-kafka:latest
````

---

## 🔍 Logs e Monitoramento

Exibir logs em tempo real:

````bash
docker logs -f lambda-kafka
````

Cada mensagem consumida do tópico aparecerá no console:

````
A mensagem chegou: <conteúdo da mensagem>
````

---

## 🧱 Estrutura de Mensagens Kafka

**Tópico:** `meu-topico`
**Formato:** Texto simples (String)

Exemplo de mensagem:

````
"Nova mensagem enviada para a Lambda"
````

---

## 🧭 Diagrama Simplificado

````
┌───────────────┐
│ Kafka Broker  │◄─── Produz mensagem
└──────┬────────┘
       │
       ▼
┌───────────────────────────────────┐
│ Lambda Kafka Consumer             │
│ Exibe: "A mensagem chegou: <msg>" │
└───────────────────────────────────┘
````

---

## 👨‍💻 Autor

**Alan de Lima Silva (MagyoDev)**

* **GitHub:** [https://github.com/MagyoDev](https://github.com/MagyoDev)
* **E-mail:** [magyodev@gmail.com](mailto:magyodev@gmail.com)

