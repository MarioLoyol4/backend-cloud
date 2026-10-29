package cl.duoc.colegio.academic.service;

import cl.duoc.colegio.academic.messaging.producer.NotaEventPublisher;
import cl.duoc.colegio.academic.model.Nota;
import cl.duoc.colegio.academic.repository.NotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotaService {
    private final NotaRepository notaRepository;
    private final NotaEventPublisher notaEventPublisher;

    @Transactional
    public Nota registrarNota(Nota nota) {
        Nota notaGuardada = notaRepository.save(nota);
        notaEventPublisher.notaRegistrada(notaGuardada);
        return notaGuardada;
    }
}
