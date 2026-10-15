package basquete.assis.features.auth.models;

import basquete.assis.shared.enums.Role;

public record RegistroForm(String email, String password, Role role) {
}
