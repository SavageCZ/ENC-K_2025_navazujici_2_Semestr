# 🛠 Vývojářská dokumentace – Krypto nástroje

Tento dokument slouží jako vývojářský popis projektu. Obsahuje architekturu aplikace, popis jednotlivých tříd a komponent, bezpečnostní rozhodnutí a další technické detaily.

---

## 🧱 Architektura

Projekt se skládá ze dvou částí:

- **Backend (Java + Spring Boot)** – poskytuje REST API pro kryptografické operace (hashování, ověřování, šifrování, dešifrování, historie)
- **Frontend (Vue 3 + Vite)** – jednoduché uživatelské rozhraní pro zadávání a zobrazování výsledků

Frontend a backend spolu komunikují pomocí HTTP, Vue frontend běží typicky samostatně (`npm run dev`) a posílá požadavky na `/api/*`.

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

Konfigurace pomocí Vite (`vite.config.js`) – žádné přesměrování, běží samostatně na `localhost:5173`.

---

## 🔐 Kryptografie

- **Argon2id** – doporučený algoritmus pro ukládání hesel, odolný proti GPU útokům
  - Salt je generována automaticky
  - Parametry: 65536kB memory, 3 iterace, 1 vlákno
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