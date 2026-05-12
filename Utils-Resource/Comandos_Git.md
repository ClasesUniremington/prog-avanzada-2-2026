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