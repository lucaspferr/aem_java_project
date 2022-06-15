package com.mysite.core.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mysite.core.config.exceptions.IdNotFoundException;
import com.mysite.core.config.exceptions.InvalidValueException;
import com.mysite.core.dao.ClienteDAO;
import com.mysite.core.models.Cliente;
import com.mysite.core.models.IdList;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

import static com.mysite.core.utils.BuildResponse.*;

@Component(immediate = true, service = ClienteService.class)
public class ClienteServiceImpl implements ClienteService {

    @Reference
    private DatabaseService databaseService;
    @Reference
    private ClienteDAO clienteDAO;

    static final String CLIENTE = "Cliente";
    static final String NOME = "nome";
    static final String ID = "id";

    @Override
    public List<Cliente> getClientes() {
        return clienteDAO.getClientes();
    }

    @Override
    public Cliente getClienteById(int id) {
        return clienteDAO.getClienteById(id);
    }

    @Override
    public Cliente postCliente(Cliente cliente) {
        return clienteDAO.postCliente(cliente);
    }

    @Override
    public Cliente putCliente(Cliente cliente) {return clienteDAO.putCliente(cliente);}

    @Override
    public void deleteCliente(int id) {
        clienteDAO.deleteCliente(id);
    }

    @Override
    public boolean idChecker(Integer id){
        try {
            Cliente cliente = clienteDAO.getClienteById(id);
            if (cliente.getId() == null) throw new IdNotFoundException(idNotFound(String.valueOf(id), CLIENTE, ""));
            return true;
        }catch (NullPointerException e){throw new InvalidValueException(invalidPayloadDetailed(CLIENTE,ID));}
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
        }catch (NullPointerException e){throw new InvalidValueException(invalidPayloadDetailed(CLIENTE,ID));}
        return ids;
    }

    @Override
    public List<Cliente> clienteListOrObject(String listOrObject) {
        List<Cliente> clientes = new ArrayList<>();
        try{
            List<Cliente> list = new Gson().fromJson(listOrObject, new TypeToken<List<Cliente>>(){}.getType());
            clientes = list;
        }catch (JsonSyntaxException e){
            Cliente cliente = new Gson().fromJson(listOrObject, Cliente.class);
            clientes.add(cliente);}
        catch (Exception e){throw new InvalidValueException(invalidPayload(CLIENTE));}

        return objectChecker(clientes);
    }

    @Override
    public List<Cliente> objectChecker(List<Cliente> clientes) {
        for(Cliente cliente : clientes){
            if(cliente.getNome() == null || cliente.getNome().isEmpty()) throw new InvalidValueException(invalidPayloadDetailed(CLIENTE,NOME));
        }
        return clientes;
    }

    @Override
    public List<Cliente> updateChecker(List<Cliente> clientes) {
        for(Cliente cliente : clientes){
            idChecker(cliente.getId());
        }
        return clientes;
    }
}
