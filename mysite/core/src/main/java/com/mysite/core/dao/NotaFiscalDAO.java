package com.mysite.core.dao;


import com.mysite.core.models.NotaFiscal;
import com.mysite.core.models.NotaFiscalDto;

public interface NotaFiscalDAO {

    NotaFiscalDto getNotaFiscal(Integer id, String identifier);


}
