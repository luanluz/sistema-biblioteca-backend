package biblioteca.dev.luanluz.api.mapper;

import biblioteca.dev.luanluz.api.dto.request.AssuntoRequestDTO;
import biblioteca.dev.luanluz.api.dto.response.AssuntoResponseDTO;
import biblioteca.dev.luanluz.api.model.Assunto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssuntoMapper {

    private final ModelMapper modelMapper;

    public Assunto toEntity(AssuntoRequestDTO dto) {
        return modelMapper.map(dto, Assunto.class);
    }

    public AssuntoResponseDTO toResponseDTO(Assunto entity) {
        return modelMapper.map(entity, AssuntoResponseDTO.class);
    }

    public void updateEntityFromDTO(AssuntoRequestDTO dto, Assunto entity) {
        modelMapper.map(dto, entity);
    }
}
