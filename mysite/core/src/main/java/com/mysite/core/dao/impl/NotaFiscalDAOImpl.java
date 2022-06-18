package com.mysite.core.dao.impl;


import com.mysite.core.config.exceptions.IdNotFoundException;
import com.mysite.core.config.exceptions.PostException;
import com.mysite.core.dao.NotaFiscalDAO;
import com.mysite.core.models.NotaFiscal;
import com.mysite.core.models.NotaFiscalDto;
import com.mysite.core.service.DatabaseService;
import com.mysite.core.utils.BuildResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;



@Component(immediate = true, service = NotaFiscalDAO.class)
public class NotaFiscalDAOImpl implements NotaFiscalDAO {

    @Reference
    DatabaseService databaseService;

    static final String NUMERO = "numero";
    static final String NOTAFISCAL = "Nota fiscal";
    static final String SQL_GENERICS = "SELECT nf.numero, cl.nome AS cliente, pr.nome AS produto, nf.valor FROM nota_fiscal AS nf " +
            "INNER JOIN produto AS pr ON pr.id = nf.idproduto " +
            "INNER JOIN cliente AS cl ON cl.id = nf.idcliente ";
    static final String SQL_NUMERO = SQL_GENERICS + "WHERE nf.numero = ?";
    static final String SQL_IDCLIENTE = SQL_GENERICS + "WHERE nf.idcliente = ?";

    @Override
    public NotaFiscal postNotaFiscal(NotaFiscal notaFiscal) {
        String sql = "SELECT preco FROM produto WHERE id = ?";
        try(Connection connection = databaseService.getConnection();
            PreparedStatement pstm = connection.prepareStatement(sql)){
            pstm.setInt(1, notaFiscal.getIdproduto());
            pstm.execute();
            try(ResultSet rst = pstm.getResultSet()){
                while (rst.next()){
                    notaFiscal.setValor(rst.getDouble(1));}}
            sql = "INSERT INTO nota_fiscal (numero, idcliente, idproduto, valor) VALUES (?,?,?,?)";
            try (PreparedStatement pstm2 = connection.prepareStatement(sql)) {
                pstm2.setInt(1, notaFiscal.getNumero());
                pstm2.setInt(2, notaFiscal.getIdcliente());
                pstm2.setInt(3, notaFiscal.getIdproduto());
                pstm2.setDouble(4, notaFiscal.getValor());
                pstm2.execute();
            }
            return notaFiscal;

        }catch (Exception e){throw new PostException(e.getMessage());}
    }

    @Override
    public List<NotaFiscalDto> getNotaFiscal(Integer id, String identifier) {
        String sql;
        if(identifier.matches(NUMERO)) sql = SQL_NUMERO;
        else sql = SQL_IDCLIENTE;
        List<NotaFiscalDto> notaFiscalDtoList = new ArrayList<>();
        List<String> produtos = new ArrayList<>();
        Double valor = 0.0;
        NotaFiscalDto notaFiscalDto = new NotaFiscalDto();

        try(Connection connection = databaseService.getConnection();
            PreparedStatement pstm = connection.prepareStatement(sql)){
            pstm.setInt(1, id);
            pstm.execute();
            try(ResultSet rst = pstm.getResultSet()){
                while(rst.next()){
                    if(notaFiscalDto.getNumero()!=null && notaFiscalDto.getNumero() != rst.getInt(1)){
                        notaFiscalDto.setValor(valor);
                        notaFiscalDto.setProdutos(produtos);
                        notaFiscalDtoList.add(notaFiscalDto);
                        produtos = new ArrayList<>();
                        valor = 0.0;
                        notaFiscalDto = new NotaFiscalDto();
                    }
                    notaFiscalDto.setNumero(rst.getInt(1));
                    notaFiscalDto.setClienteNome(rst.getString(2));
                    produtos.add(rst.getString(3));
                    valor += rst.getDouble(4);

                    if(rst.isLast()){
                        notaFiscalDto.setValor(valor);
                        notaFiscalDto.setProdutos(produtos);
                        notaFiscalDtoList.add(notaFiscalDto);
                    }
                }
            }
            if(notaFiscalDtoList.get(0).getNumero() == null) throw new IllegalStateException();
            return notaFiscalDtoList;
        }catch (Exception e){throw new IdNotFoundException(BuildResponse.idNotFound((identifier+" "+id),NOTAFISCAL,""));}
    }
}
