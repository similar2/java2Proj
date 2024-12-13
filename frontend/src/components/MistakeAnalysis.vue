<template>
  <div>
    <h1>Mistake Analysis</h1>
    <p>This page analyzes common mistakes in Java code based on the data collected.</p>

    <div>
      <label for="type">Select Type: </label>
      <select v-model="selectedType" id="type">
        <option value="exception">Exception</option>
        <option value="error">Error</option>
      </select>

      <label for="size">Top N: </label>
      <input type="number" v-model="topN" id="size" min="1" />

      <button @click="fetchData">Get Top N Mistakes</button>
    </div>

    <table v-if="exceptionData.length > 0">
      <thead>
      <tr>
        <th>Name</th>
        <th>Type</th>
        <th>Frequency</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(item, index) in exceptionData" :key="index">
        <td>{{ item.name }}</td>
        <td>{{ item.type }}</td>
        <td>{{ item.frequency }}</td>
      </tr>
      </tbody>
    </table>

    <p v-else>No data available</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'MistakeAnalysis',
  data() {
    return {
      selectedType: 'exception', // Default type is 'exception'
      topN: 10, // Default to top 10 results
      exceptionData: [], // Holds the response data from the API
    };
  },
  methods: {
    // Method to fetch data based on selected type and size
    fetchData() {
      const type = this.selectedType;
      const size = this.topN;

      axios
          .get(`/api/exception/top`, { params: { size, type } })
          .then((response) => {
            if (response.data.code === 0) {
              this.exceptionData = response.data.data;
            } else {
              alert('Failed to fetch data');
            }
          })
          .catch((error) => {
            console.error('Error fetching data:', error);
          });
    },
  },
  mounted() {
    // Fetch data when the component is mounted
    this.fetchData();
  },
};
</script>

<style scoped>
/* Style for the MistakeAnalysis page */
h1 {
  color: #FF5722;
  margin-bottom: 20px;
}

table {
  width: 100%;
  margin-top: 20px;
  border-collapse: collapse;
}

th, td {
  padding: 8px 12px;
  border: 1px solid #ddd;
}

th {
  background-color: #f2f2f2;
  text-align: left;
}

button {
  background-color: #4CAF50;
  color: white;
  padding: 10px;
  border: none;
  cursor: pointer;
  margin-top: 10px;
}

button:hover {
  background-color: #45a049;
}

label {
  margin-right: 10px;
}
</style>
