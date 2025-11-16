package biblioteca.dev.luanluz.api.repository;

import biblioteca.dev.luanluz.api.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Integer> {

    boolean existsByTituloIgnoreCase(String titulo);
}
