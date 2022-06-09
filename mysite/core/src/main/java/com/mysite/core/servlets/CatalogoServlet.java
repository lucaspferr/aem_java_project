package com.mysite.core.servlets;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = {Servlet.class}, property = {
        SLING_SERVLET_PATHS +"="+ "/bin/app/catalogo",
        SLING_SERVLET_METHODS +"="+ HttpConstants.METHOD_GET,
        SLING_SERVLET_METHODS +"="+ HttpConstants.METHOD_POST,
        SLING_SERVLET_EXTENSIONS +"="+ "json"
})
public class CatalogoServlet extends SlingAllMethodsServlet {

    @Reference
    private ProdutoService produtoService;


    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException{
        String urlParam = request.getParameter("id");
        List urlParam2 = request.getRequestParameterList();
        List<Produto> list = produtoService.getProdutos();
        String json = produtoService.strToJson(list);
        json = produtoService.strToJson(urlParam2);
        produtoService.parameterFilter(json);
//        if(urlParam != null) {
//            Produto produto = produtoService.findProdutoById(Integer.parseInt(urlParam));
//            json = produtoService.strToJson(urlParam2);
//        }

        Produto produto = produtoService.parameterFilter(json);
        json = produtoService.strToJson(produto);
        response.getWriter().write(json);
    }

    @Override
    protected void doPost( SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        System.out.println(request.getReader());
        Produto produto_response = new ObjectMapper().readValue(request.getReader(), Produto.class);
        Produto produto = produtoService.postProduto(produto_response);
        String json = produtoService.strToJson(produto);
        response.getWriter().write(json);
    }
}
