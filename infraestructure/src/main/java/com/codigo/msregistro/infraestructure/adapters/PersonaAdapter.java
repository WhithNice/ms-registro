package com.codigo.msregistro.infraestructure.adapters;

import com.codigo.msregistro.domain.aggregates.constants.Constants;
import com.codigo.msregistro.domain.aggregates.dto.PersonaDTO;
import com.codigo.msregistro.domain.aggregates.request.RequestPersona;
import com.codigo.msregistro.domain.aggregates.response.ResponseReniec;
import com.codigo.msregistro.domain.ports.out.PersonaServiceOut;
import com.codigo.msregistro.infraestructure.entity.PersonaEntity;
import com.codigo.msregistro.infraestructure.entity.TipoDocumentoEntity;
import com.codigo.msregistro.infraestructure.mapper.PersonaMapper;
import com.codigo.msregistro.infraestructure.redis.RedisService;
import com.codigo.msregistro.infraestructure.repository.PersonaRepository;
import com.codigo.msregistro.infraestructure.repository.TipoDocumentoRepository;
import com.codigo.msregistro.infraestructure.rest.client.ClienteReniec;
import com.codigo.msregistro.infraestructure.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonaAdapter implements PersonaServiceOut {
    private final PersonaRepository personaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final PersonaMapper personaMapper;
    private final ClienteReniec reniec;
    private final RedisService redisService;
    private final Util util;

    @Value("${token.api}")
    private String tokenApi;

    @Override
    public PersonaDTO crearPersonaOut(RequestPersona requestPersona) {
        ResponseReniec datosReniec = getExecutionReniec(requestPersona.getNumDoc());
        personaRepository.save(getEntity(datosReniec, requestPersona));

        return personaMapper.mapToDTO(getEntity(datosReniec, requestPersona));
    }

    @Override
    public Optional<PersonaDTO> obtenerPersonaOut(Long id) {
        String redisInfo = redisService.getFromRedis(Constants.REDIS_KEY_PERSONA + id);

        // Si el dato EXISTE EN REDIS:
        if(redisInfo != null){
            PersonaDTO personaDto = util.convertFromJson(redisInfo, PersonaDTO.class);
            return Optional.of(personaDto);

        // Si el dato NO EXISTE en REDIS.
        } else {
            PersonaDTO dto = personaMapper.mapToDTO(personaRepository.findById(id).get());
            String redis = util.convertToJson(dto);
            redisService.saveInRedis(Constants.REDIS_KEY_PERSONA+id, redis, 1);
            return Optional.of(dto);
        }
//        return Optional.ofNullable(personaMapper.mapToDTO(personaRepository.findById(id).get()));
    }

    @Override
    public List<PersonaDTO> obtenerTodosOut() {
        List<PersonaDTO> personaDTOList = new ArrayList<>();
        List<PersonaEntity> entities = personaRepository.findAll();

        for (PersonaEntity persona : entities) {
            PersonaDTO personaDTO = personaMapper.mapToDTO(persona);
            personaDTOList.add(personaDTO);
        }
        return personaDTOList;
    }

    @Override
    public PersonaDTO actualizarOut(Long id, RequestPersona requestPersona) {
        boolean existe = personaRepository.existsById(id);
        if (existe) {
            Optional<PersonaEntity> entity = personaRepository.findById(id);
            ResponseReniec responseReniec = getExecutionReniec(requestPersona.getNumDoc());
            personaRepository.save(getEntityUpdate(responseReniec, entity.get()));
            return personaMapper.mapToDTO(getEntityUpdate(responseReniec, entity.get()));
        }
        return null;
    }

    @Override
    public PersonaDTO deleteOut(Long id) {
        boolean existe = personaRepository.existsById(id);
        if (existe) {
            Optional<PersonaEntity> entity = personaRepository.findById(id);
            entity.get().setEstado(0);
            entity.get().setUsuaDelet(Constants.AUDIT_ADMIN);
            entity.get().setDateDelet(getTimestamp());
            personaRepository.save(entity.get());
            return personaMapper.mapToDTO(entity.get());
        }
        return null;
    }

    private ResponseReniec getExecutionReniec(String numero) {
        String authorization = "Bearer " + tokenApi;        // el BEARER es e TIPO DE TOKEN, tambien se puede configurar en el postman de la misma forma, buscando el tipo de TOKEN
        ResponseReniec responseReniec = reniec.getInfoReniec(numero,authorization);
        return responseReniec;
    }

    private PersonaEntity getEntity(ResponseReniec reniec, RequestPersona requestPersona){
        TipoDocumentoEntity tipoDocumento = tipoDocumentoRepository.findByCodTipo(requestPersona.getTipoDoc());
        PersonaEntity entity = new PersonaEntity();
        entity.setNumDocu(reniec.getNumeroDocumento());
        entity.setNombres(reniec.getNombres());
        entity.setApePat(reniec.getApellidoPaterno());
        entity.setApeMat(reniec.getApellidoMaterno());
        entity.setEstado(Constants.STATUS_ACTIVE);
        entity.setUsuaCrea(Constants.AUDIT_ADMIN);
        entity.setDateCreate(getTimestamp());
        entity.setTipoDocumento(tipoDocumento);
        return entity;
    }
    private PersonaEntity getEntityUpdate(ResponseReniec reniec, PersonaEntity personaActualizar){
        personaActualizar.setNombres(reniec.getNombres());
        personaActualizar.setApePat(reniec.getApellidoPaterno());
        personaActualizar.setApeMat(reniec.getApellidoMaterno());
        personaActualizar.setUsuaModif(Constants.AUDIT_ADMIN);
        personaActualizar.setDateModif(getTimestamp());
        return personaActualizar;
    }
    private Timestamp getTimestamp(){
        long currentTime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTime);
        return timestamp;
    }

}
