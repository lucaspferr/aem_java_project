package com.mysite.core.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mysite.core.config.exceptions.InvalidValueException;
import com.mysite.core.dao.NotaFiscalDAO;
import com.mysite.core.models.NotaFiscal;
import com.mysite.core.models.NotaFiscalDto;
import com.mysite.core.models.Produto;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;

import static com.mysite.core.utils.BuildResponse.*;
import static com.mysite.core.utils.BuildResponse.invalidPayload;

@Component(immediate = true, service = NotaFiscalService.class)
public class NotaFiscalServiceImpl implements NotaFiscalService {

    @Reference
    DatabaseService databaseService;
    @Reference
    NotaFiscalDAO notaFiscalDAO;

    static final String NUMERO = "numero";
    static final String IDCLIENTE = "idcliente";
    static final String IDPRODUTO = "idproduto";
    static final String NOTAFISCAL = "Nota Fiscal";

    @Override
    public NotaFiscalDto getNotaFiscal(SlingHttpServletRequest request, String parameter){
        try {
            NotaFiscalDto notaFiscalDto = new NotaFiscalDto();
            Integer identifier = Integer.parseInt(parameter);
            if (request.getParameter(NUMERO) == null) notaFiscalDto = notaFiscalDAO.getNotaFiscal(identifier,IDCLIENTE);
            else notaFiscalDto = notaFiscalDAO.getNotaFiscal(identifier,NUMERO);
            return notaFiscalDto;
        }catch (NumberFormatException e){throw new InvalidValueException(invalidInput(parameter));}
    }

    @Override
    public List<NotaFiscal> notaFiscalListOrObject(String listOrObject) {
        List<NotaFiscal> notaFiscalList = new ArrayList<>();
        try{
            List<NotaFiscal> list = new Gson().fromJson(listOrObject, new TypeToken<List<NotaFiscal>>(){}.getType());
            notaFiscalList = list;
        }catch (JsonSyntaxException e){
            NotaFiscal notaFiscal = new Gson().fromJson(listOrObject, NotaFiscal.class);
            notaFiscalList.add(notaFiscal);
        }catch (Exception e){throw new InvalidValueException(invalidPayload(NOTAFISCAL));}
        objectChecker(notaFiscalList);


        return notaFiscalList;
    }

    @Override
    public void objectChecker(List<NotaFiscal> notaFiscalList) {
        for(NotaFiscal notaFiscal : notaFiscalList){
            if(notaFiscal.getNumero()==null) throw new InvalidValueException(invalidPayloadDetailed(NUMERO,NOTAFISCAL));
            if(notaFiscal.getIdcliente()==null) throw new InvalidValueException(invalidPayloadDetailed(IDCLIENTE,NOTAFISCAL));
            if(notaFiscal.getIdproduto()==null) throw new InvalidValueException(invalidPayloadDetailed(IDPRODUTO,NOTAFISCAL));
        }
    }

    @Override
    public NotaFiscal postNotaFiscal(NotaFiscal notaFiscal) {
        return notaFiscalDAO.postNotaFiscal(notaFiscal);
    }
}
