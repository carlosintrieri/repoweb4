# 🚀 Guia de Início Rápido - AutoBoots

## Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior
- Porta 8080 disponível

## Como Executar

### 1. Via Maven (Recomendado)

```bash
# No diretório do projeto
mvn clean install
mvn spring-boot:run
```

### 2. Via JAR

```bash
# Compilar
mvn clean package

# Executar
java -jar target/autoboots-1.0.0.jar
```

## Testando a API

### 1. Login como ADMINISTRADOR

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ADMINISTRADOR"
}
```

### 2. Usar o Token nas Requisições

```bash
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

## Credenciais Padrão

| Usuário | Senha | Role |
|---------|-------|------|
| admin | admin123 | ADMINISTRADOR |
| gerente | gerente123 | GERENTE |
| vendedor | vendedor123 | VENDEDOR |
| cliente | cliente123 | CLIENTE |

## H2 Console

Acesse: http://localhost:8080/h2-console

- **JDBC URL**: `jdbc:h2:mem:autoboots`
- **Username**: `sa`
- **Password**: (deixe em branco)

## Endpoints Principais

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/api/auth/login` | POST | Login |
| `/api/auth/register` | POST | Registrar novo usuário |
| `/api/usuarios` | GET | Listar usuários |
| `/api/empresas` | GET | Listar empresas |
| `/api/mercadorias` | GET | Listar mercadorias |
| `/api/servicos` | GET | Listar serviços |
| `/api/veiculos` | GET | Listar veículos |
| `/api/vendas` | GET | Listar vendas |

## Permissões por Role

### ADMINISTRADOR
- ✅ Acesso total a todos os recursos
- ✅ Único que pode deletar usuários
- ✅ Pode criar outros administradores

### GERENTE
- ✅ Gerencia empresas, mercadorias, serviços, veículos e vendas
- ✅ Gerencia usuários (GERENTE, VENDEDOR, CLIENTE)
- ❌ Não pode ver ou criar ADMINISTRADORES
- ❌ Não pode deletar usuários

### VENDEDOR
- ✅ Visualiza mercadorias e serviços
- ✅ Cria e gerencia CLIENTES
- ✅ Cria vendas onde é o funcionário
- ✅ Gerencia veículos
- ❌ Não pode criar/editar mercadorias ou serviços
- ❌ Não pode acessar empresas

### CLIENTE
- ✅ Visualiza próprio cadastro
- ✅ Gerencia próprios veículos
- ✅ Visualiza próprias compras (vendas)
- ❌ Não pode ver outros clientes
- ❌ Não pode acessar mercadorias, serviços ou empresas

## Troubleshooting

### Erro 401 - Unauthorized
- Verifique se o token foi incluído no header `Authorization: Bearer {token}`
- Verifique se o token não expirou (validade: 10 horas)

### Erro 403 - Forbidden
- Verifique se seu usuário tem permissão para acessar o recurso
- Consulte a tabela de permissões no README.md

### Porta 8080 já em uso
- Altere a porta no `application.properties`:
  ```properties
  server.port=8081
  ```

## Documentação Completa

Consulte o arquivo `README.md` para documentação completa com exemplos detalhados.
