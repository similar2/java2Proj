<template>
  <div>
    <h1>Answer Quality Analysis</h1>
    <p>This page will analyze the quality of answers.</p>

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
    <div v-if="loading">Loading data...</div>
    <div v-else>
      <table v-if="dataList.length > 0">
        <thead>
        <tr>
          <th v-for="(header, index) in tableHeaders" :key="index">{{ header }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(item, index) in dataList" :key="index">
          <td>{{ item.reputationScore || item.accountAgeDays || item.elapsedMinutes }}</td>
          <td>{{ item.averageScore || item.qualityMetric || item.totalAnswers }}</td>
          <td v-if="currentDataType === 'time'">{{ item.acceptedAnswers }}</td>
          <td v-if="currentDataType === 'time'">{{ item.acceptanceRate }}</td>

          <td>{{ isHighQuality(item) }}</td>
<!--          <td>-->
<!--            &lt;!&ndash; 添加得分列，根据不同数据类型计算评分 &ndash;&gt;-->
<!--            <span>{{ calculateScore(item) }}</span>-->
<!--          </td>-->
        </tr>
        </tbody>
      </table>
      <div v-if="dataList.length === 0">No data available.</div>
    </div>

    <!-- 分页控件 -->
    <div class="pagination">
      <button @click="changePage('prev')" :disabled="currentPage <= 1">Previous</button>
      <span>Page:</span>
      <input
          v-model.number="inputPage"
          @keyup.enter="jumpToPage"
          type="number"
          min="1"
          placeholder="Page number"
      />
      <button @click="jumpToPage">Jump</button>
      <button @click="changePage('next')">Next</button>
    </div>
  </div>
</template>

<script>
import axios from "axios";

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
      tableHeaders: ["Score", "Average/Comment", "High Quality"], // 表格头部，新增High Quality列
    };
  },
  computed: {
    // 动态标题
    currentTableTitle() {
      // Ensure this computed property returns the appropriate title based on the data type
      return this.currentDataType === 'reputation' ? 'Reputation Analysis' : 'Custom Analysis';
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
        this.tableHeaders = ["Reputation Score", "Average Score", "High Quality"];
      } else if (this.currentDataType === "custom") {
        this.tableHeaders = ["Account Age (Days)", "Quality Metric", "High Quality"];
      } else if (this.currentDataType === "time") {
        this.tableHeaders = ["Elapsed Minutes", "Total Answers", "Accepted Answers", "Acceptance Rate", "High Quality"];
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
        console.log("Response:", response.data);
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

    // 根据数据计算得分
    calculateScore(item) {
      let score = 0;
      if (this.currentDataType === "reputation") {
        score = item.reputationScore * 0.5 + item.averageScore * 0.5; // Example score logic
      } else if (this.currentDataType === "custom") {
        score = item.qualityMetric * 0.7 + item.accountAgeDays * 0.3; // Example score logic
      } else if (this.currentDataType === "time") {
        score = item.elapsedMinutes * 0.4 + item.acceptedAnswers * 0.6; // Example score logic
      }
      return score.toFixed(2); // Return formatted score
    },

    // 判断是否为高质量数据
    isHighQuality(item) {
      // Example of high-quality criteria (can be adjusted as needed)
      if (this.currentDataType === "reputation") {
        return item.reputationScore >= 50 && item.averageScore >= 4 ? 'Yes' : 'No';
      } else if (this.currentDataType === "custom") {
        return item.qualityMetric >= 0.8 && item.accountAgeDays > 1000 ? 'Yes' : 'No';
      } else if (this.currentDataType === "time") {
        return item.acceptedAnswers > 2 && item.acceptanceRate >= 0.5 ? 'Yes' : 'No';
      }
      return 'No';
    }
  },
};
</script>

<style scoped>
h1 {
  color: #333;
}
h2 {
  color: #555;
}
.toggle-buttons {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}
.toggle-buttons button {
  padding: 8px 16px;
  margin: 0 10px;
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  cursor: pointer;
  border-radius: 4px;
}
.toggle-buttons button.active {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}
th,
td {
  padding: 8px 12px;
  border: 1px solid #ddd;
  text-align: center;
}
.pagination {
  margin-top: 20px;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}
.pagination button {
  margin: 0 5px;
  padding: 5px 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
.pagination input {
  width: 60px;
  margin: 0 10px;
  padding: 5px;
  text-align: center;
  border: 1px solid #ddd;
  border-radius: 4px;
}
</style>
