package com.mysite.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mysite.core.dao.ProdutoDAO;
import com.mysite.core.models.Parameter;
import com.mysite.core.models.Produto;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component(immediate = true, service = ProdutoService.class)
public class ProdutoServiceImpl implements ProdutoService{

    @Reference
    private DatabaseService databaseService;
    @Reference
    private ProdutoDAO produtoDAO;


    @Override
    public List<Produto> getProdutos() {
        return produtoDAO.getProdutos();
    }

    @Override
    public Produto findProdutoById(int id) {
        return produtoDAO.findProdutoById(id);
    }

    @Override
    public Produto postProduto(Produto produto) {return produtoDAO.postProduto(produto);}


    @Override
    public String strToJson(Object object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }

    @Override
    public Produto parameterFilter(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Produto produto = new Produto();
            List<Parameter> parameters = mapper.readValue(json, new TypeReference<List<Parameter>>() {});
            for (Parameter parameter : parameters) {
                if (parameter.getName().matches("id")) {
                    produto = findProdutoById(Integer.parseInt(parameter.getValue()));
                }
            }
            return produto;
        }catch (Exception e){throw new IllegalStateException(e.getMessage());}
    }
}
