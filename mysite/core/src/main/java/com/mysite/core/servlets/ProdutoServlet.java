package com.mysite.core.servlets;



import com.google.gson.Gson;
import com.mysite.core.config.exceptions.IdNotFoundException;
import com.mysite.core.config.exceptions.InvalidValueException;
import com.mysite.core.config.exceptions.NullValueException;
import com.mysite.core.models.Produto;
import com.mysite.core.service.ProdutoService;
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
        try{
            String body = request.getReader().lines().reduce("", (acc, curr) -> acc + curr);
            if(body.isEmpty()) throw new InvalidValueException(invalidPayload(PRODUTO));
            List<Produto> produtos = new ArrayList<>();

            for(Produto produto : produtoService.produtoListOrObject(body)){
                produtos.add(produtoService.postProduto(produto));
            }

            if(produtos.size() > 1) buildResponse(response, SC_CREATED, new Gson().toJson(produtos));
            else buildResponse(response, SC_CREATED, new Gson().toJson(produtos.get(0)));
        }catch (InvalidValueException e){buildResponse(response,SC_BAD_REQUEST,e.getMessage());}
        catch (Exception e){buildResponse(response,SC_BAD_REQUEST,invalidPayload(PRODUTO));}

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
        catch(Exception e){buildResponse(response,SC_BAD_REQUEST,invalidPayload(PRODUTO));}
    }

    @Override
    protected void doPut(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        try{
            String body = request.getReader().lines().reduce("", (acc, curr) -> acc + curr);
            if(body.isEmpty()) throw new InvalidValueException(invalidPayload(PRODUTO));
            List<Produto> produtos = new ArrayList<>();

            produtoService.updateChecker(produtoService.produtoListOrObject(body));
            for(Produto produto : produtoService.produtoListOrObject(body)){
                produtos.add(produtoService.putProduto(produto));
            }

            if(produtos.size() > 1) buildResponse(response, SC_CREATED, new Gson().toJson(produtos));
            else buildResponse(response, SC_CREATED, new Gson().toJson(produtos.get(0)));

        }catch (InvalidValueException e){buildResponse(response,SC_BAD_REQUEST,e.getMessage());}
        catch (IdNotFoundException e){buildResponse(response,SC_NOT_FOUND,e.getMessage());}
        catch(Exception e){buildResponse(response,SC_BAD_REQUEST,invalidPayload(PRODUTO));}
    }
}
