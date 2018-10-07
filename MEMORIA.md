# Memoria técnica – QuePasaMonitor

Soy **braco96** y en este ejercicio trabajé **métodos concurrentes con monitores** para evitar la indeterminación en accesos a un recurso compartido.

## Objetivo
Diseñar un buffer acotado seguro para múltiples productores y consumidores.

## Decisiones de diseño
- **Monitor** con `ReentrantLock(fair=true)` para reforzar la ausencia de hambre.
- **Dos condiciones** bien separadas: `notFull` para productores, `notEmpty` para consumidores.
- **Bucle while** en espera-condición para manejar despertares espurios.
- **Sección crítica mínima**: copiar/leer y actualizar índices en O(1).

## Evitando la indeterminación
- Exclusión mutua en todas las operaciones del buffer.
- Política **FIFO aproximada** del lock justo para planificar hilos.
- Uso de `signal()` específico en lugar de `signalAll()` para reducir tormentas de hilos.

## Posibles extensiones
- Métricas de latencia por operación.
- Integración con `Executor` para desacoplar producción/consumo.
- Pruebas de estrés con `jcstress`.