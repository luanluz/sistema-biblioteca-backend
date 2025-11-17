package biblioteca.dev.luanluz.api.mapper;

import biblioteca.dev.luanluz.api.dto.request.AutorRequestDTO;
import biblioteca.dev.luanluz.api.dto.response.AutorResponseDTO;
import biblioteca.dev.luanluz.api.model.Autor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutorMapper {

    private final ModelMapper modelMapper;

    public Autor toEntity(AutorRequestDTO dto) {
        return modelMapper.map(dto, Autor.class);
    }

    public AutorResponseDTO toResponseDTO(Autor entity) {
        return modelMapper.map(entity, AutorResponseDTO.class);
    }

    public void updateEntityFromDTO(AutorRequestDTO dto, Autor entity) {
        modelMapper.map(dto, entity);
    }
}
