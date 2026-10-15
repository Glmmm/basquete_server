package basquete.assis.features.auth.services.forms;

import basquete.assis.shared.entities.Usuario;

public interface RegistroFormHandler<T> {
    boolean supports(Object form);
    void handle(Usuario usuario, T form);
}


