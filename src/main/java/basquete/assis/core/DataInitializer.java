package basquete.assis.core;

import basquete.assis.shared.entities.CategoriaBase;
import basquete.assis.shared.entities.TipoAgendamento;
import basquete.assis.shared.repositories.CategoriaBaseRepository;
import basquete.assis.shared.repositories.TipoAgendamentoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoriaBaseRepository categoriaBaseRepository;
    private final TipoAgendamentoRepository tipoAgendamentoRepository;

    public DataInitializer(CategoriaBaseRepository categoriaBaseRepository,
                           TipoAgendamentoRepository tipoAgendamentoRepository) {
        this.categoriaBaseRepository = categoriaBaseRepository;
        this.tipoAgendamentoRepository = tipoAgendamentoRepository;
    }

    @Override
    public void run(String... args) {
        if (categoriaBaseRepository.count() == 0) {
            CategoriaBase c = new CategoriaBase();
            c.setNome("Adulto");
            categoriaBaseRepository.save(c);
            CategoriaBase c2 = new CategoriaBase();
            c2.setNome("Sub-17");
            categoriaBaseRepository.save(c2);
        }
        if (tipoAgendamentoRepository.count() == 0) {
            TipoAgendamento t1 = new TipoAgendamento();
            t1.setNome("Treino");
            tipoAgendamentoRepository.save(t1);
            TipoAgendamento t2 = new TipoAgendamento();
            t2.setNome("Jogo");
            tipoAgendamentoRepository.save(t2);
        }
    }
}
