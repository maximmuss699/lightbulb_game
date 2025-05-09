# IJA PROJEKT - LIGHTBULB GAME

Logická hra napsaná v Javě inspirovaná mobilní hrou **Light Bulb Puzzle Game** dostupnou pro Android na Google Play

## Autoři
- Filip Hladík - vedoucí (xhladi26)
- Maksim Samusevich (xsamus00)

## Překlad a spuštění
Pro tyto účely je zde dostupný Makefile.

### Překlad
> make

Zavola `mvn compile`, `mvn javadoc:javadoc` a `mvn package`.

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

Pro spusteni projektu je doporuceno zavolat `make run`. 

### Dokumentace
Dokumentace se vytvori po spusteni make, pripadne ji lze manualne vytvorit zavolanim:
> mvn javadoc:javadoc

Dokumentace se vytvori do slozky target/docs/apidocs.

## Implementace
Jako základ byla použita logika aplikace z úkolu 1 a 2. UI je pak realizováno pomocí JavaFX.
Jakožto architekturu jsme zvolili Model View Controller, přičemž každá z částí je uložena v příslušném adresáři.

Obrazky se nachazi v src/main/resources, kvuli metode getResourceAsStream(), ktera bere implicitni umisteni obrazku prave v teto slozce.