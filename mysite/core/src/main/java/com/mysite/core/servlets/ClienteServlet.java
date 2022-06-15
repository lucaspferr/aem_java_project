package com.mysite.core.servlets;


import com.google.gson.Gson;
import com.mysite.core.config.exceptions.IdNotFoundException;
import com.mysite.core.config.exceptions.InvalidValueException;
import com.mysite.core.models.Cliente;
import com.mysite.core.service.ClienteService;
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
        SLING_SERVLET_PATHS +"="+ "/bin/cliente",
        SLING_SERVLET_METHODS +"="+ HttpConstants.METHOD_GET,
        SLING_SERVLET_METHODS +"="+ HttpConstants.METHOD_POST,
        SLING_SERVLET_METHODS +"="+ HttpConstants.METHOD_PUT,
        SLING_SERVLET_METHODS +"="+ HttpConstants.METHOD_DELETE,
        SLING_SERVLET_EXTENSIONS +"="+ "json"
})
public class ClienteServlet extends SlingAllMethodsServlet {

    @Reference
    private ClienteService clienteService;


    static final String CLIENTE = "Cliente";

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            String body = request.getReader().lines().reduce("", (acc, line) -> acc + line);
            List<Cliente> clientes = new ArrayList<>();
            if (body.isEmpty()) {
                clientes = clienteService.getClientes();
            } else {
                List<Integer> ids = clienteService.idListOrObject(body);
                for (Integer id : ids) {
                    clientes.add(clienteService.getClienteById(id));
                }
            }
            if (clientes.size() > 1) buildResponse(response, SC_OK, new Gson().toJson(clientes));
            else if (clientes.size() == 1) buildResponse(response, SC_OK, new Gson().toJson(clientes.get(0)));
        }catch (IdNotFoundException e){buildResponse(response,SC_NOT_FOUND,e.getMessage());}
        catch(InvalidValueException e){buildResponse(response,SC_BAD_REQUEST,e.getMessage());}
    }

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        try{
            String body = request.getReader().lines().reduce("", (acc, line) -> acc + line);
            if(body.isEmpty()) throw new InvalidValueException(invalidPayload(CLIENTE));
            List<Cliente> clientes = new ArrayList<>();

            for(Cliente cliente : clienteService.clienteListOrObject(body)){
                clientes.add(clienteService.postCliente(cliente));
            }
            if (clientes.size() > 1) buildResponse(response, SC_CREATED, new Gson().toJson(clientes));
            else buildResponse(response, SC_CREATED, new Gson().toJson(clientes.get(0)));

        }catch (InvalidValueException e){buildResponse(response,SC_BAD_REQUEST,e.getMessage());}
        catch (Exception e){buildResponse(response,SC_BAD_REQUEST,invalidPayload(CLIENTE));}

    }

    @Override
    protected void doPut(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {

        try{
            String body = request.getReader().lines().reduce("", (acc, line) -> acc + line);
            if(body.isEmpty()) throw new InvalidValueException(invalidPayload(CLIENTE));
            List<Cliente> clientes = new ArrayList<>();

            clienteService.updateChecker(clienteService.clienteListOrObject(body));
            for(Cliente cliente : clienteService.clienteListOrObject(body)){
                clientes.add(clienteService.putCliente(cliente));
            }
            if (clientes.size() > 1) buildResponse(response, SC_CREATED, new Gson().toJson(clientes));
            else if (clientes.size() == 1) buildResponse(response, SC_CREATED, new Gson().toJson(clientes.get(0)));

        }catch (InvalidValueException e){buildResponse(response,SC_BAD_REQUEST,e.getMessage());}
        catch (IdNotFoundException e){buildResponse(response,SC_NOT_FOUND,e.getMessage());}
        catch (Exception e){buildResponse(response,SC_BAD_REQUEST,invalidPayload(CLIENTE));}

    }

    @Override
    protected void doDelete(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {

        try {
            String body = request.getReader().lines().reduce("", (acc, line) -> acc + line);
            List<Integer> ids = clienteService.idListOrObject(body);
            for (Integer id : ids) {
                clienteService.deleteCliente(id);
            }
            buildResponse(response, SC_NO_CONTENT, "");

        }catch (IdNotFoundException e){buildResponse(response,SC_NOT_FOUND,e.getMessage());}
        catch(InvalidValueException e){buildResponse(response,SC_BAD_REQUEST,e.getMessage());}
        catch (Exception e){buildResponse(response,SC_BAD_REQUEST,invalidPayload(CLIENTE));}
    }
}
