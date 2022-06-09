package com.mysite.core.dao;

import com.mysite.core.models.Produto;

import java.util.List;

public interface ProdutoDAO {

    List<Produto> getProdutos();
    Produto postProduto(Produto produto);

    Produto findProdutoById(int id);
}
