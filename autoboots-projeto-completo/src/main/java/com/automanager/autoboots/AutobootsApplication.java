package com.automanager.autoboots;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

/**
 * Classe principal da aplicação AutoBoots Sistema de Gerenciamento de Autopeças
 * com Spring Boot e JWT
 *
 * @author AutoBoots Team
 * @version 1.0.0
 */
@SpringBootApplication
public class AutobootsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutobootsApplication.class, args);
    }

    /**
     * Runner para exibir informações após a inicialização
     */
    @Component
    public static class StartupRunner implements ApplicationRunner {

        @Override
        public void run(ApplicationArguments args) throws Exception {
            String separator = "=".repeat(80);

            System.out.println("\n" + separator);
            System.out.println("🚗 AutoBoots - Sistema de Gerenciamento de Autopeças");
            System.out.println(separator);
            System.out.println("✅ Aplicação iniciada com sucesso!");
            System.out.println("📍 URL: http://localhost:8080");
            System.out.println("🗄️  H2 Console: http://localhost:8080/h2-console");
            System.out.println("   JDBC URL: jdbc:h2:mem:autoboots");
            System.out.println("   Username: sa");
            System.out.println("   Password: (deixe em branco)");
            System.out.println(separator);
            System.out.println("\n📋 CREDENCIAIS DE TESTE:");
            System.out.println("   ADMINISTRADOR: admin / admin123");
            System.out.println("   GERENTE:       gerente / gerente123");
            System.out.println("   VENDEDOR:      vendedor / vendedor123");
            System.out.println("   CLIENTE:       cliente / cliente123");
            System.out.println(separator + "\n");
        }
    }
}
