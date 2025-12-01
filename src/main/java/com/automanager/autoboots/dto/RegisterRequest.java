package com.automanager.autoboots.dto;

import com.automanager.autoboots.model.Role;

public class RegisterRequest {
    private String nome;
    private String username;
    private String password;
    private String email;
    private Role role;
    
    public RegisterRequest() {}
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
