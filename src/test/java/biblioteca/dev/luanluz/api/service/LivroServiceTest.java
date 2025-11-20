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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private AssuntoRepository assuntoRepository;

    @Mock
    private LivroMapper livroMapper;

    @InjectMocks
    private LivroService livroService;

    private Livro livro;
    private LivroRequestDTO requestDTO;
    private LivroResponseDTO responseDTO;
    private Autor autor;
    private Assunto assunto;

    @BeforeEach
    void setUp() {
        autor = new Autor();
        autor.setCodigo(1);
        autor.setNome("Isaac Asimov");

        assunto = new Assunto();
        assunto.setCodigo(1);
        assunto.setDescricao("Ficção Científica");

        livro = new Livro();
        livro.setCodigo(1);
        livro.setTitulo("Fundação");
        livro.setEditora("Aleph");
        livro.setEdicao(1);
        livro.setAnoPublicacao("1951");
        livro.setAutores(new HashSet<>(Collections.singletonList(autor)));
        livro.setAssuntos(new HashSet<>(Collections.singletonList(assunto)));

        requestDTO = new LivroRequestDTO();
        requestDTO.setTitulo("Fundação");
        requestDTO.setEditora("Aleph");
        requestDTO.setEdicao(1);
        requestDTO.setAnoPublicacao("1951");

        responseDTO = new LivroResponseDTO();
        responseDTO.setCodigo(1);
        responseDTO.setTitulo("Fundação");
    }

    @Test
    void deveBuscarTodosComPaginacao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Livro> page = new PageImpl<>(Collections.singletonList(livro));

        when(livroRepository.findAllWithRelations(pageable)).thenReturn(page);
        when(livroMapper.toResponseDTO(any(Livro.class))).thenReturn(responseDTO);

        // Act
        Page<LivroResponseDTO> result = livroService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(livroRepository, times(1)).findAllWithRelations(pageable);
    }

    @Test
    void deveBuscarPorId() {
        // Arrange
        when(livroRepository.findByIdWithRelations(1)).thenReturn(Optional.of(livro));
        when(livroMapper.toResponseDTO(livro)).thenReturn(responseDTO);

        // Act
        LivroResponseDTO result = livroService.findById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCodigo());
        assertEquals("Fundação", result.getTitulo());
        verify(livroRepository, times(1)).findByIdWithRelations(1);
    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontrarPorId() {
        // Arrange
        when(livroRepository.findByIdWithRelations(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> livroService.findById(999));
        verify(livroRepository, times(1)).findByIdWithRelations(999);
    }

    @Test
    void deveCriarNovoLivro() {
        // Arrange
        when(livroMapper.toEntity(requestDTO)).thenReturn(livro);
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        when(assuntoRepository.findById(1)).thenReturn(Optional.of(assunto));
        when(livroRepository.existsByTituloIgnoreCase(anyString())).thenReturn(false);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);
        when(livroMapper.toResponseDTO(livro)).thenReturn(responseDTO);

        // Act
        LivroResponseDTO result = livroService.create(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Fundação", result.getTitulo());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    void deveLancarExcecaoAoCriarLivroComTituloDuplicado() {
        // Arrange
        when(livroMapper.toEntity(requestDTO)).thenReturn(livro);
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        when(assuntoRepository.findById(1)).thenReturn(Optional.of(assunto));
        when(livroRepository.existsByTituloIgnoreCase(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> livroService.create(requestDTO));
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void deveLancarExcecaoQuandoAutorNaoExistir() {
        // Arrange
        when(livroMapper.toEntity(requestDTO)).thenReturn(livro);
        when(autorRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DomainException.class, () -> livroService.create(requestDTO));
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void deveLancarExcecaoQuandoAssuntoNaoExistir() {
        // Arrange
        when(livroMapper.toEntity(requestDTO)).thenReturn(livro);
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        when(assuntoRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DomainException.class, () -> livroService.create(requestDTO));
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void deveLancarExcecaoQuandoAnoPublicacaoInvalido() {
        // Arrange
        livro.setAnoPublicacao("ABC");
        when(livroMapper.toEntity(requestDTO)).thenReturn(livro);
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        when(assuntoRepository.findById(1)).thenReturn(Optional.of(assunto));

        // Act & Assert
        assertThrows(DomainException.class, () -> livroService.create(requestDTO));
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void deveLancarExcecaoQuandoAnoPublicacaoFuturo() {
        // Arrange
        livro.setAnoPublicacao("2100");
        when(livroMapper.toEntity(requestDTO)).thenReturn(livro);
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        when(assuntoRepository.findById(1)).thenReturn(Optional.of(assunto));

        // Act & Assert
        assertThrows(DomainException.class, () -> livroService.create(requestDTO));
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void deveAtualizarLivro() {
        // Arrange
        when(livroRepository.findByIdWithRelations(1)).thenReturn(Optional.of(livro));
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        when(assuntoRepository.findById(1)).thenReturn(Optional.of(assunto));
        when(livroRepository.existsByTituloIgnoreCaseAndCodigoNot(anyString(), anyInt())).thenReturn(false);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);
        when(livroMapper.toResponseDTO(livro)).thenReturn(responseDTO);
        doNothing().when(livroMapper).updateEntityFromDTO(any(), any());

        // Act
        LivroResponseDTO result = livroService.update(1, requestDTO);

        // Assert
        assertNotNull(result);
        verify(livroRepository, times(1)).findByIdWithRelations(1);
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    void deveDeletarLivro() {
        // Arrange
        when(livroRepository.findByIdWithRelations(1)).thenReturn(Optional.of(livro));
        doNothing().when(livroRepository).delete(livro);

        // Act
        livroService.delete(1);

        // Assert
        verify(livroRepository, times(1)).findByIdWithRelations(1);
        verify(livroRepository, times(1)).delete(livro);
    }

    @Test
    void devePermitirCriarLivroSemAutores() {
        // Arrange
        livro.setAutores(new HashSet<>());
        when(livroMapper.toEntity(requestDTO)).thenReturn(livro);
        when(assuntoRepository.findById(1)).thenReturn(Optional.of(assunto));
        when(livroRepository.existsByTituloIgnoreCase(anyString())).thenReturn(false);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);
        when(livroMapper.toResponseDTO(livro)).thenReturn(responseDTO);

        // Act
        LivroResponseDTO result = livroService.create(requestDTO);

        // Assert
        assertNotNull(result);
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    void devePermitirCriarLivroSemAssuntos() {
        // Arrange
        livro.setAssuntos(new HashSet<>());
        when(livroMapper.toEntity(requestDTO)).thenReturn(livro);
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        when(livroRepository.existsByTituloIgnoreCase(anyString())).thenReturn(false);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);
        when(livroMapper.toResponseDTO(livro)).thenReturn(responseDTO);

        // Act
        LivroResponseDTO result = livroService.create(requestDTO);

        // Assert
        assertNotNull(result);
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    void deveLancarExcecaoQuandoCodigoAutorNulo() {
        // Arrange
        Autor autorSemCodigo = new Autor();
        autorSemCodigo.setCodigo(null);
        livro.setAutores(new HashSet<>(List.of(autorSemCodigo)));

        when(livroMapper.toEntity(requestDTO)).thenReturn(livro);

        // Act & Assert
        assertThrows(DomainException.class, () -> livroService.create(requestDTO));
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void deveLancarExcecaoQuandoCodigoAssuntoNulo() {
        // Arrange
        Assunto assuntoSemCodigo = new Assunto();
        assuntoSemCodigo.setCodigo(null);
        livro.setAutores(new HashSet<>());
        livro.setAssuntos(new HashSet<>(List.of(assuntoSemCodigo)));

        when(livroMapper.toEntity(requestDTO)).thenReturn(livro);

        // Act & Assert
        assertThrows(DomainException.class, () -> livroService.create(requestDTO));
        verify(livroRepository, never()).save(any(Livro.class));
    }
}
