package com.mysite.core.servlets;

import com.google.gson.Gson;
import com.mysite.core.config.exceptions.IdNotFoundException;
import com.mysite.core.config.exceptions.InvalidValueException;
import com.mysite.core.config.exceptions.NullValueException;
import com.mysite.core.config.exceptions.PostException;
import com.mysite.core.models.NotaFiscal;
import com.mysite.core.models.NotaFiscalDto;
import com.mysite.core.service.NotaFiscalService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mysite.core.utils.BuildResponse.*;
import static com.mysite.core.utils.BuildResponse.buildResponse;
import static javax.servlet.http.HttpServletResponse.*;
import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = {Servlet.class}, property = {
        SLING_SERVLET_PATHS +"="+ "/bin/notas",
        SLING_SERVLET_METHODS +"="+ HttpConstants.METHOD_GET,
        SLING_SERVLET_METHODS +"="+ HttpConstants.METHOD_POST,
        SLING_SERVLET_EXTENSIONS +"="+ "json"
})
public class NotaFiscalServlet extends SlingAllMethodsServlet {

    @Reference
    private NotaFiscalService notaFiscalService;

    static final String PARAMETER = "Número da Nota ou ID do Cliente";
    static final String NUMERO = "numero";
    static final String IDCLIENTE = "idcliente";
    static final String NOTA_FISCAL = "Nota Fiscal";

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            String parameter = request.getParameter(NUMERO);
            if(parameter == null) parameter = request.getParameter(IDCLIENTE);
            if(parameter == null) throw new NullValueException(parameterException(PARAMETER));
            List<NotaFiscalDto> notas = notaFiscalService.getNotaFiscal(request, parameter);

            if(notas.size() == 1)buildResponse(response, SC_OK, new Gson().toJson(notas.get(0)));
            else buildResponse(response, SC_OK, new Gson().toJson(notas));
        }catch (NullValueException | NullPointerException e){buildResponse(response, SC_BAD_REQUEST, e.getMessage());}
        catch (InvalidValueException e){buildResponse(response, SC_BAD_REQUEST, e.getMessage());}
        catch(IdNotFoundException e){buildResponse(response, SC_NOT_FOUND, e.getMessage());}
    }

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        try{
            String body = request.getReader().lines().reduce("", (acc, curr) -> acc + curr);
            if(body.isEmpty()) throw new InvalidValueException(invalidPayload(NOTA_FISCAL));
            List<NotaFiscal> notaFiscalList = new ArrayList<>();

            for(NotaFiscal notaFiscal : notaFiscalService.notaFiscalListOrObject(body)){
                notaFiscalList.add(notaFiscalService.postNotaFiscal(notaFiscal));
            }

            if(notaFiscalList.size() > 1) buildResponse(response, SC_CREATED, new Gson().toJson(notaFiscalList));
            else buildResponse(response, SC_CREATED, new Gson().toJson(notaFiscalList.get(0)));
        }catch (InvalidValueException e){buildResponse(response,SC_BAD_REQUEST,e.getMessage());}
        catch (PostException e){buildResponse(response,SC_INTERNAL_SERVER_ERROR,postFailed(e.getMessage()));}
        catch (Exception e){buildResponse(response,SC_BAD_REQUEST,invalidPayload(NOTA_FISCAL));}
    }
}
