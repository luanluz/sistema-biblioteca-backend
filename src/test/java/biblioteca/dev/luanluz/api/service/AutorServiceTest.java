package biblioteca.dev.luanluz.api.service;

import biblioteca.dev.luanluz.api.dto.request.AutorRequestDTO;
import biblioteca.dev.luanluz.api.dto.response.AutorResponseDTO;
import biblioteca.dev.luanluz.api.exception.DomainException;
import biblioteca.dev.luanluz.api.exception.DuplicateResourceException;
import biblioteca.dev.luanluz.api.exception.ResourceNotFoundException;
import biblioteca.dev.luanluz.api.mapper.AutorMapper;
import biblioteca.dev.luanluz.api.model.Autor;
import biblioteca.dev.luanluz.api.repository.AutorRepository;
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
class AutorServiceTest {

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private AutorMapper autorMapper;

    @InjectMocks
    private AutorService autorService;

    private Autor autor;
    private AutorRequestDTO requestDTO;
    private AutorResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        autor = new Autor();
        autor.setCodigo(1);
        autor.setNome("Isaac Asimov");
        autor.setLivros(new HashSet<>());

        requestDTO = new AutorRequestDTO();
        requestDTO.setNome("Isaac Asimov");

        responseDTO = new AutorResponseDTO();
        responseDTO.setCodigo(1);
        responseDTO.setNome("Isaac Asimov");
    }

    @Test
    void deveBuscarTodosComPaginacao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Autor> page = new PageImpl<>(Collections.singletonList(autor));

        when(autorRepository.findAll(pageable)).thenReturn(page);
        when(autorMapper.toResponseDTO(any(Autor.class))).thenReturn(responseDTO);

        // Act
        Page<AutorResponseDTO> result = autorService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(autorRepository, times(1)).findAll(pageable);
    }

    @Test
    void deveBuscarPorId() {
        // Arrange
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        when(autorMapper.toResponseDTO(autor)).thenReturn(responseDTO);

        // Act
        AutorResponseDTO result = autorService.findById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCodigo());
        assertEquals("Isaac Asimov", result.getNome());
        verify(autorRepository, times(1)).findById(1);
    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontrarPorId() {
        // Arrange
        when(autorRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> autorService.findById(999));
        verify(autorRepository, times(1)).findById(999);
    }

    @Test
    void deveCriarNovoAutor() {
        // Arrange
        when(autorMapper.toEntity(requestDTO)).thenReturn(autor);
        when(autorRepository.existsByNomeIgnoreCase(anyString())).thenReturn(false);
        when(autorRepository.save(any(Autor.class))).thenReturn(autor);
        when(autorMapper.toResponseDTO(autor)).thenReturn(responseDTO);

        // Act
        AutorResponseDTO result = autorService.create(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Isaac Asimov", result.getNome());
        verify(autorRepository, times(1)).existsByNomeIgnoreCase(anyString());
        verify(autorRepository, times(1)).save(any(Autor.class));
    }

    @Test
    void deveLancarExcecaoAoCriarAutorComNomeDuplicado() {
        // Arrange
        when(autorMapper.toEntity(requestDTO)).thenReturn(autor);
        when(autorRepository.existsByNomeIgnoreCase(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> autorService.create(requestDTO));
        verify(autorRepository, times(1)).existsByNomeIgnoreCase(anyString());
        verify(autorRepository, never()).save(any(Autor.class));
    }

    @Test
    void deveAtualizarAutor() {
        // Arrange
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        when(autorRepository.existsByNomeIgnoreCaseAndCodigoNot(anyString(), anyInt())).thenReturn(false);
        when(autorRepository.save(any(Autor.class))).thenReturn(autor);
        when(autorMapper.toResponseDTO(autor)).thenReturn(responseDTO);
        doNothing().when(autorMapper).updateEntityFromDTO(any(), any());

        // Act
        AutorResponseDTO result = autorService.update(1, requestDTO);

        // Assert
        assertNotNull(result);
        verify(autorRepository, times(1)).findById(1);
        verify(autorRepository, times(1)).save(any(Autor.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarComNomeDuplicado() {
        // Arrange
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        when(autorRepository.existsByNomeIgnoreCaseAndCodigoNot(anyString(), anyInt())).thenReturn(true);
        doNothing().when(autorMapper).updateEntityFromDTO(any(), any());

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> autorService.update(1, requestDTO));
        verify(autorRepository, never()).save(any(Autor.class));
    }

    @Test
    void deveDeletarAutor() {
        // Arrange
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        doNothing().when(autorRepository).delete(autor);

        // Act
        autorService.delete(1);

        // Assert
        verify(autorRepository, times(1)).findById(1);
        verify(autorRepository, times(1)).delete(autor);
    }

    @Test
    void deveLancarExcecaoAoDeletarAutorComLivrosAssociados() {
        // Arrange
        autor.getLivros().add(null);
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));

        // Act & Assert
        assertThrows(DomainException.class, () -> autorService.delete(1));
        verify(autorRepository, times(1)).findById(1);
        verify(autorRepository, never()).delete(any(Autor.class));
    }
}
