# IJA PROJEKT - LIGHTBULB GAME

Logická hra napsaná v Javě inspirovaná mobilní hrou **Light Bulb Puzzle Game** dostupnou pro Android na Google Play

## Autoři
- Filip Hladík - vedoucí (xhladi26)
- Maksim Samusevich (xsamus00)

## Překlad a spuštění
Pro tyto účely je zde dostupný Makefile.

### Překlad
> make

Zavola mvn compile.

### Spuštění
> make run

Tento prikaz spusti `mvn javafx:run`.

### Vytvoreni JAR archivu
> make package

Vytvori validni jar archiv. Pro spusteni je ovsem potreba prilozit cestu ke vsem dependencies! Tedy:
- javafx-base
- javafx.controls
- javafx.fxml
- javafx.graphics

## Implementace
Jako základ byla použita logika aplikace z úkolu 1 a 2. UI je pak realizováno pomocí JavaFX.
Jakozto architekturu jsme potom zvolili pristup Model View Controller.