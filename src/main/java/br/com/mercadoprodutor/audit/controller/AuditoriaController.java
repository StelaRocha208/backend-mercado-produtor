package br.com.mercadoprodutor.audit.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.mercadoprodutor.audit.dto.AuditoriaResponseDTO;
import br.com.mercadoprodutor.audit.repository.AuditoriaLogRepository;

@RestController
@RequestMapping("/api/audit")
public class AuditoriaController {
    private final AuditoriaLogRepository repository;

    public AuditoriaController(AuditoriaLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<Page<AuditoriaResponseDTO>> listarLogs(
            @PageableDefault(size = 20, sort = "dataHora") Pageable pageable) {
        var logsPaginados = repository.findAll(pageable);
        
        var pageDto = logsPaginados.map(AuditoriaResponseDTO::new);
        
        return ResponseEntity.ok(pageDto);
    }
}
