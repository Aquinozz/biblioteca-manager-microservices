package vendas_service.controller;



import vendas_service.dto.VendaRequest;
import vendas_service.enums.EnumPagamentoVenda;
import vendas_service.enums.EnumStatusVenda;
import vendas_service.model.VendasModel;
import vendas_service.repository.VendasRepository;
import vendas_service.service.VendasService;
import vendas_service.utils.DataUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Vendas", description = "Operações relacionadas às vendas de livros")
@RestController
@RequestMapping("/vendas")
public class VendasController {


    @Autowired
    private VendasRepository vendasRepository;

    private final VendasService vendasService;

    public VendasController(VendasService vendasService) {
        this.vendasService = vendasService;
    }

    @Operation(summary = "Lista todas as vendas realizadas")
    @GetMapping
    public ResponseEntity <?>  listarVendas(
            @Parameter(
                    description = "Data inicial (dd/MM/yyyy)",
                    example = "01/01/2026",
                    schema = @Schema(type = "string")
            )
            @RequestParam (required = false) @DateTimeFormat(pattern = DataUtils.DATA_PATTERN)LocalDate inicio,

            @Parameter(
                    description = "Data final (dd/MM/yyyy)",
                    example = "02/02/2026",
                    schema = @Schema(type = "string")
            )
            @RequestParam (required = false) @DateTimeFormat (pattern = DataUtils.DATA_PATTERN) LocalDate fim,

            @Parameter(description = "ID da venda")
            @RequestParam(required = false) Long id,

            @Parameter (description = "Achar por status de venda")
            @RequestParam (required = false) EnumStatusVenda status,

            @Parameter (description = "Tipo de pagamento")
            @RequestParam (required = false)EnumPagamentoVenda pagamento

            ){



        if (status != null){
            List<VendasModel> venda = vendasService.buscarPorStatus(status);
            return ResponseEntity.ok(venda);
        }

        if (id != null){
            VendasModel venda = vendasService.buscarPorId(id);
            return ResponseEntity.ok(venda);
        }

        if (inicio != null && fim != null){
            List<VendasModel> vendas = vendasRepository.findByDataVendaBetween(inicio, fim);
            return ResponseEntity.ok (vendas);
        }
        if (pagamento !=null){
            List<VendasModel> venda = vendasService.buscarPorPagamento(pagamento);
            return ResponseEntity.ok (venda);
        }



        return ResponseEntity.ok(vendasRepository.findAll());
    }


    @Operation(summary = "Cancela venda pelo id")
    @DeleteMapping ("/{id}")
    public void deletarVenda (@PathVariable Long id){
        vendasService.cancelarVenda(id);
    }


    @Operation(summary = "Realiza uma nova venda de livros")
    @PostMapping
    public VendasModel vender(@RequestBody VendaRequest request) {
        return vendasService.vender(request);
    }
}