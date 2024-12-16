<template>
  <div class="container">
    <h1 class="title">Answer Quality Analysis</h1>
    <p class="intro">This page will analyze the quality of answers.</p>

    <!-- 切换按钮 -->
    <div class="toggle-buttons">
      <button
          :class="{ active: currentDataType === 'reputation' }"
          @click="switchDataType('reputation')"
      >
        Reputation Data
      </button>
      <button
          :class="{ active: currentDataType === 'custom' }"
          @click="switchDataType('custom')"
      >
        Custom Data
      </button>
      <button
          :class="{ active: currentDataType === 'time' }"
          @click="switchDataType('time')"
      >
        Time Data
      </button>
    </div>

    <!-- 数据展示表格 -->
    <h2>{{ currentTableTitle }}</h2>
    <div v-if="loading" class="loading">
      <span class="spinner"></span> Loading data...
    </div>

    <div v-else>
      <div v-if="dataList.length > 0">
        <table>
          <thead>
          <tr>
            <th v-for="(header, index) in tableHeaders" :key="index">{{ header }}</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(item, index) in dataList" :key="index">
            <td v-if="currentDataType === 'reputation'">{{ handleZeroValue(item.reputationScore) }}</td>
            <td v-if="currentDataType === 'reputation'">{{ handleZeroValue(item.averageScore) }}</td>
            <td v-if="currentDataType === 'custom'">{{ handleZeroValue(item.accountAgeDays) }}</td>
            <td v-if="currentDataType === 'custom'">{{ handleZeroValue(item.qualityMetric) }}</td>
            <td v-if="currentDataType === 'time'">{{ handleZeroValue(item.elapsedMinutes) }}</td>
            <td v-if="currentDataType === 'time'">{{ handleZeroValue(item.totalAnswers) }}</td>
            <td v-if="currentDataType === 'time'">{{ handleZeroValue(item.acceptedAnswers) }}</td>
            <td v-if="currentDataType === 'time'">{{ handleZeroValue(item.acceptanceRate) }}</td>
          </tr>
          </tbody>
        </table>
      </div>
      <div v-else class="no-data">No data available.</div>
    </div>

    <!-- 分页控件 -->
    <div class="pagination">
      <button @click="changePage('prev')" :disabled="currentPage <= 1" class="pagination-btn">Previous</button>
      <span>Page:</span>
      <input
          v-model.number="inputPage"
          @keyup.enter="jumpToPage"
          type="number"
          min="1"
          placeholder="Page number"
          class="pagination-input"
      />
      <button @click="jumpToPage" class="pagination-btn">Jump</button>
      <button @click="changePage('next')" class="pagination-btn">Next</button>
    </div>

    <!-- 绘图按钮 -->
    <div class="chart-btn-container">
      <button @click="generateChart" class="chart-btn">Generate Trend Chart</button>
    </div>

    <!-- ECharts 图表容器 -->
    <div id="chart-container" style="width: 100%; height: 400px;"></div>
  </div>
</template>

<script>
import axios from "axios";
import * as echarts from "echarts";

