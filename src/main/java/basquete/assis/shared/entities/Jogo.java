package basquete.assis.shared.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "jogo")
@PrimaryKeyJoinColumn(name = "id")
public class Jogo extends Agendamento {

    @Column(name = "adversario")
    private String adversario;

    @Column(name = "mandante")
    private Boolean mandante;

    @Column(name = "placar_time")
    private Integer placarTime;

    @Column(name = "placar_adversario")
    private Integer placarAdversario;

    public Jogo() {}

    public String getAdversario() { return adversario; }
    public void setAdversario(String adversario) { this.adversario = adversario; }
    public Boolean getMandante() { return mandante; }
    public void setMandante(Boolean mandante) { this.mandante = mandante; }
    public Integer getPlacarTime() { return placarTime; }
    public void setPlacarTime(Integer placarTime) { this.placarTime = placarTime; }
    public Integer getPlacarAdversario() { return placarAdversario; }
    public void setPlacarAdversario(Integer placarAdversario) { this.placarAdversario = placarAdversario; }
}
