package biblioteca.dev.luanluz.api.service;

import biblioteca.dev.luanluz.api.dto.request.LivroRequestDTO;
import biblioteca.dev.luanluz.api.dto.response.LivroResponseDTO;
import biblioteca.dev.luanluz.api.exception.DomainException;
import biblioteca.dev.luanluz.api.exception.DuplicateResourceException;
import biblioteca.dev.luanluz.api.exception.ResourceNotFoundException;
import biblioteca.dev.luanluz.api.mapper.LivroMapper;
import biblioteca.dev.luanluz.api.model.Assunto;
import biblioteca.dev.luanluz.api.model.Autor;
import biblioteca.dev.luanluz.api.model.Livro;
import biblioteca.dev.luanluz.api.repository.AssuntoRepository;
import biblioteca.dev.luanluz.api.repository.AutorRepository;
import biblioteca.dev.luanluz.api.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class LivroService extends BaseService<Livro, Integer, LivroRequestDTO, LivroResponseDTO> {

    private static final String RESOURCE_NAME = "Livro";
    private static final String IDENTIFIER_FIELD = "código";

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;
    private final AssuntoRepository assuntoRepository;
    private final LivroMapper livroMapper;

    @Override
    protected JpaRepository<Livro, Integer> getRepository() {
        return livroRepository;
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
    protected Livro toEntity(LivroRequestDTO dto) {
        dto.setTitulo(dto.getTitulo().trim());
        return livroMapper.toEntity(dto);
    }

    @Override
    protected LivroResponseDTO toResponseDTO(Livro entity) {
        return livroMapper.toResponseDTO(entity);
    }

    protected Page<Livro> getAll(Pageable pageable) {
        return livroRepository.findAllWithRelations(pageable);
    }

    protected Livro findEntityById(Integer id) {
        return livroRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException(getResourceName(), getIdentifierFieldName(), id));
    }

    @Override
    protected void updateEntityFromDTO(LivroRequestDTO dto, Livro entity) {
        dto.setTitulo(dto.getTitulo().trim());
        livroMapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected void validateForCreate(Livro livro) {
        processarRelacionamentos(livro);

        validateTituloUnico(livro.getTitulo().trim(), null);
        validateAnoPublicacao(livro.getAnoPublicacao());
    }

    @Override
    protected void validateForUpdate(Integer codigo, Livro livro) {
        processarRelacionamentos(livro);

        validateTituloUnico(livro.getTitulo().trim(), codigo);
        validateAnoPublicacao(livro.getAnoPublicacao());
    }

    @Override
    protected void validateBeforeDelete(Integer integer, Livro entity) {}

    private void validateAnoPublicacao(String anoPublicacao) {
        if (anoPublicacao == null) {
            throw new DomainException("O ano de publicação é obrigatório");
        }

        try {
            int ano = Integer.parseInt(anoPublicacao);
            int anoAtual = java.time.Year.now().getValue();

            if (ano > anoAtual) {
                throw new DomainException("O ano de publicação não pode ser superior ao ano atual (" + anoAtual + ")");
            }
        } catch (NumberFormatException e) {
            throw new DomainException("O ano de publicação deve conter apenas números");
        }
    }

    private void validateTituloUnico(String titulo, Integer codigoAtual) {
        if (codigoAtual == null) {
            if (livroRepository.existsByTituloIgnoreCase(titulo)) {
                throw new DuplicateResourceException(RESOURCE_NAME, "título", titulo);
            }
        }

        if (livroRepository.existsByTituloIgnoreCaseAndCodigoNot(titulo, codigoAtual)) {
            throw new DuplicateResourceException(RESOURCE_NAME, "título", titulo);
        }
    }

    private void processarRelacionamentos(Livro livro) {
        processarAutores(livro);
        processarAssuntos(livro);
    }

    private void processarAutores(Livro livro) {
        if (livro.getAutores() == null || livro.getAutores().isEmpty()) {
            return;
        }

        Set<Autor> autoresValidados = new HashSet<>();

        for (var autor : livro.getAutores()) {
            if (autor.getCodigo() == null) {
                throw new DomainException("O código do autor é obrigatório para associação");
            }

            var autorExistente = autorRepository.findById(autor.getCodigo())
                    .orElseThrow(() -> new DomainException("Autor com código " + autor.getCodigo() + " não encontrado"));

            autoresValidados.add(autorExistente);
        }

        livro.setAutores(autoresValidados);
    }

    private void processarAssuntos(Livro livro) {
        if (livro.getAssuntos() == null || livro.getAssuntos().isEmpty()) {
            return;
        }

        Set<Assunto> assuntosValidados = new HashSet<>();

        for (var assunto : livro.getAssuntos()) {
            if (assunto.getCodigo() == null) {
                throw new DomainException("O código do assunto é obrigatório para associação");
            }

            var assuntoExistente = assuntoRepository.findById(assunto.getCodigo())
                    .orElseThrow(() -> new DomainException("Assunto com código " + assunto.getCodigo() + " não encontrado"));

            assuntosValidados.add(assuntoExistente);
        }

        livro.setAssuntos(assuntosValidados);
    }
}
