<template>
  <div class="topic-analysis">
    <h1 class="title">Java Topic Analysis</h1>

    <!-- Input for selecting the number of top topics -->
    <div class="input-container">
      <label for="top-n">Top N Topics:</label>
      <input
          type="number"
          v-model="topN"
          id="top-n"
          min="1"
          placeholder="Enter number"
          @change="fetchTopics"
      />
    </div>

    <!-- Loading indicator -->
    <div v-if="loading" class="loading-overlay">
      <div class="spinner"></div>
    </div>

    <!-- Chart container -->
    <div v-if="viewMode === 'chart'" class="chart-container" ref="chart"></div>

    <!-- Table displaying topics -->
    <div v-if="viewMode === 'table' && !loading && topics.length > 0" class="topics-table">
      <table>
        <thead>
        <tr>
          <th>Topic</th>
          <th>Frequency</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(topic, index) in topics" :key="index">
          <td>{{ topic.tag }}</td>
          <td>{{ topic.frequency }}</td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- Message when no data is available -->
    <p v-else-if="!loading && topics.length === 0">No data available</p>

    <!-- View mode toggle -->
    <div class="toggle-container">
      <button
          @click="toggleView('table')"
          :class="{ active: viewMode === 'table' }">Table View</button>
      <button
          @click="toggleView('chart')"
          :class="{ active: viewMode === 'chart' }">Chart View</button>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import axios from 'axios';
import { ref, onMounted } from 'vue';

export default {
  name: 'TopicAnalysis',
  setup() {
    const topics = ref([]);
    const chart = ref(null);
    const loading = ref(true);
    const topN = ref(10);  // Default value for top N topics
    const viewMode = ref('table');  // Default view mode is 'table'

    // Fetch topics based on the input top N value
    const fetchTopics = async () => {
      loading.value = true;
      try {
        const response = await axios.get('api/topic/top', {
          params: { size: topN.value },
        });

        if (response.data && response.data.code === 0) {
          topics.value = response.data.data;
          if (viewMode.value === 'chart') {
            renderChart(); // Only render chart if view is 'chart'
          }
        } else {
          console.error('Error fetching topics: Unexpected response structure');
        }
      } catch (error) {
        console.error('Error fetching topics:', error);
      } finally {
        loading.value = false;
      }
    };

    // Render the chart after fetching data
    const renderChart = () => {
      if (chart.value) {
        const myChart = echarts.init(chart.value);

        const option = {
          title: {
            text: `Top ${topN.value} Java Topics`,
            left: 'center',
            textStyle: {
              color: '#333',
              fontWeight: 'bold',
            },
          },
          tooltip: {
            trigger: 'item',
            formatter: '{b}: {c}',
          },
          xAxis: {
            type: 'category',
            data: topics.value.map((topic) => topic.tag),
            axisLabel: {
              color: '#666',
            },
            axisLine: {
              lineStyle: {
                color: '#ccc',
              },
            },
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              color: '#666',
            },
            axisLine: {
              lineStyle: {
                color: '#ccc',
              },
            },
          },
          series: [
            {
              data: topics.value.map((topic) => topic.frequency),
              type: 'bar',
              barWidth: '60%',
              itemStyle: {
                color: '#007BFF',
              },
            },
          ],
        };

        myChart.setOption(option);
      }
    };

    // Toggle between chart and table view
    const toggleView = (mode) => {
      viewMode.value = mode;
      if (mode === 'chart') {
        // Clear chart data when switching to chart
        if (chart.value) {
          const myChart = echarts.getInstanceByDom(chart.value);
          if (myChart) {
            myChart.dispose(); // Dispose of the current chart instance
          }
        }
        fetchTopics();  // Fetch data and render chart
      }
    };

    // Fetch data when mounted
    onMounted(() => {
      fetchTopics();
    });

    return {
      topics,
      chart,
      loading,
      topN,
      fetchTopics,
      viewMode,
      toggleView,
    };
  },
};
</script>

<style scoped>
/* 页面布局 */
.topic-analysis {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px;
  font-family: 'Arial', sans-serif;
  max-width: 900px;
  margin: auto;
  background-color: #f7f7f7;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 标题样式 */
.title {
  font-size: 28px;
  color: #007BFF;
  margin-bottom: 30px;
  text-align: center;
}

/* Input for Top N topics */
.input-container {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

label {
  font-weight: bold;
  margin-right: 10px;
}

input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
  width: 120px;
  transition: border-color 0.3s;
}

input:focus {
  border-color: #007BFF;
}

/* Button to toggle between chart and table */
.toggle-container {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

button {
  padding: 12px 24px;
  border: 2px solid #007BFF;
  border-radius: 5px;
  background-color: #fff;
  color: #007BFF;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s, color 0.3s, transform 0.2s;
}

button.active {
  background-color: #007BFF;
  color: #fff;
}

button:hover {
  background-color: #f1f1f1;
  transform: scale(1.05);
}

/* 图表容器 */
.chart-container {
  width: 100%;
  max-width: 800px;
  height: 400px;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  position: relative;
}

/* 加载动画 */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #ccc;
  border-top-color: #007BFF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Table displaying topics */
.topics-table table {
  width: 100%;
  margin-top: 20px;
  border-collapse: collapse;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.topics-table th,
.topics-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.topics-table th {
  background-color: #007BFF;
  color: white;
}

.topics-table tr:hover {
  background-color: #f1f1f1;
}

/* No data message */
p {
  font-size: 18px;
  color: #999;
  text-align: center;
}
</style>
