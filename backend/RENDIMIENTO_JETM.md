# Pruebas no funcionales con JETM

Pruebas dinámicas no funcionales de caja blanca del backend. Los tiempos se miden **dentro del código de producción** con puntos de medida de JETM (Java Execution Time Measurement, `fm.void.jetm:jetm:1.2.3`), a nivel de **unidad** (servicio aislado, DAOs simulados) y de **integración** (servicio, DAO y MySQL).

## 1. Requisitos no funcionales

Las pantallas de cartelera, historial y compra son interactivas. Se toma como presupuesto de respuesta 100 ms, el límite a partir del cual el usuario deja de percibir la respuesta como inmediata, y se reserva para el servicio como máximo la mitad. El resto queda para la red, la serialización JSON y el navegador.

| Id | Requisito | Nivel | Umbral |
| --- | --- | --- | --- |
| RNF-1 | `getCartelera` de un día con hasta 300 sesiones (unos 20 salas × 15 pases, por encima de un multicine real) | Integración | Media < 50 ms |
| RNF-1 | Lógica propia de `getCartelera` (validación y agrupación por película) con hasta 3000 sesiones, 10 veces la carga prevista | Unidad | Media < 5 ms (10 % del presupuesto de integración) |
| RNF-1b | El número de sentencias SQL de `getCartelera` no crece con el número de películas del día | Integración | Igual con 3 y con 33 películas |
| RNF-2 | Una página (10 compras) del historial de un usuario con 1000 compras, primera y última página | Integración | Media < 30 ms |
| RNF-2 | Lógica propia de `findHistoricoCompras` | Unidad | Media < 1 ms |
| RNF-3 | `comprarEntradas` sobre una sesión con stock | Integración | Media < 50 ms |
| RNF-3 | Lógica propia de `comprarEntradas` | Unidad | Media < 1 ms |

## 2. Instrumentación

Se añadieron `EtmPoint` en el código de producción. Con el monitor anidado, cada punto interno queda como hijo del que lo contiene, lo que permite ver qué parte del servicio consume el tiempo.

| Punto de medida | Dónde |
| --- | --- |
| `CarteleraService.getCartelera` | Método completo, también cuando lanza excepción |
| `CarteleraService.getCartelera:consulta` | Llamada a `SesionDao.findByFechaHoraBetween...` |
| `CarteleraService.getCartelera:agrupacion` | Agrupación de sesiones por película |
| `CompraService.comprarEntradas` | Método completo |
| `CompraService.comprarEntradas:buscarSesion` | `SesionDao.findById` |
| `CompraService.comprarEntradas:guardar` | `CompraDao.save` |
| `CompraService.findHistoricoCompras` | Método completo |
| `CompraService.findHistoricoCompras:consulta` | Consulta paginada del DAO |

En producción no se configura JETM. `EtmManager.getEtmMonitor()` devuelve entonces un `NullMonitor` y los puntos no miden nada. Las pruebas activan el monitor con `BasicEtmConfigurator.configure(true)` y leen los agregados mediante `MedicionJetm`.

## 3. Método

- **Calentamiento:** 200 llamadas en unidad y 10 en integración, que se descartan antes de medir.
- **Repeticiones medidas:** 1000 en unidad y 50 en integración. Cada prueba comprueba la media frente al umbral, con un único assert.
- **Carga creciente:** 30, 300 y 3000 sesiones en unidad; 30 y 300 en integración; 1000 compras en el historial.
- **Integración:** tras cada llamada se vacía el contexto de persistencia (`EntityManager.clear()`), fuera del punto de medida, para que cada consulta llegue a MySQL y no a la caché de primer nivel.
- **RNF-1b:** se cuentan las sentencias preparadas con las estadísticas de Hibernate (`Statistics.getPrepareStatementCount()`).
- **Informes:** cada prueba añade la tabla de JETM a `target/jetm/*.txt`.

## 4. Pruebas

| Clase | Nivel | Qué comprueba |
| --- | --- | --- |
| `test/rendimiento/CarteleraServiceRendimientoUnitTest` | Unidad | Umbral de la agrupación y del servicio con 30, 300 y 3000 sesiones; una medición por llamada; puntos hijos `consulta` y `agrupacion`; también se mide una llamada rechazada. |
| `test/rendimiento/CompraServiceRendimientoUnitTest` | Unidad | Umbral de compra e historial; una medición por llamada; puntos hijos; también se mide una compra rechazada. |
| `test/rendimiento/CarteleraRendimientoIntegrationTest` | Integración | RNF-1 con 30 y 300 sesiones; RNF-1b. |
| `test/rendimiento/CompraRendimientoIntegrationTest` | Integración | RNF-2 en la página 0 y la 99 con 1000 compras; RNF-3. |

Las pruebas de unidad también forman parte de los `targetTests` de PIT. Comprueban que cada punto registra sus medidas, así que PIT elimina los mutantes que quitan un `collect()`.

