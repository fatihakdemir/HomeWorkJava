package com.spring.api.service.impl;import com.google.common.base.Strings;import com.spring.api.service.config.ReportApiConfig;import com.spring.api.service.ReportService;import com.spring.api.service.domain.*;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.http.HttpEntity;import org.springframework.http.HttpHeaders;import org.springframework.http.HttpMethod;import org.springframework.http.ResponseEntity;import org.springframework.stereotype.Service;import org.springframework.web.client.RestTemplate;import org.springframework.web.util.UriComponentsBuilder;import java.util.Arrays;import java.util.Collections;import java.util.List;import java.util.Optional;@Servicepublic class ReportServiceImpl implements ReportService {    private Logger LOG= LoggerFactory.getLogger(ReportServiceImpl.class);    @Autowired    private RestTemplate restTemplate;    @Autowired    private ReportApiConfig config;    @Override    public Optional<Authorization> login(String email, String password) {        Optional<Authorization> authorization=Optional.empty();        try {            UriComponentsBuilder builder=UriComponentsBuilder.fromUriString(config.getLoginPath());            builder.queryParam("email",email);            builder.queryParam("password",password);            ResponseEntity<Authorization> responseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.POST, null, Authorization.class);            if (responseEntity.getStatusCode().is2xxSuccessful()){                authorization=Optional.ofNullable(responseEntity.getBody());                if (authorization.isPresent())                    ReportApiConfig.token=authorization.get().getToken();            }        }catch (Exception e){            LOG.error("::login failed",e);        }        return authorization;    }    @Override    public Optional<Reports> getReports(String fromDate, String toDate,Integer merchant,Integer acquirer) {        Optional<Reports> reports=Optional.empty();        try {            UriComponentsBuilder builder=UriComponentsBuilder.fromUriString(config.getReportsPath());            builder.queryParam("fromDate",fromDate);            builder.queryParam("toDate",toDate);            HttpHeaders headers=new HttpHeaders();            headers.add("Authorization",ReportApiConfig.token);            HttpEntity entity=new HttpEntity(headers);            if (merchant!=0)              builder.queryParam("merchant",merchant);            if (acquirer!=0)              builder.queryParam("acquirer",acquirer);            ResponseEntity<Reports> responseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.POST, entity, Reports.class);            if (responseEntity.getStatusCode().is2xxSuccessful()){                reports=Optional.ofNullable(responseEntity.getBody());            }        }catch (Exception e){            LOG.error("::getReports failed",e);        }        return reports;    }    @Override    public List<CustomerInfo> getClient(String transactionId) {        List<CustomerInfo> customers= Collections.emptyList();        try{            UriComponentsBuilder builder=UriComponentsBuilder.fromUriString(config.getClientPath());            HttpHeaders headers=new HttpHeaders();            headers.add("Authorization",ReportApiConfig.token);            HttpEntity entity=new HttpEntity(headers);            if(Strings.isNullOrEmpty(transactionId)){                builder.queryParam("transactionId",transactionId);            }            ResponseEntity<CustomerInfo[]> responseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.POST, entity, CustomerInfo[].class);            if(responseEntity.getStatusCode().is2xxSuccessful()){                customers= Arrays.asList(responseEntity.getBody());            }        }catch (Exception e){            LOG.error("::getCustomers",e);        }        return customers;    }    @Override    public List<Merchant> getMerchant(String transactionId) {        List<Merchant> merchant= Collections.emptyList();        try{            UriComponentsBuilder builder=UriComponentsBuilder.fromUriString(config.getMerchantPath());            HttpHeaders headers=new HttpHeaders();            headers.add("Authorization",ReportApiConfig.token);            HttpEntity entity=new HttpEntity(headers);            if(!Strings.isNullOrEmpty(transactionId)){                builder.queryParam("transactionId",transactionId);            }            ResponseEntity<Merchant[]> responseEntity = restTemplate.exchange(builder.build().toUri(), HttpMethod.POST, entity, Merchant[].class);            if(responseEntity.getStatusCode().is2xxSuccessful()){               merchant= Arrays.asList(responseEntity.getBody());            }        }catch (Exception e){            LOG.error("::getCustomers",e);        }        return merchant;    }}