package com.englishhelper.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    private String name;  //文档名字
    private String content;
    private Integer changed;  //是否被修改
}
