package com.mysite.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Model(adaptables = {Resource.class})
public class NotaFiscalDto {

    private Integer numero;
    private String clienteNome;
    private List<String> produtos;
    private Double valor;

}
