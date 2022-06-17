package com.mysite.core.service;


import com.mysite.core.models.NotaFiscalDto;
import org.apache.sling.api.SlingHttpServletRequest;

public interface NotaFiscalService {

    NotaFiscalDto getNotaFiscal(SlingHttpServletRequest request, String parameter);
}
