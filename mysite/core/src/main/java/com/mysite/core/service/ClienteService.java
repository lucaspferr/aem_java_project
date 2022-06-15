package com.mysite.core.service;

import com.mysite.core.models.Cliente;

import java.util.List;

public interface ClienteService {

        List<Cliente> getClientes();

        Cliente getClienteById(int id);

        Cliente postCliente(Cliente cliente);

        Cliente putCliente(Cliente cliente);

        void deleteCliente(int id);

        boolean idChecker(Integer id);

        List<Integer> idListOrObject(String listOrObject);

        List<Cliente> clienteListOrObject(String listOrObject);

        List<Cliente> objectChecker(List<Cliente> clientes);

        List<Cliente> updateChecker(List<Cliente> clientes);

}
