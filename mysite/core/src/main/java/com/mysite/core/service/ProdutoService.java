package com.mysite.core.service;

import com.mysite.core.models.Produto;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;

public interface ProdutoService {

    List<Produto> getProdutos();

    Produto getProdutoById(int id);

    Produto postProduto(Produto produto);

    List<Produto> findProdutoByName(String name);

    List<Produto> sortProdutoByPreco();

    List<Produto> parameterFilter(SlingHttpServletRequest request);

    void deleteProdutoById(Integer id);

    Produto updateProduto(Produto produto, int id);

    List<Integer> idListOrObject(String listOrObject);

    boolean idChecker(Integer id);

}
