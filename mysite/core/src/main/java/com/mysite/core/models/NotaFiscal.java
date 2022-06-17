package com.mysite.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Model(adaptables = {Resource.class})
public class NotaFiscal {

    private Integer numero;
    private Integer idProduto;
    private Integer idCliente;
    private Double valor;

}
