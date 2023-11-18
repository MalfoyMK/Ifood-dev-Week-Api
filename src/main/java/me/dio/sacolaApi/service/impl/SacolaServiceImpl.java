package me.dio.sacolaApi.service.impl;

import lombok.RequiredArgsConstructor;
import me.dio.sacolaApi.enumeration.formaPagamento;
import me.dio.sacolaApi.model.Item;
import me.dio.sacolaApi.model.Restaurante;
import me.dio.sacolaApi.model.Sacola;
import me.dio.sacolaApi.repository.ItemRepository;
import me.dio.sacolaApi.repository.ProdutoRepository;
import me.dio.sacolaApi.repository.SacolaRepository;
import me.dio.sacolaApi.resource.dto.ItemDto;
import me.dio.sacolaApi.service.SacolaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SacolaServiceImpl implements SacolaService {
    private final SacolaRepository sacolaRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemRepository itemRepository;

    @Override
    public Item incluirItemNaSacola(ItemDto itemDto) {
        Sacola sacola = verSacola(itemDto.getSacolaId());
        if (sacola.isFechada()) {
            throw new RuntimeException("Está sacola está fechada.");
        }

        Item itemParaSerInserido = Item.builder()
                .quantidade(itemDto.getQuantidade())
                .sacola(sacola)
                .produto(produtoRepository.findById(itemDto.getProdutoId()).orElseThrow(
                        () -> new RuntimeException("Este produto não existe!")))

                .build();

        List<Item> itensDaSacola = sacola.getItens();
        if (itensDaSacola.isEmpty()) {
            itensDaSacola.add(itemParaSerInserido);
        } else {
            Restaurante restauranteAtual = itensDaSacola.get(0).getProduto().getRestaurante();
            Restaurante restauranteParaAdicionar = itemParaSerInserido.getProduto().getRestaurante();
            if (restauranteAtual.equals(restauranteParaAdicionar)) {
                itensDaSacola.add(itemParaSerInserido);
            } else {
                throw new RuntimeException("Não é possível adicionar um item de outro restaurante.");
            }
        }

        List<Double> valorDosItens = new ArrayList<>();
        for (Item itemDaSacola: itensDaSacola) {
            double valorTotalItem =
                    itemDaSacola.getProduto().getValorUnitario() * itemDaSacola.getQuantidade();
            valorDosItens.add(valorTotalItem);
        }

        double valorTotalSacola = valorDosItens.stream()
                .mapToDouble(valorTotalDeCadaItem -> valorTotalDeCadaItem)
                .sum();


        sacola.setValorTotal(valorTotalSacola);
        sacolaRepository.save(sacola);
        return itemParaSerInserido;
    }

    @Override
    public Sacola verSacola(Long id) {
        return sacolaRepository.findById(id).orElseThrow(
                () -> {
                    throw new RuntimeException("Sacola não encontrada");
                }
        );
    }

    @Override
    public Sacola fecharSacola(Long id, int numeroformaPagamento) {
        Sacola sacola = verSacola(id);
        if (sacola.getItens().isEmpty()) {
            throw new RuntimeException("Inclua ítens na sacola!");
        }
        formaPagamento formaPagamento =
                numeroformaPagamento == 0 ? me.dio.sacolaApi.enumeration.formaPagamento.DINHEIRO : me.dio.sacolaApi.enumeration.formaPagamento.MAQUINA;
        sacola.setFormaPagamento(formaPagamento);
        sacola.setFechada(true);
        return sacolaRepository.save(sacola);
    }
}
