# 🚗 AutoBoots - Sistema de Gerenciamento de Autopeças

Sistema completo de gerenciamento para autopeças com autenticação JWT e controle de acesso baseado em roles (RBAC).

## 📋 Índice

- [Tecnologias](#tecnologias)
- [Arquitetura de Segurança](#arquitetura-de-segurança)
- [Roles e Permissões](#roles-e-permissões)
- [Endpoints Completos da API](#endpoints-completos-da-api)
- [Exemplos Práticos por Recurso](#exemplos-práticos-por-recurso)

---

## 🛠 Tecnologias

- **Java 17+**
- **Spring Boot 3.2.1**
- **Spring Security**
- **JWT (JSON Web Token) 0.12.3**
- **Spring Data JPA**
- **H2 Database** (desenvolvimento)
- **HATEOAS**
- **BCrypt** (criptografia de senhas)

---

## 🔐 Arquitetura de Segurança

### Componentes de Segurança

1. **JwtUtil** - Geração e validação de tokens JWT
2. **JwtAuthenticationFilter** - Intercepta requisições e valida tokens
3. **SecurityConfig** - Configuração de segurança do Spring
4. **CustomUserDetailsService** - Carrega usuários do banco de dados

### Fluxo de Autenticação

```
1. Cliente faz POST /api/auth/login com credenciais
2. Sistema valida credenciais
3. Sistema gera token JWT (válido por 10 horas)
4. Cliente recebe token
5. Cliente inclui token no header Authorization: Bearer {token}
6. JwtAuthenticationFilter valida token em cada requisição
7. Spring Security verifica permissões (@PreAuthorize)
```

---

## 👥 Roles e Permissões

### 🔴 ADMINISTRADOR
**Poder Total no Sistema**

| Recurso | GET lista | GET /{id} | POST | PUT | DELETE |
|---------|-----------|-----------|------|-----|--------|
| Usuários | ✅ TODOS | ✅ TODOS | ✅ TODOS | ✅ TODOS | ✅ |
| Empresas | ✅ | ✅ | ✅ | ✅ | ✅ |
| Mercadorias | ✅ | ✅ | ✅ | ✅ | ✅ |
| Serviços | ✅ | ✅ | ✅ | ✅ | ✅ |
| Veículos | ✅ | ✅ | ✅ | ✅ | ✅ |
| Vendas | ✅ Todas | ✅ Todas | ✅ | ✅ | ✅ |

---

### 🟡 GERENTE
**Gerencia Operações (exceto ADMINISTRADOR)**

| Recurso | GET lista | GET /{id} | POST | PUT | DELETE |
|---------|-----------|-----------|------|-----|--------|
| Usuários | ✅ Exceto ADMIN | ✅ Exceto ADMIN | ✅ Exceto ADMIN | ✅ Exceto ADMIN | ❌ |
| Empresas | ✅ | ✅ | ✅ | ✅ | ✅ |
| Mercadorias | ✅ | ✅ | ✅ | ✅ | ✅ |
| Serviços | ✅ | ✅ | ✅ | ✅ | ✅ |
| Veículos | ✅ | ✅ | ✅ | ✅ | ✅ |
| Vendas | ✅ Todas | ✅ Todas | ✅ | ✅ | ✅ |

---

### 🟢 VENDEDOR
**Foco em Vendas e Clientes**

| Recurso | GET lista | GET /{id} | POST | PUT | DELETE |
|---------|-----------|-----------|------|-----|--------|
| Usuários | ✅ CLIENTES + VENDEDORES | ✅ CLIENTES + VENDEDORES | ✅ Só CLIENTE | ✅ Só CLIENTE | ❌ |
| Empresas | ❌ | ❌ | ❌ | ❌ | ❌ |
| Mercadorias | ✅ Visualizar apenas | ✅ Visualizar apenas | ❌ | ❌ | ❌ |
| Serviços | ✅ Visualizar apenas | ✅ Visualizar apenas | ❌ | ❌ | ❌ |
| Veículos | ✅ | ✅ | ✅ | ✅ | ❌ |
| Vendas | ✅ Só suas | ✅ Só suas | ✅ Onde é funcionário | ❌ | ❌ |

**⚠️ Observação Importante sobre VENDEDOR:**
- **PODE VER** mercadorias e serviços para consultar preços e estoque ao atender clientes
- **NÃO PODE** criar, editar ou deletar mercadorias e serviços (gestão é do GERENTE/ADMIN)
- **NÃO TEM ACESSO** a empresas (nenhuma operação)

---

### 🔵 CLIENTE
**Acesso Muito Restrito**

| Recurso | GET lista | GET /{id} | POST | PUT | DELETE |
|---------|-----------|-----------|------|-----|--------|
| Usuários | ✅ Só si mesmo | ✅ Só si mesmo | ❌ | ❌ | ❌ |
| Empresas | ❌ | ❌ | ❌ | ❌ | ❌ |
| Mercadorias | ❌ | ❌ | ❌ | ❌ | ❌ |
| Serviços | ❌ | ❌ | ❌ | ❌ | ❌ |
| Veículos | ✅ Só próprios | ✅ Só próprios | ✅ Para si | ✅ Só próprios | ❌ |
| Vendas | ✅ Só compras | ✅ Só compras | ❌ | ❌ | ❌ |

---

## 🌐 Endpoints Completos da API

### Base URL
```
http://localhost:8080
```

---

## 🔑 1. AUTENTICAÇÃO

### POST /api/auth/login (Público)
Login no sistema

**Request:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "username": "admin",
  "role": "ADMINISTRADOR"
}
```

---

### POST /api/auth/register (Público)
Cadastro de CLIENTE (sempre cria como CLIENTE)

**Request:**
```json
{
  "nome": "João Cliente",
  "username": "joao.cliente",
  "password": "senha123",
  "email": "joao.cliente@email.com"
}
```

**Response (200 OK):**
```
Cliente registrado com sucesso!
```

---

### POST /api/auth/create-user (VENDEDOR, GERENTE, ADMIN)
Criar usuário com role específica

**Request:**
```json
{
  "nome": "Pedro Vendedor",
  "username": "pedro.vendedor",
  "password": "senha123",
  "email": "pedro.vendedor@email.com",
  "role": "VENDEDOR"
}
```

**Response (200 OK):**
```
Usuário criado com sucesso com role: VENDEDOR
```

**Restrições:**
- VENDEDOR: só pode criar CLIENTE
- GERENTE: pode criar CLIENTE, VENDEDOR, GERENTE (não ADMIN)
- ADMIN: pode criar qualquer role

---

## 👤 2. USUÁRIOS

### GET /api/usuarios (ADMIN, GERENTE, VENDEDOR, CLIENTE)
Listar usuários conforme permissões

**Request:**
```bash
GET http://localhost:8080/api/usuarios
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "_embedded": {
    "usuarioList": [
      {
        "id": 1,
        "nome": "Admin Master",
        "username": "admin",
        "email": "admin@autoboots.com",
        "role": "ADMINISTRADOR",
        "dataCadastro": "2025-01-15T10:30:00",
        "_links": {
          "self": {"href": "http://localhost:8080/api/usuarios/1"}
        }
      }
    ]
  }
}
```

---

### GET /api/usuarios/{id} (ADMIN, GERENTE, VENDEDOR, CLIENTE)
Buscar usuário por ID

**Request:**
```bash
GET http://localhost:8080/api/usuarios/1
Authorization: Bearer {TOKEN}
```

**Response (200 OK):** Dados do usuário

**Response (403 FORBIDDEN):** Sem permissão

---

### POST /api/usuarios (ADMIN, GERENTE, VENDEDOR)
Criar novo usuário

**Request:**
```json
{
  "nome": "Ana Cliente",
  "username": "ana.cliente",
  "password": "senha123",
  "email": "ana@email.com",
  "role": "CLIENTE"
}
```

**Response (201 CREATED):** Usuário criado com links HATEOAS

---

### PUT /api/usuarios/{id} (ADMIN, GERENTE, VENDEDOR)
Atualizar usuário

**Request:**
```json
{
  "nome": "Ana Cliente Silva",
  "email": "ana.silva@email.com",
  "role": "CLIENTE"
}
```

**Response (200 OK):** Usuário atualizado

---

### DELETE /api/usuarios/{id} (Apenas ADMIN)
Deletar usuário

**Request:**
```bash
DELETE http://localhost:8080/api/usuarios/5
Authorization: Bearer {TOKEN_ADMIN}
```

**Response (204 NO CONTENT)**

---

## 🏢 3. EMPRESAS

### GET /api/empresas (ADMIN, GERENTE)
Listar todas as empresas

**Request:**
```bash
GET http://localhost:8080/api/empresas
Authorization: Bearer {TOKEN_ADMIN_OU_GERENTE}
```

**Response (200 OK):**
```json
{
  "_embedded": {
    "empresaList": [
      {
        "id": 1,
        "razaoSocial": "AutoBoots Ltda",
        "nomeFantasia": "AutoBoots",
        "telefones": ["11-98765-4321", "11-3456-7890"],
        "endereco": {
          "estado": "SP",
          "cidade": "São Paulo",
          "bairro": "Centro",
          "rua": "Av. Paulista",
          "numero": "1000",
          "codigoPostal": "01310-100",
          "informacoesAdicionais": "Próximo ao metrô"
        },
        "_links": {
          "self": {"href": "http://localhost:8080/api/empresas/1"}
        }
      }
    ]
  }
}
```

---

### GET /api/empresas/{id} (ADMIN, GERENTE)
Buscar empresa por ID

**Request:**
```bash
GET http://localhost:8080/api/empresas/1
Authorization: Bearer {TOKEN}
```

**Response (200 OK):** Dados da empresa

---

### POST /api/empresas (ADMIN, GERENTE)
Criar nova empresa

**Request:**
```json
{
  "razaoSocial": "AutoBoots Filial RJ Ltda",
  "nomeFantasia": "AutoBoots RJ",
  "telefones": ["21-99999-8888", "21-3333-4444"],
  "endereco": {
    "estado": "RJ",
    "cidade": "Rio de Janeiro",
    "bairro": "Centro",
    "rua": "Av. Rio Branco",
    "numero": "100",
    "codigoPostal": "20040-001",
    "informacoesAdicionais": "Próximo à Praça XV"
  }
}
```

**Response (201 CREATED):** Empresa criada com links HATEOAS

---

### PUT /api/empresas/{id} (ADMIN, GERENTE)
Atualizar empresa

**Request:**
```json
{
  "razaoSocial": "AutoBoots Filial RJ Ltda",
  "nomeFantasia": "AutoBoots Rio",
  "telefones": ["21-99999-8888"],
  "endereco": {
    "estado": "RJ",
    "cidade": "Rio de Janeiro",
    "bairro": "Centro",
    "rua": "Av. Rio Branco",
    "numero": "200",
    "codigoPostal": "20040-001",
    "informacoesAdicionais": "Novo endereço"
  }
}
```

**Response (200 OK):** Empresa atualizada

---

### DELETE /api/empresas/{id} (ADMIN, GERENTE)
Deletar empresa

**Request:**
```bash
DELETE http://localhost:8080/api/empresas/1
Authorization: Bearer {TOKEN}
```

**Response (204 NO CONTENT)**

---

## 📦 4. MERCADORIAS

### GET /api/mercadorias (ADMIN, GERENTE, VENDEDOR)
Listar todas as mercadorias

**Request:**
```bash
GET http://localhost:8080/api/mercadorias
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "_embedded": {
    "mercadoriaList": [
      {
        "id": 1,
        "nome": "Óleo Motor 5W30 Sintético",
        "valor": 45.90,
        "quantidade": 150,
        "descricao": "Óleo sintético para motor - 1 litro",
        "_links": {
          "self": {"href": "http://localhost:8080/api/mercadorias/1"}
        }
      }
    ]
  }
}
```

**⚠️ VENDEDOR:** Pode apenas VISUALIZAR para consultar preços e estoque.

---

### GET /api/mercadorias/{id} (ADMIN, GERENTE, VENDEDOR)
Buscar mercadoria por ID

**Request:**
```bash
GET http://localhost:8080/api/mercadorias/1
Authorization: Bearer {TOKEN}
```

**Response (200 OK):** Dados da mercadoria

---

### POST /api/mercadorias (ADMIN, GERENTE)
Criar nova mercadoria

**Request:**
```json
{
  "nome": "Filtro de Ar Esportivo K&N",
  "valor": 89.90,
  "quantidade": 50,
  "descricao": "Filtro de ar de alto desempenho - Marca K&N"
}
```

**Response (201 CREATED):** Mercadoria criada

**❌ VENDEDOR NÃO PODE criar mercadorias**

---

### PUT /api/mercadorias/{id} (ADMIN, GERENTE)
Atualizar mercadoria

**Request:**
```json
{
  "nome": "Filtro de Ar Esportivo K&N",
  "valor": 95.00,
  "quantidade": 45,
  "descricao": "Filtro de ar de alto desempenho - Marca K&N - Promoção"
}
```

**Response (200 OK):** Mercadoria atualizada

**❌ VENDEDOR NÃO PODE atualizar mercadorias**

---

### DELETE /api/mercadorias/{id} (ADMIN, GERENTE)
Deletar mercadoria

**Request:**
```bash
DELETE http://localhost:8080/api/mercadorias/2
Authorization: Bearer {TOKEN}
```

**Response (204 NO CONTENT)**

**❌ VENDEDOR NÃO PODE deletar mercadorias**

---

## 🔧 5. SERVIÇOS

### GET /api/servicos (ADMIN, GERENTE, VENDEDOR)
Listar todos os serviços

**Request:**
```bash
GET http://localhost:8080/api/servicos
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "_embedded": {
    "servicoList": [
      {
        "id": 1,
        "nome": "Troca de Óleo Completa",
        "valor": 80.00,
        "descricao": "Troca de óleo + filtro + revisão básica",
        "_links": {
          "self": {"href": "http://localhost:8080/api/servicos/1"}
        }
      }
    ]
  }
}
```

**⚠️ VENDEDOR:** Pode apenas VISUALIZAR para consultar serviços disponíveis.

---

### GET /api/servicos/{id} (ADMIN, GERENTE, VENDEDOR)
Buscar serviço por ID

**Request:**
```bash
GET http://localhost:8080/api/servicos/1
Authorization: Bearer {TOKEN}
```

**Response (200 OK):** Dados do serviço

---

### POST /api/servicos (ADMIN, GERENTE)
Criar novo serviço

**Request:**
```json
{
  "nome": "Alinhamento e Balanceamento",
  "valor": 120.00,
  "descricao": "Alinhamento e balanceamento das 4 rodas"
}
```

**Response (201 CREATED):** Serviço criado

**❌ VENDEDOR NÃO PODE criar serviços**

---

### PUT /api/servicos/{id} (ADMIN, GERENTE)
Atualizar serviço

**Request:**
```json
{
  "nome": "Alinhamento e Balanceamento Computadorizado",
  "valor": 150.00,
  "descricao": "Alinhamento e balanceamento das 4 rodas com tecnologia computadorizada"
}
```

**Response (200 OK):** Serviço atualizado

**❌ VENDEDOR NÃO PODE atualizar serviços**

---

### DELETE /api/servicos/{id} (ADMIN, GERENTE)
Deletar serviço

**Request:**
```bash
DELETE http://localhost:8080/api/servicos/2
Authorization: Bearer {TOKEN}
```

**Response (204 NO CONTENT)**

**❌ VENDEDOR NÃO PODE deletar serviços**

---

## 🚗 6. VEÍCULOS

### GET /api/veiculos (ADMIN, GERENTE, VENDEDOR, CLIENTE)
Listar veículos

**Comportamento:**
- CLIENTE: vê apenas seus próprios veículos
- ADMIN, GERENTE, VENDEDOR: veem todos os veículos

**Request:**
```bash
GET http://localhost:8080/api/veiculos
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "_embedded": {
    "veiculoList": [
      {
        "id": 1,
        "placa": "ABC-1234",
        "modelo": "Corolla",
        "marca": "Toyota",
        "ano": 2022,
        "tipoVeiculo": "CARRO",
        "usuario": {
          "id": 4,
          "nome": "Maria Cliente",
          "username": "cliente"
        },
        "_links": {
          "self": {"href": "http://localhost:8080/api/veiculos/1"}
        }
      }
    ]
  }
}
```

---

### GET /api/veiculos/{id} (ADMIN, GERENTE, VENDEDOR, CLIENTE)
Buscar veículo por ID

**Comportamento:**
- CLIENTE: só pode acessar seus próprios veículos (403 se não for dono)
- Outros: podem acessar qualquer veículo

**Request:**
```bash
GET http://localhost:8080/api/veiculos/1
Authorization: Bearer {TOKEN}
```

**Response (200 OK):** Dados do veículo

**Response (403 FORBIDDEN):** Se CLIENTE tentar acessar veículo de outro

---

### POST /api/veiculos (ADMIN, GERENTE, VENDEDOR, CLIENTE)
Criar novo veículo

**Request:**
```json
{
  "placa": "XYZ-9876",
  "modelo": "HB20",
  "marca": "Hyundai",
  "ano": 2023,
  "tipoVeiculo": "CARRO",
  "usuario": {
    "id": 4
  }
}
```

**Tipos de veículo válidos:** `CARRO`, `MOTO`, `CAMINHAO`, `VAN`

**Response (201 CREATED):** Veículo criado

---

### PUT /api/veiculos/{id} (ADMIN, GERENTE, VENDEDOR, CLIENTE)
Atualizar veículo

**Comportamento:**
- CLIENTE: só pode atualizar seus próprios veículos
- Outros: podem atualizar qualquer veículo

**Request:**
```json
{
  "placa": "XYZ-9876",
  "modelo": "HB20 Comfort Plus",
  "marca": "Hyundai",
  "ano": 2023,
  "tipoVeiculo": "CARRO"
}
```

**Response (200 OK):** Veículo atualizado

---

### DELETE /api/veiculos/{id} (ADMIN, GERENTE)
Deletar veículo

**Request:**
```bash
DELETE http://localhost:8080/api/veiculos/2
Authorization: Bearer {TOKEN}
```

**Response (204 NO CONTENT)**

---

## 💰 7. VENDAS

### GET /api/vendas (ADMIN, GERENTE, VENDEDOR, CLIENTE)
Listar vendas

**Comportamento:**
- ADMIN, GERENTE: veem todas as vendas
- VENDEDOR: vê apenas vendas onde ele é o funcionário
- CLIENTE: vê apenas vendas onde ele é o cliente

**Request:**
```bash
GET http://localhost:8080/api/vendas
Authorization: Bearer {TOKEN}
```

**Response (200 OK):**
```json
{
  "_embedded": {
    "vendaList": [
      {
        "id": 1,
        "dataVenda": "2025-01-26T15:30:00",
        "valorTotal": 205.80,
        "cliente": {
          "id": 4,
          "nome": "Maria Cliente",
          "username": "cliente",
          "role": "CLIENTE"
        },
        "funcionario": {
          "id": 3,
          "nome": "Ana Vendedora",
          "username": "vendedor",
          "role": "VENDEDOR"
        },
        "veiculo": {
          "id": 1,
          "placa": "ABC-1234",
          "modelo": "Corolla"
        },
        "mercadorias": [
          {
            "id": 1,
            "nome": "Óleo Motor 5W30 Sintético",
            "valor": 45.90
          }
        ],
        "servicos": [
          {
            "id": 1,
            "nome": "Troca de Óleo Completa",
            "valor": 80.00
          }
        ],
        "_links": {
          "self": {"href": "http://localhost:8080/api/vendas/1"}
        }
      }
    ]
  }
}
```

---

### GET /api/vendas/{id} (ADMIN, GERENTE, VENDEDOR, CLIENTE)
Buscar venda por ID

**Comportamento:**
- ADMIN, GERENTE: podem acessar qualquer venda
- VENDEDOR: só pode acessar vendas onde ele é o funcionário
- CLIENTE: só pode acessar vendas onde ele é o cliente

**Request:**
```bash
GET http://localhost:8080/api/vendas/1
Authorization: Bearer {TOKEN}
```

**Response (200 OK):** Dados da venda

**Response (403 FORBIDDEN):** Sem permissão

---

### POST /api/vendas (ADMIN, GERENTE, VENDEDOR)
Criar nova venda

**Request:**
```json
{
  "dataVenda": "2025-01-27T14:30:00",
  "cliente": {
    "id": 4
  },
  "funcionario": {
    "id": 3
  },
  "veiculo": {
    "id": 1
  },
  "mercadorias": [
    {"id": 1},
    {"id": 2}
  ],
  "servicos": [
    {"id": 1},
    {"id": 2}
  ]
}
```

**Observação:** O `valorTotal` é calculado automaticamente pela soma dos valores de mercadorias e serviços.

**Response (201 CREATED):** Venda criada

---

### PUT /api/vendas/{id} (ADMIN, GERENTE)
Atualizar venda

**Request:**
```json
{
  "cliente": {
    "id": 4
  },
  "funcionario": {
    "id": 3
  },
  "veiculo": {
    "id": 1
  },
  "mercadorias": [
    {"id": 1}
  ],
  "servicos": [
    {"id": 1}
  ]
}
```

**Response (200 OK):** Venda atualizada

**❌ VENDEDOR NÃO PODE atualizar vendas**

---

### DELETE /api/vendas/{id} (ADMIN, GERENTE)
Deletar venda

**Request:**
```bash
DELETE http://localhost:8080/api/vendas/1
Authorization: Bearer {TOKEN}
```

**Response (204 NO CONTENT)**

**❌ VENDEDOR NÃO PODE deletar vendas**

---

## 📊 Exemplos Práticos Completos

### 🎯 Cenário 1: ADMIN cria estrutura completa

```bash
# 1. Login como ADMIN
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

