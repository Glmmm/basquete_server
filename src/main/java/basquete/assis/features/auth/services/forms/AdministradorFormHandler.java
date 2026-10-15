package basquete.assis.features.auth.services.forms;

import basquete.assis.features.auth.models.RegistroAdministradorForm;
import basquete.assis.shared.entities.Administrador;
import basquete.assis.shared.entities.Usuario;
import basquete.assis.shared.repositories.AdministradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdministradorFormHandler implements RegistroFormHandler<RegistroAdministradorForm> {
    private final AdministradorRepository administradorRepository;

    @Override
    public boolean supports(Object form) {
        return form instanceof RegistroAdministradorForm;
    }

    @Override
    public void handle(Usuario usuario, RegistroAdministradorForm form) {
        Administrador empresa = new Administrador(usuario, form);
        administradorRepository.save(empresa);
    }
}
