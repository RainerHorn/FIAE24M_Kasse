---
applyTo: '**'
---

## 🔄 Projektbewusstsein & Kontext
- **Immer zuerst `README.md` lesen**, um Architektur, Ziele, Stil und Randbedingungen des Projekts zu verstehen.
- **`TASK.md` prüfen**, bevor du ein neues Task beginnst. Fehlt der Task, trage ihn mit kurzer Beschreibung und aktuellem Datum ein.
- **Verwende konsistente Namenskonventionen, Dateistrukturen und Architekturpatterns** gemäß `README.md` und bestehenden Codebeispielen.
- **Checke offene Pull-Requests oder aktuelle Branches**, bevor du neue Entwicklungen startest (Vermeidung von Merge-Konflikten).

## 🧱 Code-Struktur & Modularität
- **Dateien dürfen maximal 500 Zeilen umfassen.** Größere Einheiten müssen durch Modularisierung (Helper, Services, Utilities) aufgeteilt werden.
- **Strikte Trennung nach Features oder Verantwortlichkeiten** (Feature-based Architecture bevorzugt).
- **Relative Imports** innerhalb eines Moduls verwenden; absolute Imports nur projektweit.
- **Verwende Dependency Injection**, wo sinnvoll, zur besseren Testbarkeit und Austauschbarkeit.

## 🧪 Testing & Zuverlässigkeit
- **Jede neue Funktionalität benötigt Unit-Tests und bei Bedarf Integrationstests.**
- **Testabdeckung anstreben: mindestens 80% für produktiven Code**.
- **Teststruktur**:
  - `/tests`-Ordner spiegelt die Hauptstruktur wider.
  - Pro Funktion mindestens:
    - 1 "Happy Path" Test
    - 1 Edge Case Test
    - 1 Failure Case Test
- **Mock externe Systeme** in Unit-Tests.
- **Contract-Tests** bei APIs implementieren, wenn sie zwischen Teams genutzt werden.

## ✅ Task-Abschluss
- **`TASK.md` sofort aktualisieren**, wenn ein Task abgeschlossen ist.
- **Neue Entdeckungen oder Nebenaufgaben** unter "Discovered During Work" eintragen.
- **Pull Request Beschreibung** enthält:
  - Was wurde geändert?
  - Warum wurde es geändert?
  - Wie wurde es getestet?

## 📎 Stil & Konventionen
- **Primärsprachen: JAVA **.
- **JavaDocs** für jede Klasse und jede Methode nach [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
  ```java
  /**
   * Processes incoming payment requests and delegates to payment gateways.
   *
   * @param paymentRequest validated incoming request
   * @return payment confirmation
   * @throws PaymentProcessingException if the payment cannot be processed
   */
  public PaymentResponse processPayment(PaymentRequest paymentRequest) throws PaymentProcessingException {
      // ...
  }
  ```
- **TypeScript-Funktionen** mindestens mit kurzen TSDoc-Kommentaren dokumentieren (`/** ... */`).
- **Keine Magic Numbers oder Strings** im Code – immer benannte Konstanten verwenden.
- **Single Responsibility Principle (SRP)** beachten: jede Funktion/Klasse sollte genau eine Verantwortung haben.

## 📚 Dokumentation & Verständlichkeit
- **README.md aktualisieren** bei:
  - neuen Features
  - Änderungen an Abhängigkeiten oder Setup-Schritten
- **Code dokumentieren**, wenn Logik nicht sofort verständlich ist.
- **Komplexe Abschnitte kommentieren mit `// Reason: ...`**, damit der Zweck klar ist.

## ⚙️ DevOps & Automatisierung
- **Formatierung und Linting** automatisieren (Pre-Commit Hooks via Husky oder ähnlichem).
- **Code Reviews verpflichtend** für alle Pull Requests.
- **Continuous Integration Pipelines** müssen:
  - Build
  - Unit-Tests
  - Lint-Checks
  - gegebenenfalls Security-Scans (z. B. OWASP Dependency-Check) durchführen.

## 🧠 AI-Nutzung & Verhalten
- **Fehlenden Kontext immer nachfragen**, nie Annahmen treffen.
- **Keine Halluzinationen:** Nur verifizierte Standardpakete und Frameworks verwenden.
- **Dateipfade und Modulnamen prüfen**, bevor sie im Code oder Test verwendet werden.
- **Existierenden Code nur ändern**, wenn explizit im `TASK.md` vorgegeben oder wenn ein kritischer Bugfix erforderlich ist.


Coding standards, domain knowledge, and preferences that AI should follow.