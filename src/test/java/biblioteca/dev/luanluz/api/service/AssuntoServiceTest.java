package biblioteca.dev.luanluz.api.service;

import biblioteca.dev.luanluz.api.dto.request.AssuntoRequestDTO;
import biblioteca.dev.luanluz.api.dto.response.AssuntoResponseDTO;
import biblioteca.dev.luanluz.api.exception.DomainException;
import biblioteca.dev.luanluz.api.exception.DuplicateResourceException;
import biblioteca.dev.luanluz.api.exception.ResourceNotFoundException;
import biblioteca.dev.luanluz.api.mapper.AssuntoMapper;
import biblioteca.dev.luanluz.api.model.Assunto;
import biblioteca.dev.luanluz.api.repository.AssuntoRepository;
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
class AssuntoServiceTest {

    @Mock
    private AssuntoRepository assuntoRepository;

    @Mock
    private AssuntoMapper assuntoMapper;

    @InjectMocks
    private AssuntoService assuntoService;

    private Assunto assunto;
    private AssuntoRequestDTO requestDTO;
    private AssuntoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        assunto = new Assunto();
        assunto.setCodigo(1);
        assunto.setDescricao("Ficção Científica");
        assunto.setLivros(new HashSet<>());

        requestDTO = new AssuntoRequestDTO();
        requestDTO.setDescricao("Ficção Científica");

        responseDTO = new AssuntoResponseDTO();
        responseDTO.setCodigo(1);
        responseDTO.setDescricao("Ficção Científica");
    }

    @Test
    void deveBuscarTodosComPaginacao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Assunto> page = new PageImpl<>(Collections.singletonList(assunto));

        when(assuntoRepository.findAll(pageable)).thenReturn(page);
        when(assuntoMapper.toResponseDTO(any(Assunto.class))).thenReturn(responseDTO);

        // Act
        Page<AssuntoResponseDTO> result = assuntoService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(assuntoRepository, times(1)).findAll(pageable);
    }

    @Test
    void deveBuscarPorId() {
        // Arrange
        when(assuntoRepository.findById(1)).thenReturn(Optional.of(assunto));
        when(assuntoMapper.toResponseDTO(assunto)).thenReturn(responseDTO);

        // Act
        AssuntoResponseDTO result = assuntoService.findById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCodigo());
        assertEquals("Ficção Científica", result.getDescricao());
        verify(assuntoRepository, times(1)).findById(1);
    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontrarPorId() {
        // Arrange
        when(assuntoRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> assuntoService.findById(999));
        verify(assuntoRepository, times(1)).findById(999);
    }

    @Test
    void deveCriarNovoAssunto() {
        // Arrange
        when(assuntoMapper.toEntity(requestDTO)).thenReturn(assunto);
        when(assuntoRepository.existsByDescricaoIgnoreCase(anyString())).thenReturn(false);
        when(assuntoRepository.save(any(Assunto.class))).thenReturn(assunto);
        when(assuntoMapper.toResponseDTO(assunto)).thenReturn(responseDTO);

        // Act
        AssuntoResponseDTO result = assuntoService.create(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Ficção Científica", result.getDescricao());
        verify(assuntoRepository, times(1)).existsByDescricaoIgnoreCase(anyString());
        verify(assuntoRepository, times(1)).save(any(Assunto.class));
    }

    @Test
    void deveLancarExcecaoAoCriarAssuntoComDescricaoDuplicada() {
        // Arrange
        when(assuntoMapper.toEntity(requestDTO)).thenReturn(assunto);
        when(assuntoRepository.existsByDescricaoIgnoreCase(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> assuntoService.create(requestDTO));
        verify(assuntoRepository, times(1)).existsByDescricaoIgnoreCase(anyString());
        verify(assuntoRepository, never()).save(any(Assunto.class));
    }

    @Test
    void deveAtualizarAssunto() {
        // Arrange
        when(assuntoRepository.findById(1)).thenReturn(Optional.of(assunto));
        when(assuntoRepository.existsByDescricaoIgnoreCaseAndCodigoNot(anyString(), anyInt())).thenReturn(false);
        when(assuntoRepository.save(any(Assunto.class))).thenReturn(assunto);
        when(assuntoMapper.toResponseDTO(assunto)).thenReturn(responseDTO);
        doNothing().when(assuntoMapper).updateEntityFromDTO(any(), any());

        // Act
        AssuntoResponseDTO result = assuntoService.update(1, requestDTO);

        // Assert
        assertNotNull(result);
        verify(assuntoRepository, times(1)).findById(1);
        verify(assuntoRepository, times(1)).save(any(Assunto.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarComDescricaoDuplicada() {
        // Arrange
        when(assuntoRepository.findById(1)).thenReturn(Optional.of(assunto));
        when(assuntoRepository.existsByDescricaoIgnoreCaseAndCodigoNot(anyString(), anyInt())).thenReturn(true);
        doNothing().when(assuntoMapper).updateEntityFromDTO(any(), any());

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> assuntoService.update(1, requestDTO));
        verify(assuntoRepository, never()).save(any(Assunto.class));
    }

    @Test
    void deveDeletarAssunto() {
        // Arrange
        when(assuntoRepository.findById(1)).thenReturn(Optional.of(assunto));
        doNothing().when(assuntoRepository).delete(assunto);

        // Act
        assuntoService.delete(1);

        // Assert
        verify(assuntoRepository, times(1)).findById(1);
        verify(assuntoRepository, times(1)).delete(assunto);
    }

    @Test
    void deveLancarExcecaoAoDeletarAssuntoComLivrosAssociados() {
        // Arrange
        assunto.getLivros().add(null);
        when(assuntoRepository.findById(1)).thenReturn(Optional.of(assunto));

        // Act & Assert
        assertThrows(DomainException.class, () -> assuntoService.delete(1));
        verify(assuntoRepository, times(1)).findById(1);
        verify(assuntoRepository, never()).delete(any(Assunto.class));
    }
}