# 2. Criar Empresa
POST http://localhost:8080/api/empresas
Authorization: Bearer {TOKEN_ADMIN}
Content-Type: application/json

{
  "razaoSocial": "AutoBoots Matriz Ltda",
  "nomeFantasia": "AutoBoots Matriz",
  "telefones": ["11-98765-4321"],
  "endereco": {
    "estado": "SP",
    "cidade": "São Paulo",
    "bairro": "Centro",
    "rua": "Av. Paulista",
    "numero": "1000",
    "codigoPostal": "01310-100",
    "informacoesAdicionais": "Torre A"
  }
}

# 3. Criar Mercadoria
POST http://localhost:8080/api/mercadorias
Authorization: Bearer {TOKEN_ADMIN}
Content-Type: application/json

{
  "nome": "Pneu Michelin 195/65 R15",
  "valor": 350.00,
  "quantidade": 40,
  "descricao": "Pneu de alta performance"
}

# 4. Criar Serviço
POST http://localhost:8080/api/servicos
Authorization: Bearer {TOKEN_ADMIN}
Content-Type: application/json

{
  "nome": "Troca de Pneus (4 unidades)",
  "valor": 100.00,
  "descricao": "Troca completa dos 4 pneus"
}

# 5. Criar Vendedor
POST http://localhost:8080/api/auth/create-user
Authorization: Bearer {TOKEN_ADMIN}
Content-Type: application/json

