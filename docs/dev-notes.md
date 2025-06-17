# 🛠 Vývojářská dokumentace – Krypto nástroje

Tento dokument slouží jako vývojářský popis projektu. Obsahuje architekturu aplikace, popis jednotlivých tříd a komponent, bezpečnostní rozhodnutí a další technické detaily.

---

## 🧱 Architektura

Projekt se skládá ze dvou částí:

- **Backend (Java + Spring Boot)** – poskytuje REST API pro kryptografické operace (hashování, ověřování, šifrování, dešifrování, historie)
- **Frontend (Vue 3 + Vite)** – jednoduché uživatelské rozhraní pro zadávání a zobrazování výsledků

Frontend a backend spolu komunikují pomocí HTTP. Vue frontend může běžet samostatně (`npm run dev`) nebo být zabalen a nasazen společně s backendem jako součást Spring Boot JAR (pomocí Dockeru). Posílá požadavky na `/api/*`.

---

## 📦 Backend – Hlavní komponenty

### `CryptoService` / `CryptoServiceImpl`

Obsahuje metody:
- `encrypt(String)` – AES-GCM šifrování (generuje náhodný IV pro každé volání)
- `decrypt(String)` – dešifrování s načtením IV z prefixu
- `hashPassword(String)` – hashuje pomocí Argon2id (parametry: memory=65536, iterations=3, parallelism=1)
- `verifyPassword(String, String)` – ověřuje vstup vůči existujícímu hashi

Šifrovací klíč (`aesKey`) se generuje náhodně při startu aplikace (`@PostConstruct`).  
**IV se již správně generuje nově při každém šifrování.**

### `CryptoController`

REST controller, který vystavuje API:
- `POST /api/encrypt`
- `POST /api/decrypt`
- `POST /api/hash`
- `POST /api/verify`
- `GET /api/history`

Využívá třídu `CryptoOperationRepository` pro ukládání historie operací.

### `CryptoOperation` + `CryptoOperationRepository`

- `@Entity` třída využívající anotace `@Getter` a `@Setter` z Lomboku pro generování přístupových metod.
  - Konstruktory (bezparametrický a se všemi parametry) jsou implementovány ručně, bez použití `@NoArgsConstructor` a `@AllArgsConstructor`.
- Slouží k uchování typu operace, vstupu, výstupu a timestampu
- Ukládá se do databáze pomocí Spring Data JPA  
Identifikátor `id` je anotován jako primární klíč s automatickou generací pomocí `GenerationType.IDENTITY`.

---

## 🎨 Frontend (Vue 3)

Hlavní komponenta: `App.vue`  
Obsahuje vstupní pole a tlačítka pro spuštění jednotlivých operací (hash, verify, encrypt, decrypt).  
Komunikace s backendem probíhá pomocí `axios` (verze 1.9.0) volání na `/api/*`.

Konfigurace pomocí Vite (`vite.config.js`) – vývojový server běží samostatně na `localhost:5173`. Pro produkční režim se frontend sestaví (`npm run build`) a výstup se kopíruje do `src/main/webapp/` pro zahrnutí do výsledného JAR.

---

## 🔐 Kryptografie

- **Argon2id** – doporučený algoritmus pro ukládání hesel, odolný proti GPU útokům
  - Salt je generována automaticky
  - Výstupní formát:
    ```
    $argon2id$v=19$m=8192,t=2,p=1$salt$hash
    ```
    - `m=8192` – využití 8 MB paměti  
    - `t=2` – počet iterací (průchodů)  
    - `p=1` – paralelismus (počet vláken)
- **AES-GCM (128bit)** – moderní symetrická šifra s autentizací
  - IV = 12 bajtů, generován náhodně při každé šifře
  - IV je uložen v Base64 výstupu spolu se šifrovaným obsahem

---

## 🧪 Testování

Testováno ručně i automaticky (včetně edge-case vstupů):
- Ověření encrypt → decrypt roundtrip
- Hash a verify kombinace
- Ošetření chyb při špatných vstupech

Kromě ručního testování je součástí projektu i testovací třída `CryptoServiceImplTest`, která ověřuje funkčnost metod `encrypt`, `decrypt`, `hashPassword` a `verifyPassword` pomocí JUnit.

---

## 🧰 Použité knihovny a nástroje

- Java 17, Spring Boot
- Argon2 Java binding (`de.mkammerer.argon2`)
- AES-GCM (`javax.crypto`)
- Lombok pro generování boilerplate kódu
- Vue 3, Vite
- Vite Plugin Vue DevTools
- Axios (1.9.0) – HTTP klient ve frontendové části

---

## 🔜 Možnosti rozšíření

- Přidání asymetrického šifrování (např. RSA)
- Podpora ukládání uživatelů a login flow
- Předvyplněné příklady pro frontend
- Přehlednější zobrazení historie

---

## 🐳 Nasazení s Dockerem

Projekt je možné sestavit a spustit pomocí Dockeru jako samostatnou aplikaci (frontend + backend v jednom).

### Build a spuštění:

```bash
docker build -t krypto-app .
docker run -p 8080:8080 krypto-app
```

Výsledná aplikace je dostupná na `http://localhost:8080` a všechny API volání fungují přes `/api/*`.

Dockerfile využívá třífázový build:
1. Sestavení frontendu pomocí Node + Vite
2. Build Spring Boot JAR (`app.jar`)
3. Spuštění JAR pomocí `openjdk:17` bez potřeby externího Tomcatu

---

## ✅ Finální spojení frontendu a backendu

Frontend (Vite + Vue) je nyní úspěšně zabalen a nasazen jako součást backendu pomocí Dockeru. Byly provedeny následující kroky pro funkční propojení:

- Do `vite.config.js` přidán `base: './'` pro relativní cesty.
- Výstupní složka `dist/` z frontendu je v Dockerfile kopírována do `src/main/webapp/`.
- Pomocí konfigurace v `build.gradle` je obsah `webapp` zahrnut do výsledného JAR:
  ```groovy
  bootJar {
      enabled = true
      archiveFileName = "app.jar"
      from("src/main/webapp") {
          into("static")
      }
  }
  ```
- Spring Boot automaticky servíruje frontend z cesty `/`.
- Testováno úspěšně lokálně i na Railway – vše funguje.

🎉 Aplikace je nyní plně funkční jako monolitický Docker kontejner bez potřeby proxy nebo odděleného frontend serveru.