package com.drtx.ecomerce.amazon.core.ports.out.security;

import java.time.Instant;

public interface TokenRevocationPort {
    void invalidate(String token);

    boolean isInvalidated(String token);

    /**
     * Elimina tokens revocados que fueron creados antes de la fecha especificada.
     * @param before eliminar tokens revocados antes de esta fecha
     * @return cantidad de tokens eliminados
     */
    int deleteTokensRevokedBefore(Instant before);
}
