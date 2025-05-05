# LightBulb Puzzle Game

Logická puzzle hra napsaná v Javě s využitím JavaFX, inspirovaná mobilní aplikací **Light Bulb Puzzle Game**.

---

## 🎯 Přehled

Spojte jediný zdroj energie se všemi žárovkami otočením dlaždic tak, aby vznikl uzavřený elektrický obvod. Hrací pole se skládá z různých typů dlaždic:

- **Drát (`I`)**: přímé spojení
- **L-konektor (`L`)**: ohyb o 90°
- **T-konektor (`T`)**: spojení tří směrů
- **Kříž (`X`)**: spojení všech čtyř směrů
- **Zdroj (`S`)**: výchozí bod energie (zobrazen jako ikona lampičky)
- **Žárovka (`B`)**: svítí, jakmile je napájena

Cílem je, aby všechny žárovky svítily současně.

---

## 🚀 Funkce

- Volba velikosti pole: 5×5, 6×6, 8×8, 10×10  
- Nastavení počtu žárovek (1–5)  
- Generování řešitelné úlohy s náhodným promícháním  
- Architektura MVC pro čisté oddělení logiky a UI  
- Podpora **undo/redo** pro otáčení dlaždic  
- Počítadlo tahů  
- Dynamická vizualizace napájení:  
  - Připojené dlaždice a žárovky září zeleně  
  - Nepřipojené červeně  
- Temné, kontrastní téma s vysokou čitelností

---

## 🛠️ Požadavky

- Java 21 (OpenJDK 21+)  
- Maven 3.8+  
- Make (volitelně)

---

## ⚙️ Instalace a spuštění

1. **Klonujte repozitář**  
   ```bash
   git clone https://tvuj.repo.url/lightbulbgame.git
   cd lightbulbgame
   ```

2. **Sestavte projekt**  
   ```bash
   make
   ```  
   nebo  
   ```bash
   mvn clean package
   ```

3. **Spusťte hru**  
   ```bash
   make run
   ```  
   nebo  
   ```bash
   mvn javafx:run
   ```

---

## 📂 Struktura projektu

```
lightbulbgame/
├── Makefile
├── pom.xml
├── README.md           # Tato dokumentace
├── requirements.md     # Stav a podrobnosti požadavků
└── src/
    ├── main/
    │   ├── java/
    │   │   └── cz/vut/ija/game/
    │   │       ├── Main.java
    │   │       ├── controller/
    │   │       ├── command/
    │   │       ├── generator/
    │   │       ├── logic/
    │   │       ├── model/
    │   │       └── view/
    │   └── resources/
    │       ├── styles.css
    │       └── lamp.png
    └── test/
        └── java/
```

---

## 👥 Autoři

- **Filip Hladík** (xhladi26) – vedoucí projektu, návrh UI  
- **Maksim Samusevich** (xsamus00) – model a herní logika

---

## 📄 Licence

Projekt je distribuován pod licencí MIT. Více v souboru [LICENSE](LICENSE).

---

---

## 📝 Log přidaných souborů

Níže přehled klíčových souborů a jejich účel:

| Soubor | Popis |
| :--- | :--- |
| `Makefile` | Makefile s cíli `all`, `clean`, `run`, a `readme` pro pohodlnou správu buildu a spuštění. |
| `pom.xml` | Maven konfigurace s JavaFX pluginem pro kompilaci a spuštění aplikace. |
| `src/main/java/cz/vut/ija/game/Main.java` | Vstupní třída JavaFX: inicializuje okno, top-bar (volba velikosti, počtu žárovek, tlačítko New Game, počítadlo tahů). |
| `src/main/java/cz/vut/ija/game/controller/GameController.java` | Koordinátor mezi modelem a view, spravuje undo/redo a aktualizuje počet tahů pomocí `MoveListener`. |
| `src/main/java/cz/vut/ija/game/command/Command.java` | Rozhraní příkazu definující metody `execute()` a `undo()`. |
| `src/main/java/cz/vut/ija/game/command/RotateCommand.java` | Implementace otáčení dlaždice s ukládáním předchozího stavu pro undo. |
| `src/main/java/cz/vut/ija/game/generator/LevelGenerator.java` | Generuje vyřešenou síť drátů, umísťuje `SourceTile` a `BulbTile`, buduje spanning tree, a náhodně míchá orientace. |
| `src/main/java/cz/vut/ija/game/model/GameBoard.java` | Datová struktura hrací plochy: správa dlaždic, notifikace observerů, hledání zdroje (`findSource()`), kontrola hranic (`inBounds()`). |
| `src/main/java/cz/vut/ija/game/model/Tile.java` | Abstraktní třída dlaždice: správa rotace, abstraktní `getType()` a `getBaseSides()`, metody `getRotatedSides()` a `connects()`. |
| `src/main/java/cz/vut/ija/game/model/WireTile.java` | Rovná drátová dlaždice (`I`): spojuje sever a jih. |
| `src/main/java/cz/vut/ija/game/model/LTile.java` | L-konektor: spojuje sever a východ. |
| `src/main/java/cz/vut/ija/game/model/TTile.java` | T-konektor: spojuje východ, jih a západ (dírka na severu). |
| `src/main/java/cz/vut/ija/game/model/XTile.java` | Křížový konektor: spojuje všechny čtyři směry. |
| `src/main/java/cz/vut/ija/game/model/SourceTile.java` | Zdroj: zobrazen ikonou lampičky (`lamp.png`), základní spojení na jih. |
| `src/main/java/cz/vut/ija/game/model/BulbTile.java` | Žárovka: přijímá proud ze severu, svítí, když je napájena. |
| `src/main/java/cz/vut/ija/game/model/Position.java` | Pomocná třída pro pozici v mřížce (řádek, sloupec). |
| `src/main/java/cz/vut/ija/game/model/Side.java` | Enum čtyř směrů (`NORTH`, `EAST`, `SOUTH`, `WEST`). |
| `src/main/java/cz/vut/ija/game/model/BoardObserver.java` | Rozhraní pro notifikace změn v modelu. |
| `src/main/java/cz/vut/ija/game/logic/GameSimulator.java` | BFS simulace toku proudu od zdroje; označuje napájené dlaždice. |
| `src/main/java/cz/vut/ija/game/view/BoardView.java` | Vizualizace mřížky tlačítek, zobrazení ikony lampičky pro zdroj, aplikace CSS tříd `tile-powered`/`tile-unpowered`. |
| `src/main/java/cz/vut/ija/game/view/TileClickEvent.java` | Vlastní JavaFX událost pro kliknutí na dlaždici. |
| `src/main/resources/styles.css` | Stylování tmavého tématu, vzhled dlaždic, efekty. |
| `src/main/resources/lamp.png` | Ikona lampičky použitá pro zobrazení zdroje místo textového symbolu. |
```

```
Makefile       pom.xml       README.md     requirements.md
src/main/java/...                  src/main/resources/
src/test/java/...
```
