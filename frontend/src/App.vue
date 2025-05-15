<template>
  <div style="padding: 2rem; font-family: sans-serif; max-width: 600px">
    <h2>Krypto nástroje</h2>

    <input v-model="input" placeholder="Zadej text" style="width: 100%; padding: 0.5rem; margin-bottom: 1rem" />

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
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const input = ref('')
const result = ref('')
const hashToVerify = ref('')

const API = 'http://localhost:8081/api'

const hash = async () => {
  const res = await axios.post(`${API}/hash`, { raw: input.value })
  result.value = res.data.hash
}

const verify = async () => {
  const res = await axios.post(`${API}/verify`, {
    raw: input.value,
    hash: hashToVerify.value
  })
  result.value = res.data.valid ? '✅ HESLO SEDÍ' : '❌ NESOUHLASÍ'
}

const encrypt = async () => {
  const res = await axios.post(`${API}/encrypt`, { text: input.value })
  result.value = res.data.encrypted
}

const decrypt = async () => {
  const res = await axios.post(`${API}/decrypt`, { encrypted: input.value })
  result.value = res.data.decrypted
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