package basquete.assis.shared.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipos_agendamento")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TipoAgendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome; // Ex: Treino Técnico, Treino Físico, Jogo, Reunião, Evento
}