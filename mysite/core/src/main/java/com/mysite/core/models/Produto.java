package com.mysite.core.models;

import com.google.gson.annotations.Expose;
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
public class Produto{

    @Expose
    private Integer id;
    @Expose
    private String nome;
    @Expose
    private String categoria;
    @Expose
    private Double preco;


}
