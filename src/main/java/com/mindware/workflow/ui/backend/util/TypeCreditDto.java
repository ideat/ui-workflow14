package com.mindware.workflow.ui.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.config.ObjectCredit;
import com.mindware.workflow.ui.backend.entity.config.ProductTypeCredit;
import com.mindware.workflow.ui.backend.entity.config.RequestStage;
import com.mindware.workflow.ui.backend.entity.config.TypeCredit;
import com.mindware.workflow.ui.backend.entity.config.dto.TypeCreditObjectCreditDto;
import com.mindware.workflow.ui.backend.entity.config.dto.TypeCreditProductCreditDto;
import com.mindware.workflow.ui.backend.rest.typeCredit.TypeCreditRestTemplate;

import java.util.ArrayList;
import java.util.List;

public class TypeCreditDto {

    public static List<TypeCreditProductCreditDto> getTypeCreditProductCreditDto(TypeCredit typeCredit) throws JsonProcessingException {
        List<TypeCreditProductCreditDto> typeCreditProductCreditDtoList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        List<ProductTypeCredit> productTypeCreditList = mapper.readValue(typeCredit.getProductTypeCredit(),
                new TypeReference<List<ProductTypeCredit>>() {});
        for(ProductTypeCredit p: productTypeCreditList ){
            TypeCreditProductCreditDto typeCreditProductCreditDto = new TypeCreditProductCreditDto();
            typeCreditProductCreditDto.setCodeTypeCredit(typeCredit.getExternalCode());
            typeCreditProductCreditDto.setCodeProductTypeCredit(p.getExternalCode());
            typeCreditProductCreditDto.setProductTypeCreditDescription(p.getDescription());
            typeCreditProductCreditDtoList.add(typeCreditProductCreditDto);
        }
        return typeCreditProductCreditDtoList;
    }

    public static List<TypeCreditObjectCreditDto> getTypeCreditObjectCreditDto(TypeCredit typeCredit) throws JsonProcessingException {
        List<TypeCreditObjectCreditDto> typeCreditObjectCreditDtoList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        List<ObjectCredit> objectCreditList = mapper.readValue(typeCredit.getObjectCredit(),
                new TypeReference<List<ObjectCredit>>() {});
        for(ObjectCredit o:objectCreditList){
            TypeCreditObjectCreditDto typeCreditObjectCreditDto = new TypeCreditObjectCreditDto();
            typeCreditObjectCreditDto.setCodeTypeCredit(typeCredit.getExternalCode());
            typeCreditObjectCreditDto.setTypeCreditDescription(typeCredit.getDescription());
            typeCreditObjectCreditDto.setExternalCodeObjectCredit(o.getExternalCode());
            typeCreditObjectCreditDto.setObjectCreditDescription(o.getDescription());
        }
        return typeCreditObjectCreditDtoList;
    }

    public static List<TypeCreditObjectCreditDto> getAllTypeCreditObjectCreditDto() throws JsonProcessingException {
        TypeCreditRestTemplate restTemplate = new TypeCreditRestTemplate();

        List<TypeCredit> typeCreditList = restTemplate.getAll();

        List<TypeCreditObjectCreditDto> typeCreditObjectCreditDtoList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for(TypeCredit t:typeCreditList){
            List<ObjectCredit> objectCreditList = mapper.readValue(t.getObjectCredit(),
                    new TypeReference<List<ObjectCredit>>() {});
            for(ObjectCredit o:objectCreditList){
                TypeCreditObjectCreditDto typeCreditObjectCreditDto = new TypeCreditObjectCreditDto();
                typeCreditObjectCreditDto.setCodeTypeCredit(t.getExternalCode());
                typeCreditObjectCreditDto.setTypeCreditDescription(t.getDescription());
                typeCreditObjectCreditDto.setExternalCodeObjectCredit(o.getExternalCode());
                typeCreditObjectCreditDto.setObjectCreditDescription(o.getDescription());
                typeCreditObjectCreditDtoList.add(typeCreditObjectCreditDto);
            }
        }

        return typeCreditObjectCreditDtoList;
    }

    public static List<String> getTypeCredit(){
        TypeCreditRestTemplate restTemplate = new TypeCreditRestTemplate();
        List<String> result = new ArrayList<>();
        List<TypeCredit> typeCreditList = restTemplate.getAll();
        for(TypeCredit t:typeCreditList){
            String typeCredit = t.getExternalCode()+"-"+t.getDescription();
            result.add(typeCredit);
        }
        return result;
    }

    public static List<String> getProductList(String codeTypeCredit)  {
        TypeCreditRestTemplate restTemplate = new TypeCreditRestTemplate();
        List<String> result = new ArrayList<>();
        TypeCredit typeCredit = restTemplate.getByExternalCode(codeTypeCredit);
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ProductTypeCredit> productTypeCreditList = mapper.readValue(typeCredit.getProductTypeCredit(),
                    new TypeReference<List<ProductTypeCredit>>() {});
            for(ProductTypeCredit p: productTypeCreditList){
                String productTypeCredit = p.getExternalCode()+"-"+p.getDescription();
                result.add(productTypeCredit);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> getObjectList(String codeTypeCredit)  {
        TypeCreditRestTemplate restTemplate = new TypeCreditRestTemplate();
        List<String> result = new ArrayList<>();
        TypeCredit typeCredit = restTemplate.getByExternalCode(codeTypeCredit);
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ObjectCredit> objectCreditList = mapper.readValue(typeCredit.getObjectCredit(),
                    new TypeReference<List<ObjectCredit>>() {});
            for(ObjectCredit p: objectCreditList){
                String objectCredit = p.getExternalCode()+"-"+ p.getDescription();
                result.add(objectCredit);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
