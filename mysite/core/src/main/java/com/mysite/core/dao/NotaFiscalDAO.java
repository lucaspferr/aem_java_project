package com.mysite.core.dao;


import com.mysite.core.models.NotaFiscal;
import com.mysite.core.models.NotaFiscalDto;

import java.util.List;

public interface NotaFiscalDAO {

    NotaFiscal postNotaFiscal(NotaFiscal notaFiscal);

    List<NotaFiscalDto> getNotaFiscal(Integer id, String identifier);


}
