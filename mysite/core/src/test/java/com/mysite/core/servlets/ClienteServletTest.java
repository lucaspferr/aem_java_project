package com.mysite.core.servlets;

import com.google.gson.Gson;
import com.mysite.core.config.exceptions.IdNotFoundException;
import com.mysite.core.config.exceptions.InvalidValueException;
import com.mysite.core.models.Cliente;
import com.mysite.core.service.ClienteService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mysite.core.utils.BuildResponse.*;
import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
public class ClienteServletTest {

    @Mock
    private ClienteService clienteService;

    private ClienteServlet clienteServlet;
    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;

    static final String CONTENT = "application/json;charset=UTF-8";
    static final String PATH = "/bin/cliente";
    static final String ID = "id";
    static final String CLIENTE = "Cliente";
    static final String POST = "POST";
    static final String PUT = "PUT";
    static final String DELETE = "DELETE";

    //JSONs
    static final String SING_CLIENTE = "{\"nome\":\"Lucas\"}";
    static final String MULT_CLIENTES = "[{\"nome\":\"Lucas\"},{\"nome\":\"Joao\"},{\"nome\":\"Maria\"}]";
    static final String BAD_PAYLOAD = "{\"name\":\"Bad\"}";
    static final String SING_ID = "{\"id\":1}";
    static final String MULT_IDS = "[{\"id\":1},{\"id\":2},{\"id\":3}]";

    private Cliente cliente;
    private List<Cliente> clientes;
    private List<Cliente> customClienteList;
    private List<Integer> ids;

    @BeforeEach
    void setUp(AemContext context) {
        MockitoAnnotations.openMocks(this);

        request = context.request();
        response = context.response();

        createCliente();
        clienteServlet = new ClienteServlet(clienteService);
    }

    //----------------------- GET -----------------------------

