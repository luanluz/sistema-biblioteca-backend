package biblioteca.dev.luanluz.api.service;

import biblioteca.dev.luanluz.api.exception.DomainException;
import biblioteca.dev.luanluz.api.model.Autor;
import biblioteca.dev.luanluz.api.repository.AutorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutorService extends BaseService<Autor, Integer> {

    private static final String RESOURCE_NAME = "Autor";
    private static final String IDENTIFIER_FIELD = "código";
    private static final int MAX_NOME_LENGTH = 40;

    private final AutorRepository autorRepository;

    @Override
    protected JpaRepository<Autor, Integer> getRepository() {
        return autorRepository;
    }

    @Override
    protected String getResourceName() {
        return RESOURCE_NAME;
    }

    @Override
    protected String getIdentifierFieldName() {
        return IDENTIFIER_FIELD;
    }

    @Override
    protected void validateForCreate(Autor autor) {
        validateNotNull(autor);
        validateRequiredField(autor.getNome(), "nome");
        validateMaxLength(autor.getNome(), "nome", MAX_NOME_LENGTH);
        validateNomeUnico(autor.getNome(), null);
    }

    @Override
    protected void validateForUpdate(Integer codigo, Autor autor) {
        validateNotNull(autor);
        validateRequiredField(autor.getNome(), "nome");
        validateMaxLength(autor.getNome(), "nome", MAX_NOME_LENGTH);
        validateNomeUnico(autor.getNome(), codigo);
    }

    @Override
    protected void updateEntityFields(Autor existingAutor, Autor updatedAutor) {
        existingAutor.setNome(updatedAutor.getNome());
    }

    @Override
    protected void validateBeforeDelete(Integer codigo, Autor autor) {
        if (! autor.getLivros().isEmpty()) {
            throw new DomainException(
                    "Não é possível deletar o autor pois existem livros associados a ele");
        }
    }

    private void validateNomeUnico(String nome, Integer codigoAtual) {
        if (! autorRepository.existsByNomeIgnoreCase(nome)) {
            return;
        }

        if (codigoAtual != null) {
            var autorExistente = autorRepository.findById(codigoAtual).orElse(null);
            if (autorExistente != null &&
                    autorExistente.getNome().equalsIgnoreCase(nome)) {
                return;
            }
        }

        throwDuplicateException("nome", nome);
    }
}
