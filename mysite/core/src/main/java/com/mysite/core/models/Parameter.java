package com.mysite.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Model(adaptables = {Resource.class})
public class Parameter {
    private String value;
    private List<Integer> content;
    private String name;
    private String encoding;
}