{
  "nome": "João Vendedor",
  "username": "joao.vendedor",
  "password": "senha123",
  "email": "joao@autoboots.com",
  "role": "VENDEDOR"
}

# 6. Criar Cliente (público)
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "nome": "Carlos Cliente",
  "username": "carlos.cliente",
  "password": "senha123",
  "email": "carlos@email.com"
}
```

---

### 🎯 Cenário 2: VENDEDOR registra venda completa

```bash
# 1. Login como Vendedor
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "vendedor",
  "password": "vendedor123"
}

# 2. Criar Cliente
POST http://localhost:8080/api/auth/create-user
Authorization: Bearer {TOKEN_VENDEDOR}
Content-Type: application/json

{
  "nome": "Roberto Novo Cliente",
  "username": "roberto.cliente",
  "password": "senha123",
  "email": "roberto@email.com",
  "role": "CLIENTE"
}

# 3. Login como novo cliente
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "roberto.cliente",
  "password": "senha123"
}

# 4. Cliente cadastra seu veículo
POST http://localhost:8080/api/veiculos
Authorization: Bearer {TOKEN_ROBERTO}
Content-Type: application/json

{
  "placa": "DEF-5678",
  "modelo": "Civic",
  "marca": "Honda",
  "ano": 2023,
  "tipoVeiculo": "CARRO",
  "usuario": {
    "id": 5
  }
}

