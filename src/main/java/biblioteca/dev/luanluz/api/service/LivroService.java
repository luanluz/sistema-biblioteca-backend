package biblioteca.dev.luanluz.api.service;

import biblioteca.dev.luanluz.api.exception.DomainException;
import biblioteca.dev.luanluz.api.model.Assunto;
import biblioteca.dev.luanluz.api.model.Autor;
import biblioteca.dev.luanluz.api.model.Livro;
import biblioteca.dev.luanluz.api.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LivroService extends BaseService<Livro, Integer> {

    private static final String RESOURCE_NAME = "Livro";
    private static final String IDENTIFIER_FIELD = "código";
    private static final int MAX_TITULO_LENGTH = 40;
    private static final int MAX_EDITORA_LENGTH = 40;
    private static final int ANO_LENGTH = 4;

    private final LivroRepository livroRepository;
    private final AutorService autorService;
    private final AssuntoService assuntoService;

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
    protected void validateForCreate(Livro livro) {
        validateNotNull(livro);
        validateBasicFields(livro);
        validateTituloUnico(livro.getTitulo(), null);
        validateRelationships(livro);
        processarRelacionamentos(livro);
    }

    @Override
    protected void validateForUpdate(Integer codigo, Livro livro) {
        validateNotNull(livro);
        validateBasicFields(livro);
        validateTituloUnico(livro.getTitulo(), codigo);
        validateRelationships(livro);
        processarRelacionamentos(livro);
    }

    @Override
    protected void updateEntityFields(Livro existingLivro, Livro updatedLivro) {
        existingLivro.setTitulo(updatedLivro.getTitulo());
        existingLivro.setEditora(updatedLivro.getEditora());
        existingLivro.setEdicao(updatedLivro.getEdicao());
        existingLivro.setAnoPublicacao(updatedLivro.getAnoPublicacao());
        existingLivro.setValorEmCentavos(updatedLivro.getValorEmCentavos());

        existingLivro.getAutores().clear();
        existingLivro.getAutores().addAll(updatedLivro.getAutores());

        existingLivro.getAssuntos().clear();
        existingLivro.getAssuntos().addAll(updatedLivro.getAssuntos());
    }

    @Override
    protected void validateBeforeDelete(Integer integer, Livro entity) {}

    private void validateBasicFields(Livro livro) {
        validateRequiredField(livro.getTitulo(), "título");
        validateMaxLength(livro.getTitulo(), "título", MAX_TITULO_LENGTH);
        validateMaxLength(livro.getEditora(), "editora", MAX_EDITORA_LENGTH);
        validatePositive(livro.getEdicao(), "edição");
        validateAnoPublicacao(livro.getAnoPublicacao());
        validateNonNegative(livro.getValorEmCentavos(), "valor");
    }

    private void validateAnoPublicacao(String anoPublicacao) {
        if (anoPublicacao == null) {
            return;
        }

        if (anoPublicacao.length() != ANO_LENGTH) {
            throw new DomainException(
                    "O ano de publicação deve ter exatamente " + ANO_LENGTH + " caracteres");
        }

        try {
            int ano = Integer.parseInt(anoPublicacao);
            int anoAtual = java.time.Year.now().getValue();

            if (ano > anoAtual + 1) {
                throw new DomainException(
                        "O ano de publicação deve ser menor que " + (anoAtual + 1));
            }
        } catch (NumberFormatException e) {
            throw new DomainException("O ano de publicação deve conter apenas números");
        }
    }

    private void validateRelationships(Livro livro) {
        if (livro.getAutores() == null || livro.getAutores().isEmpty()) {
            throw new DomainException("O livro deve ter pelo menos um autor");
        }

        if (livro.getAssuntos() == null || livro.getAssuntos().isEmpty()) {
            throw new DomainException("O livro deve ter pelo menos um assunto");
        }
    }

    private void validateTituloUnico(String titulo, Integer codigoAtual) {
        if (! livroRepository.existsByTituloIgnoreCase(titulo)) {
            return;
        }

        if (codigoAtual != null) {
            var livroExistente = livroRepository.findById(codigoAtual).orElse(null);
            if (livroExistente != null &&
                    livroExistente.getTitulo().equalsIgnoreCase(titulo)) {
                return;
            }
        }

        throwDuplicateException("título", titulo);
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
                throw new DomainException(
                        "O código do autor é obrigatório para associação");
            }

            var autorExistente = autorService.findById(autor.getCodigo());
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
                throw new DomainException(
                        "O código do assunto é obrigatório para associação");
            }

            var assuntoExistente = assuntoService.findById(assunto.getCodigo());
            assuntosValidados.add(assuntoExistente);
        }

        livro.setAssuntos(assuntosValidados);
    }
}
