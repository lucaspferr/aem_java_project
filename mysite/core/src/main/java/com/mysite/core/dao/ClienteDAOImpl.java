package com.mysite.core.dao;

import com.mysite.core.config.exceptions.NullValueException;
import com.mysite.core.models.Cliente;
import com.mysite.core.service.DatabaseService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;

@Component(immediate = true, service = ClienteDAO.class)
public class ClienteDAOImpl implements ClienteDAO{

    @Reference
    DatabaseService databaseService;

    @Override
    public List<Cliente> getClientes() {
        String sql = "SELECT * FROM cliente";
        try(Connection connection = databaseService.getConnection();
            PreparedStatement pstm = connection.prepareStatement(sql)){
            pstm.execute();
            try(ResultSet rst = pstm.getResultSet()){
                List<Cliente> clientes = new ArrayList<>();
                while(rst.next()){
                    Cliente cliente = new Cliente();
                    cliente.setId(rst.getInt(1));
                    cliente.setNome(rst.getString(2));
                    clientes.add(cliente);}
                return clientes;}
        }catch (SQLException e){throw new IllegalStateException(e.getMessage());}}


    @Override
    public Cliente getClienteById(int id){
        String sql = "SELECT * FROM cliente WHERE id = ?";
        try(Connection connection = databaseService.getConnection();
            PreparedStatement pstm = connection.prepareStatement(sql)){

            pstm.setInt(1, id);
            pstm.execute();

            try(ResultSet rst = pstm.getResultSet()){
                Cliente cliente = new Cliente();

                while(rst.next()){
                    cliente.setId(rst.getInt(1));
                    cliente.setNome(rst.getString(2));
                }return cliente;}
        }catch (SQLException e){throw new IllegalStateException(e.getMessage());}
    }

    @Override
    public Cliente postCliente(Cliente cliente) {
        try(Connection connection = databaseService.getConnection()){
            String sql = "INSERT INTO cliente (nome) VALUES (?)";

            try(PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                pstm.setString(1, cliente.getNome());
                pstm.execute();

                try(ResultSet rst = pstm.getGeneratedKeys()){
                    while(rst.next()){
                        cliente.setId(rst.getInt(1));
                    }
                    return cliente;
                }
            }

        }catch (SQLException e){throw new NullValueException(e.getMessage());}
    }

    @Override
    public Cliente putCliente(Cliente cliente) {
        String sql = "UPDATE cliente C SET C.nome = ? WHERE C.id = ?";
        try(Connection connection = databaseService.getConnection()){
            try(PreparedStatement pstm = connection.prepareStatement(sql)){
                pstm.setString(1, cliente.getNome());
                pstm.setInt(2, cliente.getId());
                pstm.execute();

                return cliente;
            }

        }catch (SQLException e){throw new IllegalStateException(e.getMessage());}
    }

    @Override
    public void deleteCliente(int id) {
        String sql = "DELETE FROM cliente WHERE id = ?";
        try(Connection connection = databaseService.getConnection()){
            try(PreparedStatement pstm = connection.prepareStatement(sql)){
                pstm.setInt(1, id);
                pstm.execute();

            }}catch (SQLException e){throw new IllegalStateException(e.getMessage());}
    }
}