# 5. Login como vendedor novamente
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "vendedor",
  "password": "vendedor123"
}

# 6. Vendedor VISUALIZA mercadorias disponíveis
GET http://localhost:8080/api/mercadorias
Authorization: Bearer {TOKEN_VENDEDOR}

# 7. Vendedor VISUALIZA serviços disponíveis
GET http://localhost:8080/api/servicos
Authorization: Bearer {TOKEN_VENDEDOR}

# 8. Vendedor cria a venda
POST http://localhost:8080/api/vendas
Authorization: Bearer {TOKEN_VENDEDOR}
Content-Type: application/json

{
  "dataVenda": "2025-01-27T16:00:00",
  "cliente": {
    "id": 5
  },
  "funcionario": {
    "id": 3
  },
  "veiculo": {
    "id": 3
  },
  "mercadorias": [
    {"id": 1},
    {"id": 2}
  ],
  "servicos": [
    {"id": 1}
  ]
}

# 9. Cliente visualiza suas compras
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "roberto.cliente",
  "password": "senha123"
}

GET http://localhost:8080/api/vendas
Authorization: Bearer {TOKEN_ROBERTO}
```

---

### 🎯 Cenário 3: CLIENTE gerencia seus veículos

```bash
# 1. Login como Cliente
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "cliente",
  "password": "cliente123"
}

