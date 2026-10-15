package basquete.assis.features.fichatecnica.services;

import basquete.assis.features.fichatecnica.models.FichaTecnicaFormDTO;
import basquete.assis.shared.entities.Atleta;
import basquete.assis.shared.entities.FichaTecnica;
import basquete.assis.shared.repositories.AtletaRepository;
import basquete.assis.shared.repositories.FichaTecnicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FichaTecnicaService {

    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final AtletaRepository atletaRepository;

    @Transactional
    public FichaTecnica salvarOuAtualizar(FichaTecnicaFormDTO dto) {
        Atleta atleta = atletaRepository.findById(dto.getAtletaId())
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        FichaTecnica ficha = atleta.getFichaTecnica();
        if (ficha == null) {
            ficha = new FichaTecnica();
        }

        ficha.setAltura(dto.getAltura());
        ficha.setPeso(dto.getPeso());
        ficha.setExperienciaAnos(dto.getExperienciaAnos());
        ficha.setHistoricoDoencas(dto.getHistoricoDoencas());
        ficha.setMedicamentosUsoContinuo(dto.getMedicamentosUsoContinuo());
        ficha.setDataAtualizacao(LocalDateTime.now());

        FichaTecnica salva = fichaTecnicaRepository.save(ficha);

        atleta.setFichaTecnica(salva);
        atletaRepository.save(atleta);

        return salva;
    }

    public FichaTecnica buscarPorAtleta(Long atletaId) {
        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));
        return atleta.getFichaTecnica();
    }
}