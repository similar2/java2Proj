<template>
  <div class="data-collection">
    <h1>Data Collection</h1>
    <p>Click the button below to collect Java-tagged threads from Stack Overflow.</p>
    <button @click="collectData" :disabled="loading">
      {{ loading ? 'Collecting...' : 'Collect Data' }}
    </button>
    <p v-if="message" class="status">{{ message }}</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'DataCollection',
  data() {
    return {
      loading: false,
      message: '',
    };
  },
  methods: {
    async collectData() {
      this.loading = true;
      this.message = '';
      try {
        const response = await axios.post('/api/data/collect');
        this.message = response.data.message || 'Data collected successfully!';
      } catch (error) {
        this.message = 'Error collecting data. Please try again later.';
        console.error(error);
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.data-collection {
  text-align: center;
  padding: 20px;
}

button {
  padding: 10px 20px;
  font-size: 16px;
  background-color: #007BFF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #ccc;
}

.status {
  margin-top: 10px;
  font-size: 14px;
  color: green;
}
</style>
