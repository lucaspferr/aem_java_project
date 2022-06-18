package com.mysite.core.service;


import com.mysite.core.models.NotaFiscal;
import com.mysite.core.models.NotaFiscalDto;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;

public interface NotaFiscalService {

    List<NotaFiscalDto> getNotaFiscal(SlingHttpServletRequest request, String parameter);

    List<NotaFiscal> notaFiscalListOrObject(String listOrObject);

    void objectChecker(List<NotaFiscal> notaFiscalList);

    NotaFiscal postNotaFiscal(NotaFiscal notaFiscal);

}