export default {
  name: "AnswerQuality",
  data() {
    return {
      currentDataType: "reputation", // 当前展示的数据类型: reputation, custom, time
      dataList: [], // 数据列表
      loading: false, // 加载状态
      currentPage: 1, // 当前页码
      pageSize: 10, // 每页数据量
      inputPage: 1, // 用户输入的页码
      tableHeaders: ["Score", "Average/Comment"], // 表格头部
    };
  },
  computed: {
    // 动态标题
    currentTableTitle() {
      return this.currentDataType === 'reputation' ? 'Reputation Analysis' : this.currentDataType === 'custom' ? 'Custom Analysis' : 'Time Analysis';
    },
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    // 切换数据类型
    switchDataType(type) {
      this.currentDataType = type;
      this.currentPage = 1; // 切换时重置页码
      this.updateTableHeaders();
      this.fetchData();
    },

    // 更新表头
    updateTableHeaders() {
      if (this.currentDataType === "reputation") {
        this.tableHeaders = ["Reputation Score", "Average Score"];
      } else if (this.currentDataType === "custom") {
        this.tableHeaders = ["Account Age (Days)", "Quality Metric"];
      } else if (this.currentDataType === "time") {
        this.tableHeaders = ["Elapsed Minutes", "Total Answers", "Accepted Answers", "Acceptance Rate"];
      }
    },

    // 请求数据
    async fetchData() {
      this.loading = true;
      let apiUrl = "";
      switch (this.currentDataType) {
        case "reputation":
          apiUrl = "/api/answer/reputation";
          break;
        case "custom":
          apiUrl = "/api/answer/custom";
          break;
        case "time":
          apiUrl = "/api/answer/time";
          break;
      }
      try {
        const response = await axios.get(apiUrl, {
          params: {
            pageSize: this.pageSize,
            currentPage: this.currentPage,
          },
        });
        if (response.data.code === 0) {
          this.dataList = response.data.data || [];
        } else {
          console.error("Error fetching data:", response.data.message);
        }
      } catch (error) {
        console.error(`Error fetching ${this.currentDataType} data:`, error);
      } finally {
        this.loading = false;
      }
    },

    // 上一页/下一页
    changePage(direction) {
      if (direction === "prev" && this.currentPage > 1) {
        this.currentPage--;
      } else if (direction === "next") {
        this.currentPage++;
      }
      this.fetchData();
    },

    // 跳转到输入页码
    jumpToPage() {
      if (this.inputPage >= 1) {
        this.currentPage = this.inputPage;
        this.fetchData();
      } else {
        alert("Please enter a valid page number!");
      }
    },

    // 处理0值展示
    handleZeroValue(value) {
      return value === undefined || value === null ? '0' : value;
    },

    // 生成趋势图
    async generateChart() {
      let apiUrl = "";
      let showRightYAxis = false; // 默认不显示右侧 Y 轴

      switch (this.currentDataType) {
        case "reputation":
          apiUrl = "/api/answer/reputation";
          break;
        case "custom":
          apiUrl = "/api/answer/custom";
          break;
        case "time":
          apiUrl = "/api/answer/time";
          showRightYAxis = true; // 如果是 time 类型，则显示右侧 Y 轴
          break;
      }

      try {
        const response = await axios.get(apiUrl, {
          params: {
            pageSize: 1000,
            currentPage: 1,
          },
        });

        if (response.data.code === 0) {
          let dataList = response.data.data || [];

          if (this.currentDataType === "custom") {
            dataList = dataList.sort((a, b) => a.accountAgeDays - b.accountAgeDays);
          }

          let xAxisData = [];
          let yAxisData = [];
          let totalAnswersData = [];
          let accumulatedAnswersData = [];  // 用来保存累积的 totalAnswers
          let acceptedAnswersData = [];
          let xAxisName = '';
          let yAxisName = '';

          if (this.currentDataType === "reputation") {
            xAxisName = 'reputationScore';
            yAxisName = 'averageScore';
          } else if (this.currentDataType === "custom") {
            xAxisName = 'accountAgeDays';
            yAxisName = 'qualityMetric';
          } else if (this.currentDataType === "time") {
            xAxisName = 'elapsedMinutes';
            yAxisName = 'acceptanceRate';
          }

          let totalAnswersAccumulator = 0; // 累计的 totalAnswers

          for (let i = 0; i < dataList.length; i += 25) {
            let chunk = dataList.slice(i, i + 25);

            let xAvg = chunk.reduce((sum, item) => {
              let value = item[xAxisName];
              return (value !== undefined && value !== null && !isNaN(value)) ? sum + value : sum;
            }, 0) / chunk.length;

            let yAvg = chunk.reduce((sum, item) => {
              let value = item[yAxisName];
              return (value !== undefined && value !== null && !isNaN(value)) ? sum + value : sum;
            }, 0) / chunk.length;

            let totalAnswersSum = chunk.reduce((sum, item) => item.totalAnswers !== undefined ? sum + item.totalAnswers : sum, 0);
            let acceptedAnswersSum = chunk.reduce((sum, item) => item.acceptedAnswers !== undefined ? sum + item.acceptedAnswers : sum, 0);

            // 累积的 totalAnswers
            totalAnswersAccumulator += totalAnswersSum;

            if (!isNaN(xAvg) && !isNaN(yAvg)) {
              xAxisData.push(xAvg);
              yAxisData.push(yAvg);
              totalAnswersData.push(totalAnswersSum);
              accumulatedAnswersData.push(totalAnswersAccumulator); // 保存累积数据
              acceptedAnswersData.push(acceptedAnswersSum);
            }
          }

          const { slope, intercept } = this.linearRegression(xAxisData, yAxisData);

          let regressionLineData = xAxisData.map(x => slope * x + intercept);

          const chart = echarts.init(document.getElementById('chart-container'));

          const option = {
            title: {
              text: 'Answer Quality Trend',
              left: 'center',
            },
            tooltip: {
              trigger: 'item',
            },
            xAxis: {
              type: 'category',
              data: xAxisData,
              name: xAxisName.charAt(0).toUpperCase() + xAxisName.slice(1),
            },
            yAxis: [
              {
                type: 'value',
                name: yAxisName.charAt(0).toUpperCase() + yAxisName.slice(1),
              },
              showRightYAxis ? {
                type: 'value',
                name: 'Total/Accepted Answers',
                position: 'right',
                axisLine: { show: true },
                axisLabel: { formatter: '{value}' },
              } : null
            ].filter(Boolean), // 如果不需要右侧 Y 轴，则去掉它
            series: [
              {
                data: yAxisData,
                type: 'scatter',
                symbolSize: 8,
                itemStyle: {
                  color: '#007bff',
                },
              },
              {
                data: regressionLineData,
                type: 'line',
                smooth: true,
                lineStyle: {
                  color: '#ff6600',
                  width: 2,
                },
                symbol: 'none',
              },
              showRightYAxis ? {
                data: totalAnswersData,
                type: 'line',
                yAxisIndex: 1,
                lineStyle: {
                  color: '#34a853',
                  width: 2,
                },
                smooth: true,
                name: 'Total Answers',
              } : null,
              showRightYAxis ? {
                data: acceptedAnswersData,
                type: 'line',
                yAxisIndex: 1,
                lineStyle: {
                  color: '#ea4335',
                  width: 2,
                },
                smooth: true,
                name: 'Accepted Answers',
              } : null,
              showRightYAxis ? {
                data: accumulatedAnswersData,  // 添加累积的 totalAnswers 数据
                type: 'line',
                yAxisIndex: 1,
                lineStyle: {
                  color: '#f39c12',
                  width: 2,
                },
                smooth: true,
                name: 'Accumulated Total Answers',  // 累计答案数线
              } : null
            ].filter(Boolean), // 如果不需要右侧的线，则去掉它
            graphic: [
              {
                type: 'text',
                left: 'center',
                top: 'bottom',
                style: {
                  text: 'Regression Line',
                  fill: '#ff6600',
                  font: 'bold 16px sans-serif',
                },
                position: [0, 20],
                z: 200
              }
            ]
          };

          chart.setOption(option);
        } else {
          console.error("Error fetching data:", response.data.message);
        }
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    },

    linearRegression(x, y) {
      const n = x.length;
      const xSum = x.reduce((acc, xi) => acc + xi, 0);
      const ySum = y.reduce((acc, yi) => acc + yi, 0);
      const xSquaredSum = x.reduce((acc, xi) => acc + xi * xi, 0);
      const xySum = x.reduce((acc, xi, index) => acc + xi * y[index], 0);

      const slope = (n * xySum - xSum * ySum) / (n * xSquaredSum - xSum * xSum);
      const intercept = (ySum - slope * xSum) / n;

      return { slope, intercept };
    }




  },
};
</script>

<style scoped>
/* 样式 */
.container {
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.title {
  text-align: center;
  font-size: 36px;
  color: #333;
  margin-bottom: 10px;
}

.intro {
  text-align: center;
  font-size: 16px;
  color: #777;
  margin-bottom: 20px;
  font-style: italic;
}

.toggle-buttons {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.toggle-buttons button {
  margin: 0 10px;
  padding: 10px 20px;
  font-size: 16px;
  border: 1px solid #ccc;
  background-color: #f5f5f5;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.toggle-buttons button:hover {
  background-color: #007bff;
  color: white;
}

.toggle-buttons button.active {
  background-color: #007bff;
  color: white;
}

.table-container {
  margin-top: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

table th,
table td {
  padding: 12px 15px;
  text-align: center;
  border: 1px solid #ccc;
}

table tr:nth-child(even) {
  background-color: #f9f9f9;
}

table th {
  background-color: #007bff;
  color: white;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination-input {
  width: 60px;
  margin: 0 10px;
  padding: 5px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.pagination-btn {
  margin: 0 5px;
  padding: 5px 15px;
  background-color: #007bff;
  color: white;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pagination-btn:hover {
  background-color: #0056b3;
}

.pagination-btn:disabled {
  background-color: #ccc;
}

.chart-btn-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.chart-btn {
  padding: 10px 20px;
  background-color: #28a745;
  color: white;
  border-radius: 4px;
  border: none;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.chart-btn:hover {
  background-color: #218838;
}

#chart-container {
  width: 100%;
  height: 400px;
  margin-top: 30px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.no-data {
  text-align: center;
  color: #f00;
}

.loading {
  text-align: center;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1.5s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
