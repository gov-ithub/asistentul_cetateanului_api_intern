# Asistentul Cetateanului: REST Api pentru consumeri (istoric, submissions, meta-data, etc.)

## Configurare
```
cp src/main/resources/application.properties.dist src/main/resources/application.properties
cp src/test/resources/test.properties.dist src/test/resources/test.properties
```
Editati apoi cele doua fisiere noi.

## Dependinte

Acest proiect necesita biblioteca asistentul_cetateanului_dao_lib pentru a rula.
Urmati instructiunile de impachetare din acel repository, apoi copiati jar-ul rezultat in src/main/resources

## Impachetare
```
mvn package
```
In sub-folder-ul target, se va crea un .jar

## Rulare
- clone asistentul_cetateanului_docker_dev_end si urmati instructiunile de acolo.
- Modificati user-ul, parola si url-ul de conectare la baza de date in fisierul src/main/resources/application.properties
- Executati comanda:

```
java -jar target/jar-file-SNAPSHOT.jar
```

## Testare:
- Modificati user-ul, parola si url-ul de conectare la baza de date in fisierul src/test/resources/test.properties
- Executati comanda:
```
mvn test
```
