# KryptoNavazujiciApplication

Projekt poskytuje jednoduchou webovou aplikaci pro práci s kryptografickými funkcemi (hashování, šifrování, ověřování), založený na Spring Boot backendu a Vue frontendu.

## 🛠 Technologie

- **Backend:** Java, Spring Boot
- **Frontend:** Vue 3 + Vite
- **HTTP klient:** Axios
- **Build nástroje:** Gradle, npm

---

## 🧪 Funkce aplikace

Backend používá hashování pomocí algoritmu Argon2 a šifrování pomocí AES-GCM (128-bit klíč).

- Hashování zadaného textu
- Ověření *hashovaného* hesla
- Šifrování a dešifrování textu

---

## 🚀 Spuštění projektu

### Backend (Spring Boot)

```bash
./gradlew bootRun
```

### Frontend (Vue 3 + Vite)

```bash
cd frontend
npm install
npm run dev
```