package biblioteca.dev.luanluz.api.service;

import biblioteca.dev.luanluz.api.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
public abstract class BaseService<T, ID, REQUEST_DTO, RESPONSE_DTO> {

    protected abstract JpaRepository<T, ID> getRepository();

    protected abstract String getResourceName();

    protected abstract String getIdentifierFieldName();

    protected abstract T toEntity(REQUEST_DTO dto);

    protected abstract RESPONSE_DTO toResponseDTO(T entity);

    protected abstract void updateEntityFromDTO(REQUEST_DTO dto, T entity);

    public Page<RESPONSE_DTO> findAll(Pageable pageable) {
        Page<T> page = getRepository().findAll(pageable);

        log.info("Encontrados {} {} na página {} de {}",
                page.getNumberOfElements(),
                getResourceName(),
                page.getNumber() + 1,
                page.getTotalPages());

        return page.map(this::toResponseDTO);
    }

    public RESPONSE_DTO findById(ID id) {
        T entity = findEntityById(id);
        log.info("{} encontrado: {}", getResourceName(), entity);
        return toResponseDTO(entity);
    }

    @Transactional
    public RESPONSE_DTO create(REQUEST_DTO requestDTO) {
        T entity = toEntity(requestDTO);

        validateForCreate(entity);

        T savedEntity = getRepository().save(entity);

        log.info("{} criado com sucesso: {}", getResourceName(), savedEntity);

        return toResponseDTO(savedEntity);
    }

    @Transactional
    public RESPONSE_DTO update(ID id, REQUEST_DTO requestDTO) {
        T existingEntity = findEntityById(id);

        updateEntityFromDTO(requestDTO, existingEntity);

        validateForUpdate(id, existingEntity);

        T savedEntity = getRepository().save(existingEntity);

        log.info("{} atualizado com sucesso: {}", getResourceName(), savedEntity);

        return toResponseDTO(savedEntity);
    }

    @Transactional
    public void delete(ID id) {
        T entity = findEntityById(id);

        validateBeforeDelete(id, entity);

        getRepository().delete(entity);

        log.info("{} deletado com sucesso: {}", getResourceName(), entity);
    }

    protected T findEntityById(ID id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getResourceName(), getIdentifierFieldName(), id));
    }

    protected abstract void validateForCreate(T entity);

    protected abstract void validateForUpdate(ID id, T entity);

    protected abstract void validateBeforeDelete(ID id, T entity);
}
