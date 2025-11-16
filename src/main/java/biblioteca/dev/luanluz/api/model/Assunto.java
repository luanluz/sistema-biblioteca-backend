package biblioteca.dev.luanluz.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "assunto")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "livros")
public class Assunto extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codas")
    private Integer codigo;

    @Column(name = "descricao", nullable = false, length = 20)
    private String descricao;

    @ManyToMany(mappedBy = "assuntos", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Livro> livros = new HashSet<>();
}
