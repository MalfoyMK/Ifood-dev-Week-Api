package me.dio.sacolaApi.resource;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import me.dio.sacolaApi.model.Item;
import me.dio.sacolaApi.model.Sacola;
import me.dio.sacolaApi.resource.dto.ItemDto;
import me.dio.sacolaApi.service.SacolaService;
import org.springframework.web.bind.annotation.*;

@Api(value="ifood-devweek/sacolas")
@RestController
@RequestMapping("ifood-devweek/sacolas")
@RequiredArgsConstructor
public class SacolaResource {
    private final SacolaService sacolaService;

    //Serve para incluir os itens na sacola
    @PostMapping
    public Item incluirItemNaSacola(@RequestBody ItemDto itemDto){
        return sacolaService.incluirItemNaSacola(itemDto);
    }
    @GetMapping("/{id}")
    public Sacola verSacola(@PathVariable("id")Long id) {
        return sacolaService.verSacola(id);
    }

    @PatchMapping("fecharSacola/{sacolaId}")
    public Sacola fecharSacola(@PathVariable("sacolaId") long sacolaId,
                               @RequestParam(value = "formaPagamento", required = false) int formaPagamento){
        return sacolaService.fecharSacola(sacolaId, formaPagamento);
    }

}
