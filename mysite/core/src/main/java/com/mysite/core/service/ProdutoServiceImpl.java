package com.mysite.core.service;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mysite.core.config.exceptions.IdNotFoundException;
import com.mysite.core.config.exceptions.InvalidValueException;
import com.mysite.core.config.exceptions.NullValueException;
import com.mysite.core.dao.ProdutoDAO;
import com.mysite.core.models.Cliente;
import com.mysite.core.models.IdList;
import com.mysite.core.models.Produto;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

import static com.mysite.core.utils.BuildResponse.*;

@Component(immediate = true, service = ProdutoService.class)
public class ProdutoServiceImpl implements ProdutoService{

    @Reference
    private DatabaseService databaseService;
    @Reference
    private ProdutoDAO produtoDAO;
    static final String CATEGORIA = "categoria";
    static final String ID = "id";
    static final String NOME = "nome";
    static final String PRECO = "preco";
    static final String PRODUTO = "Produto";


    @Override
    public List<Produto> getProdutos() {
        return produtoDAO.getProdutos();
    }

    @Override
    public Produto getProdutoById(int id) {
        return produtoDAO.getProdutoById(id);
    }

    @Override
    public Produto postProduto(Produto produto) {return produtoDAO.postProduto(produto);}

    @Override
    public List<Produto> parameterFilter(SlingHttpServletRequest request){
        List<Produto> produtos = new ArrayList<>();
        String parameter = "";
        if(request.getParameter(ID)!=null){
            try{
                produtos.add(getProdutoById(Integer.parseInt(request.getParameter(ID))));
                if(produtos.isEmpty()) throw new NullValueException(idNotFound(request.getParameter(ID),PRODUTO,""));
                return produtos;
            }catch (Exception e){throw new InvalidValueException(invalidInput(request.getParameter(ID)));}
        }else if(request.getParameter(CATEGORIA)!=null){
            produtos = produtoDAO.findProdutoByCategory(request.getParameter(CATEGORIA));
            if(!produtos.isEmpty()) return produtos;
            else parameter=CATEGORIA;
        }else if(request.getParameter(NOME)!=null){
            produtos = produtoDAO.findProdutoByName(request.getParameter(NOME));
            if(!produtos.isEmpty()) return produtos;
            else parameter=NOME;
        }else if(request.getParameter(PRECO)!=null){
            return produtoDAO.sortProdutoByPreco();
        }
        throw new NullValueException(noResultsFound(request.getParameter(parameter)));
    }

    @Override
    public void deleteProdutoById(Integer id) {produtoDAO.deleteProdutoById(id);}

    @Override
    public Produto putProduto(Produto produto) {
        return produtoDAO.putProduto(produto);
    }

    @Override
    public List<Integer> idListOrObject(String listOrObject){
        List<Integer> ids = new ArrayList<>();
        try{
            List<IdList> list = new Gson().fromJson(listOrObject, new TypeToken<List<IdList>>(){}.getType());
            for(IdList idList : list){
                if(idChecker(idList.getId())) ids.add(idList.getId());
            }
        }catch (JsonSyntaxException e){
            IdList idList = new Gson().fromJson(listOrObject, IdList.class);
            if(idChecker(idList.getId())) ids.add(idList.getId());
        }catch (NullPointerException e){throw new InvalidValueException(invalidPayloadDetailed(PRODUTO,ID));}
        return ids;
    }

    @Override
    public boolean idChecker(Integer id){
        try {
            Produto produto = produtoDAO.getProdutoById(id);
            if (produto.getId() == null) throw new IdNotFoundException(idNotFound(String.valueOf(id), PRODUTO, ""));
            return true;
        }catch (NullPointerException e){throw new InvalidValueException(invalidPayloadDetailed(PRODUTO,ID));}
    }

    @Override
    public List<Produto> produtoListOrObject(String listOrObject) {
        List<Produto> produtos = new ArrayList<>();
        try{
            List<Produto> list = new Gson().fromJson(listOrObject, new TypeToken<List<Produto>>(){}.getType());
            produtos = list;
        }catch (JsonSyntaxException e){
            Produto produto = new Gson().fromJson(listOrObject, Produto.class);
            produtos.add(produto);
        }catch (Exception e){throw new InvalidValueException(invalidPayload(PRODUTO));}

        return objectChecker(produtos);
    }

    @Override
    public List<Produto> objectChecker(List<Produto> produtos) {
        for(Produto produto : produtos){
            if(produto.getNome()==null || produto.getNome().isEmpty()) throw new InvalidValueException(invalidPayloadDetailed(PRODUTO,NOME));
            if(produto.getCategoria()==null || produto.getCategoria().isEmpty()) throw new InvalidValueException(invalidPayloadDetailed(PRODUTO,CATEGORIA));
            if(produto.getPreco()==null) throw new InvalidValueException(invalidPayloadDetailed(PRODUTO,PRECO));
        }
        return produtos;
    }

    @Override
    public List<Produto> updateChecker(List<Produto> produtos) {
        for(Produto produto : produtos){
            idChecker(produto.getId());
        }
        return produtos;
    }
}
