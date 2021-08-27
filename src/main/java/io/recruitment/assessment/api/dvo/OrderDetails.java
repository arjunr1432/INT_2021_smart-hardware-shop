package io.recruitment.assessment.api.dvo;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetails {

    @Id
    private String id;

    @Column
    private Timestamp createDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "DEFAULT_ORDER_ID", referencedColumnName = "id", nullable = false)
    private List<ProductItem> productItems = new ArrayList<>();

}
