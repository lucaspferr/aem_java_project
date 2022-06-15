package com.mysite.core.dao;

import com.mysite.core.config.exceptions.NullValueException;
import com.mysite.core.models.Produto;
import com.mysite.core.service.DatabaseService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true, service = ProdutoDAO.class)
public class ProdutoDAOImpl implements ProdutoDAO{

    @Reference
    private DatabaseService databaseService;

    @Override
    public List<Produto> getProdutos() {
        try(Connection connection = databaseService.getConnection()){
            String sql = "SELECT * FROM produto";

            try(PreparedStatement pstm = connection.prepareStatement(sql)){
                pstm.execute();

                try(ResultSet rst = pstm.getResultSet()){
                    List<Produto> produtos = new ArrayList<>();

                    while(rst.next()){
                        Produto produto = new Produto();
                        produto.setId(rst.getInt(1));
                        produto.setNome(rst.getString(2));
                        produto.setCategoria(rst.getString(3));
                        produto.setPreco(rst.getDouble(4));
                        produtos.add(produto);

                    }
                    return produtos;}}
        }catch (SQLException e){throw new IllegalStateException("SQL Exception. Error: "+e.getMessage());}}

    @Override
    public Produto postProduto(Produto produto) {
        try(Connection connection = databaseService.getConnection()){
            String sql = "INSERT INTO produto (nome, categoria, preco) VALUES (?, ?, ?)";

            try(PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

                pstm.setString(1, produto.getNome());
                pstm.setString(2, produto.getCategoria());
                pstm.setDouble(3,produto.getPreco());

                pstm.execute();

                try(ResultSet rst = pstm.getGeneratedKeys()){

                    while(rst.next()){produto.setId(rst.getInt(1));}

                    return produto;}}
        }catch (Exception e){throw new IllegalStateException(e);}}

    @Override
    public Produto getProdutoById(int id) {
        try(Connection connection = databaseService.getConnection()){
            String sql = "SELECT * FROM produto WHERE id = ?";

            try(PreparedStatement pstm = connection.prepareStatement(sql)){
                pstm.setInt(1,id);
                pstm.execute();

                try(ResultSet rst = pstm.getResultSet()){
                    Produto produto = new Produto();

                    while(rst.next()){
                        produto.setId(rst.getInt(1));
                        produto.setNome(rst.getString(2));
                        produto.setCategoria(rst.getString(3));
                        produto.setPreco(rst.getDouble(4));
                    }
                    return produto;}}
        }catch (SQLException e){throw new IllegalStateException(e);}}

    @Override
    public List<Produto> findProdutoByName(String name) {
        try(Connection connection = databaseService.getConnection()){
            String sql = "SELECT * FROM produto WHERE nome LIKE ?";

            try(PreparedStatement pstm = connection.prepareStatement(sql)){
                name = "%"+name+"%";
                pstm.setString(1,name);
                pstm.execute();

                try(ResultSet rst = pstm.getResultSet()){
                    List<Produto> produtos = new ArrayList<>();

                    while(rst.next()){
                        Produto produto = new Produto();
                        produto.setId(rst.getInt(1));
                        produto.setNome(rst.getString(2));
                        produto.setCategoria(rst.getString(3));
                        produto.setPreco(rst.getDouble(4));
                        produtos.add(produto);

                    }
                    return produtos;}}
        }catch (SQLException e){throw new IllegalStateException(e);}}

    @Override
    public List<Produto> sortProdutoByPreco() {
        try(Connection connection = databaseService.getConnection()){
            String sql = "SELECT * FROM produto ORDER BY preco";

            try(PreparedStatement pstm = connection.prepareStatement(sql)){
                pstm.execute();

                try(ResultSet rst = pstm.getResultSet()){
                    List<Produto> produtos = new ArrayList<>();

                    while(rst.next()){
                        Produto produto = new Produto();
                        produto.setId(rst.getInt(1));
                        produto.setNome(rst.getString(2));
                        produto.setCategoria(rst.getString(3));
                        produto.setPreco(rst.getDouble(4));
                        produtos.add(produto);

                    }
                    return produtos;}}
        }catch (SQLException e){throw new IllegalStateException(e);}}

    @Override
    public List<Produto> findProdutoByCategory(String category) {
        try(Connection connection = databaseService.getConnection()){
            String sql = "SELECT * FROM produto WHERE category = ?";
            List<Produto> produtos = new ArrayList<>();

            try(PreparedStatement pstm = connection.prepareStatement(sql)){
                pstm.setString(1,category);
                pstm.execute();

                try(ResultSet rst = pstm.getResultSet()){
                    Produto produto = new Produto();

                    while(rst.next()){
                        produto.setId(rst.getInt(1));
                        produto.setNome(rst.getString(2));
                        produto.setCategoria(rst.getString(3));
                        produto.setPreco(rst.getDouble(4));
                        produtos.add(produto);
                    }
                    return produtos;}}
        }catch (SQLException e){throw new IllegalStateException(e);}}

    @Override
    public void deleteProdutoById(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        try(Connection connection = databaseService.getConnection()){
            try(PreparedStatement pstm = connection.prepareStatement(sql)){
                pstm.setInt(1,id);
                pstm.execute();
                if(pstm.getUpdateCount() == 0) throw new SQLException();
            }
        }catch (SQLException e){throw new IllegalStateException();}}

    @Override
    public Produto updateProduto(Produto produto, int id) {
        String sql = "UPDATE produto P SET P.nome = ?, P.categoria= ?, P.preco = ? WHERE P.id = ?";
        try(Connection connection = databaseService.getConnection()){
            try(PreparedStatement pstm = connection.prepareStatement(sql)){
                pstm.setString(1, produto.getNome());
                pstm.setString(2, produto.getCategoria());
                pstm.setDouble(3,produto.getPreco());
                pstm.setInt(4,id);
                pstm.execute();
                if(pstm.getUpdateCount() > 0) produto.setId(id);
                return produto;
            }

        }catch (Exception e){throw new NullValueException(e.toString());}
    }
}
