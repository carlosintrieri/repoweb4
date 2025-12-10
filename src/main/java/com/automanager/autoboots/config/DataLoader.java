package com.automanager.autoboots.config;

import com.automanager.autoboots.model.*;
import com.automanager.autoboots.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * ✅ CORRIGIDO: 
 * - Usando LocalDateTime ao invés de Date
 * - Senhas de teste (ainda fracas para facilitar testes, mas avisando)
 */
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private EmpresaRepository empresaRepository;
    
    @Autowired
    private MercadoriaRepository mercadoriaRepository;
    
    @Autowired
    private ServicoRepository servicoRepository;
    
    @Autowired
    private VeiculoRepository veiculoRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n🚀 Iniciando carga de dados de teste...\n");
        
        // Criar Usuários
        Usuario admin = new Usuario("Admin Master", "admin", 
                passwordEncoder.encode("admin123"), "admin@autoboots.com", Role.ADMINISTRADOR);
        admin.setDataCadastro(LocalDateTime.now());
        usuarioRepository.save(admin);
        System.out.println("✅ Usuário ADMINISTRADOR criado: admin / admin123");
        
        Usuario gerente = new Usuario("Carlos Gerente", "gerente", 
                passwordEncoder.encode("gerente123"), "gerente@autoboots.com", Role.GERENTE);
        gerente.setDataCadastro(LocalDateTime.now());
        usuarioRepository.save(gerente);
        System.out.println("✅ Usuário GERENTE criado: gerente / gerente123");
        
        Usuario vendedor = new Usuario("Ana Vendedora", "vendedor", 
                passwordEncoder.encode("vendedor123"), "vendedor@autoboots.com", Role.VENDEDOR);
        vendedor.setDataCadastro(LocalDateTime.now());
        usuarioRepository.save(vendedor);
        System.out.println("✅ Usuário VENDEDOR criado: vendedor / vendedor123");
        
        Usuario cliente = new Usuario("Maria Cliente", "cliente", 
                passwordEncoder.encode("cliente123"), "cliente@autoboots.com", Role.CLIENTE);
        cliente.setDataCadastro(LocalDateTime.now());
        usuarioRepository.save(cliente);
        System.out.println("✅ Usuário CLIENTE criado: cliente / cliente123");
        
        // Criar Empresas
        Endereco endereco1 = new Endereco("SP", "São Paulo", "Centro", 
                "Av. Paulista", "1000", "01310-100", "Próximo ao metrô");
        Empresa empresa1 = new Empresa("AutoBoots Ltda", "AutoBoots", 
                Arrays.asList("11-98765-4321", "11-3456-7890"), endereco1);
        empresaRepository.save(empresa1);
        System.out.println("✅ Empresa criada: AutoBoots Ltda");
        
        // Criar Mercadorias
        Mercadoria m1 = new Mercadoria("Óleo Motor 5W30", 45.90, 150, "Óleo sintético para motor");
        mercadoriaRepository.save(m1);
        
        Mercadoria m2 = new Mercadoria("Filtro de Ar", 35.00, 80, "Filtro de ar esportivo");
        mercadoriaRepository.save(m2);
        
        Mercadoria m3 = new Mercadoria("Pneu 195/65 R15", 289.90, 40, "Pneu aro 15 para veículos de passeio");
        mercadoriaRepository.save(m3);
        System.out.println("✅ 3 Mercadorias criadas");
        
        // Criar Serviços
        Servico s1 = new Servico("Troca de Óleo", 80.00, "Troca completa de óleo do motor");
        servicoRepository.save(s1);
        
        Servico s2 = new Servico("Alinhamento e Balanceamento", 120.00, "Alinhamento e balanceamento das 4 rodas");
        servicoRepository.save(s2);
        
        Servico s3 = new Servico("Revisão Completa", 350.00, "Revisão completa de 10.000 km");
        servicoRepository.save(s3);
        System.out.println("✅ 3 Serviços criados");
        
        // Criar Veículos
        Veiculo v1 = new Veiculo("ABC-1234", "Corolla", "Toyota", 2022, TipoVeiculo.CARRO, cliente);
        veiculoRepository.save(v1);
        
        Veiculo v2 = new Veiculo("XYZ-9876", "Civic", "Honda", 2023, TipoVeiculo.CARRO, cliente);
        veiculoRepository.save(v2);
        System.out.println("✅ 2 Veículos criados");
        
        System.out.println("\n✨ Carga de dados concluída com sucesso!\n");
        System.out.println("=".repeat(60));
        System.out.println("📋 CREDENCIAIS DE TESTE:");
        System.out.println("=".repeat(60));
        System.out.println("ADMINISTRADOR: admin / admin123");
        System.out.println("GERENTE:       gerente / gerente123");
        System.out.println("VENDEDOR:      vendedor / vendedor123");
        System.out.println("CLIENTE:       cliente / cliente123");
        System.out.println("=".repeat(60));
        System.out.println("🌐 H2 Console: http://localhost:8080/h2-console");
        System.out.println("   JDBC URL: jdbc:h2:mem:autoboots");
        System.out.println("   Username: sa");
        System.out.println("   Password: (deixe em branco)");
        System.out.println("=".repeat(60) + "\n");
        System.out.println("⚠️  ATENÇÃO: Senhas de teste são fracas!");
        System.out.println("   Em produção, use senhas fortes e variáveis de ambiente.\n");
    }
}
