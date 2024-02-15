package com.codigo.msregistro.infraestructure.mapper;

import com.codigo.msregistro.domain.aggregates.dto.PersonaDTO;
import com.codigo.msregistro.infraestructure.entity.PersonaEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class PersonaMapper {
    // El model mapper es como el MapStruck pero mas simple, con la condicion que ambas entidades que se mapeara tengan los mismos atributos.
    private static final ModelMapper modelMapper = new ModelMapper();

    public PersonaDTO mapToDTO(PersonaEntity entity) {
        return modelMapper.map(entity, PersonaDTO.class);
    }

    public PersonaEntity mapToEntity(PersonaDTO personaDTO) {
        return modelMapper.map(personaDTO, PersonaEntity.class);
    }
}
