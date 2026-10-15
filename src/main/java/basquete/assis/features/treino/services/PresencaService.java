package basquete.assis.features.treino.services;

import basquete.assis.shared.entities.Agendamento;
import basquete.assis.shared.entities.Atleta;
import basquete.assis.shared.entities.Presenca;
import basquete.assis.shared.enums.StatusPresenca;
import basquete.assis.shared.repositories.AgendamentoRepository;
import basquete.assis.shared.repositories.AtletaRepository;
import basquete.assis.shared.repositories.PresencaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PresencaService {

    private final PresencaRepository presencaRepository;
    private final AtletaRepository atletaRepository;
    private final AgendamentoRepository agendamentoRepository;

    @Transactional
    public Presenca confirmarPresenca(Long atletaId, Long agendamentoId, StatusPresenca status) {
        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado"));

        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        Presenca presenca = presencaRepository.findAll().stream()
                .filter(p -> p.getAtleta().getId().equals(atletaId) && p.getAgendamento().getId().equals(agendamentoId))
                .findFirst()
                .orElse(new Presenca());

        presenca.setAtleta(atleta);
        presenca.setAgendamento(agendamento);
        presenca.setStatus(status);
        presenca.setDataConfirmacao(LocalDateTime.now());

        return presencaRepository.save(presenca);
    }
}