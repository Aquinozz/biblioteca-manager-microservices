package vendas_service.model;


import vendas_service.enums.EnumPagamentoVenda;
import vendas_service.enums.EnumStatusVenda;
import vendas_service.utils.DataUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Schema(description = "Modelo de vendas")

// getters e setters
@Getter
@Setter

@Entity
@Table(name = "vendas")
public class VendasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @JsonFormat(pattern = DataUtils.DATA_TIME_PATTERN)
    @Column(name = "data_venda")
    private LocalDateTime dataVenda;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Column (name = "numero_parcelas")
    private Integer numeroParcelas;

    @Column (name = "valor_parcelas")
    private BigDecimal valorParcelas;

    @Column(name = "forma_pagamento", nullable = false)
    @Enumerated (EnumType.STRING)
    private EnumPagamentoVenda formaPagamento;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    @Column (name = "Status da venda", nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumStatusVenda status;

    @PrePersist
    public void prePersist() {
        this.dataVenda = LocalDateTime.now();
    }


}