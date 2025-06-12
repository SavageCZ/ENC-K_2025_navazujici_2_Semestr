<template>
  <div style="display: flex; align-items: center; justify-content: center; min-height: 100vh">
    <div style="padding: 2rem; font-family: sans-serif; max-width: 600px; background-color: rgba(0,0,0,0.6); border-radius: 12px;">
      <h2>Krypto nástroje</h2>

      <input v-model="input" placeholder="Zadej text" style="width: 100%; padding: 0.5rem; margin-bottom: 1rem" />

      <div style="margin-bottom: 1rem">
        <label for="plaintext">Plaintext:</label>
        <textarea id="plaintext" v-model="plaintext" placeholder="Zadej text k zašifrování" style="width: 100%; padding: 0.5rem;"></textarea>

        <label for="ciphertext" style="margin-top: 1rem; display: block;">Ciphertext:</label>
        <textarea id="ciphertext" v-model="ciphertext" placeholder="Zadej zašifrovaný text" style="width: 100%; padding: 0.5rem;"></textarea>

        <label for="key" style="margin-top: 1rem; display: block;">Klíč:</label>
        <input id="key" v-model="key" placeholder="Zadej klíč (např. 16 znaků)" style="width: 100%; padding: 0.5rem;" />
      </div>

      <div style="display: flex; gap: 1rem; flex-wrap: wrap">
        <button @click="hash">Hashuj</button>
        <button @click="verify">Ověř</button>
        <button @click="encrypt">Šifruj</button>
        <button @click="decrypt">Dešifruj</button>
      </div>

      <div style="margin-top: 1rem">
        <label>Hash pro ověření:</label>
        <input v-model="hashToVerify" placeholder="Zadej hash" style="width: 100%; padding: 0.5rem;" />
      </div>

      <p style="margin-top: 1rem"><strong>Výsledek:</strong><br>{{ result }}</p>
      <div style="margin-top: 2rem">
        <h3>Historie operací</h3>
        <ul>
          <li v-for="item in history" :key="item.id">
            {{ item.timestamp }} — <strong>{{ item.operation }}</strong>: {{ item.input }} → {{ item.result }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const input = ref('')
const result = ref('')
const hashToVerify = ref('')
const history = ref([])

const plaintext = ref('')
const ciphertext = ref('')
const key = ref('')

const loadHistory = async () => {
  const res = await axios.get('/api/history')
  history.value = res.data
}

const hash = async () => {
  const res = await axios.post('/api/hash', { raw: input.value })
  result.value = res.data.hash
  await loadHistory()
}

const verify = async () => {
  const res = await axios.post('/api/verify', {
    raw: input.value,
    hash: hashToVerify.value
  })
  result.value = res.data.valid ? '✅ HESLO SEDÍ' : '❌ NESOUHLASÍ'
  await loadHistory()
}

const encrypt = async () => {
  const res = await axios.post('/api/encrypt', { text: plaintext.value, key: key.value })
  result.value = res.data.encrypted
  ciphertext.value = res.data.encrypted
  await loadHistory()
}

const decrypt = async () => {
  const res = await axios.post('/api/decrypt', {
    encrypted: ciphertext.value,
    key: key.value
  })
  result.value = res.data.decrypted
  plaintext.value = res.data.decrypted
  await loadHistory()
}
</script>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: sans-serif;
  background: url('./assets/images.png') no-repeat center center fixed;
  background-size: cover;
  color: white;
}

button {
  padding: 0.5rem 1rem;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover {
  background-color: #2563eb;
}
</style>