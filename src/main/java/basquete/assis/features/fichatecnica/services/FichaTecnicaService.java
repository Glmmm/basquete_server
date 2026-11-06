package basquete.assis.features.fichatecnica.services;

import basquete.assis.features.fichatecnica.models.FichaTecnicaFormDTO;
import basquete.assis.shared.entities.Atleta;
import basquete.assis.shared.entities.FichaTecnica;
import basquete.assis.shared.repositories.AtletaRepository;
import basquete.assis.shared.repositories.FichaTecnicaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FichaTecnicaService {

    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final AtletaRepository atletaRepository;

    public FichaTecnicaService(FichaTecnicaRepository fichaTecnicaRepository, AtletaRepository atletaRepository) {
        this.fichaTecnicaRepository = fichaTecnicaRepository;
        this.atletaRepository = atletaRepository;
    }

    @Transactional
    public FichaTecnica salvarOuAtualizar(FichaTecnicaFormDTO dto) {
        Atleta atleta = atletaRepository.findById(dto.getAtletaId())
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        FichaTecnica ficha = fichaTecnicaRepository.findById(atleta.getId()).orElseGet(() -> {
            FichaTecnica n = new FichaTecnica();
            n.setAtleta(atleta);
            return n;
        });
        ficha.setAtleta(atleta);

        ficha.setAltura(dto.getAltura());
        ficha.setPeso(dto.getPeso());
        ficha.setExperienciaAnos(dto.getExperienciaAnos());
        ficha.setHistoricoDoencas(dto.getHistoricoDoencas());
        ficha.setMedicamentosUsoContinuo(dto.getMedicamentosUsoContinuo());
        ficha.setDataAtualizacao(LocalDateTime.now());

        FichaTecnica salva = fichaTecnicaRepository.save(ficha);
        return salva;
    }

    public FichaTecnica buscarPorAtleta(Long atletaId) {
        if (!atletaRepository.existsById(atletaId)) {
            throw new RuntimeException("Atleta não encontrado");
        }
        return fichaTecnicaRepository.findById(atletaId).orElse(null);
    }
}