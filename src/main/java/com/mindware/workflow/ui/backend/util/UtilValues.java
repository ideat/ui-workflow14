package com.mindware.workflow.ui.backend.util;

import com.mindware.workflow.ui.backend.entity.Office;
import com.mindware.workflow.ui.backend.entity.config.ExchangeRate;
import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.entity.stageHistory.StageHistory;
import com.mindware.workflow.ui.backend.rest.exchangeRate.ExchangeRateRestTemplate;
import com.mindware.workflow.ui.backend.rest.office.OfficeRestTemplate;
import com.mindware.workflow.ui.backend.rest.parameter.ParameterRestTemplate;
import com.mindware.workflow.ui.backend.rest.stageHistory.StageHistoryRestTemplate;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UtilValues {

    public static List<String> getParameterValueDescription(String typeParameter){
        ParameterRestTemplate rest = new ParameterRestTemplate();
        List<Parameter> listCaedec = rest.getParametersByCategory(typeParameter);
        List<String> list = new ArrayList<>();
        for(Parameter p:listCaedec){
            list.add(p.getValue()+"-"+p.getDescription());
        }
        return list;
    }

    public static List<String> getParamterValue(String typeParameter){
        ParameterRestTemplate rest = new ParameterRestTemplate();
        List<Parameter> listState = rest.getParametersByCategory(typeParameter);
        List<String> list = new ArrayList<>();
        for(Parameter p:listState){
            list.add(p.getValue());
        }
        return list;
    }



    public static List<String> getListOfficeCodeName(){
        OfficeRestTemplate rest = new OfficeRestTemplate();
        List<Office> listOffice = rest.getAllOffice();
        List<String> list = new ArrayList<>();
        for(Office o : listOffice){
            list.add(o.getInternalCode() + "-" + o.getName());
        }
        return list;
    }

    public static class DoubleToIntegerConverter implements Converter<Double, Integer> {

        private static final long serialVersionUID = 1L;

        @Override
        public Result<Integer> convertToModel(Double presentation, ValueContext valueContext) {
            return Result.ok(presentation.intValue());
        }

        @Override
        public Double convertToPresentation(Integer model, ValueContext valueContext) {
            return model == null?0.0:model.doubleValue();
        }

    }

    public static class LocalDateTimeToStringConverter implements Converter<String, LocalDateTime> {
        @Override
        public Result<LocalDateTime> convertToModel(String presentation, ValueContext valueContext) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return Result.ok(LocalDateTime.parse(presentation,formatter));
        }

        @Override
        public String convertToPresentation(LocalDateTime model, ValueContext valueContext) {
            return model.toString();
        }
    }

    public static class InstantToStringConverter implements Converter<String, Instant> {

        @Override
        public Result<Instant> convertToModel(String presentation, ValueContext valueContext) {
//            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm",Locale.UK);
            DateTimeFormatter f = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale( Locale.UK )
                    .withZone( ZoneId.systemDefault() );

            LocalDateTime ldt = LocalDateTime.parse(presentation,f);
            ZoneId z = ZoneId.systemDefault();
            ZonedDateTime zdt = ldt.atZone( z ) ;

            return Result.ok(zdt.toInstant());
        }

        @Override
        public String convertToPresentation(Instant model, ValueContext valueContext) {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                            .withLocale( Locale.UK )
                            .withZone( ZoneId.systemDefault() );

            String output="";
            if(model!=null)
             output= formatter.format( model );

            return output;
        }
    }

    public static  class SetToStringConverter implements Converter<Set<String>, String>{

        @Override
        public Result<String> convertToModel(Set<String> presentation, ValueContext valueContext) {
            String joined = String.join(",",presentation);

            return Result.ok(joined);
        }

        @Override
        public Set<String> convertToPresentation(String model, ValueContext valueContext) {
            Set<String> output = new HashSet<>();
            if(model!=null) {
                String[] list = model.split(",");
                for(String s:list){
                    output.add(s);
                }
            }
            return output;
        }
    }

    public static class StringToSet implements Converter<String,Set<String>>{

        @Override
        public Result<Set<String>> convertToModel(String model, ValueContext valueContext) {
            Set<String> output = new HashSet<>();
            if(model!=null) {
                String[] list = model.split(",");
                for(String s:list){
                    output.add(s);
                }
            }
            return Result.ok(output);
        }

        @Override
        public String convertToPresentation(Set<String> presentation, ValueContext valueContext) {

            String joined = String.join(",",presentation);

            return joined;
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static String generateRandomPassword(){
        Random random = new Random();
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    public static Double getExchange(){
        ExchangeRateRestTemplate restTemplate = new ExchangeRateRestTemplate();
        ExchangeRate exchangeRate= restTemplate.getActiveExchangeRateByCurrency("$us.");
        return exchangeRate.getExchange();
    }

    public static boolean isActiveCreditRequest(Integer numberRequest){
        StageHistoryRestTemplate stageHistoryRestTemplate = new StageHistoryRestTemplate();
        List<StageHistory> stageHistoryList = stageHistoryRestTemplate.getByNumberRequest(numberRequest);
        stageHistoryList = stageHistoryList.stream()
                .filter(s->s.getStage().equals("DESEMBOLSO") && s.getState().equals("CONCLUIDO"))
                .collect(Collectors.toList());
        if(stageHistoryList.size()>0){
            return false;
        }else{
            return true;
        }
    }
}
