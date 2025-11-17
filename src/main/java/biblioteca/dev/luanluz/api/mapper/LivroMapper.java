package biblioteca.dev.luanluz.api.mapper;

import biblioteca.dev.luanluz.api.dto.request.LivroRequestDTO;
import biblioteca.dev.luanluz.api.dto.response.LivroResponseDTO;
import biblioteca.dev.luanluz.api.model.Assunto;
import biblioteca.dev.luanluz.api.model.Autor;
import biblioteca.dev.luanluz.api.model.Livro;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LivroMapper {

    private final ModelMapper modelMapper;
    private final AutorMapper autorMapper;
    private final AssuntoMapper assuntoMapper;

    public Livro toEntity(LivroRequestDTO dto) {
        Livro livro = modelMapper.map(dto, Livro.class);
        livro.setAutores(convertAutoresCodigos(dto.getAutoresCodigos()));
        livro.setAssuntos(convertAssuntosCodigos(dto.getAssuntosCodigos()));
        return livro;
    }

    public LivroResponseDTO toResponseDTO(Livro entity) {
        LivroResponseDTO dto = modelMapper.map(entity, LivroResponseDTO.class);
        dto.setAutores(entity.getAutores().stream()
                .map(autorMapper::toResponseDTO)
                .collect(Collectors.toSet()));
        dto.setAssuntos(entity.getAssuntos().stream()
                .map(assuntoMapper::toResponseDTO)
                .collect(Collectors.toSet()));
        return dto;
    }

    public void updateEntityFromDTO(LivroRequestDTO dto, Livro entity) {
        modelMapper.map(dto, entity);
        entity.setAutores(convertAutoresCodigos(dto.getAutoresCodigos()));
        entity.setAssuntos(convertAssuntosCodigos(dto.getAssuntosCodigos()));
    }

    private Set<Autor> convertAutoresCodigos(Set<Integer> codigos) {
        if (codigos == null || codigos.isEmpty()) {
            return new HashSet<>();
        }

        return codigos.stream()
                .map(codigo -> Autor.builder().codigo(codigo).build())
                .collect(Collectors.toSet());
    }

    private Set<Assunto> convertAssuntosCodigos(Set<Integer> codigos) {
        if (codigos == null || codigos.isEmpty()) {
            return new HashSet<>();
        }

        return codigos.stream()
                .map(codigo -> Assunto.builder().codigo(codigo).build())
                .collect(Collectors.toSet());
    }
}