# 2. Listar meus veículos
GET http://localhost:8080/api/veiculos
Authorization: Bearer {TOKEN_CLIENTE}

# 3. Ver detalhes de um veículo específico
GET http://localhost:8080/api/veiculos/1
Authorization: Bearer {TOKEN_CLIENTE}

# 4. Criar novo veículo
POST http://localhost:8080/api/veiculos
Authorization: Bearer {TOKEN_CLIENTE}
Content-Type: application/json

{
  "placa": "GHI-9012",
  "modelo": "Onix",
  "marca": "Chevrolet",
  "ano": 2024,
  "tipoVeiculo": "CARRO",
  "usuario": {
    "id": 4
  }
}

# 5. Atualizar veículo
PUT http://localhost:8080/api/veiculos/4
Authorization: Bearer {TOKEN_CLIENTE}
Content-Type: application/json

{
  "placa": "GHI-9012",
  "modelo": "Onix Plus",
  "marca": "Chevrolet",
  "ano": 2024,
  "tipoVeiculo": "CARRO"
}

# 6. Ver histórico de compras
GET http://localhost:8080/api/vendas
Authorization: Bearer {TOKEN_CLIENTE}

# 7. Ver dados da própria conta
GET http://localhost:8080/api/usuarios/4
Authorization: Bearer {TOKEN_CLIENTE}
```

---

## 🔒 Códigos de Status HTTP

| Código | Significado | Quando Ocorre |
|--------|-------------|---------------|
| `200 OK` | Sucesso | GET bem-sucedido, PUT concluído |
| `201 CREATED` | Criado | POST bem-sucedido |
| `204 NO CONTENT` | Sem conteúdo | DELETE bem-sucedido |
| `400 BAD REQUEST` | Requisição inválida | Dados inválidos ou malformados |
| `401 UNAUTHORIZED` | Não autorizado | Token ausente, inválido ou expirado |
| `403 FORBIDDEN` | Proibido | Token válido mas sem permissão |
| `404 NOT FOUND` | Não encontrado | Recurso não existe |
| `500 INTERNAL SERVER ERROR` | Erro do servidor | Erro inesperado no servidor |

---

## ⚙️ Configuração e Execução

### Requisitos
- Java 17+
- Maven 3.6+
- Porta 8080 disponível

### Executar o Projeto
```bash
# Clonar repositório
git clone https://github.com/carlosintrieri/repoweb4.git

