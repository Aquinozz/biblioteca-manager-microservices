package vendas_service.repository;

import vendas_service.enums.EnumPagamentoVenda;
import vendas_service.enums.EnumStatusVenda;
import vendas_service.model.VendasModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface VendasRepository extends JpaRepository<VendasModel, Long> {

    @Query("SELECT COALESCE(SUM(v.valorTotal), 0) FROM VendasModel v")
    Double somarTotalVendas();

    @Query("SELECT v FROM VendasModel v WHERE CAST(v.dataVenda AS date) BETWEEN :inicio AND :fim")
    List<VendasModel> findByDataVendaBetween(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    List<VendasModel> findByStatus (EnumStatusVenda status);

    List<VendasModel> findByFormaPagamento (EnumPagamentoVenda formaPagamento);

    @Query("SELECT MAX(v.dataVenda) FROM VendasModel v")
    LocalDateTime buscarUltimaVenda();

    @Query("SELECT SUM(iv.quantidade) FROM ItemVenda iv")
    Long somarQuantidadePorVenda();
}