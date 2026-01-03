package org.example.rootmanage.option;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.example.rootmanage.common.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "option_item")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OptionItem extends BaseEntity {

    @Column(nullable = false, name = "group_code")
    @JsonProperty("group")
    private String groupCode;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false, name = "order_no")
    @JsonProperty("order")
    private Integer orderNo;
}

