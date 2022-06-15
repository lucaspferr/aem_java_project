package com.mysite.core.dao;

import com.mysite.core.models.Cliente;

import java.util.List;

public interface ClienteDAO {

        List<Cliente> getClientes();

        Cliente getClienteById(int id);

        Cliente postCliente(Cliente cliente);

        Cliente putCliente(Cliente cliente);

        void deleteCliente(int id);


}
