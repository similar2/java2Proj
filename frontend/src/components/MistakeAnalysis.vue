<template>
  <div class="container">
    <div class="card">
      <h1>Mistake Analysis</h1>
      <p>This page analyzes common mistakes in Java code based on the data collected.</p>

      <div class="filters">
        <div class="filter-item">
          <label for="type">Select Type:</label>
          <select v-model="selectedType" id="type">
            <option value="exception">Exception</option>
            <option value="error">Error</option>
          </select>
        </div>

        <div class="filter-item">
          <label for="size">Top N:</label>
          <input type="number" v-model="topN" id="size" min="1" />
        </div>

        <button class="fetch-button" @click="fetchData" :disabled="loading">
          <span v-if="loading" class="loading">Loading...</span>
          <span v-else>Get Top N Mistakes</span>
        </button>
      </div>

      <!-- View Toggle Buttons -->
      <div class="view-toggle">
        <button @click="viewMode = 'table'; fetchData()" :class="{ active: viewMode === 'table' }">Table View</button>
        <button @click="viewMode = 'chart'; fetchData()" :class="{ active: viewMode === 'chart' }">Chart View</button>
      </div>

      <!-- Conditionally render Table or Chart -->
      <div v-if="viewMode === 'table'">
        <div v-if="exceptionData.length > 0">
          <table class="results-table">
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
        </div>
        <p v-else-if="!loading">No data available</p>
      </div>

      <div v-if="viewMode === 'chart'">
        <!-- ECharts component -->
        <div ref="chart" class="chart-container"></div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import axios from 'axios';

export default {
  name: 'MistakeAnalysis',
  data() {
    return {
      selectedType: 'exception',
      topN: 10,
      exceptionData: [],
      loading: false,
      viewMode: 'table', // Default to table view
    };
  },
  methods: {
    fetchData() {
      const type = this.selectedType;
      const size = this.topN;

      this.loading = true;

      axios
          .get(`/api/exception/top`, { params: { size, type } })
          .then((response) => {
            if (response.data.code === 0) {
              this.exceptionData = response.data.data;
              // Call the function to render the chart when data is fetched
              this.renderChart();
            } else {
              alert('Failed to fetch data');
            }
          })
          .catch((error) => {
            console.error('Error fetching data:', error);
          })
          .finally(() => {
            this.loading = false;
          });
    },

    renderChart() {
      // Prepare data for the chart
      const labels = this.exceptionData.map(item => item.name);
      const data = this.exceptionData.map(item => item.frequency);

      // ECharts options
      const option = {
        title: {
          text: 'Mistake Frequency Analysis',
          left: 'center',
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}',
        },
        xAxis: {
          type: 'category',
          data: labels,
        },
        yAxis: {
          type: 'value',
        },
        series: [
          {
            data: data,
            type: 'bar',
            itemStyle: {
              color: '#FF5722',
            },
          },
        ],
      };

      // Initialize the chart
      const chart = echarts.init(this.$refs.chart);
      chart.setOption(option);
    },
  },
  mounted() {
    this.fetchData();
  },
};
</script>

<style scoped>
/* Container styles */
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f4f7fa;
}

/* Card styling */
.card {
  background-color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  padding: 30px;
  width: 100%;
  max-width: 800px;
  margin: 20px;
  transition: transform 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
}

h1 {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 20px;
  font-weight: bold;
}

p {
  text-align: center;
  margin-bottom: 20px;
  color: #666;
}

.filters {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.filter-item {
  display: flex;
  flex-direction: column;
  margin-right: 15px;
}

label {
  font-weight: bold;
  margin-bottom: 5px;
}

select, input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 120px;
}

button {
  background-color: #FF5722;
  color: white;
  padding: 10px 18px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  width: auto;
  margin-top: 10px;
  transition: transform 0.3s ease, background-color 0.3s ease;
}

button:disabled {
  background-color: #ddd;
  cursor: not-allowed;
}

button:hover:not(:disabled) {
  background-color: #FF7043;
  transform: scale(1.05);
}

.loading {
  font-size: 14px;
  font-style: italic;
}

.results-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  border-radius: 8px;
  overflow: hidden;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f2f2f2;
  font-weight: bold;
}

tbody tr:nth-child(odd) {
  background-color: #f9f9f9;
}

tbody tr:hover {
  background-color: #f1f1f1;
}

.view-toggle {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.view-toggle button {
  background-color: #f0f0f0;
  color: #333;
  padding: 8px 16px;
  border: 1px solid #ddd;
  cursor: pointer;
  font-size: 14px;
  margin: 0 5px;
  transition: background-color 0.3s ease;
}

.view-toggle button.active {
  background-color: #FF5722;
  color: white;
}

.view-toggle button:hover {
  background-color: #FF7043;
}

@media (max-width: 768px) {
  .filters {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-item {
    margin-bottom: 10px;
  }

  button {
    width: auto;
  }
}

.chart-container {
  width: 100%;
  height: 400px;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  position: relative;
}
</style>
