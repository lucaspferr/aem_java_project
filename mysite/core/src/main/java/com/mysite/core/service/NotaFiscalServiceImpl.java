package com.mysite.core.service;

import com.mysite.core.config.exceptions.InvalidValueException;
import com.mysite.core.dao.NotaFiscalDAO;
import com.mysite.core.models.NotaFiscalDto;
import com.mysite.core.utils.BuildResponse;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = NotaFiscalService.class)
public class NotaFiscalServiceImpl implements NotaFiscalService {

    @Reference
    DatabaseService databaseService;
    @Reference
    NotaFiscalDAO notaFiscalDAO;

    static final String NUMERO = "numero";
    static final String IDCLIENTE = "idcliente";

    @Override
    public NotaFiscalDto getNotaFiscal(SlingHttpServletRequest request, String parameter){
        try {
            NotaFiscalDto notaFiscalDto = new NotaFiscalDto();
            Integer identifier = Integer.parseInt(parameter);
            if (request.getParameter(NUMERO) == null) notaFiscalDto = notaFiscalDAO.getNotaFiscal(identifier,IDCLIENTE);
            else notaFiscalDto = notaFiscalDAO.getNotaFiscal(identifier,NUMERO);
            return notaFiscalDto;
        }catch (NumberFormatException e){throw new InvalidValueException(BuildResponse.invalidInput(parameter));}
    }
}