# Entrar no diretório - IMPORTANTE!
cd autoboots-projeto-completo

# Compilar
mvn clean install

# Executar
mvn spring-boot:run
```

### Acessar H2 Console
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:autoboots
Username: sa
Password: (deixe em branco)
```

### Credenciais de Teste (criadas pelo DataLoader)
```
ADMINISTRADOR: admin / admin123
GERENTE:       gerente / gerente123
VENDEDOR:      vendedor / vendedor123
CLIENTE:       cliente / cliente123
```

---

## 📝 Observações Importantes

### Tokens JWT
- Expiram em **10 horas**
- Devem ser incluídos em TODAS as requisições protegidas
- Formato: `Authorization: Bearer {token}`

### Senhas
- Todas as senhas são criptografadas com BCrypt automaticamente
- No cadastro, envie a senha em texto plano (ex: `"senha123"`)
- O sistema faz o hash automaticamente

### Cálculo de Valor Total em Vendas
- O `valorTotal` é calculado automaticamente
- Soma dos valores de todas as mercadorias + serviços
- Não é necessário informar no POST/PUT

### HATEOAS
- Todas as respostas incluem links de navegação
- Links variam conforme as permissões do usuário
- Facilita a descoberta da API

### Hierarquia de Permissões
```
ADMINISTRADOR (poder absoluto)
    ↓
GERENTE (gerencia tudo, exceto ADMIN)
    ↓
VENDEDOR (foco em vendas e clientes - pode VER mercadorias/serviços)
    ↓
CLIENTE (acesso muito restrito)
```

