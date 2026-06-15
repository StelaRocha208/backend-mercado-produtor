package br.com.mercadoprodutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BackendMercadoProdutorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendMercadoProdutorApplication.class, args);
	}

}
