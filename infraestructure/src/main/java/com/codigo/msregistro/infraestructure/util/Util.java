package com.codigo.msregistro.infraestructure.util;

import com.codigo.msregistro.domain.aggregates.dto.PersonaDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component      // Es de tipo component porque unicamente se definen metodos de APOYO, o sea no son metodos propiamente de la logica del negocio.
                // Otro claro ejemplo de otra clase al que deberiamos poner como @Component seria la clase "EmpresaMapper" o "PersonaMapper" ya que son clases de apoyo.
public class Util {

    public static String convertToJson(PersonaDTO personaDTO){  // es de tipo STATIC porque solo me va a traer un unico valor, que no afecta a la regla de negocio.
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(personaDTO);
        }catch (JsonProcessingException e){;
            throw new RuntimeException(e);
        }
    }
    public static <T> T convertFromJson(String json, Class<T> valueType){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, valueType);
        }catch (JsonProcessingException e){;
            throw new RuntimeException(e);
        }
    }

}