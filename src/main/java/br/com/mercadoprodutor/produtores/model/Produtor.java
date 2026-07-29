package br.com.mercadoprodutor.produtores.model;

import br.com.mercadoprodutor.core.model.BaseEntity;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produtores")
@Getter
@Setter
@NoArgsConstructor
public class Produtor extends BaseEntity {

    @Column(nullable = false, length = 14, unique = true)
    private String cpf;

    @Column(length = 20)
    private String telefone;

    /**
     * Controle temporário de inadimplência.
     * Futuramente será calculado automaticamente
     * pelo módulo financeiro.
     */
    @Column(nullable = false)
    private Boolean inadimplente = false;

    /**
     * Justificativa informada pelo operador
     * quando a saída é permitida mesmo havendo pendência.
     */
    @Column(name = "justificativa_inadimplencia", length = 500)
    private String justificativaInadimplencia;

    /**
     * Indica que o operador autorizou
     * uma entrada excepcional para este produtor.
     * Após o registro da entrada, este campo
     * deve voltar para false.
     */
    @Column(name = "liberacao_excepcional", nullable = false)
    private Boolean liberacaoExcepcional = false;

    // Buscar um produtor não traz o Usuario inteiro na query a menos que você chame produtor.getUsuario().getNome()
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "produtor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Veiculo> veiculos = new ArrayList<>();

    public Produtor(String cpf, String telefone, Usuario usuario) {
        this.cpf = cpf;
        this.telefone = telefone;
        this.usuario = usuario;
    }

    public void adicionarVeiculo(Veiculo veiculo) {
        veiculos.add(veiculo);
        veiculo.setProdutor(this);
    }

    public void marcarComoInadimplente() {
        this.inadimplente = true;
    }


    public void removerInadimplencia() {
        this.inadimplente = false;
        this.justificativaInadimplencia = null;
    }


    public void liberarEntradaExcepcional(String justificativa) {
        this.liberacaoExcepcional = true;
        this.justificativaInadimplencia = justificativa;
    }


    public void consumirLiberacaoExcepcional() {
        this.liberacaoExcepcional = false;
    }
}