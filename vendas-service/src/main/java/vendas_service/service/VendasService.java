package vendas_service.service;

import vendas_service.client.LivroClient;
import vendas_service.dto.AtualizarEstoqueRequest;
import vendas_service.dto.ItemRequest;
import vendas_service.dto.LivroDTO;
import vendas_service.dto.VendaRequest;
import vendas_service.enums.EnumPagamentoVenda;
import vendas_service.enums.EnumStatusVenda;
import vendas_service.model.ItemVenda;
import vendas_service.model.VendasModel;
import vendas_service.repository.VendasRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VendasService {


    private final VendasRepository vendasRepository;
    private final LivroClient livroClient;

    public VendasService(
                         VendasRepository vendasRepository,
                         LivroClient livroClient) {
        this.vendasRepository = vendasRepository;
        this.livroClient = livroClient;
    }




    public VendasModel buscarPorId(Long id) {
        VendasModel vendas = vendasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrado"));

        return vendas;
    }

    public List<VendasModel> buscarPorPagamento (EnumPagamentoVenda pagamento){
        return vendasRepository.findByFormaPagamento(pagamento);
    }

    public List<VendasModel> buscarPorStatus(EnumStatusVenda status){
        return vendasRepository.findByStatus(status);
    }


    @Transactional
    public void cancelarVenda(Long id){
        VendasModel venda = vendasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrado"));

        if (venda.getStatus() == EnumStatusVenda.CANCELADA){
            throw new RuntimeException("ERRO venda já cancelada");
        }

        for (ItemVenda item : venda.getItens()) {

            LivroDTO livro = livroClient.buscarPorId(item.getLivroId());

            livroClient.atualizarEstoque(
                    livro.getId(),
                    new AtualizarEstoqueRequest(
                            livro.getQuantidade() + item.getQuantidade()
                    )
            );
        }

        log.info("Venda " + id + " cancelada com sucesso");
        venda.setStatus(EnumStatusVenda.CANCELADA);
    }





    @Transactional
    public VendasModel vender(VendaRequest request) {

        log.info("Iniciando venda - formaPagamento: {}, quantidadeItens: {}",
                request.getFormaPagamento(),
                request.getItens().size());




        VendasModel venda = new VendasModel();
        venda.setFormaPagamento(request.getFormaPagamento());

        List<ItemVenda> itensVenda = new ArrayList<>();
        BigDecimal totalVenda = BigDecimal.ZERO;

        // Processando os itens
        for (ItemRequest itemReq : request.getItens()) {

            LivroDTO livro = livroClient.buscarPorId(itemReq.getLivroId());

            if (livro == null) {
                log.error("Livro não encontrado - ID: {}", itemReq.getLivroId());
                throw new RuntimeException("Livro ID" + itemReq.getLivroId() + " não encontrado");
            }

            if (livro.getQuantidade() < itemReq.getQuantidade()) {
                log.warn("Estoque insuficiente - livro: {}, solicitado: {}, disponível: {}",
                        livro.getTitulo(),
                        itemReq.getQuantidade(),
                        livro.getQuantidade());

                throw new RuntimeException("Estoque insuficiente para o livro: " + livro.getTitulo());
            }

            //Atualiza o estoque
            Integer novaQuantidade =
                    livro.getQuantidade() - itemReq.getQuantidade();

            livroClient.atualizarEstoque(
                    livro.getId(),
                    new AtualizarEstoqueRequest(novaQuantidade)
            );

            //Criar item de venda
            ItemVenda item = new ItemVenda();
            item.setLivroId(livro.getId());
            item.setVenda(venda);
            item.setQuantidade(itemReq.getQuantidade());
            item.setPrecoUnitario(livro.getPreco());

            BigDecimal subtotal = livro.getPreco().multiply(BigDecimal.valueOf(itemReq.getQuantidade()));
            totalVenda = totalVenda.add(subtotal);

            itensVenda.add(item);
        }

        int parcelas = (request.getNumeroParcelas() == null || request.getNumeroParcelas() <= 0)
                ? 1
                : request.getNumeroParcelas();

        if (parcelas > 1 && request.getFormaPagamento() != EnumPagamentoVenda.CREDITO) {
            log.warn("Tentativa inválida de parcelamento - formaPagamento: {}, parcelas: {}",
                    request.getFormaPagamento(),
                    parcelas);

            throw new RuntimeException("Parcelamento só permitido no crédito");
        }

        BigDecimal valorParcela = totalVenda.divide(
                BigDecimal.valueOf(parcelas),
                2,
                RoundingMode.HALF_UP
        );

        venda.setNumeroParcelas(parcelas);
        venda.setValorParcelas(valorParcela);
        venda.setItens(itensVenda);
        venda.setValorTotal(totalVenda);
        venda.setStatus(EnumStatusVenda.REALIZADA);


        log.info("Venda realizada com sucesso - total: {}, parcelas: {}, valorParcela: {}",
                totalVenda,
                parcelas,
                valorParcela);

        // Salva a venda
        return vendasRepository.save(venda);
    }
}