# KryptoNavazujiciApplication

Projekt poskytuje jednoduchou webovou aplikaci pro práci s kryptografickými funkcemi (hashování, šifrování, ověřování), založený na Spring Boot backendu a Vue frontendu.

## 🛠 Technologie

- **Backend:** Java, Spring Boot
- **Frontend:** Vue 3 + Vite
- **HTTP klient:** Axios
- **Build nástroje:** Gradle, npm

---

## 🧪 Funkce aplikace

Backend používá hashování pomocí algoritmu Argon2id a šifrování pomocí AES-GCM s 128bitovým klíčem. Pro každé šifrování je generován nový náhodný IV, který je součástí výsledného výstupu.

- Hashování zadaného textu
- Ověření *hashovaného* hesla
- Šifrování a dešifrování textu

---

## 🚀 Spuštění projektu

### 🐳 Nasazení přes Docker (produkční režim)

Aplikaci lze spustit jako celek (backend + zabalený frontend) pomocí Dockeru:

```bash
docker build -t krypto-app .
docker run -p 8080:8080 krypto-app
```

Po spuštění bude dostupná na:

```
http://localhost:8080
```

---

### 🧪 Lokální vývoj

#### Backend (Spring Boot)

```bash
./gradlew bootRun
```

#### Frontend (Vue 3 + Vite)

```bash
cd frontend
npm install
npm run dev
```

Frontend poběží samostatně na:

```
http://localhost:5173

---

## 🔐 Poznámky k šifrování (AES-GCM)

- **Délka vstupu:** Pro AES-GCM je potřeba, aby vstupní text (plaintext) i klíč měly délku **přesně 16 znaků**.
- **Šifrování:** Před šifrováním je nutné zadat čistý text a 16znakový klíč. Při odeslání se automaticky vygeneruje IV a výsledkem je `ciphertext`, který obsahuje IV i data.
- **Dešifrování:**
  - Nejprve je třeba odstranit předchozí výstup z pole „text“.
  - Do pole se vloží `ciphertext` získaný ze šifrování.
  - Po zadání stejného klíče (16 znaků) lze kliknutím na **Dešifrovat** získat zpět původní plaintext.
```