### Por que VENDEDOR pode VER mercadorias e serviços?
O VENDEDOR precisa consultar:
- **Preços** para informar ao cliente
- **Estoque** de mercadorias disponíveis
- **Serviços** oferecidos pela oficina
- **Dados** necessários para criar vendas

Mas **NÃO PODE** gerenciar (criar/editar/deletar) - essa é função do GERENTE/ADMIN.

---

## 🛠 Ferramentas Recomendadas

- **Postman** - Para testar APIs
- **Insomnia** - Alternativa ao Postman
- **cURL** - Linha de comando
- **VS Code com REST Client** - Extensão para testar APIs
- **jwt.io** - Para decodificar e verificar tokens JWT

---

## 📚 Recursos Adicionais

### Tipos de Veículo
```
CARRO
MOTO
CAMINHAO
VAN
```

### Estrutura de Endereço
```json
{
  "estado": "String (obrigatório)",
  "cidade": "String (obrigatório)",
  "bairro": "String (obrigatório)",
  "rua": "String (obrigatório)",
  "numero": "String (obrigatório)",
  "codigoPostal": "String (obrigatório)",
  "informacoesAdicionais": "String (opcional)"
}
```

### Validações
- **Username**: único no sistema
- **Email**: único e formato válido
- **Valores**: devem ser positivos
- **Quantidades**: devem ser zero ou positivas
- **Datas**: formato ISO 8601

