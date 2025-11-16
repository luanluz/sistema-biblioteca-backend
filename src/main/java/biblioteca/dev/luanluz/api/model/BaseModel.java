package biblioteca.dev.luanluz.api.model;

import jakarta.persistence.MappedSuperclass;

import java.io.Serial;
import java.io.Serializable;

@MappedSuperclass
public abstract class BaseModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
