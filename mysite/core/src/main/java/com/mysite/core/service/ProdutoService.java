package com.mysite.core.service;

import com.mysite.core.models.Parameter;
import com.mysite.core.models.Produto;

import java.util.List;

public interface ProdutoService {

    List<Produto> getProdutos();
    Produto findProdutoById(int id);
    Produto postProduto(Produto produto);
    String strToJson(Object object);
    Produto parameterFilter(String json);

}
