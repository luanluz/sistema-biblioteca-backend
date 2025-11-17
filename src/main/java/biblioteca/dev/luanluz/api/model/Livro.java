package biblioteca.dev.luanluz.api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "livro")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"autores", "assuntos"})
public class Livro extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codl")
    private Integer codigo;

    @Column(name = "titulo", nullable = false, length = 40)
    private String titulo;

    @Column(name = "editora", length = 40)
    private String editora;

    @Column(name = "edicao")
    private Integer edicao;

    @Column(name = "anopublicacao", length = 4)
    private String anoPublicacao;

    @Column(name = "valoremcentavos")
    private Integer valorEmCentavos;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "livro_autor",
            joinColumns = @JoinColumn(name = "livro_codl"),
            inverseJoinColumns = @JoinColumn(name = "autor_codau")
    )
    @Builder.Default
    private Set<Autor> autores = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "livro_assunto",
            joinColumns = @JoinColumn(name = "livro_codl"),
            inverseJoinColumns = @JoinColumn(name = "assunto_codas")
    )
    @Builder.Default
    private Set<Assunto> assuntos = new HashSet<>();
}
