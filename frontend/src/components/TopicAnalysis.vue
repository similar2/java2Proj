<template>
  <div class="topic-analysis">
    <h1 class="title">Java Topic Analysis</h1>
    <div class="chart-container" ref="chart"></div>
    <div v-if="loading" class="loading-overlay">
      <div class="spinner"></div>
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

    const fetchTopics = async () => {
      try {
        const response = await axios.get('api/topic/top', {
          params: { size: 10 },
        });

        if (response.data && response.data.code === 0) {
          topics.value = response.data.data;
          renderChart();
        } else {
          console.error('Error fetching topics: Unexpected response structure');
        }
      } catch (error) {
        console.error('Error fetching topics:', error);
      } finally {
        loading.value = false;
      }
    };

    const renderChart = () => {
      const myChart = echarts.init(chart.value);

      const option = {
        title: {
          text: 'Top Java Topics',
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
    };

    onMounted(() => {
      fetchTopics();
    });

    return {
      topics,
      chart,
      loading,
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
  padding: 20px;
  font-family: 'Arial', sans-serif;
}

/* 标题样式 */
.title {
  font-size: 24px;
  color: #007BFF;
  margin-bottom: 20px;
  text-align: center;
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
</style>
