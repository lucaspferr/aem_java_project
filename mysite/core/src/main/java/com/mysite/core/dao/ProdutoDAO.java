package com.mysite.core.dao;

import com.mysite.core.models.Produto;

import java.util.List;

public interface ProdutoDAO {

    List<Produto> getProdutos();
    Produto postProduto(Produto produto);
    Produto getProdutoById(int id);
    List<Produto> findProdutoByName(String name);
    List<Produto> sortProdutoByPreco();
    List<Produto> findProdutoByCategory(String category);
    void deleteProdutoById(int id);
    Produto updateProduto(Produto produto, int id);

}