## 5. Resultados

Entorno: macOS (Apple Silicon), OpenJDK 21.0.12, Maven 3.9. Tiempos en milisegundos.

### Unidad

| Medida | Carga | Media | Mín. | Máx. | Umbral | Resultado |
| --- | --- | ---: | ---: | ---: | ---: | --- |
| `getCartelera` | 30 sesiones | 0,010 | 0,006 | 2,152 | 5 | Cumple |
| `getCartelera` | 300 sesiones | 0,008 | 0,007 | 0,026 | 5 | Cumple |
| `getCartelera` | 3000 sesiones | 0,047 | 0,036 | 0,109 | 5 | Cumple |
| `comprarEntradas` | 1 entrada | 0,015 | 0,007 | 0,095 | 1 | Cumple |
| `findHistoricoCompras` | página de 10 | 0,007 | 0,005 | 0,031 | 1 | Cumple |

La agrupación crece con el número de sesiones, pero con 3000 sesiones sigue dos órdenes de magnitud por debajo del umbral. El máximo de 2 ms con 30 sesiones corresponde a una pausa aislada de la JVM: no afecta a la media y por eso el requisito se fija sobre la media.

### Integración

MySQL 26.10 local, base `paprojecttest`.

| Medida | Carga | Media | Mín. | Máx. | Umbral | Resultado |
| --- | --- | ---: | ---: | ---: | ---: | --- |
| `getCartelera` | 30 sesiones | 1,732 | 1,242 | 2,399 | 50 | Cumple |
| `getCartelera` | 300 sesiones | 4,841 | 4,032 | 8,206 | 50 | Cumple |
| `findHistoricoCompras` | 1000 compras, página 0 | 2,176 | 1,379 | 3,535 | 30 | Cumple |
| `findHistoricoCompras` | 1000 compras, página 99 | 1,645 | 1,161 | 2,417 | 30 | Cumple |
| `comprarEntradas` | 1 entrada | 0,575 | 0,390 | 0,951 | 50 | Cumple |
| Sentencias SQL de `getCartelera` (RNF-1b) | 3 películas / 33 películas | 9 / 44 | | | Iguales | **No cumple** |

Reparto interno del tiempo, gracias a los puntos hijos:

| Punto | 30 sesiones | 300 sesiones |
| --- | ---: | ---: |
| `getCartelera:consulta` | 1,711 ms (99 %) | 4,779 ms (99 %) |
| `getCartelera:agrupacion` | 0,012 ms | 0,050 ms |

En `comprarEntradas`, `buscarSesion` (0,214 ms) y `guardar` (0,174 ms) suman un 67 % del total. El resto es la comprobación del usuario con `PermissionChecker`, que no tiene punto propio.

## 6. Hallazgo: consultas N+1 en la cartelera

La agrupación en memoria es despreciable: casi todo el tiempo de `getCartelera` está en la consulta, y al multiplicar por 10 las sesiones el tiempo se multiplica por 2,8. RNF-1b muestra la causa. `Sesion.sala` y `Sesion.pelicula` son `@ManyToOne` con carga `EAGER` por defecto, y la consulta derivada no los trae con un `JOIN FETCH`. Hibernate lanza una sentencia adicional por cada película y por cada sala distinta:

- 30 sesiones, 3 películas, 5 salas: 1 + 3 + 5 = **9 sentencias**.
- 330 sesiones, 33 películas, 10 salas: 1 + 33 + 10 = **44 sentencias**.

Con la carga actual se cumple el umbral de tiempo, pero el coste crece linealmente con el número de películas y salas del día. La corrección propuesta es cargar las relaciones en la misma consulta (`@EntityGraph(attributePaths = {"pelicula", "sala"})` en `SesionDao`, o un `JOIN FETCH`). Tras corregirlo, `sentenciasSqlNoCrecenConElNumeroDePeliculas` debe pasar.

## 7. Cómo reproducirlo

Desde `backend/`:

```
mvn resources:resources compiler:compile resources:testResources compiler:testCompile surefire:test -Dtest='*RendimientoUnitTest'
```

Las pruebas de integración necesitan la base de datos de pruebas en MySQL:

```
mvn resources:resources compiler:compile resources:testResources compiler:testCompile surefire:test -Dtest='*RendimientoIntegrationTest'
```

Los informes de JETM quedan en `target/jetm/`.

## 8. Limitaciones

- Los tiempos dependen de la máquina. Los umbrales están lejos de lo medido para que la prueba no falle por variaciones normales del entorno.
- Las pruebas de integración se ejecutan dentro de una transacción que se deshace al terminar. Se mide el servicio hasta el `INSERT` de la compra, pero no el `COMMIT`.
- No se mide la concurrencia de varias compras simultáneas: es una propiedad de corrección (bloqueo optimista, issue #11) y no de tiempo.
