package biblioteca.dev.luanluz.api.service;

import biblioteca.dev.luanluz.api.exception.DomainException;
import biblioteca.dev.luanluz.api.exception.DuplicateResourceException;
import biblioteca.dev.luanluz.api.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
public abstract class BaseService<T, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    protected abstract String getResourceName();

    protected abstract String getIdentifierFieldName();

    public Page<T> findAll(Pageable pageable) {
        Page<T> page = getRepository().findAll(pageable);

        log.info("Encontrados {} {} na página {} de {}",
                page.getNumberOfElements(),
                getResourceName(),
                page.getNumber() + 1,
                page.getTotalPages());

        return page;
    }

    public T findById(ID id) {
        T entity = getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getResourceName(), getIdentifierFieldName(), id));

        log.info("{} encontrado: {}", getResourceName(), entity);

        return entity;
    }

    @Transactional
    public T create(T entity) {
        validateForCreate(entity);

        T savedEntity = getRepository().save(entity);

        log.info("{} criado com sucesso: {}", getResourceName(), savedEntity);

        return savedEntity;
    }

    @Transactional
    public T update(ID id, T updatedEntity) {
        T existingEntity = findById(id);

        validateForUpdate(id, updatedEntity);

        updateEntityFields(existingEntity, updatedEntity);

        T savedEntity = getRepository().save(existingEntity);

        log.info("{} atualizado com sucesso: {}", getResourceName(), savedEntity);

        return savedEntity;
    }

    @Transactional
    public void delete(ID id) {
        T entity = findById(id);

        validateBeforeDelete(id, entity);

        getRepository().delete(entity);

        log.info("{} deletado com sucesso: {}", getResourceName(), entity);
    }

    protected abstract void validateForCreate(T entity);

    protected void validateForUpdate(ID id, T entity) {
        validateForCreate(entity);
    }

    protected abstract void updateEntityFields(T existingEntity, T updatedEntity);

    protected abstract void validateBeforeDelete(ID id, T entity);

    protected void validateNotNull(T entity) {
        if (entity == null) {
            throw new DomainException(getResourceName() + " não pode ser nulo");
        }
    }

    protected void validateRequiredField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException(String.format("%s do %s é obrigatório",
                    fieldName,
                    getResourceName().toLowerCase()));
        }
    }

    protected void validateMaxLength(String value, String fieldName, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new DomainException(String.format("%s do %s deve ter no máximo %d caracteres",
                    fieldName,
                    getResourceName().toLowerCase(),
                    maxLength));
        }
    }

    protected void validatePositive(Integer value, String fieldName) {
        if (value != null && value < 1) {
            throw new DomainException(String.format("%s do %s deve ser maior que zero",
                    fieldName,
                    getResourceName().toLowerCase()));
        }
    }

    protected void validateNonNegative(Integer value, String fieldName) {
        if (value != null && value < 0) {
            throw new DomainException(String.format("%s do %s não pode ser negativo",
                    fieldName,
                    getResourceName().toLowerCase()));
        }
    }

    protected void throwDuplicateException(String fieldName, Object value) {
        throw new DuplicateResourceException(getResourceName(), fieldName, value);
    }
}