    @Test
    void returnAllClientes() {
        request.setPathInfo(PATH);
        when(clienteService.getClientes()).thenReturn(clientes);
        try {
            clienteServlet.doGet(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();}

        assertEquals(SC_OK, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
        assertEquals(new Gson().toJson(clientes), response.getOutputAsString());
    }

    @Test
    void returnClienteById() {
        request.setPathInfo(PATH);
        request.setQueryString("id=1");
        when(clienteService.getClienteById(1)).thenReturn(cliente);
        when(clienteService.idChecker(1)).thenReturn(true);

        try {
            clienteServlet.doGet(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();}

        assertEquals(SC_OK, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
        assertEquals(new Gson().toJson(cliente), response.getOutputAsString());
    }

    @Test
    void returnClienteInvalidIdFormat(){
        request.setPathInfo(PATH);
        request.setQueryString("id=a");
        try {
            clienteServlet.doGet(request, response);
        } catch (Exception e) {e.printStackTrace();}

        assertEquals(invalidInput(request.getParameter(ID)), response.getOutputAsString());
        assertEquals(SC_BAD_REQUEST, response.getStatus());
        assertEquals(CONTENT, response.getContentType());

    }

    @Test
    void returnClienteIdNotFound(){
        request.setPathInfo(PATH);
        request.setQueryString("id=1");
        when(clienteService.idChecker(1)).thenThrow(new IdNotFoundException(idNotFound(request.getParameter(ID),CLIENTE,"")));
        try {
            clienteServlet.doGet(request, response);
        } catch (Exception e) {e.printStackTrace();}

        assertEquals(idNotFound(request.getParameter(ID),CLIENTE,""), response.getOutputAsString());
        assertEquals(SC_NOT_FOUND, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
    }

    //----------------------- POST -----------------------------

    @Test
    void postSingleClienteOK(){
        request.setPathInfo(PATH);
        request.setContent(SING_CLIENTE.getBytes());
        request.setContentType(CONTENT);
        request.setMethod(POST);
        when(clienteService.clienteListOrObject(SING_CLIENTE)).thenReturn(customClienteList);
        when(clienteService.postCliente(cliente)).thenReturn(cliente);
        try {
            clienteServlet.doPost(request, response);
        } catch (ServletException | IOException e) {e.printStackTrace();}
        assertEquals(SC_CREATED, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
        assertEquals(new Gson().toJson(cliente), response.getOutputAsString());
    }

    @Test
    void postMultipleClienteOK(){
        request.setPathInfo(PATH);
        request.setContent(MULT_CLIENTES.getBytes());
        request.setContentType(CONTENT);
        request.setMethod(POST);
        when(clienteService.clienteListOrObject(MULT_CLIENTES)).thenReturn(clientes);

        for(Cliente c: clientes){when(clienteService.postCliente(c)).thenReturn(c);}

        try {
            clienteServlet.doPost(request, response);
        } catch (ServletException | IOException e) {e.printStackTrace();}
        assertEquals(SC_CREATED, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
        assertEquals(new Gson().toJson(clientes), response.getOutputAsString());
    }

    @Test
    void postClienteEmptyBody(){
        request.setPathInfo(PATH);
        request.setContentType(CONTENT);
        request.setMethod(POST);

        try {
            clienteServlet.doPost(request, response);
        } catch (ServletException | IOException e) {e.printStackTrace();}
        assertEquals(SC_BAD_REQUEST, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
        assertEquals(invalidPayload(CLIENTE), response.getOutputAsString());
    }

    @Test
    void postClienteBadPayload(){
        request.setPathInfo(PATH);
        request.setContent(BAD_PAYLOAD.getBytes());
        request.setContentType(CONTENT);
        request.setMethod(POST);
        when(clienteService.clienteListOrObject(BAD_PAYLOAD)).thenThrow(new InvalidValueException(invalidPayload(CLIENTE)));

        try {
            clienteServlet.doPost(request, response);
        } catch (ServletException | IOException e) {e.printStackTrace();}
        assertEquals(SC_BAD_REQUEST, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
        assertEquals(invalidPayload(CLIENTE), response.getOutputAsString());
    }

    //----------------------- PUT -----------------------------

    @Test
    void putSingleClienteOK(){
        request.setPathInfo(PATH);
        request.setContent(new Gson().toJson(cliente).getBytes());
        request.setContentType(CONTENT);
        request.setMethod(PUT);
        when(clienteService.updateChecker(customClienteList)).thenReturn(customClienteList);
        when(clienteService.clienteListOrObject(new Gson().toJson(cliente))).thenReturn(customClienteList);
        when(clienteService.putCliente(cliente)).thenReturn(cliente);
        try {
            clienteServlet.doPut(request, response);
        } catch (ServletException | IOException e) {e.printStackTrace();}
        assertEquals(SC_CREATED, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
        assertEquals(new Gson().toJson(cliente), response.getOutputAsString());
    }

    @Test
    void putMultipleClienteOK(){
        request.setPathInfo(PATH);
        request.setContent(new Gson().toJson(clientes).getBytes());
        request.setContentType(CONTENT);
        request.setMethod(PUT);

        when(clienteService.updateChecker(clientes)).thenReturn(clientes);
        when(clienteService.clienteListOrObject(new Gson().toJson(clientes))).thenReturn(clientes);

        for(Cliente c: clientes){when(clienteService.putCliente(c)).thenReturn(c);}

        try {
            clienteServlet.doPut(request, response);
        } catch (ServletException | IOException e) {e.printStackTrace();}
        assertEquals(SC_CREATED, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
        assertEquals(new Gson().toJson(clientes), response.getOutputAsString());
    }

    @Test
    void putClienteEmptyBody(){
        request.setPathInfo(PATH);
        request.setContentType(CONTENT);
        request.setMethod(PUT);

        try {
            clienteServlet.doPut(request, response);
        } catch (ServletException | IOException e) {e.printStackTrace();}
        assertEquals(SC_BAD_REQUEST, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
        assertEquals(invalidPayload(CLIENTE), response.getOutputAsString());
    }

    @Test
    void putClienteBadPayload(){
        request.setPathInfo(PATH);
        request.setContent(BAD_PAYLOAD.getBytes());
        request.setContentType(CONTENT);
        request.setMethod(PUT);
        when(clienteService.clienteListOrObject(BAD_PAYLOAD)).thenThrow(new InvalidValueException(invalidPayload(CLIENTE)));

        try {
            clienteServlet.doPut(request, response);
        } catch (ServletException | IOException e) {e.printStackTrace();}
        assertEquals(SC_BAD_REQUEST, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
        assertEquals(invalidPayload(CLIENTE), response.getOutputAsString());
    }

    @Test
    void putClientIdNotExist(){
        request.setPathInfo(PATH);
        request.setContent(new Gson().toJson(cliente).getBytes());
        request.setContentType(CONTENT);
        request.setMethod(PUT);
        when(clienteService.updateChecker(customClienteList)).thenReturn(customClienteList);
        when(clienteService.clienteListOrObject(new Gson().toJson(cliente))).thenReturn(customClienteList);
        when(clienteService.putCliente(cliente)).thenReturn(cliente);
        try {
            clienteServlet.doPut(request, response);
        } catch (ServletException | IOException e) {e.printStackTrace();}
        assertEquals(SC_CREATED, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
        assertEquals(new Gson().toJson(cliente), response.getOutputAsString());
    }

    //----------------------- DELETE -----------------------------

    @Test
    void deleteSingleClienteOK(){
        request.setPathInfo(PATH);
        request.setContent(SING_ID.getBytes());
        request.setContentType(CONTENT);
        request.setMethod(DELETE);
        ids.add(1);
        when(clienteService.idListOrObject(SING_ID)).thenReturn(ids);
        try {
            clienteServlet.doDelete(request, response);
        } catch (ServletException | IOException e) {e.printStackTrace();}
        assertEquals(SC_NO_CONTENT, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
        assertEquals("", response.getOutputAsString());
    }

    @Test
    void deleteMultipleClienteOK(){
        request.setPathInfo(PATH);
        request.setContent(MULT_IDS.getBytes());
        request.setContentType(CONTENT);
        request.setMethod(DELETE);
        ids = Arrays.asList(1,2,3);
        when(clienteService.idListOrObject(MULT_IDS)).thenReturn(ids);
        try {
            clienteServlet.doDelete(request, response);
        } catch (ServletException | IOException e) {e.printStackTrace();}
        assertEquals(SC_NO_CONTENT, response.getStatus());
        assertEquals(CONTENT, response.getContentType());
        assertEquals("", response.getOutputAsString());
    }


    //----------------------- CREATOR -----------------------------

    void createCliente() {
        cliente = new Cliente(1,"Lucas");
        clientes = Arrays.asList(cliente, new Cliente(2,"Joao"), new Cliente(3,"Maria"));
        customClienteList = Collections.singletonList(cliente);
        ids = new ArrayList<>();
    }
}
