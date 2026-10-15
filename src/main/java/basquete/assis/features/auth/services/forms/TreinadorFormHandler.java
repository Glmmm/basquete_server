package basquete.assis.features.auth.services.forms;

import basquete.assis.features.auth.models.RegistroTreinadorForm;
import basquete.assis.shared.entities.Treinador;
import basquete.assis.shared.entities.Usuario;
import basquete.assis.shared.repositories.TreinadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TreinadorFormHandler implements RegistroFormHandler<RegistroTreinadorForm> {
    private final TreinadorRepository treinadorRepository;

    @Override
    public boolean supports(Object form) {
        return form instanceof RegistroTreinadorForm;
    }

    @Override
    public void handle(Usuario usuario, RegistroTreinadorForm form) {
        Treinador treinador = new Treinador(usuario, form);
        treinadorRepository.save(treinador);
    }
}
