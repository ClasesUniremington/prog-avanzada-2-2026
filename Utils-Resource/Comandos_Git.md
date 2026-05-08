# COMANDOS ESENCIALES DE GIT

## CONFIGURACIÓN INICIAL
- `git config --global user.name "Tu Nombre"` → Establece tu nombre para todos los repositorios.
- `git config --global user.email "tu@email.com"` → Establece tu correo electrónico.
- `git config --list` → Muestra la configuración actual de Git.

## INICIAR UN REPOSITORIO
- `git init` → Inicializa un nuevo repositorio Git en el directorio actual.
- `git clone <url>` → Clona un repositorio remoto existente.

## TRABAJO CON ARCHIVOS (ÁREA DE TRABAJO)
- `git status` → Muestra el estado de los archivos (modificados, en staging, sin seguimiento).
- `git add <archivo>` → Agrega un archivo específico al área de staging.
- `git add .` → Agrega todos los archivos nuevos y modificados al staging.
- `git rm <archivo>` → Elimina un archivo del repositorio y del sistema de archivos.
- `git mv <origen> <destino>` → Renombra o mueve un archivo.

## REALIZAR COMMITS
- `git commit -m "mensaje"` → Confirma los cambios del staging con un mensaje.
- `git commit -am "mensaje"` → Agrega y confirma en un solo paso (solo archivos con seguimiento).
- `git commit --amend` → Modifica el último commit (mensaje o contenido).

## HISTORIAL Y DIFERENCIAS
- `git log` → Muestra el historial de commits.
- `git log --oneline` → Historial compacto (un commit por línea).
- `git diff` → Muestra diferencias sin stagear.
- `git diff --staged` → Muestra diferencias en el staging.

## RAMAS (BRANCHES)
- `git branch` → Lista ramas locales (* = activa).
- `git branch <nombre>` → Crea una nueva rama.
- `git branch -d <nombre>` → Elimina una rama.
- `git checkout <rama>` → Cambia a otra rama.
- `git switch <rama>` → Alternativa moderna a checkout.
- `git checkout -b <rama>` → Crea y cambia a nueva rama.
- `git merge <rama>` → Fusiona la rama especificada en la actual.
- `git merge --abort` → Cancela un merge con conflictos.

## TRABAJO CON REMOTOS
- `git remote -v` → Muestra repositorios remotos configurados.
- `git remote add origin <url>` → Añade un remoto llamado origin.
- `git push -u origin <rama>` → Sube la rama y establece upstream.
- `git push` → Sube commits al remoto configurado.
- `git pull` → Descarga y fusiona cambios del remoto.
- `git fetch` → Descarga cambios sin fusionarlos.

## DESHACER CAMBIOS
- `git restore <archivo>` → Descarta cambios en el área de trabajo.
- `git restore --staged <archivo>` → Saca archivo del staging (no lo modifica).
- `git reset --soft HEAD~1` → Elimina último commit, mantiene cambios en staging.
- `git reset --hard HEAD~1` → Elimina último commit Y los cambios (peligroso).
- `git revert <commit>` → Crea un nuevo commit que deshace otro commit.

## STASH (GUARDADO TEMPORAL)
- `git stash` → Guarda cambios sin commitear temporalmente.
- `git stash list` → Lista los stash guardados.
- `git stash pop` → Aplica el último stash y lo elimina.
- `git stash drop` → Elimina el último stash.

## ETIQUETAS (TAGS)
- `git tag` → Lista etiquetas.
- `git tag -a v1.0 -m "versión 1.0"` → Crea una etiqueta anotada.
- `git push --tags` → Sube todas las etiquetas al remoto.

## ÚTILES ADICIONALES
- `git help <comando>` → Ayuda detallada de un comando.
- `git grep "texto"` → Busca texto en el repositorio.
- `git blame <archivo>` → Muestra quién modificó cada línea.

## CONVENTIONAL COMMITS
Convención para escribir mensajes de commits de forma estructurada:

```
<tipo>(<alcance>): <descripción>

<cuerpo>

<pie de página>
```

