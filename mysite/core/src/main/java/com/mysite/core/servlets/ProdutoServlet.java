package com.mysite.core.servlets;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mysite.core.config.exceptions.IdNotFoundException;
import com.mysite.core.config.exceptions.InvalidValueException;
import com.mysite.core.config.exceptions.NullValueException;
import com.mysite.core.models.Cliente;
import com.mysite.core.models.Produto;
import com.mysite.core.service.ProdutoService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.List;

import static com.mysite.core.utils.BuildResponse.*;
import static javax.servlet.http.HttpServletResponse.*;
import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = {Servlet.class}, property = {
        SLING_SERVLET_PATHS +"="+ "/bin/produto",
        SLING_SERVLET_METHODS +"="+ HttpConstants.METHOD_GET,
        SLING_SERVLET_METHODS +"="+ HttpConstants.METHOD_POST,
        SLING_SERVLET_METHODS +"="+ HttpConstants.METHOD_PUT,
        SLING_SERVLET_METHODS +"="+ HttpConstants.METHOD_DELETE,
        SLING_SERVLET_EXTENSIONS +"="+ "json"
})
public class ProdutoServlet extends SlingAllMethodsServlet {

    @Reference
    private ProdutoService produtoService;

    private Gson gson = new Gson();
    static final String PRODUTO = "Produto";
    static final String ID = "id";

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            String param = request.getQueryString();
            List<Produto> produtos;
            if (!param.isEmpty() || !param.equals("")) {
                produtos = produtoService.parameterFilter(request);
            } else {
                produtos = produtoService.getProdutos();
            }
            String json = gson.toJson(produtos);
            if (produtos.size() == 1) json = json.substring(1, json.length() - 1);
            buildResponse(response, SC_OK, json);
        } catch (NullValueException e) {
            buildResponse(response, SC_NOT_FOUND, e.getMessage());
        } catch (InvalidValueException e) {
            buildResponse(response, SC_BAD_REQUEST, e.getMessage());
        }

    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            String body = request.getReader().lines().reduce("", (acc, curr) -> acc + curr);
            String json = "";
            if (body.contains("[")) {
                List<Produto> produtos = gson.fromJson(body, new TypeToken<List<Produto>>() {
                }.getType());
                for (Produto produto : produtos) {
                    produtoService.postProduto(produto);
                }
                json = gson.toJson(produtos);
            } else {
                Produto produto = gson.fromJson(body, Produto.class);
                produtoService.postProduto(produto);
                json = gson.toJson(produto);
            }
            buildResponse(response, SC_CREATED, json);
        } catch (Exception e) {
            buildResponse(response, SC_INTERNAL_SERVER_ERROR, invalidPayload(PRODUTO));
        }
    }

    @Override
    protected void doDelete(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {

        try{
            String body = request.getReader().lines().reduce("", (acc, curr) -> acc + curr);
            List<Integer> ids = produtoService.idListOrObject(body);
            for(Integer id : ids){
                produtoService.deleteProdutoById(id);
            }
            buildResponse(response, SC_NO_CONTENT, "");
        }catch (IdNotFoundException e){buildResponse(response,SC_NOT_FOUND,e.getMessage());}
        catch (InvalidValueException e){buildResponse(response,SC_BAD_REQUEST,e.getMessage());}
//        String idsDeleted = "";
//        String idNotFound = "";
//        try {
//            //Ate o momento, o servlet apaga multiplos produtos apenas pelo envio do param no formato id=1,2,3 , por exemplo
//            RequestParameter id = request.getRequestParameter("id");
//            if (id.getString().isEmpty()) throw new NullValueException(nullParameter(ID));
//            String[] param = id.getString().split(",");
//            for (String s : param) {
//                idNotFound = s;
//                produtoService.deleteProdutoById(s);
//                if (idsDeleted.isEmpty()) idsDeleted = s;
//                else idsDeleted = new StringBuilder().append(idsDeleted).append(",").append(s).toString();
//            }
//            buildResponse(response, SC_NO_CONTENT, "");
//
//        } catch (InvalidValueException e) {
//            buildResponse(response, SC_BAD_REQUEST, e.getMessage());
//        } catch (IllegalStateException e) {
//            buildResponse(response, SC_BAD_REQUEST, idNotFound(idNotFound, PRODUTO, idsDeleted));
//        } catch (NullPointerException e) {
//            buildResponse(response, SC_BAD_REQUEST, badRequest(request.getQueryString()));
//        }
    }

    @Override
    protected void doPut(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {



//        try {
//            Produto produto = new ObjectMapper().readValue(request.getReader(), Produto.class);
//            produto = produtoService.updateProduto(produto, Integer.parseInt(request.getParameter("id")));
//            if (produto.getId() == null) {buildResponse(response, SC_NOT_FOUND, idNotFound(request.getParameter("id"), PRODUTO, ""));}
//            else buildResponse(response, SC_CREATED, gson.toJson(produto));
//        } catch (UnrecognizedPropertyException | NullValueException e) {
//            buildResponse(response, SC_BAD_REQUEST, invalidPayload(PRODUTO));
//        }catch (Exception e) {
//            buildResponse(response, SC_BAD_REQUEST, badRequest(request.getQueryString()));
//        }
    }
}