---

## 📋 Resumo de Endpoints por Role

### ADMINISTRADOR
✅ Acesso total a todos os endpoints

### GERENTE
✅ Todos os endpoints exceto:
- Deletar usuários
- Ver/gerenciar usuários ADMIN

### VENDEDOR
✅ Pode acessar:
- GET /api/usuarios (vê CLIENTES + VENDEDORES)
- GET /api/usuarios/{id} (CLIENTES + VENDEDORES)
- POST /api/usuarios (apenas CLIENTE)
- PUT /api/usuarios/{id} (apenas CLIENTE)
- **GET /api/mercadorias** (visualizar apenas)
- **GET /api/mercadorias/{id}** (visualizar apenas)
- **GET /api/servicos** (visualizar apenas)
- **GET /api/servicos/{id}** (visualizar apenas)
- GET/POST/PUT /api/veiculos
- GET /api/vendas (apenas suas)
- GET /api/vendas/{id} (apenas suas)
- POST /api/vendas (onde é funcionário)

❌ NÃO pode:
- Acessar /api/empresas
- POST/PUT/DELETE em mercadorias e serviços
- PUT/DELETE em vendas
- DELETE em veículos

### CLIENTE
✅ Pode acessar:
- GET /api/usuarios (apenas si mesmo)
- GET /api/usuarios/{id} (apenas seu ID)
- GET/POST/PUT /api/veiculos (apenas próprios)
- GET /api/vendas (apenas suas compras)
- GET /api/vendas/{id} (apenas suas compras)

❌ NÃO pode:
- Nada em empresas, mercadorias, serviços
- POST/PUT/DELETE em usuários
- DELETE em veículos
- POST/PUT/DELETE em vendas

---

**Desenvolvido para o projeto AutoBoots - Sistema Completo de Gerenciamento de Autopeças com Spring Boot e JWT** 🚗✨
