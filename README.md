# QuePasaMonitor

**Autor:** braco96

## ¿Qué es?
Una demo de **monitores** en Java usando `ReentrantLock` y `Condition` para implementar un **buffer acotado** con múltiples productores y consumidores. Se garantiza exclusión mutua, espera-condición y ausencia de indeterminación al acceder a la estructura compartida.

## Puntos clave
- **Lock justo (`fair=true`)** para minimizar hambre.
- Dos condiciones: `notFull` y `notEmpty` para coordinar productores/consumidores.
- Estructura circular (head/tail) sin bloqueos activos.
- Señalización fina (`signal`) para despertar al rol necesario.

## Ejecutar
```bash
# Desde la carpeta del proyecto
bash run.sh
```

## Estructura
```
src/main/java/braco96/quepasamonitor/MonitorApp.java
README.md
MEMORIA.md
run.sh
```