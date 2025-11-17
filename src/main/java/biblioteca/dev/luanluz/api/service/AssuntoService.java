package biblioteca.dev.luanluz.api.service;

import biblioteca.dev.luanluz.api.dto.request.AssuntoRequestDTO;
import biblioteca.dev.luanluz.api.dto.response.AssuntoResponseDTO;
import biblioteca.dev.luanluz.api.exception.DomainException;
import biblioteca.dev.luanluz.api.exception.DuplicateResourceException;
import biblioteca.dev.luanluz.api.mapper.AssuntoMapper;
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
public class AssuntoService extends BaseService<Assunto, Integer, AssuntoRequestDTO, AssuntoResponseDTO> {

    private static final String RESOURCE_NAME = "Assunto";
    private static final String IDENTIFIER_FIELD = "código";

    private final AssuntoRepository assuntoRepository;
    private final AssuntoMapper assuntoMapper;

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
    protected Assunto toEntity(AssuntoRequestDTO dto) {
        return assuntoMapper.toEntity(dto);
    }

    @Override
    protected AssuntoResponseDTO toResponseDTO(Assunto entity) {
        return assuntoMapper.toResponseDTO(entity);
    }

    @Override
    protected void updateEntityFromDTO(AssuntoRequestDTO dto, Assunto entity) {
        assuntoMapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected void validateForCreate(Assunto assunto) {
        validateDescricaoUnica(assunto.getDescricao(), null);
    }

    @Override
    protected void validateForUpdate(Integer codigo, Assunto assunto) {
        validateDescricaoUnica(assunto.getDescricao(), codigo);
    }

    @Override
    protected void validateBeforeDelete(Integer codigo, Assunto assunto) {
        if (! assunto.getLivros().isEmpty()) {
            throw new DomainException(
                    "Não é possível deletar o assunto pois existem livros associados a ele");
        }
    }

    private void validateDescricaoUnica(String descricao, Integer codigoAtual) {
        if (codigoAtual == null) {
            if (assuntoRepository.existsByDescricaoIgnoreCase(descricao)) {
                throw new DuplicateResourceException(RESOURCE_NAME, "descrição", descricao);
            }
        }

        if (assuntoRepository.existsByDescricaoIgnoreCaseAndCodigoNot(descricao, codigoAtual)) {
            throw new DuplicateResourceException(RESOURCE_NAME, "descrição", descricao);
        }
    }
}
