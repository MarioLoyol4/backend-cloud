package cl.duoc.colegio.communication.service;

import cl.duoc.colegio.communication.model.Comunicado;
import cl.duoc.colegio.communication.repository.ComunicadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComunicadoService {


    private final ComunicadoRepository comunicadoRepository;

    public Comunicado publicar(Comunicado comunicado) {
        return comunicadoRepository.save(comunicado);
    }

    public List<Comunicado> getTodos(){
        return comunicadoRepository.findAll();
    }

    public List<Comunicado> getByDestinatario(String destinatario) {
        return comunicadoRepository.findByDestinatario(destinatario);
    }
}
