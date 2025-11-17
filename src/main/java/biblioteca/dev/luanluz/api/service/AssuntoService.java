package biblioteca.dev.luanluz.api.service;

import biblioteca.dev.luanluz.api.exception.DomainException;
import biblioteca.dev.luanluz.api.model.Assunto;
import biblioteca.dev.luanluz.api.repository.AssuntoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssuntoService extends BaseService<Assunto, Integer> {

    private static final String RESOURCE_NAME = "Assunto";
    private static final String IDENTIFIER_FIELD = "código";
    private static final int MAX_DESCRICAO_LENGTH = 20;

    private final AssuntoRepository assuntoRepository;

    @Override
    protected JpaRepository<Assunto, Integer> getRepository() {
        return assuntoRepository;
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
    protected void validateForCreate(Assunto assunto) {
        validateNotNull(assunto);
        validateRequiredField(assunto.getDescricao(), "descrição");
        validateMaxLength(assunto.getDescricao(), "descrição", MAX_DESCRICAO_LENGTH);
        validateDescricaoUnica(assunto.getDescricao(), null);
    }

    @Override
    protected void validateForUpdate(Integer codigo, Assunto assunto) {
        validateNotNull(assunto);
        validateRequiredField(assunto.getDescricao(), "descrição");
        validateMaxLength(assunto.getDescricao(), "descrição", MAX_DESCRICAO_LENGTH);
        validateDescricaoUnica(assunto.getDescricao(), codigo);
    }

    @Override
    protected void updateEntityFields(Assunto existingAssunto, Assunto updatedAssunto) {
        existingAssunto.setDescricao(updatedAssunto.getDescricao());
    }

    @Override
    protected void validateBeforeDelete(Integer codigo, Assunto assunto) {
        if (! assunto.getLivros().isEmpty()) {
            throw new DomainException(
                    "Não é possível deletar o assunto pois existem livros associados a ele");
        }
    }

    private void validateDescricaoUnica(String descricao, Integer codigoAtual) {
        if (! assuntoRepository.existsByDescricaoIgnoreCase(descricao)) {
            return;
        }

        if (codigoAtual != null) {
            var assuntoExistente = assuntoRepository.findById(codigoAtual).orElse(null);
            if (assuntoExistente != null &&
                    assuntoExistente.getDescricao().equalsIgnoreCase(descricao)) {
                return;
            }
        }

        throwDuplicateException("descrição", descricao);
    }
}
