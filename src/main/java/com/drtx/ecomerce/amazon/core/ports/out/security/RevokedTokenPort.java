package com.drtx.ecomerce.amazon.core.ports.out.security;

import java.time.Instant;

public interface RevokedTokenPort {
    void save(String token);

    boolean exists(String token);

    /**
     * Elimina tokens revocados antes de la fecha especificada.
     * @param before eliminar tokens revocados antes de esta fecha
     * @return cantidad de tokens eliminados
     */
    int deleteRevokedBefore(Instant before);
}

