package com.mysite.core.service;

import com.mysite.core.config.exceptions.IdNotFoundException;
import com.mysite.core.config.exceptions.InvalidValueException;
import com.mysite.core.dao.ClienteDAO;
import com.mysite.core.models.Cliente;
import com.mysite.core.service.impl.ClienteServiceImpl;
import com.mysite.core.servlets.ClienteServlet;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mysite.core.utils.BuildResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(AemContextExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteDAO clienteDAO;

    private ClienteServiceImpl clienteService;
    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;

    private Cliente cliente;
    private List<Cliente> clientes;
    private List<Integer> ids;
    private List<Cliente> customClienteList;

    static final String CLIENTE = "Cliente";
    static final String ID = "id";
    static final String EMPTY = "";

    @BeforeEach
    void setUp(AemContext context) {
        MockitoAnnotations.openMocks(this);

        request = context.request();
        response = context.response();

        createCliente();
        clienteService = new ClienteServiceImpl(clienteDAO);
    }

    //------------------------------TESTS-------------------------------------

    @Test
    void idCheckerShouldReturnTrue() {
        when(clienteDAO.getClienteById(1)).thenReturn(cliente);
        assertEquals(true, clienteService.idChecker(cliente.getId()));
    }

    @Test
    void idCheckerShouldThrowIdNotFoundException() {
        when(clienteDAO.getClienteById(1)).thenReturn(new Cliente(null,CLIENTE));

        Exception exception = assertThrows(IdNotFoundException.class, () -> clienteService.idChecker(1));
        assertEquals(idNotFound(String.valueOf(1), CLIENTE, EMPTY), exception.getMessage());
    }

    @Test
    void idCheckerShouldThrowInvalidValueException() {
        when(clienteDAO.getClienteById(1)).thenReturn(null);

        Exception exception = assertThrows(InvalidValueException.class, () -> clienteService.idChecker(1));
        assertEquals(invalidPayloadDetailed(CLIENTE, ID), exception.getMessage());
    }


    //------------------------------BUILD CLIENTE-------------------------------------

    void createCliente() {
        cliente = new Cliente(1,"Lucas");
        clientes = Arrays.asList(cliente, new Cliente(2,"Joao"), new Cliente(3,"Maria"));
        customClienteList = Collections.singletonList(cliente);
        ids = new ArrayList<>();
    }
}
