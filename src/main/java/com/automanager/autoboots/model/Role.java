package com.automanager.autoboots.model;

/**
 * Enum que define os perfis de usuário do sistema
 * 
 * Hierarquia de permissões:
 * ADMINISTRADOR > GERENTE > VENDEDOR > CLIENTE
 */
public enum Role {
    ADMINISTRADOR,
    GERENTE,
    VENDEDOR,
    CLIENTE
}