### Tipos comunes:
- `feat`: Nueva característica
- `fix`: Corrección de error
- `docs`: Cambios en documentación
- `style`: Cambios de formato (espacios, indentación)
- `refactor`: Cambios sin agregar features ni corregir bugs
- `perf`: Mejoras de rendimiento
- `test`: Agregar o actualizar pruebas
- `chore`: Cambios en dependencias, configuración

### Ejemplo:
```
feat(auth): agregar autenticación por JWT

Se implementó autenticación basada en tokens JWT 
para mejorar la seguridad de la API.

Closes #45
```

## OPERACIONES AVANZADAS
- `git rebase <rama>` → Reorganiza commits sobre otra rama.
- `git cherry-pick <commit>` → Aplica un commit específico en la rama actual.
- `git reflog` → Muestra historial de cambios HEAD (útil para recuperar commits).
- `git bisect start` → Inicia búsqueda binaria para encontrar commit problemático.
- `git log --grep="palabra"` → Busca commits por mensaje.
- `git log --author="nombre"` → Filtra commits por autor.
- `git log --since="2 weeks ago"` → Commits de los últimos 15 días.

## FLUJO GIT RECOMENDADO
1. `git pull` → Actualizar rama local
2. `git checkout -b feature/nombre` → Crear rama de feature
3. Hacer cambios y commits
4. `git push -u origin feature/nombre` → Subir rama
5. Crear Pull Request en la plataforma
6. `git switch main && git pull` → Volver a main
7. `git merge feature/nombre` → Fusionar cambios

## RESOLUCIÓN DE CONFLICTOS
- Los conflictos se marcan con `<<<<< HEAD`, `=====`, `>>>>> rama`
- Edita el archivo, elige qué versión mantener
- `git add <archivo>` → Marca como resuelto
- `git commit` → Completa el merge

---

# FUTURES EN JAVA

## ¿QUÉ ES UN FUTURE?
Un `Future` representa el resultado de una operación asíncrona que se completará en el futuro. Es una promesa de que tendrás un valor cuando esté listo.

## CREANDO UN FUTURE SIMPLE

### Usando ExecutorService:
```java
import java.util.concurrent.*;

ExecutorService executor = Executors.newFixedThreadPool(2);

// Sumitiendo una tarea que retorna un valor
Future<Integer> future = executor.submit(() -> {
    Thread.sleep(2000); // Simular trabajo
    return 42;
});

// Obtener el resultado (bloqueante)
try {
    Integer resultado = future.get(); // Espera hasta 2000ms
    System.out.println("Resultado: " + resultado);
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
} finally {
    executor.shutdown();
}
```

### Comprobando si está completo:
```java
if (future.isDone()) {
    System.out.println("¡La tarea terminó!");
} else {
    System.out.println("Aún en progreso...");
}
```

### Cancelar un Future:
```java
boolean cancelado = future.cancel(true); // true = interrumpir si está corriendo
if (cancelado) {
    System.out.println("Tarea cancelada");
}
```

## TRABAJAR CON MÚLTIPLES FUTURES

### CompletableFuture (Java 8+):
```java
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hola");
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> " Mundo");

// Combinar resultados
CompletableFuture<String> resultado = future1.thenCombine(future2, (s1, s2) -> s1 + s2);

System.out.println(resultado.join()); // "Hola Mundo"
```

### Esperar múltiples futures:
```java
CompletableFuture[] futures = { future1, future2, future3 };
CompletableFuture.allOf(futures).join(); // Espera todos

// O cualquiera que termine primero:
CompletableFuture.anyOf(futures).join();
```

## DIFERENCIAS: Future vs CompletableFuture
| Aspecto | Future | CompletableFuture |
|--------|--------|-----------------|
| Resultado manual | ✅ Se obtiene manualmente | ❌ Más automático |
| Composición | ❌ Difícil | ✅ Fácil con métodos |
| Manejo de errores | ❌ Limitado | ✅ Con exceptionally() |
| Java mínima | Java 5 | Java 8 |

## BUENAS PRÁCTICAS
- Siempre llamar a `shutdown()` en ExecutorService
- Usar `thenApply()`, `thenAccept()` en lugar de `get()` cuando sea posible
- Manejar excepciones con `exceptionally()` o `handle()`
- Considerar usar `ForkJoinPool.commonPool()` para tareas ligeras