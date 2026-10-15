package basquete.assis.features.auth.services.forms;

import basquete.assis.features.auth.models.RegistroAtletaForm;
import basquete.assis.shared.entities.Atleta;
import basquete.assis.shared.entities.Usuario;
import basquete.assis.shared.repositories.AtletaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AtletaFormHandler implements RegistroFormHandler<RegistroAtletaForm> {
    private final AtletaRepository atletaRepository;

    @Override
    public boolean supports(Object form) {
        return form instanceof RegistroAtletaForm;
    }

    @Override
    public void handle(Usuario usuario, RegistroAtletaForm form) {
        Atleta  empresa = new Atleta(usuario, form);
        atletaRepository.save(empresa);
    }
}
