# 🚗 AutoBoots - Sistema de Gerenciamento de Autopeças

Sistema de gerenciamento para autopeças com autenticação JWT e controle de acesso baseado em roles (RBAC).

## 📋 Índice

- [Tecnologias](#tecnologias)
- [Arquitetura de Segurança](#arquitetura-de-segurança)
- [Roles e Permissões](#roles-e-permissões)
- [Matriz Completa de Permissões](#matriz-completa-de-permissões)
- [Endpoints da API por Role](#endpoints-da-api-por-role)

---

## 🛠 Tecnologias

- **Java 17+**
- **Spring Boot 3.2.1**
- **Spring Security 6.x**
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

✅ Pode fazer TODAS as operações CRUD em TODOS os recursos  
✅ Único que pode criar/remover outros ADMINISTRADORES  
✅ Único que pode DELETAR usuários  
✅ Vê e gerencia TODOS os usuários (incluindo outros ADMINs)

### 🟡 GERENTE
**Gerencia Operações (exceto ADMINISTRADOR)**

✅ CRUD completo em: Empresas, Mercadorias, Serviços, Veículos, Vendas  
✅ CRUD em usuários (exceto ADMIN): Gerente, Vendedor, Cliente  
❌ NÃO pode ver/criar/editar ADMINISTRADORES  
❌ NÃO pode deletar nenhum usuário

### 🟢 VENDEDOR
**Foco em Vendas e Clientes**

✅ CRUD completo em CLIENTES (criar/editar/visualizar)  
✅ Leitura (GET) de Mercadorias e Serviços  
✅ CRUD de Veículos  
✅ Criar vendas (onde ele é o funcionário)  
✅ Ver apenas suas próprias vendas  
❌ NÃO pode acessar Empresas  
❌ NÃO pode criar/editar Mercadorias ou Serviços  
❌ NÃO pode editar/deletar vendas

### 🔵 CLIENTE
**Acesso Muito Restrito**

✅ Ver apenas SEU próprio cadastro  
✅ CRUD de seus próprios veículos  
✅ Ver apenas suas próprias compras (vendas)  
❌ NÃO pode acessar Empresas, Mercadorias, Serviços  
❌ NÃO pode criar vendas  
❌ NÃO pode ver outros usuários

---

## 📊 Matriz Completa de Permissões

### **USUÁRIOS** (`/api/usuarios`)

| Operação | Endpoint | ADMIN | GERENTE | VENDEDOR | CLIENTE |
|----------|----------|-------|---------|----------|---------|
| **Listar todos** | `GET /usuarios` | ✅ Vê todos | ✅ Não vê ADMIN | ✅ Só CLIENTES | ❌ |
| **Ver detalhes** | `GET /usuarios/{id}` | ✅ Qualquer um | ✅ Não vê ADMIN | ✅ Só CLIENTES | ✅ Só próprio |
| **Criar** | `POST /usuarios` | ✅ Qualquer role | ✅ Não cria ADMIN | ✅ Só CLIENTE | ❌ |
| **Atualizar** | `PUT /usuarios/{id}` | ✅ Qualquer um | ✅ Não edita ADMIN | ✅ Só CLIENTE | ❌ |
| **Deletar** | `DELETE /usuarios/{id}` | ✅ | ❌ | ❌ | ❌ |

---

### **EMPRESAS** (`/api/empresas`)

| Operação | Endpoint | ADMIN | GERENTE | VENDEDOR | CLIENTE |
|----------|----------|-------|---------|----------|---------|
| **Listar todas** | `GET /empresas` | ✅ | ✅ | ❌ | ❌ |
| **Ver detalhes** | `GET /empresas/{id}` | ✅ | ✅ | ❌ | ❌ |
| **Criar** | `POST /empresas` | ✅ | ✅ | ❌ | ❌ |
| **Atualizar** | `PUT /empresas/{id}` | ✅ | ✅ | ❌ | ❌ |
| **Deletar** | `DELETE /empresas/{id}` | ✅ | ✅ | ❌ | ❌ |

---

### **MERCADORIAS** (`/api/mercadorias`)

| Operação | Endpoint | ADMIN | GERENTE | VENDEDOR | CLIENTE |
|----------|----------|-------|---------|----------|---------|
| **Listar todas** | `GET /mercadorias` | ✅ | ✅ | ✅ Só leitura | ❌ |
| **Ver detalhes** | `GET /mercadorias/{id}` | ✅ | ✅ | ✅ Só leitura | ❌ |
| **Criar** | `POST /mercadorias` | ✅ | ✅ | ❌ | ❌ |
| **Atualizar** | `PUT /mercadorias/{id}` | ✅ | ✅ | ❌ | ❌ |
| **Deletar** | `DELETE /mercadorias/{id}` | ✅ | ✅ | ❌ | ❌ |

---

### **SERVIÇOS** (`/api/servicos`)

| Operação | Endpoint | ADMIN | GERENTE | VENDEDOR | CLIENTE |
|----------|----------|-------|---------|----------|---------|
| **Listar todos** | `GET /servicos` | ✅ | ✅ | ✅ Só leitura | ❌ |
| **Ver detalhes** | `GET /servicos/{id}` | ✅ | ✅ | ✅ Só leitura | ❌ |
| **Criar** | `POST /servicos` | ✅ | ✅ | ❌ | ❌ |
| **Atualizar** | `PUT /servicos/{id}` | ✅ | ✅ | ❌ | ❌ |
| **Deletar** | `DELETE /servicos/{id}` | ✅ | ✅ | ❌ | ❌ |

---

### **VEÍCULOS** (`/api/veiculos`)

| Operação | Endpoint | ADMIN | GERENTE | VENDEDOR | CLIENTE |
|----------|----------|-------|---------|----------|---------|
| **Listar todos** | `GET /veiculos` | ✅ Todos | ✅ Todos | ✅ Todos | ✅ Só próprios |
| **Ver detalhes** | `GET /veiculos/{id}` | ✅ Qualquer um | ✅ Qualquer um | ✅ Qualquer um | ✅ Só próprios |
| **Criar** | `POST /veiculos` | ✅ | ✅ | ✅ | ✅ Só p/ si |
| **Atualizar** | `PUT /veiculos/{id}` | ✅ Qualquer um | ✅ Qualquer um | ✅ Qualquer um | ✅ Só próprios |
| **Deletar** | `DELETE /veiculos/{id}` | ✅ | ✅ | ❌ | ❌ |

---

### **VENDAS** (`/api/vendas`)

| Operação | Endpoint | ADMIN | GERENTE | VENDEDOR | CLIENTE |
|----------|----------|-------|---------|----------|---------|
| **Listar todas** | `GET /vendas` | ✅ Todas | ✅ Todas | ✅ Só suas | ✅ Só compras |
| **Ver detalhes** | `GET /vendas/{id}` | ✅ Qualquer uma | ✅ Qualquer uma | ✅ Só suas | ✅ Só compras |
| **Criar** | `POST /vendas` | ✅ | ✅ | ✅ Como func. | ❌ |
| **Atualizar** | `PUT /vendas/{id}` | ✅ | ✅ | ❌ | ❌ |
| **Deletar** | `DELETE /vendas/{id}` | ✅ | ✅ | ❌ | ❌ |

---

## 🌐 Endpoints da API por Role

### 🔴 ADMINISTRADOR - Pode Tudo

| Método | Endpoint | ✅ ADMIN | Descrição |
|--------|----------|----------|-----------|
| **POST** | `/api/auth/login` | ✅ | Login |
| **GET** | `/api/usuarios` | ✅ Vê TODOS | Listar todos usuários |
| **GET** | `/api/usuarios/{id}` | ✅ Qualquer um | Ver qualquer usuário |
| **POST** | `/api/usuarios` | ✅ Qualquer role | Criar qualquer usuário |
| **POST** | `/api/auth/register` | ✅ Qualquer role | Criar qualquer usuário (alt) |
| **PUT** | `/api/usuarios/{id}` | ✅ Qualquer um | Editar qualquer usuário |
| **DELETE** | `/api/usuarios/{id}` | ✅ | Deletar usuário |
| **GET/POST/PUT/DELETE** | `/api/empresas/**` | ✅ | CRUD completo empresas |
| **GET/POST/PUT/DELETE** | `/api/mercadorias/**` | ✅ | CRUD completo mercadorias |
| **GET/POST/PUT/DELETE** | `/api/servicos/**` | ✅ | CRUD completo serviços |
| **GET/POST/PUT/DELETE** | `/api/veiculos/**` | ✅ | CRUD completo veículos |
| **GET/POST/PUT/DELETE** | `/api/vendas/**` | ✅ Todas | CRUD completo vendas |

---

### 🟡 GERENTE - Gerencia Tudo (exceto ADMIN)

| Método | Endpoint | ✅ GERENTE | Descrição |
|--------|----------|------------|-----------|
| **POST** | `/api/auth/login` | ✅ | Login |
| **GET** | `/api/usuarios` | ✅ Não vê ADMIN | Listar (sem ADMIN) |
| **GET** | `/api/usuarios/{id}` | ✅ Não vê ADMIN | Ver (exceto ADMIN) |
| **POST** | `/api/usuarios` | ✅ Não cria ADMIN | Criar (exceto ADMIN) |
| **POST** | `/api/auth/register` | ✅ Não cria ADMIN | Criar (exceto ADMIN) |
| **PUT** | `/api/usuarios/{id}` | ✅ Não edita ADMIN | Editar (exceto ADMIN) |
| **DELETE** | `/api/usuarios/{id}` | ❌ | **NEGADO** |
| **GET/POST/PUT/DELETE** | `/api/empresas/**` | ✅ | CRUD completo empresas |
| **GET/POST/PUT/DELETE** | `/api/mercadorias/**` | ✅ | CRUD completo mercadorias |
| **GET/POST/PUT/DELETE** | `/api/servicos/**` | ✅ | CRUD completo serviços |
| **GET/POST/PUT/DELETE** | `/api/veiculos/**` | ✅ | CRUD completo veículos |
| **GET/POST/PUT/DELETE** | `/api/vendas/**` | ✅ Todas | CRUD completo vendas |

---

### 🟢 VENDEDOR - Vendas e Clientes

| Método | Endpoint | ✅ VENDEDOR | Descrição |
|--------|----------|-------------|-----------|
| **POST** | `/api/auth/login` | ✅ | Login |
| **GET** | `/api/usuarios` | ✅ Só CLIENTES | Listar apenas clientes |
| **GET** | `/api/usuarios/{id}` | ✅ Só CLIENTES | Ver apenas clientes |
| **POST** | `/api/usuarios` | ✅ Só CLIENTE | Criar apenas cliente |
| **POST** | `/api/auth/register` | ✅ Só CLIENTE | Criar apenas cliente |
| **PUT** | `/api/usuarios/{id}` | ✅ Só CLIENTE | Editar apenas cliente |
| **DELETE** | `/api/usuarios/{id}` | ❌ | **NEGADO** |
| **GET/POST/PUT/DELETE** | `/api/empresas/**` | ❌ | **NEGADO** |
| **GET** | `/api/mercadorias` | ✅ Só leitura | Listar mercadorias |
| **GET** | `/api/mercadorias/{id}` | ✅ Só leitura | Ver mercadoria |
| **POST/PUT/DELETE** | `/api/mercadorias/**` | ❌ | **NEGADO** |
| **GET** | `/api/servicos` | ✅ Só leitura | Listar serviços |
| **GET** | `/api/servicos/{id}` | ✅ Só leitura | Ver serviço |
| **POST/PUT/DELETE** | `/api/servicos/**` | ❌ | **NEGADO** |
| **GET/POST/PUT** | `/api/veiculos/**` | ✅ | CRUD (exceto DELETE) |
| **DELETE** | `/api/veiculos/{id}` | ❌ | **NEGADO** |
| **GET** | `/api/vendas` | ✅ Só suas | Listar suas vendas |
| **GET** | `/api/vendas/{id}` | ✅ Só suas | Ver suas vendas |
| **POST** | `/api/vendas` | ✅ Como func. | Criar venda (onde é funcionário) |
| **PUT/DELETE** | `/api/vendas/**` | ❌ | **NEGADO** |

---

### 🔵 CLIENTE - Apenas Próprios Dados

| Método | Endpoint | ✅ CLIENTE | Descrição |
|--------|----------|-----------|-----------|
| **POST** | `/api/auth/login` | ✅ | Login |
| **GET** | `/api/usuarios` | ❌ | **NEGADO** |
| **GET** | `/api/usuarios/{id}` | ✅ Só próprio | Ver apenas próprio cadastro |
| **POST/PUT/DELETE** | `/api/usuarios/**` | ❌ | **NEGADO** |
| **GET/POST/PUT/DELETE** | `/api/empresas/**` | ❌ | **NEGADO** |
| **GET/POST/PUT/DELETE** | `/api/mercadorias/**` | ❌ | **NEGADO** |
| **GET/POST/PUT/DELETE** | `/api/servicos/**` | ❌ | **NEGADO** |
| **GET** | `/api/veiculos` | ✅ Só próprios | Listar próprios veículos |
| **GET** | `/api/veiculos/{id}` | ✅ Só próprios | Ver próprio veículo |
| **POST** | `/api/veiculos` | ✅ Para si | Criar próprio veículo |
| **PUT** | `/api/veiculos/{id}` | ✅ Só próprios | Editar próprio veículo |
| **DELETE** | `/api/veiculos/{id}` | ❌ | **NEGADO** |
| **GET** | `/api/vendas` | ✅ Só compras | Listar próprias compras |
| **GET** | `/api/vendas/{id}` | ✅ Só compras | Ver própria compra |
| **POST/PUT/DELETE** | `/api/vendas/**` | ❌ | **NEGADO** |

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

### Credenciais Padrão
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
- Formato: `Authorization: Bearer {token}`
- Devem ser incluídos em TODAS as requisições protegidas

### Senhas
- Criptografadas com BCrypt
- Podem ser enviadas em texto plano (sistema criptografa)
- Ou pré-criptografadas (sistema detecta e não criptografa novamente)

### HATEOAS
- Todos os recursos retornam links para navegação
- Links variam de acordo com as permissões do usuário

### Hierarquia de Permissões
```
ADMINISTRADOR (poder absoluto)
    ↓
GERENTE (tudo exceto ADMIN)
    ↓  
VENDEDOR (clientes e vendas)
    ↓
CLIENTE (apenas próprios dados)
```

---

## 📝 Exemplos de Criação (POST) de Recursos

### **1. Criar USUÁRIO**

#### **Criar ADMINISTRADOR (só ADMIN pode)**
```json
POST http://localhost:8080/api/usuarios
Authorization: Bearer {{token_admin}}
Content-Type: application/json

{
  "nome": "Roberto Admin Silva",
  "username": "roberto.admin",
  "password": "admin2024",
  "email": "roberto.admin@autoboots.com",
  "role": "ADMINISTRADOR"
}
```

#### **Criar GERENTE (ADMIN ou GERENTE podem)**
```json
POST http://localhost:8080/api/usuarios
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nome": "Fernanda Gerente Costa",
  "username": "fernanda.gerente",
  "password": "gerente2024",
  "email": "fernanda.gerente@autoboots.com",
  "role": "GERENTE"
}
```

#### **Criar VENDEDOR (ADMIN, GERENTE ou VENDEDOR podem)**
```json
POST http://localhost:8080/api/usuarios
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nome": "Pedro Vendas Santos",
  "username": "pedro.vendas",
  "password": "vendedor2024",
  "email": "pedro.vendas@autoboots.com",
  "role": "VENDEDOR"
}
```

#### **Criar CLIENTE (ADMIN, GERENTE ou VENDEDOR podem)**
```json
POST http://localhost:8080/api/usuarios
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nome": "Maria Cliente Silva",
  "username": "maria.cliente",
  "password": "cliente2024",
  "email": "maria.cliente@email.com",
  "role": "CLIENTE"
}
```

---

### **2. Criar EMPRESA** (ADMIN ou GERENTE)

```json
POST http://localhost:8080/api/empresas
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "razaoSocial": "AutoBoots Matriz Ltda",
  "nomeFantasia": "AutoBoots Matriz",
  "telefones": ["11-98765-4321", "11-3456-7890"],
  "endereco": {
    "estado": "SP",
    "cidade": "São Paulo",
    "bairro": "Vila Mariana",
    "rua": "Av. Domingos de Morais",
    "numero": "2564",
    "codigoPostal": "04035-000",
    "informacoesAdicionais": "Próximo ao metrô Santa Cruz"
  }
}
```

**Outros Exemplos de Empresas:**

```json
{
  "razaoSocial": "AutoBoots Filial RJ Ltda",
  "nomeFantasia": "AutoBoots Rio",
  "telefones": ["21-99888-7766", "21-3344-5566"],
  "endereco": {
    "estado": "RJ",
    "cidade": "Rio de Janeiro",
    "bairro": "Centro",
    "rua": "Av. Rio Branco",
    "numero": "156",
    "codigoPostal": "20040-901",
    "informacoesAdicionais": "Próximo à Praça XV"
  }
}
```

```json
{
  "razaoSocial": "AutoBoots Sul Ltda",
  "nomeFantasia": "AutoBoots Porto Alegre",
  "telefones": ["51-99777-8899", "51-3222-4455"],
  "endereco": {
    "estado": "RS",
    "cidade": "Porto Alegre",
    "bairro": "Centro Histórico",
    "rua": "Rua dos Andradas",
    "numero": "1001",
    "codigoPostal": "90020-015",
    "informacoesAdicionais": "Próximo ao Mercado Público"
  }
}
```

---

### **3. Criar MERCADORIA** (ADMIN ou GERENTE)

```json
POST http://localhost:8080/api/mercadorias
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nome": "Óleo Motor 5W30 Sintético",
  "valor": 45.90,
  "quantidade": 150,
  "descricao": "Óleo sintético para motor - 1 litro - Aprovado API SN"
}
```

**Outros Exemplos de Mercadorias:**

```json
{
  "nome": "Filtro de Ar Esportivo K&N",
  "valor": 129.90,
  "quantidade": 80,
  "descricao": "Filtro de ar de alto desempenho - Lavável e reutilizável"
}
```

```json
{
  "nome": "Pastilha de Freio Dianteira",
  "valor": 189.90,
  "quantidade": 60,
  "descricao": "Pastilha de freio cerâmica - Alta performance e durabilidade"
}
```

```json
{
  "nome": "Bateria 60Ah Moura",
  "valor": 379.90,
  "quantidade": 25,
  "descricao": "Bateria automotiva 60Ah - 12V - Livre de manutenção"
}
```

```json
{
  "nome": "Pneu 185/65 R15 Pirelli",
  "valor": 289.90,
  "quantidade": 100,
  "descricao": "Pneu aro 15 - Cinturato P1 - Excelente aderência"
}
```

```json
{
  "nome": "Vela de Ignição NGK",
  "valor": 18.90,
  "quantidade": 200,
  "descricao": "Vela de ignição platinada - Maior durabilidade"
}
```

---

### **4. Criar SERVIÇO** (ADMIN ou GERENTE)

```json
POST http://localhost:8080/api/servicos
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nome": "Troca de Óleo Completa",
  "valor": 95.00,
  "descricao": "Troca de óleo sintético + filtro de óleo + filtro de ar + revisão de 22 itens"
}
```

**Outros Exemplos de Serviços:**

```json
{
  "nome": "Alinhamento e Balanceamento",
  "valor": 120.00,
  "descricao": "Alinhamento computadorizado + balanceamento das 4 rodas"
}
```

```json
{
  "nome": "Revisão Completa 10.000km",
  "valor": 350.00,
  "descricao": "Revisão completa: troca de óleo, filtros, verificação de freios, suspensão e geometria"
}
```

```json
{
  "nome": "Troca de Pastilhas de Freio",
  "valor": 180.00,
  "descricao": "Troca de pastilhas dianteiras ou traseiras + limpeza do sistema"
}
```

```json
{
  "nome": "Troca de Bateria",
  "valor": 50.00,
  "descricao": "Troca de bateria + teste do sistema elétrico + limpeza dos terminais"
}
```

```json
{
  "nome": "Geometria Completa",
  "valor": 89.90,
  "descricao": "Alinhamento + balanceamento + cambagem + caster"
}
```

```json
{
  "nome": "Diagnóstico Eletrônico",
  "valor": 120.00,
  "descricao": "Diagnóstico completo por computador + relatório detalhado"
}
```

---

### **5. Criar VEÍCULO** (Todos podem, com restrições)

#### **Como ADMIN/GERENTE/VENDEDOR (para qualquer cliente)**
```json
POST http://localhost:8080/api/veiculos
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "placa": "ABC-1234",
  "modelo": "Corolla",
  "marca": "Toyota",
  "ano": 2022,
  "tipoVeiculo": "CARRO",
  "usuario": {
    "id": 4
  }
}
```

#### **Como CLIENTE (apenas para si mesmo)**
```json
POST http://localhost:8080/api/veiculos
Authorization: Bearer {{token_cliente}}
Content-Type: application/json

{
  "placa": "XYZ-5678",
  "modelo": "Civic",
  "marca": "Honda",
  "ano": 2023,
  "tipoVeiculo": "CARRO",
  "usuario": {
    "id": 4
  }
}
```

**Outros Exemplos de Veículos:**

```json
{
  "placa": "DEF-9876",
  "modelo": "HB20",
  "marca": "Hyundai",
  "ano": 2021,
  "tipoVeiculo": "CARRO",
  "usuario": { "id": 5 }
}
```

```json
{
  "placa": "GHI-3456",
  "modelo": "Onix",
  "marca": "Chevrolet",
  "ano": 2024,
  "tipoVeiculo": "CARRO",
  "usuario": { "id": 6 }
}
```

```json
{
  "placa": "JKL-7890",
  "modelo": "CB 500",
  "marca": "Honda",
  "ano": 2023,
  "tipoVeiculo": "MOTO",
  "usuario": { "id": 7 }
}
```

```json
{
  "placa": "MNO-1357",
  "modelo": "Hilux",
  "marca": "Toyota",
  "ano": 2022,
  "tipoVeiculo": "CAMINHONETE",
  "usuario": { "id": 8 }
}
```

**Tipos de Veículo Disponíveis:**
- `CARRO`
- `MOTO`
- `CAMINHONETE`
- `VAN`

---

### **6. Criar VENDA** (ADMIN, GERENTE ou VENDEDOR)

```json
POST http://localhost:8080/api/vendas
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "dataVenda": "2024-12-10T14:30:00",
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
    { "id": 1 },
    { "id": 2 }
  ],
  "servicos": [
    { "id": 1 },
    { "id": 2 }
  ]
}
```

**Exemplo de Venda Completa:**

```json
POST http://localhost:8080/api/vendas
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "dataVenda": "2024-12-10T16:45:00",
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
    { "id": 1 },
    { "id": 3 },
    { "id": 6 }
  ],
  "servicos": [
    { "id": 1 },
    { "id": 2 },
    { "id": 3 }
  ]
}
```

**Exemplo de Venda Simples (só serviço):**

```json
{
  "dataVenda": "2024-12-10T10:00:00",
  "cliente": { "id": 5 },
  "funcionario": { "id": 3 },
  "veiculo": { "id": 2 },
  "mercadorias": [],
  "servicos": [
    { "id": 2 }
  ]
}
```

**Exemplo de Venda (só mercadorias):**

```json
{
  "dataVenda": "2024-12-10T11:30:00",
  "cliente": { "id": 6 },
  "funcionario": { "id": 3 },
  "veiculo": { "id": 3 },
  "mercadorias": [
    { "id": 4 },
    { "id": 5 }
  ],
  "servicos": []
}
```

**Observações sobre Vendas:**
- ✅ `valorTotal` é calculado automaticamente (soma de mercadorias + serviços)
- ✅ Se `dataVenda` não for informada, usa a data/hora atual
- ✅ VENDEDOR só pode criar vendas onde ELE é o funcionário
- ✅ Pode incluir só mercadorias, só serviços, ou ambos

---

## 📋 Fluxo Completo de Cadastro

```http
### 1. LOGIN COMO ADMIN
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

### 2. CRIAR EMPRESA
POST http://localhost:8080/api/empresas
Authorization: Bearer {{token}}
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
    "codigoPostal": "01310-100"
  }
}

### 3. CRIAR MERCADORIAS
POST http://localhost:8080/api/mercadorias
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nome": "Óleo Motor 5W30",
  "valor": 45.90,
  "quantidade": 150,
  "descricao": "Óleo sintético"
}

### 4. CRIAR SERVIÇOS
POST http://localhost:8080/api/servicos
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nome": "Troca de Óleo",
  "valor": 95.00,
  "descricao": "Troca completa"
}

### 5. CRIAR VENDEDOR
POST http://localhost:8080/api/usuarios
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nome": "Pedro Vendas",
  "username": "pedro.vendas",
  "password": "vendedor123",
  "email": "pedro@autoboots.com",
  "role": "VENDEDOR"
}

### 6. CRIAR CLIENTE
POST http://localhost:8080/api/usuarios
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nome": "Maria Cliente",
  "username": "maria.cliente",
  "password": "cliente123",
  "email": "maria@email.com",
  "role": "CLIENTE"
}

### 7. CRIAR VEÍCULO DO CLIENTE
POST http://localhost:8080/api/veiculos
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "placa": "ABC-1234",
  "modelo": "Corolla",
  "marca": "Toyota",
  "ano": 2022,
  "tipoVeiculo": "CARRO",
  "usuario": { "id": 6 }
}

### 8. CRIAR VENDA
POST http://localhost:8080/api/vendas
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "dataVenda": "2024-12-10T14:30:00",
  "cliente": { "id": 6 },
  "funcionario": { "id": 5 },
  "veiculo": { "id": 1 },
  "mercadorias": [{ "id": 1 }],
  "servicos": [{ "id": 1 }]
}
```

---

## 🛠 Ferramentas Recomendadas

- **Insomnia** - Cliente REST
- **Postman** - Cliente REST
- **cURL** - Linha de comando
- **H2 Console** - Visualizar banco de dados

---

**AutoBoots - Sistema de Gerenciamento de Autopeças com Spring Boot e JWT** 🚗✨
