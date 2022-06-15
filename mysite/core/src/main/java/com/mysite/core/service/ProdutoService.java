package com.mysite.core.service;

import com.mysite.core.models.Produto;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;

public interface ProdutoService {

    List<Produto> getProdutos();

    Produto getProdutoById(int id);

    Produto postProduto(Produto produto);

    List<Produto> parameterFilter(SlingHttpServletRequest request);

    void deleteProdutoById(Integer id);

    Produto putProduto(Produto produto);

    List<Integer> idListOrObject(String listOrObject);

    boolean idChecker(Integer id);

    List<Produto> produtoListOrObject(String listOrObject);

    List<Produto> objectChecker(List<Produto> produtos);

    List<Produto> updateChecker(List<Produto> produtos);

}
