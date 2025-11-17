package biblioteca.dev.luanluz.api.repository;

import biblioteca.dev.luanluz.api.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Integer> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndCodigoNot(String nome, Integer codigo);
}
