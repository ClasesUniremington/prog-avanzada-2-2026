# COMANDOS ESENCIALES DE MAVEN

## INFORMACIÓN Y VERIFICACIÓN
- `mvn --version` → Muestra la versión de Maven y la configuración del entorno (Java, OS).
- `mvn help:describe` → Describe un plugin o goal específico.
- `mvn help:effective-pom` → Muestra el POM final después de aplicar herencia y perfiles.
- `mvn help:effective-settings` → Muestra la configuración efectiva de settings.xml.

## CICLO DE VIDA BÁSICO (BUILD)
- `mvn validate` → Valida que el proyecto sea correcto y que todos los datos necesarios estén disponibles.
- `mvn compile` → Compila el código fuente del proyecto.
- `mvn test-compile` → Compila el código de pruebas.
- `mvn test` → Ejecuta las pruebas unitarias.
- `mvn package` → Empaqueta el código compilado en un JAR, WAR, etc.
- `mvn verify` → Ejecuta verificaciones de integración (checks).
- `mvn install` → Instala el artefacto en el repositorio local (~/.m2/repository).
- `mvn deploy` → Copia el artefacto al repositorio remoto para compartirlo.

## LIMPIEZA
- `mvn clean` → Elimina el directorio `target` (limpia builds anteriores).
- `mvn clean compile` → Limpia y luego compila.
- `mvn clean install` → Limpia y luego instala (muy común).

## EJECUCIÓN DE PLUGINS ESPECÍFICOS
- `mvn dependency:tree` → Muestra el árbol de dependencias del proyecto.
- `mvn dependency:list` → Lista todas las dependencias resueltas.
- `mvn dependency:analyze` → Analiza dependencias no usadas o faltantes.
- `mvn dependency:purge-local-repository` → Limpia dependencias específicas del repositorio local.
- `mvn site:site` → Genera la documentación del sitio del proyecto.
- `mvn site:deploy` → Despliega el sitio generado en un servidor.

## CONSTRUCCIÓN OMITIENDO PRUEBAS
- `mvn package -DskipTests` → Empaqueta sin compilar ni ejecutar pruebas.
- `mvn install -Dmaven.test.skip=true` → Instala omitiendo completamente las pruebas.
- `mvn test -Dtest=MiTest` → Ejecuta solo una clase de prueba específica.
- `mvn test -Dtest=MiTest#miMetodo` → Ejecuta solo un método de prueba específico.

## PERFILES (PROFILES)
- `mvn package -Pproduccion` → Activa el perfil llamado "produccion".
- `mvn help:active-profiles` → Muestra qué perfiles están activos actualmente.

## RESOLUCIÓN DE PROBLEMAS
- `mvn -X` → Modo debug (muestra información detallada de ejecución).
- `mvn -e` → Muestra el stacktrace completo de errores.
- `mvn -q` → Modo quiet (solo muestra errores).
- `mvn -U` → Forzar actualización de snapshots/versiones del repositorio remoto.
- `mvn -o` → Modo offline (no intenta descargar dependencias remotas).

## MULTIMÓDULOS (REACTOR)
- `mvn -pl moduloA` → Construye solo el módulo especificado (y sus dependencias).
- `mvn -pl moduloA -am` → Construye móduloA + los módulos que depende.
- `mvn -pl moduloA -amd` → Construye móduloA + los módulos que dependen de él.
- `mvn -rf moduloB` → Reanuda la construcción desde el móduloB (tras un fallo).

## PROPIEDADES Y ARGUMENTOS
- `mvn -Dpropiedad=valor` → Define una propiedad del sistema.
- `mvn -Dmaven.compiler.source=11` → Especifica versión de Java fuente.
- `mvn -Dmaven.compiler.target=11` → Especifica versión de Java objetivo.

## GENERAR PROYECTO RÁPIDO (ARCHETYPE)
- `mvn archetype:generate` → Genera un nuevo proyecto desde un arquetipo.
- `mvn archetype:generate -DgroupId=com.ejemplo -DartifactId=mi-app -DarchetypeArtifactId=maven-archetype-quickstart` → Crea un proyecto Java básico.

## ÚTILES PARA SPRING BOOT
- `mvn spring-boot:run` → Ejecuta una aplicación Spring Boot.
- `mvn spring-boot:start` → Inicia la aplicación en segundo plano.
- `mvn spring-boot:stop` → Detiene la aplicación iniciada con `start`.

## ACTUALIZACIÓN DE PROYECTOS
- `mvn versions:display-dependency-updates` → Muestra dependencias con versiones más nuevas.
- `mvn versions:display-plugin-updates` → Muestra plugins con versiones más nuevas.
- `mvn versions:use-latest-releases` → Actualiza dependencias a latest release.

## REPOSITORIO LOCAL
- `mvn dependency:get -Dartifact=groupId:artifactId:version` → Descarga una dependencia específica al repositorio local.
- `mvn install:install-file -Dfile=mi-lib.jar -DgroupId=com.ejemplo -DartifactId=mi-lib -Dversion=1.0 -Dpackaging=jar` → Instala un JAR externo en el repositorio local.