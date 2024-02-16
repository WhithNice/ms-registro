package com.codigo.msregistro.infraestructure.adapters;

import com.codigo.msregistro.domain.aggregates.constants.Constants;
import com.codigo.msregistro.domain.aggregates.dto.EmpresaDTO;
import com.codigo.msregistro.domain.aggregates.request.RequestEmpresa;
import com.codigo.msregistro.domain.aggregates.response.ResponseSunat;
import com.codigo.msregistro.domain.ports.out.EmpresaServiceOut;
import com.codigo.msregistro.infraestructure.entity.EmpresaEntity;
import com.codigo.msregistro.infraestructure.entity.TipoDocumentoEntity;
import com.codigo.msregistro.infraestructure.mapper.EmpresaMapper;
import com.codigo.msregistro.infraestructure.repository.EmpresaRepository;
import com.codigo.msregistro.infraestructure.repository.TipoDocumentoRepository;
import com.codigo.msregistro.infraestructure.rest.client.ClienteSunat;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpresaAdapter implements EmpresaServiceOut {

    private final EmpresaRepository empresaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final EmpresaMapper empresaMapper;
    private final ClienteSunat sunat;
    @Value("${token.api}")
    private String tokenApi;

    @Override
    public EmpresaDTO crearEmpresaOut(RequestEmpresa requestEmpresa) {
        ResponseSunat datosSunat = getExecutionSunat(requestEmpresa.getNumDoc());
        empresaRepository.save(getEntity(datosSunat, requestEmpresa));

        EmpresaDTO resultado = empresaMapper.mapToDTO(getEntity(datosSunat, requestEmpresa));
        return resultado;
    }

    @Override
    public Optional<EmpresaDTO> obtenerEmpresaOut(Long id) {
        return Optional.ofNullable(empresaMapper.mapToDTO(empresaRepository.findById(id).get()));
    }

    @Override
    public List<EmpresaDTO> obtenerTodosOut() {
        List<EmpresaDTO> empresaDTOList = new ArrayList<>();
        List<EmpresaEntity> entities = empresaRepository.findAll();

        for (EmpresaEntity empresa : entities) {
            EmpresaDTO empresaDTO = empresaMapper.mapToDTO(empresa);
            empresaDTOList.add(empresaDTO);
        }
        return empresaDTOList;
    }

    @Override
    public EmpresaDTO actualizarOut(Long id, RequestEmpresa requestEmpresa) {
        boolean existe = empresaRepository.existsById(id);
        if (existe) {
            Optional<EmpresaEntity> entity = empresaRepository.findById(id);
            ResponseSunat responseSunat = getExecutionSunat(requestEmpresa.getNumDoc());
            empresaRepository.save(getEntityUpdate(responseSunat, entity.get()));
            return empresaMapper.mapToDTO(getEntityUpdate(responseSunat, entity.get()));
        }
        return null;
    }

    @Override
    public EmpresaDTO deleteOut(Long id) {
        boolean existe = empresaRepository.existsById(id);
        if (existe) {
            Optional<EmpresaEntity> entity = empresaRepository.findById(id);
            entity.get().setEstado(0);
            entity.get().setUsuaDelet(Constants.AUDIT_ADMIN);
            entity.get().setDateDelet(getTimestamp());
            empresaRepository.save(entity.get());
            return empresaMapper.mapToDTO(entity.get());
        }
        return null;
    }
    private ResponseSunat getExecutionSunat(String numero) {
        String authorization = "Bearer " + tokenApi;
        ResponseSunat responseSunat = sunat.getInfoSunat(numero,authorization);
        return responseSunat;
    }

    private EmpresaEntity getEntity(ResponseSunat sunat, RequestEmpresa requestEmpresa){
        TipoDocumentoEntity tipoDocumento = tipoDocumentoRepository.findByCodTipo(requestEmpresa.getTipoDoc());
        EmpresaEntity entity = new EmpresaEntity();
        entity.setNumDocu(sunat.getNumeroDocumento());
        entity.setRazonSocial(sunat.getRazonSocial());
        entity.setNomComercial(sunat.getRazonSocial());
        entity.setEstado(Constants.STATUS_ACTIVE);
        entity.setUsuaCrea(Constants.AUDIT_ADMIN);
        entity.setDateCreate(getTimestamp());
        entity.setTipoDocumento(tipoDocumento);
        return entity;
    }

    private EmpresaEntity getEntityUpdate(ResponseSunat sunat, EmpresaEntity empresaActualizar){
        empresaActualizar.setRazonSocial(sunat.getRazonSocial());
        empresaActualizar.setNomComercial(sunat.getRazonSocial());
        empresaActualizar.setUsuaModif(Constants.AUDIT_ADMIN);
        empresaActualizar.setDateModif(getTimestamp());
        return empresaActualizar;
    }

    private Timestamp getTimestamp(){
        long currentTime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTime);
        return timestamp;
    }
}
