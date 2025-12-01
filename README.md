# 🚗 AutoBoots - Sistema de Gerenciamento de Autopeças

Sistema de gerenciamento para autopeças com autenticação JWT e controle de acesso baseado em roles (RBAC).

## 📋 Índice

- [Tecnologias](#tecnologias)
- [Arquitetura de Segurança](#arquitetura-de-segurança)
- [Roles e Permissões](#roles-e-permissões)
- [Endpoints da API](#endpoints-da-api)
- [Exemplos Completos por Role](#exemplos-completos-por-role)

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

| Recurso | Listar | Visualizar | Criar | Atualizar | Deletar |
|---------|--------|------------|-------|-----------|---------|
| Usuários | ✅ Todos | ✅ Todos | ✅ Todos | ✅ Todos | ✅ |
| Empresas | ✅ | ✅ | ✅ | ✅ | ✅ |
| Mercadorias | ✅ | ✅ | ✅ | ✅ | ✅ |
| Serviços | ✅ | ✅ | ✅ | ✅ | ✅ |
| Veículos | ✅ | ✅ | ✅ | ✅ | ✅ |
| Vendas | ✅ Todas | ✅ Todas | ✅ | ✅ | ✅ |

---

### 🟡 GERENTE
**Gerencia Operações (exceto ADMINISTRADOR)**

| Recurso | Listar | Visualizar | Criar | Atualizar | Deletar |
|---------|--------|------------|-------|-----------|---------|
| Usuários | ✅ Não vê ADMIN | ✅ Não vê ADMIN | ✅ Não pode criar ADMIN | ✅ Não pode editar ADMIN | ❌ |
| Empresas | ✅ | ✅ | ✅ | ✅ | ✅ |
| Mercadorias | ✅ | ✅ | ✅ | ✅ | ✅ |
| Serviços | ✅ | ✅ | ✅ | ✅ | ✅ |
| Veículos | ✅ | ✅ | ✅ | ✅ | ✅ |
| Vendas | ✅ Todas | ✅ Todas | ✅ | ✅ | ✅ |

---

### 🟢 VENDEDOR
**Foco em Vendas e Clientes**

| Recurso | Listar | Visualizar | Criar | Atualizar | Deletar |
|---------|--------|------------|-------|-----------|---------|
| Usuários | ✅ Só CLIENTES | ✅ Só CLIENTES | ✅ Só CLIENTE | ✅ Só CLIENTE | ❌ |
| Empresas | ❌ | ❌ | ❌ | ❌ | ❌ |
| Mercadorias | ✅ | ✅ | ❌ | ❌ | ❌ |
| Serviços | ✅ | ✅ | ❌ | ❌ | ❌ |
| Veículos | ✅ | ✅ | ✅ | ✅ | ❌ |
| Vendas | ✅ Só suas vendas | ✅ Só suas vendas | ✅ Onde é funcionário | ❌ | ❌ |

---

### 🔵 CLIENTE
**Acesso Muito Restrito**

| Recurso | Listar | Visualizar | Criar | Atualizar | Deletar |
|---------|--------|------------|-------|-----------|---------|
| Usuários | ❌ | ✅ Só próprio | ❌ | ❌ | ❌ |
| Empresas | ❌ | ❌ | ❌ | ❌ | ❌ |
| Mercadorias | ❌ | ❌ | ❌ | ❌ | ❌ |
| Serviços | ❌ | ❌ | ❌ | ❌ | ❌ |
| Veículos | ✅ Só próprios | ✅ Só próprios | ✅ Para si | ✅ Só próprios | ❌ |
| Vendas | ✅ Só suas compras | ✅ Só suas compras | ❌ | ❌ | ❌ |

---

## 🌐 Endpoints da API

### Base URL
```
http://localhost:8080
```

---

## 📝 Exemplos Completos por Role

---

## 🔴 ADMINISTRADOR - Exemplos Completos

### 1. Registro e Login

```json
// POST /api/auth/register
{
  "nome": "Admin Master",
  "username": "admin",
  "password": "admin123",
  "email": "admin@autoboots.com",
  "role": "ADMINISTRADOR"
}

// Response: 200 OK
"Usuário registrado com sucesso!"
```

```json
// POST /api/auth/login
{
  "username": "admin",
  "password": "admin123"
}

// Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwNjI4MDAwMCwiZXhwIjoxNzA2MzE2MDAwfQ.xxxxx",
  "username": "admin",
  "role": "ADMINISTRADOR"
}
```

### 2. Criar Outro Administrador (Só ADMIN pode)

```json
// POST /api/usuarios
// Authorization: Bearer {token_admin}
{
  "nome": "Admin Secundário",
  "username": "admin2",
  "password": "$2a$10$XqJqhPQfPkXVHkFfYvHT6ePgI0BVZhXcxKfOZfSLqKPr8oKOPSIgC",
  "email": "admin2@autoboots.com",
  "role": "ADMINISTRADOR",
  "dataCadastro": "2024-01-26T10:00:00"
}

// Response: 201 CREATED
{
  "id": 2,
  "nome": "Admin Secundário",
  "username": "admin2",
  "email": "admin2@autoboots.com",
  "role": "ADMINISTRADOR",
  "dataCadastro": "2024-01-26T10:00:00",
  "_links": {
    "self": { "href": "http://localhost:8080/api/usuarios/2" },
    "all-usuarios": { "href": "http://localhost:8080/api/usuarios" }
  }
}
```

### 3. Listar Todos os Usuários (Vê TODOS, incluindo ADMIN)

```json
// GET /api/usuarios
// Authorization: Bearer {token_admin}


// Response: 200 OK
{
  "_embedded": {
    "usuarioList": [
      {
        "id": 1,
        "nome": "Admin Master",
        "username": "admin",
        "email": "admin@autoboots.com",
        "role": "ADMINISTRADOR",
        "dataCadastro": "2024-01-15T10:30:00"
      },
      {
        "id": 2,
        "nome": "Carlos Gerente",
        "username": "carlos.gerente",
        "email": "carlos@autoboots.com",
        "role": "GERENTE",
        "dataCadastro": "2024-01-20T09:00:00"
      },
      {
        "id": 3,
        "nome": "Pedro Vendas",
        "username": "pedro.vendas",
        "email": "pedro@autoboots.com",
        "role": "VENDEDOR",
        "dataCadastro": "2024-01-22T11:00:00"
      },
      {
        "id": 4,
        "nome": "Maria Santos",
        "username": "maria.santos",
        "email": "maria@email.com",
        "role": "CLIENTE",
        "dataCadastro": "2024-01-25T14:00:00"
      }
    ]
  }
}
```

### 4. Deletar Usuário (Só ADMIN pode deletar)

```json
// DELETE /api/usuarios/5
// Authorization: Bearer {token_admin}

// Response: 204 NO CONTENT
```

### 5. Criar Empresa

```json
// POST /api/empresas
// Authorization: Bearer {token_admin}
{
  "razaoSocial": "AutoBoots Matriz Ltda",
  "nomeFantasia": "AutoBoots Matriz",
  "telefones": ["11-98765-4321", "11-3456-7890"],
  "endereco": {
    "estado": "SP",
    "cidade": "São Paulo",
    "bairro": "Centro",
    "rua": "Av. Paulista",
    "numero": "1000",
    "codigoPostal": "01310-100",
    "informacoesAdicionais": "Próximo ao metrô Trianon-MASP"
  }
}

// Response: 201 CREATED
{
  "id": 1,
  "razaoSocial": "AutoBoots Matriz Ltda",
  "nomeFantasia": "AutoBoots Matriz",
  "telefones": ["11-98765-4321", "11-3456-7890"],
  "endereco": {
    "estado": "SP",
    "cidade": "São Paulo",
    "bairro": "Centro",
    "rua": "Av. Paulista",
    "numero": "1000",
    "codigoPostal": "01310-100",
    "informacoesAdicionais": "Próximo ao metrô Trianon-MASP"
  },
  "_links": {
    "self": { "href": "http://localhost:8080/api/empresas/1" }
  }
}
```

### 6. Criar Mercadoria

```json
// POST /api/mercadorias
// Authorization: Bearer {token_admin}
{
  "nome": "Óleo Motor 5W30 Sintético",
  "valor": 45.90,
  "quantidade": 150,
  "descricao": "Óleo sintético para motor - 1 litro"
}

// Response: 201 CREATED
{
  "id": 1,
  "nome": "Óleo Motor 5W30 Sintético",
  "valor": 45.90,
  "quantidade": 150,
  "descricao": "Óleo sintético para motor - 1 litro",
  "_links": {
    "self": { "href": "http://localhost:8080/api/mercadorias/1" }
  }
}
```

### 7. Criar Serviço

```json
// POST /api/servicos
// Authorization: Bearer {token_admin}
{
  "nome": "Troca de Óleo Completa",
  "valor": 80.00,
  "descricao": "Troca de óleo + filtro + revisão básica"
}

// Response: 201 CREATED
{
  "id": 1,
  "nome": "Troca de Óleo Completa",
  "valor": 80.00,
  "descricao": "Troca de óleo + filtro + revisão básica",
  "_links": {
    "self": { "href": "http://localhost:8080/api/servicos/1" }
  }
}
```

### 8. Ver Todas as Vendas (ADMIN vê TODAS)

```json
// GET /api/vendas
// Authorization: Bearer {token_admin}

// Response: 200 OK
{
  "_embedded": {
    "vendaList": [
      {
        "id": 1,
        "dataVenda": "2024-01-26T15:30:00",
        "valorTotal": 205.80,
        "cliente": {
          "id": 4,
          "nome": "Maria Santos",
          "username": "maria.santos"
        },
        "funcionario": {
          "id": 3,
          "nome": "Pedro Vendas",
          "username": "pedro.vendas"
        },
        "veiculo": {
          "id": 1,
          "placa": "ABC-1234",
          "modelo": "Corolla"
        },
        "mercadorias": [
          { "id": 1, "nome": "Óleo Motor 5W30 Sintético", "valor": 45.90 }
        ],
        "servicos": [
          { "id": 1, "nome": "Troca de Óleo Completa", "valor": 80.00 }
        ]
      }
    ]
  }
}
```

### 9. Deletar Venda

```json
// DELETE /api/vendas/1
// Authorization: Bearer {token_admin}

// Response: 204 NO CONTENT
```

---

## 🟡 GERENTE - Exemplos Completos

### 1. Registro e Login

```json
// POST /api/auth/register
{
  "nome": "Carlos Gerente",
  "username": "carlos.gerente",
  "password": "gerente123",
  "email": "carlos@autoboots.com",
  "role": "GERENTE"
}

// Response: 200 OK
"Usuário registrado com sucesso!"
```

```json
// POST /api/auth/login
{
  "username": "carlos.gerente",
  "password": "gerente123"
}

// Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYXJsb3MuZ2VyZW50ZSIsImlhdCI6MTcwNjI4MDAwMCwiZXhwIjoxNzA2MzE2MDAwfQ.yyyyy",
  "username": "carlos.gerente",
  "role": "GERENTE"
}
```

### 2. Criar Vendedor (GERENTE pode)

```json
// POST /api/usuarios
// Authorization: Bearer {token_gerente}
{
  "nome": "Ana Vendedora",
  "username": "ana.vendas",
  "password": "$2a$10$XqJqhPQfPkXVHkFfYvHT6ePgI0BVZhXcxKfOZfSLqKPr8oKOPSIgC",
  "email": "ana@autoboots.com",
  "role": "VENDEDOR",
  "dataCadastro": "2024-01-26T10:00:00"
}

// Response: 201 CREATED
{
  "id": 5,
  "nome": "Ana Vendedora",
  "username": "ana.vendas",
  "email": "ana@autoboots.com",
  "role": "VENDEDOR",
  "dataCadastro": "2024-01-26T10:00:00"
}
```

### 3. Tentar Criar ADMINISTRADOR (NEGADO)

```json
// POST /api/usuarios
// Authorization: Bearer {token_gerente}
{
  "nome": "Tentativa Admin",
  "username": "tentativa.admin",
  "password": "$2a$10$XqJqhPQfPkXVHkFfYvHT6ePgI0BVZhXcxKfOZfSLqKPr8oKOPSIgC",
  "email": "tentativa@autoboots.com",
  "role": "ADMINISTRADOR",
  "dataCadastro": "2024-01-26T10:00:00"
}

// Response: 403 FORBIDDEN
"GERENTE não pode criar usuários do tipo ADMINISTRADOR"
```

### 4. Listar Usuários (NÃO vê ADMINISTRADOR)

```json
// GET /api/usuarios
// Authorization: Bearer {token_gerente}

// Response: 200 OK
{
  "_embedded": {
    "usuarioList": [
      {
        "id": 2,
        "nome": "Carlos Gerente",
        "username": "carlos.gerente",
        "role": "GERENTE"
      },
      {
        "id": 3,
        "nome": "Pedro Vendas",
        "username": "pedro.vendas",
        "role": "VENDEDOR"
      },
      {
        "id": 4,
        "nome": "Maria Santos",
        "username": "maria.santos",
        "role": "CLIENTE"
      }
      
    ]
  }
}
```

### 5. Criar Empresa

```json
// POST /api/empresas
// Authorization: Bearer {token_gerente}
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

// Response: 201 CREATED
```

### 6. Criar e Gerenciar Mercadorias

```json
// POST /api/mercadorias
// Authorization: Bearer {token_gerente}
{
  "nome": "Filtro de Ar Esportivo",
  "valor": 89.90,
  "quantidade": 50,
  "descricao": "Filtro de ar de alto desempenho"
}

// Response: 201 CREATED
```

```json
// PUT /api/mercadorias/2
// Authorization: Bearer {token_gerente}
{
  "nome": "Filtro de Ar Esportivo K&N",
  "valor": 95.00,
  "quantidade": 45,
  "descricao": "Filtro de ar de alto desempenho - Marca K&N"
}

// Response: 200 OK
```

```json
// DELETE /api/mercadorias/2
// Authorization: Bearer {token_gerente}

// Response: 204 NO CONTENT
```

### 7. Criar e Gerenciar Serviços

```json
// POST /api/servicos
// Authorization: Bearer {token_gerente}
{
  "nome": "Alinhamento e Balanceamento",
  "valor": 120.00,
  "descricao": "Alinhamento e balanceamento das 4 rodas"
}

// Response: 201 CREATED
```

### 8. Criar Venda

```json
// POST /api/vendas
// Authorization: Bearer {token_gerente}
{
  "dataVenda": "2024-01-27T11:00:00",
  "cliente": { "id": 4 },
  "funcionario": { "id": 3 },
  "veiculo": { "id": 1 },
  "mercadorias": [
    { "id": 1 },
    { "id": 2 }
  ],
  "servicos": [
    { "id": 1 },
    { "id": 2 }
  ]
}

// Response: 201 CREATED
{
  "id": 2,
  "dataVenda": "2024-01-27T11:00:00",
  "valorTotal": 335.80,
  "cliente": { "id": 4, "nome": "Maria Santos" },
  "funcionario": { "id": 3, "nome": "Pedro Vendas" },
  "veiculo": { "id": 1, "placa": "ABC-1234" },
  "mercadorias": [
    { "id": 1, "nome": "Óleo Motor 5W30 Sintético", "valor": 45.90 },
    { "id": 2, "nome": "Filtro de Ar Esportivo K&N", "valor": 95.00 }
  ],
  "servicos": [
    { "id": 1, "nome": "Troca de Óleo Completa", "valor": 80.00 },
    { "id": 2, "nome": "Alinhamento e Balanceamento", "valor": 120.00 }
  ]
}
```

### 9. Atualizar e Deletar Vendas (GERENTE pode)

```json
// PUT /api/vendas/2
// Authorization: Bearer {token_gerente}
{
  "cliente": { "id": 4 },
  "funcionario": { "id": 5 },
  "veiculo": { "id": 1 },
  "mercadorias": [{ "id": 1 }],
  "servicos": [{ "id": 1 }]
}

// Response: 200 OK
```

```json
// DELETE /api/vendas/2
// Authorization: Bearer {token_gerente}

// Response: 204 NO CONTENT
```

### 10. Tentar Deletar Usuário (NEGADO - só ADMIN pode)

```json
// DELETE /api/usuarios/5
// Authorization: Bearer {token_gerente}

// Response: 403 FORBIDDEN
"Acesso negado"
```

---

## 🟢 VENDEDOR - Exemplos Completos

### 1. Registro e Login

```json
// POST /api/auth/register
{
  "nome": "Pedro Vendas",
  "username": "pedro.vendas",
  "password": "vendedor123",
  "email": "pedro@autoboots.com",
  "role": "VENDEDOR"
}

// Response: 200 OK
"Usuário registrado com sucesso!"
```

```json
// POST /api/auth/login
{
  "username": "pedro.vendas",
  "password": "vendedor123"
}

// Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwZWRyby52ZW5kYXMiLCJpYXQiOjE3MDYyODAwMDAsImV4cCI6MTcwNjMxNjAwMH0.zzzzz",
  "username": "pedro.vendas",
  "role": "VENDEDOR"
}
```

### 2. Criar Cliente (VENDEDOR pode criar apenas CLIENTE)

```json
// POST /api/usuarios
// Authorization: Bearer {token_vendedor}
{
  "nome": "João Cliente",
  "username": "joao.cliente",
  "password": "$2a$10$XqJqhPQfPkXVHkFfYvHT6ePgI0BVZhXcxKfOZfSLqKPr8oKOPSIgC",
  "email": "joao@email.com",
  "role": "CLIENTE",
  "dataCadastro": "2024-01-26T11:00:00"
}

// Response: 201 CREATED
{
  "id": 6,
  "nome": "João Cliente",
  "username": "joao.cliente",
  "email": "joao@email.com",
  "role": "CLIENTE",
  "dataCadastro": "2024-01-26T11:00:00"
}
```

### 3. Tentar Criar Vendedor (NEGADO)

```json
// POST /api/usuarios
// Authorization: Bearer {token_vendedor}
{
  "nome": "Outro Vendedor",
  "username": "outro.vendedor",
  "password": "$2a$10$XqJqhPQfPkXVHkFfYvHT6ePgI0BVZhXcxKfOZfSLqKPr8oKOPSIgC",
  "email": "outro@autoboots.com",
  "role": "VENDEDOR",
  "dataCadastro": "2024-01-26T11:00:00"
}

// Response: 403 FORBIDDEN
"VENDEDOR só pode criar usuários do tipo CLIENTE"
```

### 4. Listar Usuários (Vê apenas CLIENTES)

```json
// GET /api/usuarios
// Authorization: Bearer {token_vendedor}

// Response: 200 OK
{
  "_embedded": {
    "usuarioList": [
      {
        "id": 4,
        "nome": "Maria Santos",
        "username": "maria.santos",
        "role": "CLIENTE"
      },
      {
        "id": 6,
        "nome": "João Cliente",
        "username": "joao.cliente",
        "role": "CLIENTE"
      }
      // ❌ NÃO APARECE: ADMINISTRADOR, GERENTE, outros VENDEDORES
    ]
  }
}
```

### 5. Visualizar Mercadorias (Pode VER, mas NÃO modificar)

```json
// GET /api/mercadorias
// Authorization: Bearer {token_vendedor}

// Response: 200 OK
{
  "_embedded": {
    "mercadoriaList": [
      {
        "id": 1,
        "nome": "Óleo Motor 5W30 Sintético",
        "valor": 45.90,
        "quantidade": 150
      },
      {
        "id": 2,
        "nome": "Filtro de Ar Esportivo K&N",
        "valor": 95.00,
        "quantidade": 45
      }
    ]
  }
}
```

### 6. Tentar Criar Mercadoria (NEGADO)

```json
// POST /api/mercadorias
// Authorization: Bearer {token_vendedor}
{
  "nome": "Tentativa Mercadoria",
  "valor": 100.00,
  "quantidade": 10,
  "descricao": "Teste"
}

// Response: 403 FORBIDDEN
"Acesso negado"
```

### 7. Visualizar Serviços (Pode VER, mas NÃO modificar)

```json
// GET /api/servicos
// Authorization: Bearer {token_vendedor}

// Response: 200 OK
{
  "_embedded": {
    "servicoList": [
      {
        "id": 1,
        "nome": "Troca de Óleo Completa",
        "valor": 80.00
      },
      {
        "id": 2,
        "nome": "Alinhamento e Balanceamento",
        "valor": 120.00
      }
    ]
  }
}
```

### 8. Criar Veículo para Cliente

```json
// POST /api/veiculos
// Authorization: Bearer {token_vendedor}
{
  "placa": "XYZ-9876",
  "modelo": "HB20",
  "marca": "Hyundai",
  "ano": 2023,
  "tipoVeiculo": "CARRO",
  "usuario": { "id": 6 }
}

// Response: 201 CREATED
{
  "id": 2,
  "placa": "XYZ-9876",
  "modelo": "HB20",
  "marca": "Hyundai",
  "ano": 2023,
  "tipoVeiculo": "CARRO",
  "usuario": {
    "id": 6,
    "nome": "João Cliente"
  }
}
```

### 9. Criar Venda (Onde ELE é o funcionário)

```json
// POST /api/vendas
// Authorization: Bearer {token_vendedor}
{
  "dataVenda": "2024-01-27T14:30:00",
  "cliente": { "id": 6 },
  "funcionario": { "id": 3 },  // ⚠️ DEVE ser o ID do vendedor logado
  "veiculo": { "id": 2 },
  "mercadorias": [
    { "id": 1 }
  ],
  "servicos": [
    { "id": 1 }
  ]
}

// Response: 201 CREATED
{
  "id": 3,
  "dataVenda": "2024-01-27T14:30:00",
  "valorTotal": 125.90,
  "cliente": {
    "id": 6,
    "nome": "João Cliente"
  },
  "funcionario": {
    "id": 3,
    "nome": "Pedro Vendas"
  },
  "veiculo": {
    "id": 2,
    "placa": "XYZ-9876"
  },
  "mercadorias": [
    { "id": 1, "nome": "Óleo Motor 5W30 Sintético", "valor": 45.90 }
  ],
  "servicos": [
    { "id": 1, "nome": "Troca de Óleo Completa", "valor": 80.00 }
  ]
}
```

### 10. Listar Vendas (Vê apenas SUAS vendas)

```json
// GET /api/vendas
// Authorization: Bearer {token_vendedor}

// Response: 200 OK
{
  "_embedded": {
    "vendaList": [
      {
        "id": 3,
        "dataVenda": "2024-01-27T14:30:00",
        "valorTotal": 125.90,
        "cliente": { "id": 6, "nome": "João Cliente" },
        "funcionario": { "id": 3, "nome": "Pedro Vendas" }
      }
      // ❌ NÃO APARECE: Vendas de outros vendedores
    ]
  }
}
```

### 11. Tentar Ver Venda de Outro Vendedor (NEGADO)

```json
// GET /api/vendas/1
// Authorization: Bearer {token_vendedor}

// Response: 403 FORBIDDEN
"VENDEDOR só pode visualizar suas próprias vendas"
```

### 12. Tentar Atualizar Venda (NEGADO - só ADMIN e GERENTE)

```json
// PUT /api/vendas/3
// Authorization: Bearer {token_vendedor}
{
  "cliente": { "id": 6 },
  "funcionario": { "id": 3 },
  "mercadorias": [{ "id": 2 }],
  "servicos": [{ "id": 2 }]
}

// Response: 403 FORBIDDEN
"Acesso negado"
```

### 13. Tentar Acessar Empresas (NEGADO)

```json
// GET /api/empresas
// Authorization: Bearer {token_vendedor}

// Response: 403 FORBIDDEN
"Acesso negado"
```

---

## 🔵 CLIENTE - Exemplos Completos

### 1. Registro e Login

```json
// POST /api/auth/register
{
  "nome": "Maria Santos",
  "username": "maria.santos",
  "password": "cliente123",
  "email": "maria@email.com",
  "role": "CLIENTE"
}

// Response: 200 OK
"Usuário registrado com sucesso!"
```

```json
// POST /api/auth/login
{
  "username": "maria.santos",
  "password": "cliente123"
}

// Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYXJpYS5zYW50b3MiLCJpYXQiOjE3MDYyODAwMDAsImV4cCI6MTcwNjMxNjAwMH0.wwwww",
  "username": "maria.santos",
  "role": "CLIENTE"
}
```

### 2. Visualizar Próprio Cadastro

```json
// GET /api/usuarios/4
// Authorization: Bearer {token_cliente}

// Response: 200 OK
{
  "id": 4,
  "nome": "Maria Santos",
  "username": "maria.santos",
  "email": "maria@email.com",
  "role": "CLIENTE",
  "dataCadastro": "2024-01-25T14:00:00"
}
```

### 3. Tentar Ver Outro Usuário (NEGADO)

```json
// GET /api/usuarios/1
// Authorization: Bearer {token_cliente}

// Response: 403 FORBIDDEN
"CLIENTE só pode visualizar o próprio cadastro"
```

### 4. Tentar Listar Usuários (NEGADO)

```json
// GET /api/usuarios
// Authorization: Bearer {token_cliente}

// Response: 403 FORBIDDEN
"Acesso negado"
```

### 5. Criar Próprio Veículo

```json
// POST /api/veiculos
// Authorization: Bearer {token_cliente}
{
  "placa": "ABC-1234",
  "modelo": "Gol",
  "marca": "Volkswagen",
  "ano": 2020,
  "tipoVeiculo": "CARRO",
  "usuario": { "id": 4 }  // ⚠️ DEVE ser o próprio ID
}

// Response: 201 CREATED
{
  "id": 1,
  "placa": "ABC-1234",
  "modelo": "Gol",
  "marca": "Volkswagen",
  "ano": 2020,
  "tipoVeiculo": "CARRO",
  "usuario": {
    "id": 4,
    "nome": "Maria Santos"
  }
}
```

### 6. Listar Próprios Veículos

```json
// GET /api/veiculos
// Authorization: Bearer {token_cliente}

// Response: 200 OK
{
  "_embedded": {
    "veiculoList": [
      {
        "id": 1,
        "placa": "ABC-1234",
        "modelo": "Gol",
        "marca": "Volkswagen",
        "ano": 2020,
        "tipoVeiculo": "CARRO",
        "usuario": { "id": 4, "nome": "Maria Santos" }
      }
      // ❌ NÃO APARECE: Veículos de outros clientes
    ]
  }
}
```

### 7. Atualizar Próprio Veículo

```json
// PUT /api/veiculos/1
// Authorization: Bearer {token_cliente}
{
  "placa": "ABC-1234",
  "modelo": "Gol G7",
  "marca": "Volkswagen",
  "ano": 2020,
  "tipoVeiculo": "CARRO"
}

// Response: 200 OK
{
  "id": 1,
  "placa": "ABC-1234",
  "modelo": "Gol G7",
  "marca": "Volkswagen",
  "ano": 2020,
  "tipoVeiculo": "CARRO"
}
```

### 8. Tentar Ver Veículo de Outro Cliente (NEGADO)

```json
// GET /api/veiculos/2
// Authorization: Bearer {token_cliente}

// Response: 403 FORBIDDEN
"CLIENTE só pode visualizar seus próprios veículos"
```

### 9. Listar Próprias Compras

```json
// GET /api/vendas
// Authorization: Bearer {token_cliente}

// Response: 200 OK
{
  "_embedded": {
    "vendaList": [
      {
        "id": 1,
        "dataVenda": "2024-01-26T15:30:00",
        "valorTotal": 205.80,
        "cliente": {
          "id": 4,
          "nome": "Maria Santos"
        },
        "funcionario": {
          "id": 3,
          "nome": "Pedro Vendas"
        },
        "mercadorias": [
          { "id": 1, "nome": "Óleo Motor 5W30 Sintético", "valor": 45.90 }
        ],
        "servicos": [
          { "id": 1, "nome": "Troca de Óleo Completa", "valor": 80.00 }
        ]
      }
      // ❌ NÃO APARECE: Vendas de outros clientes
    ]
  }
}
```

### 10. Ver Detalhes de Própria Compra

```json
// GET /api/vendas/1
// Authorization: Bearer {token_cliente}

// Response: 200 OK
{
  "id": 1,
  "dataVenda": "2024-01-26T15:30:00",
  "valorTotal": 205.80,
  "cliente": {
    "id": 4,
    "nome": "Maria Santos",
    "username": "maria.santos"
  },
  "funcionario": {
    "id": 3,
    "nome": "Pedro Vendas",
    "username": "pedro.vendas"
  },
  "veiculo": {
    "id": 1,
    "placa": "ABC-1234",
    "modelo": "Gol G7"
  },
  "mercadorias": [
    {
      "id": 1,
      "nome": "Óleo Motor 5W30 Sintético",
      "valor": 45.90,
      "quantidade": 150
    }
  ],
  "servicos": [
    {
      "id": 1,
      "nome": "Troca de Óleo Completa",
      "valor": 80.00
    }
  ]
}
```

### 11. Tentar Ver Compra de Outro Cliente (NEGADO)

```json
// GET /api/vendas/3
// Authorization: Bearer {token_cliente}

// Response: 403 FORBIDDEN
"CLIENTE só pode visualizar vendas onde ele é o cliente"
```

### 12. Tentar Criar Venda (NEGADO)

```json
// POST /api/vendas
// Authorization: Bearer {token_cliente}
{
  "dataVenda": "2024-01-27T16:00:00",
  "cliente": { "id": 4 },
  "funcionario": { "id": 3 },
  "veiculo": { "id": 1 },
  "mercadorias": [{ "id": 1 }],
  "servicos": [{ "id": 1 }]
}

// Response: 403 FORBIDDEN
"Acesso negado"
```

### 13. Tentar Acessar Empresas (NEGADO)

```json
// GET /api/empresas
// Authorization: Bearer {token_cliente}

// Response: 403 FORBIDDEN
"Acesso negado"
```

### 14. Tentar Visualizar Mercadorias (NEGADO)

```json
// GET /api/mercadorias
// Authorization: Bearer {token_cliente}

// Response: 403 FORBIDDEN
"Acesso negado"
```

### 15. Tentar Visualizar Serviços (NEGADO)

```json
// GET /api/servicos
// Authorization: Bearer {token_cliente}

// Response: 403 FORBIDDEN
"Acesso negado"
```

### 16. Tentar Deletar Próprio Veículo (NEGADO - só ADMIN/GERENTE podem)

```json
// DELETE /api/veiculos/1
// Authorization: Bearer {token_cliente}

// Response: 403 FORBIDDEN
"Acesso negado"
```

---

## 📊 Resumo de Permissões por Endpoint

| Endpoint | ADMIN | GERENTE | VENDEDOR | CLIENTE |
|----------|-------|---------|----------|---------|
| **POST /api/auth/register** | ✅ | ✅ | ✅ | ✅ |
| **POST /api/auth/login** | ✅ | ✅ | ✅ | ✅ |
| **GET /api/usuarios** | ✅ Todos | ✅ Não vê ADMIN | ✅ Só CLIENTES | ❌ |
| **GET /api/usuarios/{id}** | ✅ Todos | ✅ Não vê ADMIN | ✅ Só CLIENTES | ✅ Só próprio |
| **POST /api/usuarios** | ✅ Todos | ✅ Não pode criar ADMIN | ✅ Só CLIENTE | ❌ |
| **PUT /api/usuarios/{id}** | ✅ Todos | ✅ Não pode editar ADMIN | ✅ Só CLIENTE | ❌ |
| **DELETE /api/usuarios/{id}** | ✅ | ❌ | ❌ | ❌ |
| **GET /api/empresas** | ✅ | ✅ | ❌ | ❌ |
| **POST /api/empresas** | ✅ | ✅ | ❌ | ❌ |
| **PUT /api/empresas/{id}** | ✅ | ✅ | ❌ | ❌ |
| **DELETE /api/empresas/{id}** | ✅ | ✅ | ❌ | ❌ |
| **GET /api/mercadorias** | ✅ | ✅ | ✅ | ❌ |
| **POST /api/mercadorias** | ✅ | ✅ | ❌ | ❌ |
| **PUT /api/mercadorias/{id}** | ✅ | ✅ | ❌ | ❌ |
| **DELETE /api/mercadorias/{id}** | ✅ | ✅ | ❌ | ❌ |
| **GET /api/servicos** | ✅ | ✅ | ✅ | ❌ |
| **POST /api/servicos** | ✅ | ✅ | ❌ | ❌ |
| **PUT /api/servicos/{id}** | ✅ | ✅ | ❌ | ❌ |
| **DELETE /api/servicos/{id}** | ✅ | ✅ | ❌ | ❌ |
| **GET /api/veiculos** | ✅ Todos | ✅ Todos | ✅ Todos | ✅ Só próprios |
| **POST /api/veiculos** | ✅ | ✅ | ✅ | ✅ Para si |
| **PUT /api/veiculos/{id}** | ✅ | ✅ | ✅ | ✅ Só próprios |
| **DELETE /api/veiculos/{id}** | ✅ | ✅ | ❌ | ❌ |
| **GET /api/vendas** | ✅ Todas | ✅ Todas | ✅ Só suas | ✅ Só suas compras |
| **GET /api/vendas/{id}** | ✅ Todas | ✅ Todas | ✅ Só suas | ✅ Só suas compras |
| **POST /api/vendas** | ✅ | ✅ | ✅ Onde é funcionário | ❌ |
| **PUT /api/vendas/{id}** | ✅ | ✅ | ❌ | ❌ |
| **DELETE /api/vendas/{id}** | ✅ | ✅ | ❌ | ❌ |

---

## 🔒 Códigos de Status HTTP

| Código | Significado | Quando Ocorre |
|--------|-------------|---------------|
| `200 OK` | Sucesso | Requisição bem-sucedida (GET, PUT) |
| `201 CREATED` | Criado | Recurso criado com sucesso (POST) |
| `204 NO CONTENT` | Sem conteúdo | Recurso deletado com sucesso (DELETE) |
| `400 BAD REQUEST` | Requisição inválida | Dados inválidos ou malformados |
| `401 UNAUTHORIZED` | Não autorizado | Token ausente ou inválido |
| `403 FORBIDDEN` | Proibido | Sem permissão para acessar recurso |
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
git clone https://github.com/seu-usuario/autoboots.git

# Entrar no diretório
cd autoboots

# Compilar
mvn clean install

# Executar
mvn spring-boot:run
```

### Acessar H2 Console
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (deixe em branco)
```

---

## 🚀 Testando com cURL

### Exemplo Completo: Fluxo de Venda

```bash
# 1. Login como ADMIN
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Resposta: { "token": "eyJhbG...", "role": "ADMINISTRADOR" }

# 2. Criar VENDEDOR
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbG..." \
  -d '{
    "nome": "Pedro Vendas",
    "username": "pedro.vendas",
    "password": "$2a$10$...",
    "email": "pedro@autoboots.com",
    "role": "VENDEDOR",
    "dataCadastro": "2024-01-26T10:00:00"
  }'

# 3. Login como VENDEDOR
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"pedro.vendas","password":"vendedor123"}'

# 4. Criar CLIENTE
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token_vendedor}" \
  -d '{
    "nome": "João Cliente",
    "username": "joao.cliente",
    "password": "$2a$10$...",
    "email": "joao@email.com",
    "role": "CLIENTE",
    "dataCadastro": "2024-01-26T11:00:00"
  }'

# 5. Listar Mercadorias
curl -X GET http://localhost:8080/api/mercadorias \
  -H "Authorization: Bearer {token_vendedor}"

# 6. Criar Venda
curl -X POST http://localhost:8080/api/vendas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token_vendedor}" \
  -d '{
    "dataVenda": "2024-01-27T14:30:00",
    "cliente": { "id": 10 },
    "funcionario": { "id": 3 },
    "veiculo": { "id": 5 },
    "mercadorias": [{ "id": 1 }, { "id": 2 }],
    "servicos": [{ "id": 1 }]
  }'
```

---

## 📝 Observações Importantes

### Senhas
- Todas as senhas devem ser criptografadas com BCrypt antes de serem salvas
- Exemplo de hash BCrypt: `$2a$10$XqJqhPQfPkXVHkFfYvHT6ePgI0BVZhXcxKfOZfSLqKPr8oKOPSIgC`

### Tokens JWT
- Expiram em **10 horas**
- Devem ser incluídos em TODAS as requisições protegidas
- Formato: `Authorization: Bearer {token}`

### HATEOAS
- Todos os recursos retornam links para navegação
- Links variam de acordo com as permissões do usuário

### Hierarquia de Permissões
```
ADMINISTRADOR (poder absoluto)
    ↓
GERENTE (gerencia tudo, exceto ADMIN)
    ↓
VENDEDOR (foco em vendas e clientes)
    ↓
CLIENTE (acesso muito restrito)
```

### Validações Importantes
- Username deve ser único
- Email deve ser único
- Campos obrigatórios são validados
- Datas são geradas automaticamente quando não informadas
- `valorTotal` em vendas é calculado automaticamente

---

## 🛠 Ferramentas Recomendadas

- **Postman** - Para testar APIs
- **Insomnia** - Alternativa ao Postman
- **cURL** - Linha de comando
- **VS Code com REST Client** - Extensão para testar APIs


---

**Desenvolvido para o projeto AutoBoots - Sistema de Gerenciamento de Autopeças com Spring Boot e JWT** 🚗✨