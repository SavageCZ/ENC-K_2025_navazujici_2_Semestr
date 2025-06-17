# 👤 Uživatelská dokumentace

Tato aplikace umožňuje provádět kryptografické operace přes jednoduché webové rozhraní – konkrétně:
- **Hashování** vstupu pomocí algoritmu **Argon2id**
- **Šifrování / dešifrování** pomocí algoritmu **AES**

---

## 🧪 Jak aplikaci spustit

### ✅ Nasazeno online:
Aplikace je nasazena zde:  
[https://enc-k2025navazujici2semestr-production.up.railway.app](https://enc-k2025navazujici2semestr-production.up.railway.app)

Pokud by v budoucnu došlo k výpadku, kontaktujte autora projektu přes MS Teams – příčinou může být překročení limitů paměti platformy Railway (512 MB).

---

### ✅ Lokálně přes Docker:

1. Z rootu projektu spusť:
   ```bash
   docker build -t krypto-app .
   docker run -p 8080:8080 krypto-app
   ```
2. Otevři prohlížeč a jdi na:
   ```
   http://localhost:8080
   ```

---

## 🧭 Jak používat aplikaci

Po otevření webové aplikace se zobrazí jedno vstupní pole a 3 akční tlačítka:

1. **Zadejte vstupní text**
   - např. `tajna_zprava`

2. **Vyberte operaci:**
   - `Hashuj` – vypočítá bezpečný hash pomocí Argon2id
   - `Zašifruj` – zašifruje text pomocí AES
   - `Dešifruj` – dešifruje předtím zašifrovaný výstup (ve správném formátu)

ℹ️ Při šifrování a dešifrování musí mít klíč délku přesně 16 znaků (AES-128). Pokud zadáte kratší klíč, aplikace jej automaticky doplní null znaky (`\0`) na požadovanou délku. V případě, že do pole vložíte výstup předchozí operace (např. plaintext místo ciphertextu), nezapomeňte ho nejdříve odstranit ručně.

3. **Zobrazí se výsledek** – buď zakódovaný hash, šifrovaný text, nebo výstup dešifrování

4. **V případě ověřování hashe**
   - Pokud vstupní text odpovídá zadanému hashi, zobrazí se potvrzení `✅ HESLO SEDÍ`
   - Jinak aplikace hlásí, že heslo neodpovídá

---

## 📌 Poznámky

- Výstup z `hash()` obsahuje kromě samotného hashe i parametry použité pro výpočet (typ, verzi, sůl atd.)
- Šifrování AES používá náhodný IV, výstupní formát je vhodný k uložení i dešifrování
- AES šifrování/dešifrování očekává, že **vstupní text i klíč budou přesně 16 znaků dlouhé** – kratší hodnoty je potřeba doplnit (např. mezerami), delší zkrátit.
- Před spuštěním dešifrování je nutné **ručně odstranit obsah vstupního pole** (pokud tam je původní plaintext) a vložit zašifrovaný text.

---