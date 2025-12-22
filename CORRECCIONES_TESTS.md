# Correcciones necesarias para los tests de controladores

## Problemas encontrados:

1. **BigDecimal vs double**: Product.price, Product.averageRating y Order.total usan BigDecimal
2. **UUID vs String**: Incidence.publicUi usa UUID
3. **AppealResponse**: Usa AppealStatus y AppealDecision (enums) no String
4. **IncidenceResponse**: Usa IncidenceStatus y IncidenceDecision (enums) no String
5. **OrderResponse**: Está vacío, necesita definirse correctamente
6. **Mockito any()**: Necesita usar ArgumentMatchers.any() correctamente

## Archivos a corregir:

- ProductControllerTest.java - BigDecimal
- FavoriteControllerTest.java - BigDecimal
- OrderControllerTest.java - BigDecimal y OrderResponse
- CartControllerTest.java - BigDecimal
- AppealControllerTest.java - UUID, enums, IncidenceResponse
- IncidenceControllerTest.java - UUID, enums

## Correcciones aplicadas:

Se crearán versiones corregidas de los archivos de test.
