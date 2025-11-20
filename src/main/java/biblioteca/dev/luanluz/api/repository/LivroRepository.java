package biblioteca.dev.luanluz.api.repository;

import biblioteca.dev.luanluz.api.model.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Integer> {

    boolean existsByTituloIgnoreCase(String titulo);

    boolean existsByTituloIgnoreCaseAndCodigoNot(String titulo, Integer codigo);

    @Query("SELECT l FROM Livro l " +
            "LEFT JOIN FETCH l.autores " +
            "LEFT JOIN FETCH l.assuntos " +
            "WHERE l.codigo = :id")
    Optional<Livro> findByIdWithRelations(@Param("id") Integer id);

    @Query("SELECT DISTINCT l FROM Livro l " +
            "LEFT JOIN FETCH l.autores " +
            "LEFT JOIN FETCH l.assuntos")
    Page<Livro> findAllWithRelations(Pageable pageable);
}
