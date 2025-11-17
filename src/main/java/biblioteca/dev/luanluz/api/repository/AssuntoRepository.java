package biblioteca.dev.luanluz.api.repository;

import biblioteca.dev.luanluz.api.model.Assunto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssuntoRepository extends JpaRepository<Assunto, Integer> {

    boolean existsByDescricaoIgnoreCase(String descricao);

    boolean existsByDescricaoIgnoreCaseAndCodigoNot(String descricao, Integer codigo);
}
