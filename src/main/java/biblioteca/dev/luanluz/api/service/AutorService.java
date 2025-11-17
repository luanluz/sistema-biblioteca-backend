package biblioteca.dev.luanluz.api.service;

import biblioteca.dev.luanluz.api.dto.request.AutorRequestDTO;
import biblioteca.dev.luanluz.api.dto.response.AutorResponseDTO;
import biblioteca.dev.luanluz.api.exception.DomainException;
import biblioteca.dev.luanluz.api.exception.DuplicateResourceException;
import biblioteca.dev.luanluz.api.mapper.AutorMapper;
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
public class AutorService extends BaseService<Autor, Integer, AutorRequestDTO, AutorResponseDTO> {

    private static final String RESOURCE_NAME = "Autor";
    private static final String IDENTIFIER_FIELD = "código";

    private final AutorRepository autorRepository;
    private final AutorMapper autorMapper;

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
    protected Autor toEntity(AutorRequestDTO dto) {
        return autorMapper.toEntity(dto);
    }

    @Override
    protected AutorResponseDTO toResponseDTO(Autor entity) {
        return autorMapper.toResponseDTO(entity);
    }

    @Override
    protected void updateEntityFromDTO(AutorRequestDTO dto, Autor entity) {
        autorMapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected void validateForCreate(Autor autor) {
        validateNomeUnico(autor.getNome(), null);
    }

    @Override
    protected void validateForUpdate(Integer codigo, Autor autor) {
        validateNomeUnico(autor.getNome(), codigo);
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

        throw new DuplicateResourceException(RESOURCE_NAME, "nome", nome);
    }
}
